import Lib.*
import Domain.Auth
import Domain.Validation
import Adapter.InMemory.Auth as M
import Log as Log

@main def hello(): Unit =
  // Log.runLog()
  Validation.testValidations()
  Auth.testAuth()
  M.testInMemory()
  Lib.action()
