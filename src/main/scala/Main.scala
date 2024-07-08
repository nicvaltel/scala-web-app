import Lib.routine
import Domain.Auth
import Domain.Validation

@main def hello(): Unit =
  Validation.testValidations()
  Auth.testAuth()
