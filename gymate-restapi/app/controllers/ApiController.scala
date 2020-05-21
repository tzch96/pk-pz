package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class ApiController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  def ping = Action { implicit request =>
    Ok("pong")
  }

  def docs = Action {
    Redirect("/docs/swagger-ui/index.html?url=/assets/swagger.json")
  }
}
