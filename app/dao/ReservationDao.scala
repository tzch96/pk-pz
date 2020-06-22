package dao

import models.{DatabaseExecutionContext, Reservation}

import java.sql.Timestamp
import org.postgresql.util.PSQLException
import play.api.db.Database

import scala.collection.mutable.ListBuffer

class ReservationDao(db: Database, dbec: DatabaseExecutionContext) {
  def getAll = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * FROM reservations")
      val listBuffer = ListBuffer[Reservation]()

      while (rs.next()) {
        listBuffer.append(Reservation(rs.getLong("reservation_id"), rs.getTimestamp("event_date"), rs.getLong("user_id"), rs.getLong("offer_id")))
      }

      listBuffer.toList
    }
  }

  def getReservationsForUser(id: Long) = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * FROM reservations WHERE user_id = $id")
      val listBuffer = ListBuffer[Reservation]()

      while (rs.next()) {
        listBuffer.append(Reservation(rs.getLong("reservation_id"), rs.getTimestamp("event_date"), rs.getLong("user_id"), rs.getLong("offer_id")))
      }

      listBuffer.toList
    }
  }

  def getReservationsForOffer(id: Long) = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * FROM reservations WHERE offer_id = $id")
      val listBuffer = ListBuffer[Reservation]()

      while (rs.next()) {
        listBuffer.append(Reservation(rs.getLong("reservation_id"), rs.getTimestamp("event_date"), rs.getLong("user_id"), rs.getLong("offer_id")))
      }

      listBuffer.toList
    }
  }

  // TODO verify dates when creating a reservation
  def createReservationForOffer(eventDate: Timestamp, userId: Long, offerId: Long) = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = try {
        stmt.executeUpdate(s"INSERT INTO reservations VALUES(" +
          s"(nextval('seq_reservations')), " +
          s"'$eventDate', " +
          s"$userId, " +
          s"$offerId)")
      } catch {
        case e: PSQLException => e.getServerErrorMessage
      }

      rs.toString
    }
  }

  def isUserAlreadyRegistered(eventDate: Timestamp, userId: Long, offerId: Long): Boolean = {
    db.withConnection { conn =>
      val stmt = conn.createStatement

      val rs = stmt.executeQuery(s"SELECT * FROM reservations WHERE event_date = '$eventDate' AND user_id = $userId AND offer_id = $offerId")

      // TODO fix this: there is probably a better way to implement this
      if (rs.next()) true else false
    }
  }
}
