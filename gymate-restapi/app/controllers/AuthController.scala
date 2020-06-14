package controllers

import models.DatabaseExecutionContext
import dao.UserDao
import javax.inject.{Inject, Singleton}
import play.api.db.Database
import play.api.mvc.{AbstractController, ControllerComponents}
import com.github.t3hnar.bcrypt._
import play.api.libs.json.Json

@Singleton
class AuthController @Inject()(db: Database, dbec: DatabaseExecutionContext, cc: ControllerComponents) extends AbstractController(cc) {
  val userDao = new UserDao(db, dbec)

  def login = Action(parse.json) { implicit request =>
    val username = (request.body \ "username").as[String]
    val userId = userDao.getIdByName(username)

    if (userId.isEmpty) {
      BadRequest(Json.obj("response" -> s"User $username does not exist"))
    } else {
      val userPasswordHash = userDao.getPasswordHash(userId.get)

      if ((request.body \ "password").as[String].isBcrypted(userPasswordHash.get)) {
        Ok.withSession(request.session + ("connectedUser" -> userId.toString))
      } else {
        Unauthorized
      }
    }
  }

  def logout = Action { implicit request =>
    Redirect("/").withNewSession
  }

  def getCurrentUser = Action { implicit request =>
    request.session.get("connectedUser").map(user =>
      Ok(user)
    ).getOrElse(
      Unauthorized
    )
  }
}