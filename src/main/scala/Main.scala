import Lib.*
import Domain.Auth
import Domain.Validation
import Adapter.InMemory.Auth as M
import Examples.Log as Log
import Adapter.Redis.Auth as RDS 
import io.github.cdimascio.dotenv.Dotenv


@main def m(): Unit =

  val dotenv: Dotenv = Dotenv.load()

  // Log.runLog()
  // Validation.testValidations()
  // Auth.testAuth()
  // M.testInMemory()
  // RDS.runRedisExample()

  Lib.action(dotenv)
