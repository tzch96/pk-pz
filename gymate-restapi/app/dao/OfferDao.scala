package dao


import models.{DatabaseExecutionContext, Offer}

import play.api.db.Database
import org.postgresql.util.PSQLException

import scala.collection.mutable.ListBuffer

class OfferDao(db: Database, dbec: DatabaseExecutionContext) extends Dao {
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
          rs.getBigDecimal("single_price"), rs.getBoolean("is_first_free"), convertArray(rs.getArray("dates")) , rs.getLong("provider_id"),
          rs.getBigDecimal("latitude"), rs.getBigDecimal("longitude"), rs.getLong("sport_id"), rs.getInt("spots")))
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
          rs.getBigDecimal("single_price"), rs.getBoolean("is_first_free"), convertArray(rs.getArray("dates")), rs.getLong("provider_id"),
          rs.getBigDecimal("latitude"), rs.getBigDecimal("longitude"), rs.getLong("sport_id"), rs.getInt("spots")))
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
          rs.getBigDecimal("single_price"), rs.getBoolean("is_first_free"), convertArray(rs.getArray("dates")), rs.getLong("provider_id"),
          rs.getBigDecimal("latitude"), rs.getBigDecimal("longitude"), rs.getLong("sport_id"), rs.getInt("spots")))
      } else {
        None
      }
    }
  }

  def getByNameMultiple(name: String): List[Offer] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * FROM offers WHERE name LIKE '%$name%'")
      val listBuffer = ListBuffer[Offer]()

      while (rs.next()) {
        listBuffer.append(Offer(rs.getLong("offer_id"), rs.getString("name"), Option(rs.getString("description")),
          rs.getBigDecimal("single_price"), rs.getBoolean("is_first_free"), convertArray(rs.getArray("dates")), rs.getLong("provider_id"),
          rs.getBigDecimal("latitude"), rs.getBigDecimal("longitude"), rs.getLong("sport_id"), rs.getInt("spots")))
      }

      listBuffer.toList
    }
  }

  def getBySport(sport: String): List[Offer] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * FROM offers o WHERE o.sport_id IN (SELECT sport_id FROM sports WHERE name = '$sport')")
      val listBuffer = ListBuffer[Offer]()

      while (rs.next()) {
        listBuffer.append(Offer(rs.getLong("offer_id"), rs.getString("name"), Option(rs.getString("description")),
          rs.getBigDecimal("single_price"), rs.getBoolean("is_first_free"), convertArray(rs.getArray("dates")), rs.getLong("provider_id"),
          rs.getBigDecimal("latitude"), rs.getBigDecimal("longitude"), rs.getLong("sport_id"), rs.getInt("spots")))
      }

      listBuffer.toList
    }
  }

  def getSpots(id: Long): Option[Int] = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT spots FROM offers WHERE offer_id = $id")

      if (rs.next()) {
        Some(rs.getInt("spots"))
      } else {
        None
      }
    }
  }

  def decrementSpots(id: Long) = {
    db.withConnection { conn =>
      val stmt = conn.createStatement

      stmt.executeUpdate(s"UPDATE offers SET spots = spots - 1 WHERE offer_id = $id")
    }
  }

  def create(name: String, description: Option[String], singlePrice: BigDecimal, isFirstFree: Boolean, providerId: Long,
             latitude: BigDecimal, longitude: BigDecimal, sportId: Long, spots: Int): String = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val descriptionInsert = description match {
        case None => "NULL"
        case _ => s"'${description.get}'"
      }
      val rs = try {
        stmt.executeUpdate(s"INSERT INTO offers(offer_id, name, description, single_price, is_first_free, provider_id, latitude, longitude, sport_id, spots) VALUES(" +
          s"(nextval('seq_offers')), " +
          s"'$name', " +
          s"$descriptionInsert, " +
          s"$singlePrice, " +
          s"$isFirstFree, " +
          s"$providerId, " +
          s"$latitude, " +
          s"$longitude, " +
          s"$sportId, " +
          s"$spots)")
      } catch {
        case e: PSQLException => e.getServerErrorMessage
      }

      rs.toString
    }
  }

  def update(id: Long, name: String, description: Option[String], singlePrice: BigDecimal, isFirstFree: Boolean, providerId: Long,
             latitude: BigDecimal, longitude: BigDecimal, sportId: Long, spots: Int): String = {
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
          s"provider_id = $providerId," +
          s"latitude = $latitude, " +
          s"longitude = $longitude," +
          s"sport_id = $sportId," +
          s"spots = $spots " +
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

  // TODO add the possibility of adding dates to existing offers by other means than update
}
