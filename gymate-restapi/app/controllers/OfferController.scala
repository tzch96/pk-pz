package controllers

import models.{DatabaseExecutionContext, Offer}
import dao.OfferDao

import java.sql.Timestamp
import java.text.SimpleDateFormat

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.db.Database

@Singleton
class OfferController @Inject()(db: Database, dbec: DatabaseExecutionContext, cc: ControllerComponents) extends AbstractController(cc) {
  val offerDao = new OfferDao(db, dbec)

  implicit object TimestampFormat extends Format[Timestamp] {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'")
    def reads(json: JsValue) = {
      val str = json.as[String]
      JsSuccess(new Timestamp(format.parse(str).getTime))
    }
    def writes(ts: Timestamp) = JsString(format.format(ts))
  }

  implicit val offerWrites = new Writes[Offer] {
    def writes(offer: Offer) = Json.obj(
      "id" -> offer.id,
      "name" -> offer.name,
      "description" -> offer.description,
      "singlePrice" -> offer.singlePrice,
      "isFirstFree" -> offer.isFirstFree,
      "dates" -> offer.dates,
      "providerId" -> offer.providerId,
      "latitude" -> offer.latitude,
      "longitude" -> offer.longitude,
      "sportId" -> offer.sportId,
      "spots" -> offer.spots
    )
  }

  implicit val offerReads: Reads[Offer] = (
    (__ \ "id").read[Long] and
      (__ \ "name").read[String] and
      (__ \ "description").readNullable[String] and
      (__ \ "singlePrice").read[BigDecimal] and
      (__ \ "isFirstFree").read[Boolean] and
      (__ \ "dates").read[Array[Timestamp]] and
      (__ \ "providerId").read[Long] and
      (__ \ "latitude").read[BigDecimal] and
      (__ \ "longitude").read[BigDecimal] and
      (__ \ "sportId").read[Long] and
      (__ \ "spots").read[Int]
  )(Offer.apply _)

  def getOffers = Action { implicit request =>
    Ok(Json.toJson(offerDao.getAll))
  }

  def getOfferById(id: Long) = Action { implicit request =>
    val maybeOffer = offerDao.getById(id)

    maybeOffer match {
      case None => NotFound(Json.obj("response" -> s"Offer with id $id not found"))
      case _ => Ok(Json.toJson(maybeOffer))
    }
  }

  def searchOffersByName(name: String) = Action { implicit request =>
    Ok(Json.toJson(offerDao.getByNameMultiple(name)))
  }

  def searchOffersBySport(sport: String) = Action { implicit request =>
    Ok(Json.toJson(offerDao.getBySport(sport)))
  }

  def createOffer = Action(parse.json) { implicit request =>
    val response = offerDao.create(
      (request.body \ "name").as[String],
      (request.body \ "description").asOpt[String],
      (request.body \ "singlePrice").as[BigDecimal],
      (request.body \ "isFirstFree").as[Boolean],
      (request.body \ "providerId").as[Long],
      (request.body \ "latitude").as[BigDecimal],
      (request.body \ "longitude").as[BigDecimal],
      (request.body \ "sportId").as[Long],
      (request.body \ "spots").as[Int])

    if (response != "1") {
      BadRequest(Json.obj("response" -> s"$response"))
    } else {
      Created(Json.obj("response" -> s"Offer created successfully"))
    }
  }

  def updateOffer(id: Long) = Action(parse.json) { implicit request =>
    val response = offerDao.update(id,
      (request.body \ "name").as[String],
      (request.body \ "description").asOpt[String],
      (request.body \ "singlePrice").as[BigDecimal],
      (request.body \ "isFirstFree").as[Boolean],
      (request.body \ "providerId").as[Long],
      (request.body \ "latitude").as[BigDecimal],
      (request.body \ "longitude").as[BigDecimal],
      (request.body \ "sportId").as[Long],
      (request.body \ "spots").as[Int])

    if (response != "1") {
      BadRequest(Json.obj("response" -> s"$response"))
    } else {
      Ok(Json.obj("response" -> s"Offer $id updated successfully"))
    }
  }

  def deleteOffer(id: Long) = Action { implicit request =>
    val response = offerDao.delete(id)

    response match {
      case "1" => Ok(Json.obj("response" -> s"Deleted offer $id"))
      case _ => BadRequest(Json.obj("response" -> response))
    }
  }
}
