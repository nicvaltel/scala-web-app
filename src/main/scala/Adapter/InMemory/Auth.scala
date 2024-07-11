package Adapter.InMemory.Auth

import Domain.Auth as A
import scala.collection.mutable
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.stm._
import com.mifmif.common.regex.Generex

private class State(
    var auths: List[(A.UserId, A.Auth)] = List()
  , var unverifiedEmails: Map[A.VerificationCode, A.Email] = Map()
  , var verifiedEmails: Set[A.Email] = Set()
  , var userIdCounter: Int = 0
  , var notifications: Map[A.Email, A.VerificationCode] = Map()
  , var sessions: Map[A.SessionId, A.UserId] = Map()
)

private object InMemory:
  val state: State = new State()

trait AuthRepoInMemory extends A.AuthRepo:
  val generexAuth = new Generex("[A-Za-z0-9]{16}")

  override def addAuth(auth: A.Auth): Either[A.RegistrationError, (A.UserId, A.VerificationCode)] =
    val vCode = generexAuth.random()
    atomic { implicit txn =>
      val isDuplicate = InMemory.state.auths.exists((_,a) => a.email == auth.email)
      if isDuplicate
        then Left(A.RegistrationError.EmailTaken)
        else
          val newUserId = InMemory.state.userIdCounter + 1
          InMemory.state.auths = (newUserId, auth) :: InMemory.state.auths
          InMemory.state.unverifiedEmails += (vCode -> auth.email)
          InMemory.state.userIdCounter = newUserId
          Right(newUserId, vCode)
    }

  override def setEmailAsVerified(vCode: A.VerificationCode): Either[A.EmailVerficationError, (A.UserId, A.Email)] =
    atomic { implicit txn =>
      InMemory.state.unverifiedEmails.get(vCode) match
        case None => Left(A.EmailVerficationError.InvalidCode)
        case Some(email) =>
          InMemory.state.auths.find((_, a) => a.email == email) match
            case None => Left(A.EmailVerficationError.InvalidCode)
            case Some ((uId, auth)) =>
              InMemory.state.unverifiedEmails -= vCode
              InMemory.state.verifiedEmails += email
              Right((uId, email))
    }

  override def findUserByAuth(auth: A.Auth): Option[(A.UserId, Boolean)] =
    InMemory.state.auths.find((_,a) => a == auth).map((uId,_) =>
      val isVerified = InMemory.state.verifiedEmails.contains(auth.email)
      (uId,isVerified) 
      )
  
  override def findEmailFromUserId(uId: A.UserId): Option[A.Email] =
    InMemory.state.auths.find((u,_) => u == uId).map((_,auth) => auth.email)

trait EmailVerficationNotifInMemory extends A.EmailVerficationNotif:
  override def notifyEmailVerification(email: A.Email, vCode: A.VerificationCode): Unit =
    atomic { implicit txn =>
      InMemory.state.notifications += (email -> vCode)
    }

trait SessionRepoInMemory extends A.SessionRepo:
  val generexSession = new Generex("[A-Za-z0-9]{16}")

  override def newSession(uId: A.UserId): A.SessionId =
    val sId = generexSession.random()
    atomic { implicit txn =>
      InMemory.state.sessions += (sId -> uId)
    }
    sId

  override def findUserBySessionId(sId: A.SessionId): Option[A.UserId] =
    InMemory.state.sessions.get(sId)
    

def getNotificationsForEmail(email: A.Email): Option[A.VerificationCode] =
  InMemory.state.notifications.get(email)

class SessionInMemory extends A.Session, AuthRepoInMemory, EmailVerficationNotifInMemory, SessionRepoInMemory

def testInMemory() =
  println("TESTIN IN MEMORY:")

  val email1 = A.Email.mkEmail("test@example.md").getOrElse(???)
  val pass1 = A.Password.mkPassword("1234567AAAaaa").getOrElse(???)
  val auth1 = new A.Auth(email1, pass1)
  
  val email2 = A.Email.mkEmail("test@example.md").getOrElse(???)
  val pass2 = A.Password.mkPassword("1234567AAAaaa").getOrElse(???)
  val auth2 = new A.Auth(email2, pass2)
  

  val s = new SessionInMemory
  println(s.register(auth1))

  val vCode = s.addAuth(auth2)
  println(vCode)
  val pairUIdVerified = s.findUserByAuth(auth2).getOrElse(???)
  println(pairUIdVerified)
  val uId = pairUIdVerified(0)
  println(s.findEmailFromUserId(uId))
  val sId = s.newSession(uId)
  println(sId)
  println(s.findUserBySessionId(sId))


