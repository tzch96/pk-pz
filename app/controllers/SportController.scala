package controllers

import models.{DatabaseExecutionContext, Sport}
import dao.SportDao

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.db.Database

@Singleton
class SportController @Inject()(db: Database, dbec: DatabaseExecutionContext, cc: ControllerComponents) extends AbstractController(cc) {
  val sportDao = new SportDao(db, dbec)

  implicit val sportWrites = new Writes[Sport] {
    def writes(sport: Sport) = Json.obj(
      "id" -> sport.id,
      "name" -> sport.name
    )
  }

  implicit val sportReads: Reads[Sport] = (
    (__ \ "id").read[Long] and
      (__ \ "name").read[String]
    )(Sport.apply _)

  def getSports = Action { implicit request =>
    Ok(Json.toJson(sportDao.getAll))
  }

  def getSportById(id: Long) = Action { implicit request =>
    val maybeSport = sportDao.getById(id)

    maybeSport match {
      case None => NotFound(Json.obj("response" -> s"Sport with id $id not found"))
      case _ => Ok(Json.toJson(maybeSport))
    }
  }

  def getIdBySportName(name: String) = Action { implicit request =>
    val maybeSport = sportDao.getIdByName(name)

    maybeSport match {
      case None => NotFound(Json.obj("response" -> s"Sport with name $name not found"))
      case _ => Ok(Json.toJson(maybeSport))
    }
  }
}
