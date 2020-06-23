package dao


import models.{DatabaseExecutionContext, Sport}

import play.api.db.Database
import org.postgresql.util.PSQLException

import scala.collection.mutable.ListBuffer

class SportDao(db: Database, dbec: DatabaseExecutionContext) extends Dao {
  override def getAll: List[Sport] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT * FROM sports")
      val listBuffer = ListBuffer[Sport]()

      while (rs.next()) {
        listBuffer.append(Sport(rs.getLong("sport_id"), rs.getString("name")))
      }

      listBuffer.toList
    }
  }

  override def getById(id: Long): Option[Sport] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * FROM sports WHERE sport_id = $id")

      if (rs.next()) {
        Some(Sport(rs.getLong("sport_id"), rs.getString("name")))
      } else {
        None
      }
    }
  }

  override def getIdByName(name: String): Option[Long] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT sport_id FROM sports WHERE name = '$name'")

      if (rs.next()) {
        Some(rs.getLong("sport_id"))
      } else {
        None
      }
    }
  }

  override def getByName(name: String): Option[Sport] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * FROM sports WHERE name = '$name'")

      if (rs.next()) {
        Some(Sport(rs.getLong("sport_id"), rs.getString("name")))
      } else {
        None
      }
    }
  }

  def create(name: String): String = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = try {
        stmt.executeUpdate(s"INSERT INTO sports(sport_id, name) VALUES((nextval('seq_sports')), '$name')")
      } catch {
        case e: PSQLException => e.getServerErrorMessage
      }

      rs.toString
    }
  }

  def update(id: Long, name: String): String = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = try {
        stmt.executeUpdate(s"UPDATE sports SET name = '$name' WHERE sport_id = $id")
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
        stmt.executeUpdate(s"DELETE FROM sports WHERE sport_id = $id")
      } catch {
        case e: PSQLException => e.getServerErrorMessage
      }

      rs.toString
    }
  }
}
