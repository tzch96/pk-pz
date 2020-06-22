package models

case class User(id: Long, username: String, email: Option[String], accountType: Long) extends Model
