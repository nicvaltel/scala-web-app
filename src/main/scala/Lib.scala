package Lib

import Domain.Auth as A
import Adapter.InMemory.Auth as M
import Adapter.PostgreSQL.Auth as PG
import java.sql.{Connection, DriverManager, Statement}


class App(pgStatement: Statement) extends A.Session, PG.AuthRepoInMemory(pgStatement), M.EmailVerficationNotifInMemory, M.SessionRepoInMemory


def action(): Unit =
  println("TESTIN LIB ACTION:")

  val url = "jdbc:postgresql://localhost:6666/scala-web-app"
  val username = "username"
  val password = "pass"
  val pgStatement: Statement = PG.mkStatement(url, username, password)
  val s = new App(pgStatement)

  val email = A.Email.mkEmail("mail7@example.md").getOrElse(???)
  val pass = A.Password.mkPassword("1234567AAAaaa").getOrElse(???)
  val auth = new A.Auth(email, pass)
  
  s.register(auth)
  val vCode = M.getNotificationsForEmail(email).getOrElse(???)
  s.verifyEmail(vCode)
  val session = s.login(auth).getOrElse(???)
  val uId = s.resolveSessionId(session).getOrElse(???)
  val registeredEmail = s.getUser(uId).getOrElse(???)
  println(s"session = $session, uId = $uId, regEmail = $registeredEmail")

