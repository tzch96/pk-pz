package models

import java.sql.Timestamp

case class Reservation(id: Long, eventDate: Timestamp, userId: Long, offerId: Long) extends Model
