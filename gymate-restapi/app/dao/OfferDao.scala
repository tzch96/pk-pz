package dao


import models.{DatabaseExecutionContext, Offer}

import play.api.db.Database
import org.postgresql.util.PSQLException

import scala.collection.mutable.ListBuffer

class OfferDao(db: Database, dbec: DatabaseExecutionContext) extends Dao {
  // TODO fix dates display and get rid of this ugly thing
  private def convertArray[T](array: java.sql.Array): scala.Array[T] = {
    array.getArray().asInstanceOf[scala.Array[T]]
  }

  override def getAll: List[Offer] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT * FROM offers")
      val listBuffer = ListBuffer[Offer]()

      while (rs.next()) {
        listBuffer.append(Offer(rs.getLong("offer_id"), rs.getString("name"), Option(rs.getString("description")),
          rs.getBigDecimal("single_price"), rs.getBoolean("is_first_free"), convertArray(rs.getArray("dates")), rs.getLong("provider_id")))
      }

      listBuffer.toList
    }
  }

  override def getById(id: Long): Option[Offer] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * FROM offers WHERE offer_id = $id")

      if (rs.next()) {
        Some(Offer(rs.getLong("offer_id"), rs.getString("name"), Option(rs.getString("description")),
          rs.getBigDecimal("single_price"), rs.getBoolean("is_first_free"), convertArray(rs.getArray("dates")), rs.getLong("provider_id")))
      } else {
        None
      }
    }
  }

  override def getIdByName(name: String): Option[Long] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT offer_id FROM offers WHERE name = '$name'")

      if (rs.next()) {
        Some(rs.getLong("offer_id"))
      } else {
        None
      }
    }
  }

  override def getByName(name: String): Option[Offer] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * FROM offers WHERE name = '$name'")

      if (rs.next()) {
        Some(Offer(rs.getLong("offer_id"), rs.getString("name"), Option(rs.getString("description")),
          rs.getBigDecimal("single_price"), rs.getBoolean("is_first_free"), convertArray(rs.getArray("dates")), rs.getLong("provider_id")))
      } else {
        None
      }
    }
  }

  def getByNameMultiple(name: String): List[Offer] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * FROM offers WHERE name = '$name'")
      val listBuffer = ListBuffer[Offer]()

      while (rs.next()) {
        listBuffer.append(Offer(rs.getLong("offer_id"), rs.getString("name"), Option(rs.getString("description")),
          rs.getBigDecimal("single_price"), rs.getBoolean("is_first_free"), convertArray(rs.getArray("dates")), rs.getLong("provider_id")))
      }

      listBuffer.toList
    }
  }

  def create(name: String, description: Option[String], singlePrice: BigDecimal, isFirstFree: Boolean, providerId: Long): String = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val descriptionInsert = description match {
        case None => "NULL"
        case _ => s"'${description.get}'"
      }
      val rs = try {
        stmt.executeUpdate(s"INSERT INTO offers(offer_id, name, description, single_price, is_first_free, provider_id) VALUES(" +
          s"(nextval('seq_offers')), " +
          s"'$name', " +
          s"$descriptionInsert, " +
          s"$singlePrice, " +
          s"$isFirstFree, " +
          s"$providerId)")
      } catch {
        case e: PSQLException => e.getServerErrorMessage
      }

      rs.toString
    }
  }

  def update(id: Long, name: String, description: Option[String], singlePrice: BigDecimal, isFirstFree: Boolean, providerId: Long): String = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val descriptionInsert = description match {
        case None => "NULL"
        case _ => s"'${description.get}'"
      }
      val rs = try {
        stmt.executeUpdate(s"UPDATE offers SET " +
          s"name = '$name', " +
          s"description = $descriptionInsert, " +
          s"single_price = $singlePrice, " +
          s"is_first_free = $isFirstFree, " +
          s"provider_id = $providerId " +
          s"WHERE offer_id = $id")
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
        stmt.executeUpdate(s"DELETE FROM offers WHERE offer_id = $id")
      } catch {
        case e: PSQLException => e.getServerErrorMessage
      }

      rs.toString
    }
  }

  // TODO add dates to offers
}
