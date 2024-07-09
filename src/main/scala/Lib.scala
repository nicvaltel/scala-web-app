package Lib

import Adapter.InMemory.Auth as M
import Domain.Auth as A


class App extends A.Session, M.AuthRepoInMemory, M.EmailVerficationNotifInMemory, M.SessionRepoInMemory


def action(): Unit =
  println("TESTIN LIB ACTION:")

  val email = A.Email.mkEmail("mail@example.md").getOrElse(???)
  val pass = A.Password.mkPassword("1234567AAAaaa").getOrElse(???)
  val auth = new A.Auth(email, pass)
  
  val s = new App
  s.register(auth)
  val vCode = M.getNotificationsForEmail(email).getOrElse(???)
  s.verifyEmail(vCode)
  val session = s.login(auth).getOrElse(???)
  val uId = s.resolveSessionId(session).getOrElse(???)
  val registeredEmail = s.getUser(uId).getOrElse(???)
  println(s"session = $session, uId = $uId, regEmail = $registeredEmail")

