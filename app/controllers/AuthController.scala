package controllers

import models.{DatabaseExecutionContext, User}
import dao.UserDao

import java.time.Clock
import javax.inject.{Inject, Singleton}
import play.api.db.Database
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.Configuration
import com.github.t3hnar.bcrypt._
import play.api.libs.json._
import pdi.jwt.{JwtJson, JwtAlgorithm}

@Singleton
class AuthController @Inject()(db: Database, dbec: DatabaseExecutionContext, cc: ControllerComponents, configuration: Configuration) extends AbstractController(cc) {
  val userDao = new UserDao(db, dbec)

  val secret = configuration.underlying.getString("jwt.secret")
  val algorithm = JwtAlgorithm.HS256

  implicit val clock: Clock = Clock.systemUTC

  implicit val userWrites = new Writes[User] {
    def writes(user: User) = Json.obj(
      "id" -> user.id,
      "username" -> user.username,
      "email" -> user.email,
      "accountType" -> user.accountType
    )
  }

  // TODO move this and UserController.userToken to something like Utils class
  def userToken(user: Option[User], key: String, algorithm: JwtAlgorithm) = {
    val claim = Json.toJson(user).as[JsObject]

    JwtJson.encode(claim, key, algorithm)
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
        val user = userDao.getByName(username.get)

        Ok(Json.toJson(user).as[JsObject] ++ Json.obj("jwt" -> userToken(user, secret, algorithm))).
          withSession(request.session + ("connectedUser" -> userId.toString) +
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