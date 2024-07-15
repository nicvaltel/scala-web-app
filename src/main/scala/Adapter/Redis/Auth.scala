package Adapter.Redis.Auth

import redis.clients.jedis.*;
import scala.collection.mutable.HashMap
import java.util.Map
import Domain.Auth as A
import com.mifmif.common.regex.Generex


def isEmpty(x: String) = x == null || x.isEmpty

trait SessionRepoInRedis(jedis: Jedis) extends A.SessionRepo:
  val generexSession = new Generex("[A-Za-z0-9]{32}")

  override def newSession(uId: A.UserId): A.SessionId =
    val sId = generexSession.random()
    jedis.set(sId, uId.toString)
    sId

  override def findUserBySessionId(sId: A.SessionId): Option[A.UserId] =
    jedis.get(sId) match
      case uId if isEmpty(uId) => None
      case uId =>
        try {
          val n = uId.toInt
          Some(n)
        } catch{
            case e: NumberFormatException => throw new RuntimeException( "Unexpected Redis exception: " + e)
            case _ => None
        }




def runRedisExample() =
  val pool: JedisPool = new JedisPool("localhost", 6379);

  try {
      val jedis: Jedis = pool.getResource()
      // Store & Retrieve a simple string
      jedis.set("foo", "bar");
      println(jedis.get("foo")); // prints bar

      jedis.get("OKAY") match
        case v if isEmpty(v) => println("Empty result return")
        case v => println(v)
      
      // Store & Retrieve a HashMap
      val hash: java.util.Map[String, String] = new java.util.HashMap();
      hash.put("name", "John");
      hash.put("surname", "Smith");
      hash.put("company", "Redis");
      hash.put("age", "29");
      jedis.hset("user-session:123", hash);
      println(jedis.hgetAll("user-session:123"));
      // Prints: {name=John, surname=Smith, company=Redis, age=29}
  } catch {
      case e => println(e)
  }