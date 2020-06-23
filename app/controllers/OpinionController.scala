package controllers

import models.{DatabaseExecutionContext, Opinion}
import dao.OpinionDao

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.db.Database

@Singleton
class OpinionController @Inject()(db: Database, dbec: DatabaseExecutionContext, cc: ControllerComponents) extends AbstractController(cc) {
  val opinionDao = new OpinionDao(db, dbec)

  implicit val opinionWrites = new Writes[Opinion] {
    def writes(opinion: Opinion) = Json.obj(
      "id" -> opinion.id,
      "rating" -> opinion.rating,
      "contents" -> opinion.contents,
      "userId" -> opinion.userId,
      "offerId" -> opinion.offerId
    )
  }

  implicit val opinionReads: Reads[Opinion] = (
    (__ \ "id").read[Long] and
      (__ \ "rating").read[Int] and
      (__ \ "contents").readNullable[String] and
      (__ \ "user_id").read[Long] and
      (__ \ "offer_id").read[Long]
    )(Opinion.apply _)

  def getOpinions = Action { implicit request =>
    Ok(Json.toJson(opinionDao.getAll))
  }

  def getOpinionsForOffer(id: Long) = Action { implicit request =>
    val opinions = opinionDao.getOpinionsForOffer(id)

    opinions match {
      case Nil => NotFound(Json.obj("response" -> s"No opinions for offer $id"))
      case _ => Ok(Json.toJson(opinions))
    }
  }

  def createOpinionForOffer(id: Long) = Action(parse.json) { implicit request =>
    val response = opinionDao.createOpinionForOffer(
      (request.body \ "rating").as[Int],
      (request.body \ "contents").asOpt[String],
      (request.body \ "user_id").as[Long],
      (request.body \ "offer_id").as[Long])

    if (response != "1") {
      BadRequest(Json.obj("response" -> s"$response"))
    } else {
      Created(Json.obj("response" -> s"Opinion created successfully"))
    }
  }

}
