package dao

import models.{DatabaseExecutionContext, Opinion}

import org.postgresql.util.PSQLException
import play.api.db.Database

import scala.collection.mutable.ListBuffer

class OpinionDao(db: Database, dbec: DatabaseExecutionContext) {
  def getAll = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * FROM opinions")
      val listBuffer = ListBuffer[Opinion]()

      while (rs.next()) {
        listBuffer.append(Opinion(rs.getLong("opinion_id"), rs.getInt("rating"), Option(rs.getString("contents")), rs.getLong("user_id"), rs.getLong("offer_id")))
      }

      listBuffer.toList
    }
  }

  def getOpinionsForOffer(id: Long) = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(s"SELECT * FROM opinions WHERE offer_id = $id")
      val listBuffer = ListBuffer[Opinion]()

      while (rs.next()) {
        listBuffer.append(Opinion(rs.getLong("opinion_id"), rs.getInt("rating"), Option(rs.getString("contents")), rs.getLong("user_id"), rs.getLong("offer_id")))
      }

      listBuffer.toList
    }
  }

  // TODO pass currentUserId as default userId - after LoginController is done
  def createOpinionForOffer(rating: Int, contents: Option[String], userId: Long, offerId: Long) = {
    db.withConnection { conn =>
      val stmt = conn.createStatement
      val contentsInsert = contents match {
        case None => "NULL"
        case _ => s"'${contents.get}'"
      }
      val rs = try {
        stmt.executeUpdate(s"INSERT INTO opinions VALUES(" +
          s"(nextval('seq_opinions')), " +
          s"$rating, " +
          s"$contentsInsert, " +
          s"$userId, " +
          s"$offerId)")
      } catch {
        case e: PSQLException => e.getServerErrorMessage
      }

      rs.toString
    }
  }
}
