package Examples.PGTest

import java.sql.{Connection, DriverManager, Statement}





def runTest() =

  val url = "jdbc:postgresql://localhost:6666/scala-web-app"
  val username = "username"
  val password = "pass"
  val connection: Connection = DriverManager.getConnection(url, username, password)

  val statement: Statement = connection.createStatement()

  val empId = 10
  val age = 28
  val address = "Delhi"
  val dateOfBirth = "01-02-1984"

  val insertQuery = s"INSERT INTO tmp_test_employees (emp_id, age, address, dateOfBirth) VALUES ('$empId', $age, '$address', '$dateOfBirth');"
  statement.executeUpdate(insertQuery)

  val selectQuery = "SELECT * FROM tmp_test_employees"

  val rs = statement.executeQuery(selectQuery)
  val result =  Iterator.from(0).takeWhile(_ => rs.next()).map(_ => rs.getString(3)).toList
  println (result)


  println("Adapter.PostgreSQL.PGTest runTest: DONE")