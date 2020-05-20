package controllers

import models.{DatabaseExecutionContext, User}
import dao.UserDao

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.db.Database
import play.api.libs.json._
import play.api.libs.functional.syntax._
import com.github.t3hnar.bcrypt._

@Singleton
class UserController @Inject()(db: Database, dbec: DatabaseExecutionContext, cc: ControllerComponents) extends AbstractController(cc) {
  val userDao = new UserDao(db, dbec)

  implicit val userWrites = new Writes[User] {
    def writes(user: User) = Json.obj(
      "id" -> user.id,
      "username" -> user.username,
      "email" -> user.email,
      "accountType" -> user.accountType
    )
  }

  implicit val userReads: Reads[User] = (
    (__ \ "id").read[Long] and
      (__ \ "username").read[String] and
      (__ \ "email").readNullable[String] and
      (__ \ "accountType").read[Long]
    )(User.apply _)

  def getUsers = Action { implicit request =>
    Ok(Json.toJson(userDao.getAll))
  }

  def getUserById(id: Long) = Action { implicit request =>
    val maybeUser = userDao.getById(id)

    maybeUser match {
      case None => NotFound(Json.obj("response" -> s"User with id $id not found"))
      case _ => Ok(Json.toJson(maybeUser))
    }
  }

  def getIdByUsername(username: String) = Action { implicit request =>
    val maybeId = userDao.getIdByName(username)

    maybeId match {
      case None => NotFound(Json.obj("response" -> s"User $username not found"))
      case _ => Ok(Json.obj("id" -> maybeId.get))
    }
  }

  def searchUserByUsername(username: String) = Action { implicit request =>
    val maybeUser = userDao.getByName(username)

    maybeUser match {
      case None => NotFound(Json.obj("response" -> s"User $username not found"))
      case _ => Ok(Json.toJson(maybeUser))
    }
  }

  def createUser = Action(parse.json) { implicit request =>
    val response = userDao.create(
      (request.body \ "username").as[String],
      (request.body \ "email").asOpt[String],
      (request.body \ "password").as[String].bcrypt,
      (request.body \ "accountType").as[String])

    if (response != "1") {
      BadRequest(Json.obj("response" -> s"$response"))
    } else {
      Created(Json.obj("response" -> s"User created successfully"))
    }
  }

  def updateUser(id: Long) = Action(parse.json) { implicit request =>
    val response = userDao.update(
      id,
      (request.body \ "username").as[String],
      (request.body \ "email").asOpt[String],
      (request.body \ "password").as[String].bcrypt,
      (request.body \ "accountType").as[String])

    if (response != "1") {
      BadRequest(Json.obj("response" -> s"$response"))
    } else {
      Ok(Json.obj("response" -> s"User $id updated successfully"))
    }
  }

  def deleteUser(id: Long) = Action { implicit request =>
    val response = userDao.delete(id)

    response match {
      case "1" => Ok(Json.obj("response" -> s"Deleted user $id"))
      case _ => BadRequest(Json.obj("response" -> response))
    }
  }
}
