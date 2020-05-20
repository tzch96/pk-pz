package controllers

import models.{DatabaseExecutionContext, Offer}
import dao.OfferDao

import java.util.Date
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.db.Database

@Singleton
class OfferController @Inject()(db: Database, dbec: DatabaseExecutionContext, cc: ControllerComponents) extends AbstractController(cc) {
  val offerDao = new OfferDao(db, dbec)

  implicit val offerWrites = new Writes[Offer] {
    def writes(offer: Offer) = Json.obj(
      "id" -> offer.id,
      "name" -> offer.name,
      "description" -> offer.description,
      "singlePrice" -> offer.singlePrice,
      "isFirstFree" -> offer.isFirstFree,
      "dates" -> offer.dates,
      "providerId" -> offer.providerId
    )
  }

  implicit val offerReads: Reads[Offer] = (
    (__ \ "id").read[Long] and
      (__ \ "name").read[String] and
      (__ \ "description").readNullable[String] and
      (__ \ "singlePrice").read[BigDecimal] and
      (__ \ "isFirstFree").read[Boolean] and
      (__ \ "dates").read[Array[Date]] and
      (__ \ "providerId").read[Long]
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

  def createOffer = Action(parse.json) { implicit request =>
    val response = offerDao.create(
      (request.body \ "name").as[String],
      (request.body \ "description").asOpt[String],
      (request.body \ "singlePrice").as[BigDecimal],
      (request.body \ "isFirstFree").as[Boolean],
      (request.body \ "providerId").as[Long])

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
      (request.body \ "providerId").as[Long])

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
