package models

case class Opinion(id: Long, rating: Int, contents: Option[String], userId: Long, offerId: Long) extends Model
