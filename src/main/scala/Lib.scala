package Lib

import Domain.Auth as A
import Adapter.InMemory.Auth as M
import Adapter.PostgreSQL.Auth as PG
import java.sql.{Connection, DriverManager, Statement}
import io.github.cdimascio.dotenv.Dotenv


class App(pgStatement: Statement) extends A.Session, PG.AuthRepoInMemory(pgStatement), M.EmailVerficationNotifInMemory, M.SessionRepoInMemory


def action(dotenv: Dotenv): Unit =
  println("TESTIN LIB ACTION:")

  val url = s"jdbc:postgresql://${dotenv.get("POSTGRES_HOST")}:${dotenv.get("POSTGRES_PORT")}/${dotenv.get("POSTGRES_DB")}"
  val username = dotenv.get("POSTGRES_USER")
  val password = dotenv.get("POSTGRES_PASSWORD")
  val pgStatement: Statement = PG.mkStatement(url, username, password)
  val s = new App(pgStatement)

  val email = A.Email.mkEmail("mail8@example.md").getOrElse(???)
  val pass = A.Password.mkPassword("1234567AAAaaa").getOrElse(???)
  val auth = new A.Auth(email, pass)
  
  s.register(auth)
  val vCode = M.getNotificationsForEmail(email).getOrElse(???)
  s.verifyEmail(vCode)
  val session = s.login(auth).getOrElse(???)
  val uId = s.resolveSessionId(session).getOrElse(???)
  val registeredEmail = s.getUser(uId).getOrElse(???)
  println(s"session = $session, uId = $uId, regEmail = $registeredEmail")

