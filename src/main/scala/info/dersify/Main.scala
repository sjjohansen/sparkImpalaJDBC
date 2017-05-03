package info.dersify

import java.sql.{Connection, DriverManager}

import org.apache.log4j.{LogManager, Logger}

/*
 * Completely in debt to: http://alvinalexander.com/scala/scala-jdbc-connection-mysql-sql-select-example
 */

object Main {

  val JDBC_DRIVER = "com.cloudera.impala.jdbc41.Driver"
  val SHOW_DBS_SQL = "show databases"
  var connection: Connection = _

  def main(args: Array[String]): Unit = {
    val log: Logger  = LogManager.getLogger(getClass.getName)

    if (args.length != 1) {
      System.err.println("usage: scalaImpalaJDBC <jdbc:url>")
      System.exit(1)
    }

    val jdbcURL = args(0)
    log.info(s"JDBC URL: $jdbcURL")

    try {
      Class.forName(JDBC_DRIVER)
      connection = DriverManager.getConnection(jdbcURL)

      val stmt = connection.createStatement()
      val rs = stmt.executeQuery(SHOW_DBS_SQL)

      while(rs.next()) {
        log.info(s"DB name: ${rs.getString("name")}")
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }

    if (! connection.isClosed) connection.close()
  }
}
