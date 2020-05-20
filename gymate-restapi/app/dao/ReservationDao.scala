package dao

import models.{DatabaseExecutionContext, Reservation}

import java.util.Date
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
        listBuffer.append(Reservation(rs.getLong("reservation_id"), rs.getDate("event_date"), rs.getLong("user_id"), rs.getLong("offer_id")))
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
        listBuffer.append(Reservation(rs.getLong("reservation_id"), rs.getDate("event_date"), rs.getLong("user_id"), rs.getLong("offer_id")))
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
        listBuffer.append(Reservation(rs.getLong("reservation_id"), rs.getDate("event_date"), rs.getLong("user_id"), rs.getLong("offer_id")))
      }

      listBuffer.toList
    }
  }

  // TODO pass currentUserId as default userId - after LoginController is done
  // TODO verify dates when creating a reservation - after dates are added to OfferDao
  def createReservationForOffer(eventDate: Date, userId: Long, offerId: Long) = {
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
}
