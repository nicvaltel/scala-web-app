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
  // def apply(emailRaw: String) = new Email(emailRaw)
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
  // def apply(passwordRaw: String) = new Password(passwordRaw)
  def mkPassword(passwordRaw: String): Either[List[PasswordValidationErr], Password] =
    def constructor = (pass: String) => new Password(pass)
    val validations: List[Validation[PasswordValidationErr, String]] = List(
        lengthBetween(8, 50, PasswordValidationErr.Length(8,50))
      , regexMatches(new Regex("\\d"), PasswordValidationErr.MustContainNumber)
      , regexMatches(new Regex("[A-Z]"), PasswordValidationErr.MustContainUpperCase)
      , regexMatches(new Regex("[a-z]"), PasswordValidationErr.MustContainLowerCase)
    )
    validate(constructor, validations, passwordRaw)


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