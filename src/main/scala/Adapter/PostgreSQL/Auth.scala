package Adapter.PostgreSQL.Auth

import java.sql.{Connection, DriverManager, Statement, ResultSet}
import Domain.Auth as A
import com.mifmif.common.regex.Generex
import org.postgresql.util.PSQLException

def mkStatement(url: String, username: String, password: String): Statement =
  DriverManager.getConnection(url, username, password).createStatement()
  

// n - column number starts from 1
private def resultStringToList(rs: ResultSet, n: Int): List[String] = Iterator.from(0).takeWhile(_ => rs.next()).map(_ => rs.getString(n)).toList
private def resultIntToList(rs: ResultSet, n: Int): List[Int] = Iterator.from(0).takeWhile(_ => rs.next()).map(_ => rs.getInt(n)).toList
private def resultToList[A](f: ResultSet => A, rs: ResultSet): List[A] = 
  Iterator.from(0).takeWhile(_ => rs.next()).map(_ => f(rs)).toList

trait AuthRepoInMemory(statement: Statement) extends A.AuthRepo:
  val generexAuth = new Generex("[A-Za-z0-9]{16}")

  
  override def addAuth(auth: A.Auth): Either[A.RegistrationError, (A.UserId, A.VerificationCode)] =
    val email = auth.email.rawEmail
    val pass = auth.password.rawPassword
    val vCode = email + "_" + generexAuth.random()
    val qry = s"insert into auths (email, pass, email_verification_code, is_email_verified) values ('$email', crypt('$pass', gen_salt('bf')), '$vCode', 'f') returning id"
    try {
      val result = statement.executeQuery(qry)
      resultIntToList(result, 1) match
      
        case uId :: Nil => Right((uId, vCode))
        case _ => throw new RuntimeException("Should not happen: PG doesn't return userId")
    } catch {
        case e: PSQLException =>
          if e.getSQLState == "23505" && (e.getServerErrorMessage.getMessage `contains` "auths_email_key")
            then Left(A.RegistrationError.EmailTaken)
            else throw new RuntimeException( "Unhandled PG exception: " + e)
        case e => throw new RuntimeException( "Unhandled PG exception: " + e)
    }

  override def setEmailAsVerified(vCode: A.VerificationCode): Either[A.EmailVerficationError, (A.UserId, A.Email)] =
    val qry = s"update auths set is_email_verified = 't' where email_verification_code = '$vCode' returning id, cast (email as text)"
    try {
      val result = statement.executeQuery(qry)
      println(resultToList(r => (r.getInt(1), r.getString(2)), result))
      resultToList(r => (r.getInt(1), r.getString(2)), result) match
        case (uId, mail) :: Nil => A.Email.mkEmail(mail) match
            case Right(email) => Right(uId, email)
            case _ => throw new RuntimeException("Should not happen: email in DB is not valid: " + mail)
        case _ => Left(A.EmailVerficationError.InvalidCode) 
    } catch {
        case e => throw new RuntimeException( "Unhandled PG exception: " + e)
    }

  override def findUserByAuth(auth: A.Auth): Option[(A.UserId, Boolean)] =
    val email = auth.email.rawEmail
    val pass = auth.password.rawPassword
    val qry = s"select id, is_email_verified from auths where email = '$email' and pass = crypt('$pass', pass)"
    try {
      val result = statement.executeQuery(qry)
      resultToList(r => (r.getInt(1), r.getBoolean(2)), result) match
        case (uId, isVerified) :: Nil => Some ((uId, isVerified))
        case _ => None
    } catch {
        case e => throw new RuntimeException( "Unhandled PG exception: " + e)
    }

  override def findEmailFromUserId(uId: A.UserId): Option[A.Email] =
    val qry = s"select cast(email as text) from auths where id = '$uId'"
    try {
      val result = statement.executeQuery(qry)
      resultStringToList(result, 1) match
        case mail :: Nil => A.Email.mkEmail(mail) match
            case Right(email) => Some(email)
            case _ => throw new RuntimeException("Should not happen: email in DB is not valid: " + mail)
        case _ => None
    } catch {
        case e => throw new RuntimeException( "Unhandled PG exception: " + e)
    }
