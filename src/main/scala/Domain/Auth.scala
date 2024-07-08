package Domain.Auth

import Domain.Validation.*
import scala.util.matching.Regex


class Auth(
  val email: Email,
  val password: Password):

  override def toString = s"Auth: email = $email, password = $password"
end Auth


class Email private(private val emailRaw: String):  
  def rawEmail = emailRaw
  override def toString = s"Email: $rawEmail"

object Email:
  def mkEmail(emailRaw: String): Either[List[EmailValidationErr], Email] =
    def constructor = (email: String) => new Email(email)
    val regexValidation: Validation[EmailValidationErr, String] = regexMatches(
      new Regex("^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}$"), 
      EmailValidationErr.InvalidEmail)
    validate(constructor, List(regexValidation), emailRaw)

class Password private(private val passwordRaw: String):
  def rawPassword = passwordRaw
  override def toString = s"Password: $rawPassword"

object Password:
  def mkPassword(passwordRaw: String): Either[List[PasswordValidationErr], Password] =
    def constructor = (pass: String) => new Password(pass)
    val validations: List[Validation[PasswordValidationErr, String]] = List(
        lengthBetween(8, 50, PasswordValidationErr.Length(8,50))
      , regexMatches(new Regex("\\d"), PasswordValidationErr.MustContainNumber)
      , regexMatches(new Regex("[A-Z]"), PasswordValidationErr.MustContainUpperCase)
      , regexMatches(new Regex("[a-z]"), PasswordValidationErr.MustContainLowerCase)
    )
    validate(constructor, validations, passwordRaw)


type VerificationCode = String

trait AuthRepo:
  def addAuth(auth: Auth): Either[RegistrationError, VerificationCode]

trait EmailVerficationNotif:
  def notifyEmailVerification(email: Email, vCode: VerificationCode): Unit

abstract class Session extends AuthRepo, EmailVerficationNotif:
  def register(auth: Auth): Either[RegistrationError, Unit] =
    val eitherVCode = addAuth(auth)
    eitherVCode match
      case Left(err) => Left(err)
      case Right(vCode) => Right(notifyEmailVerification(auth.email, vCode))

class SessionIO extends Session, AuthRepoIO, EmailVerficationNotifIO

trait AuthRepoIO extends Session, AuthRepo:
  override def addAuth(auth: Auth) =
    println(s"adding auth: ${auth.email.rawEmail}")
    Right("fake verification code")

trait EmailVerficationNotifIO extends EmailVerficationNotif:
  override def notifyEmailVerification(email: Email, vcode: VerificationCode) =
    println(s"Notify ${email.rawEmail} - $vcode")

enum RegistrationError:
  case EmailTaken

enum EmailValidationErr:
  case InvalidEmail

enum PasswordValidationErr: 
  case Length(lenMin: Int, lenMax: Int)
  case MustContainUpperCase
  case MustContainLowerCase
  case MustContainNumber

def testAuth():Unit = 
  println("TEST AUTH:")

  val mail = Email.mkEmail("name@test.md")
  val pass = Password.mkPassword("22asdfQWE")
  println(mail)
  println(pass)

  val session = new SessionIO
  val email = Email.mkEmail("test@example.md").toOption.getOrElse(???)
  println(email)
  val password = Password.mkPassword("1234ASDFqwerty").toOption.getOrElse(???)
  println(password)
  val auth = new Auth(email, password)
  session.register(auth)
