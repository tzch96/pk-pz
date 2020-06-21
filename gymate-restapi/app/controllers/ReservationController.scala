package controllers

import models.{DatabaseExecutionContext, Reservation}
import dao.ReservationDao

import java.sql.Timestamp
import java.text.SimpleDateFormat

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.db.Database

@Singleton
class ReservationController @Inject()(db: Database, dbec: DatabaseExecutionContext, cc: ControllerComponents) extends AbstractController(cc) {
  val reservationDao = new ReservationDao(db, dbec)

  implicit object TimestampFormat extends Format[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    def reads(json: JsValue) = {
      val str = json.as[String]
      JsSuccess(new Timestamp(format.parse(str).getTime))
    }
    def writes(ts: Timestamp) = JsString(format.format(ts))
  }

  implicit val reservationWrites = new Writes[Reservation] {
    def writes(reservation: Reservation) = Json.obj(
      "id" -> reservation.id,
      "eventDate" -> reservation.eventDate,
      "userId" -> reservation.userId,
      "offerId" -> reservation.offerId
    )
  }

  implicit val reservationReads: Reads[Reservation] = (
    (__ \ "id").read[Long] and
      (__ \ "event_date").read[Timestamp] and
      (__ \ "user_id").read[Long] and
      (__ \ "offer_id").read[Long]
    )(Reservation.apply _)

  def getReservations = Action { implicit request =>
    Ok(Json.toJson(reservationDao.getAll))
  }

  def getReservationsForUser(id: Long) = Action { implicit request =>
    val reservations = reservationDao.getReservationsForUser(id)

    reservations match {
      case Nil => NotFound(Json.obj("response" -> s"No reservations for user $id"))
      case _ => Ok(Json.toJson(reservations))
    }
  }

  def getReservationsForOffer(id: Long) = Action { implicit request =>
    val reservations = reservationDao.getReservationsForOffer(id)

    reservations match {
      case Nil => NotFound(Json.obj("response" -> s"No reservations for offer $id"))
      case _ => Ok(Json.toJson(reservations))
    }
  }

  def createReservationForOffer(id: Long) = Action(parse.json) { implicit request =>
    val response = reservationDao.createReservationForOffer(
      (request.body \ "event_date").as[Timestamp],
      (request.body \ "user_id").as[Long],
      (request.body \ "offer_id").as[Long])

    if (response != "1") {
      BadRequest(Json.obj("response" -> s"$response"))
    } else {
      Created(Json.obj("response" -> s"Reservation created successfully"))
    }
  }
}
