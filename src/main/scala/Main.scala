import Lib.routine
import Domain.Auth
import Domain.Validation
import Adapter.InMemory.Auth as M

@main def hello(): Unit =
  Validation.testValidations()
  Auth.testAuth()
  M.testInMemory()
