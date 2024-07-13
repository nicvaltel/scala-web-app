import Lib.*
import Domain.Auth
import Domain.Validation
import Adapter.InMemory.Auth as M
import Examples.Log as Log


@main def m(): Unit =
  Log.runLog()
  Validation.testValidations()
  Auth.testAuth()
  M.testInMemory()
  Lib.action()
