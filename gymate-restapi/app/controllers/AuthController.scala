package controllers

import models.{DatabaseExecutionContext, User}
import dao.UserDao

import javax.inject.{Inject, Singleton}
import play.api.db.Database
import play.api.mvc.{AbstractController, ControllerComponents}
import com.github.t3hnar.bcrypt._
import play.api.libs.json.{Json, Writes}

@Singleton
class AuthController @Inject()(db: Database, dbec: DatabaseExecutionContext, cc: ControllerComponents) extends AbstractController(cc) {
  val userDao = new UserDao(db, dbec)

  implicit val userWrites = new Writes[User] {
    def writes(user: User) = Json.obj(
      "id" -> user.id,
      "username" -> user.username,
      "email" -> user.email,
      "accountType" -> user.accountType
    )
  }

  def login = Action(parse.json) { implicit request =>
    val username = (request.body \ "username").asOpt[String]
    val email = (request.body \ "email").asOpt[String]

    val userId =
      if (username.isDefined) userDao.getIdByName(username.get)
      else if (email.isDefined) userDao.getIdByEmail(email.get)
      else None

    if (userId.isEmpty) {
      BadRequest(Json.obj("response" -> s"User $username does not exist"))
    } else {
      val userPasswordHash = userDao.getPasswordHash(userId.get)

      if ((request.body \ "password").as[String].isBcrypted(userPasswordHash.get)) {
        Ok(Json.toJson(userDao.getByName(username.get))).withSession(request.session + ("connectedUser" -> userId.toString) +
          ("connectedUserType" -> userDao.getUserType(userId.get).getOrElse("undefined")))
      } else {
        Unauthorized
      }
    }
  }

  def logout = Action { implicit request =>
    Redirect("/docs").withNewSession.flashing("success" -> "Logged out")
  }

  def getCurrentUser = Action { implicit request =>
    request.session.get("connectedUser").map(user =>
      Ok(user)
    ).getOrElse(
      Unauthorized
    )
  }

  def getCurrentUserType = Action { implicit request =>
    request.session.get("connectedUserType").map(userType =>
      Ok(userType)
    ).getOrElse(
      Unauthorized
    )
  }
}