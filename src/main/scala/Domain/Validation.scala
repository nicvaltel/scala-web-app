package Domain.Validation

import scala.util.matching.Regex
import Utils.Utils.identity

type Validation[E,A] = A => Option[E]


def validate[A,B,E](constructor: A => B, validations: List[Validation[E,A]], value: A): Either[List[E],B] =
  val mbErrors = validations.map(f => f(value))
  val errors = mbErrors.toList.flatten
  errors match
    case Nil => Right(constructor(value))
    case errs => Left(errs)


def rangeBetween[A,E](minRange: A, maxRange: A, msg:E)(using ord: Ordering[A]): Validation[E,A] =
  (value: A) => Some(msg)
    if ord.gteq(value, minRange) && ord.lteq(value, maxRange) 
      then None
      else Some(msg)  

def lengthBetween[E](minLen: Int, maxLen: Int, msg: E): Validation[E,String] =
  (value: String) => rangeBetween(minLen, maxLen, msg)(value.length)

def regexMatches[E](regex: Regex, msg: E): Validation[E, String] =
  (value: String) => 
    regex.findFirstMatchIn(value) match
      case Some(_) => None
      case None => Some(msg)


def testValidations():Unit = 
  println("TEST VALIDATIONS:")
  println(lengthBetween(1,5,"err")("12345"))
  println(lengthBetween(1,5,"err")("123456"))
  println(regexMatches(new Regex("^hello"),"err")("hello world"))
  println(regexMatches(new Regex("^hello"),"err")("failed world"))

  val mustContainA: Validation[String, String] = regexMatches(new Regex("A"), "Must contain 'A'")
  val mustContainB: Validation[String, String] = regexMatches(new Regex("B"), "Must contain 'B'")

  println(validate(identity[String], List(mustContainA, mustContainB), "abc"))
  println(validate(identity[String], List(mustContainA, mustContainB), "ABc"))
  

  