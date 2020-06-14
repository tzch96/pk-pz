package dao

import models.{DatabaseExecutionContext, User}

import play.api.db.Database
import org.postgresql.util.PSQLException

import scala.collection.mutable.ListBuffer

class UserDao(db: Database, dbec: DatabaseExecutionContext) extends Dao {
  override def getAll: List[User] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT * FROM users")
      val listBuffer = ListBuffer[User]()

      while (rs.next()) {
        listBuffer.append(User(rs.getLong("user_id"), rs.getString("username"), Option(rs.getString("email")), rs.getLong("account_type_id")))
      }

      listBuffer.toList
    }
  }

  override def getById(id: Long): Option[User] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * FROM users WHERE user_id = $id")

      if (rs.next()) {
        Some(User(rs.getLong("user_id"), rs.getString("username"), Option(rs.getString("email")), rs.getLong("account_type_id")))
      } else {
        None
      }
    }
  }

  def getPasswordHash(id: Long): Option[String] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT password_hash FROM users WHERE user_id = $id")

      if (rs.next()) {
        Some(rs.getString("password_hash"))
      } else {
        None
      }
    }
  }

  override def getIdByName(name: String): Option[Long] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT user_id FROM users WHERE username = '$name'")

      if (rs.next()) {
        Some(rs.getLong("user_id"))
      } else {
        None
      }
    }
  }

  override def getByName(name: String): Option[User] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * FROM users WHERE username = '$name'")

      if (rs.next()) {
        Some(User(rs.getLong("user_id"), rs.getString("username"), Option(rs.getString("email")), rs.getLong("account_type_id")))
      } else {
        None
      }
    }
  }

  def create(username: String, email: Option[String], passwordHash: String, accountType: String): String = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val emailInsert = email match {
        case None => "NULL"
        case _ => s"'${email.get}'"
      }
      val rs = try {
        stmt.executeUpdate(s"INSERT INTO users VALUES(" +
          s"(nextval('seq_users')), " +
          s"'$username', " +
          s"$emailInsert, " +
          s"'$passwordHash', " +
          s"(SELECT now()), " +
          s"(SELECT account_type_id FROM account_types WHERE account_type = '$accountType'))")
      } catch {
        case e: PSQLException => e.getServerErrorMessage
      }

      rs.toString
    }
  }

  def update(id: Long, username: String, email: Option[String], passwordHash: String, accountType: String): String = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val emailInsert = email match {
        case None => "NULL"
        case _ => s"'${email.get}'"
      }
      val rs = try {
        stmt.executeUpdate(s"UPDATE users SET " +
          s"username = '$username', " +
          s"email = $emailInsert, " +
          s"password_hash = '$passwordHash', " +
          s"account_type_id = (SELECT account_type_id FROM account_types WHERE account_type = '$accountType')" +
          s"WHERE user_id = $id")
      } catch {
        case e: PSQLException => e.getServerErrorMessage
      }

      rs.toString
    }
  }

  override def delete(id: Long): String = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = try {
        stmt.executeUpdate(s"DELETE FROM users WHERE user_id = $id")
      } catch {
        case e: PSQLException => e.getServerErrorMessage
      }

      rs.toString
    }
  }
}
