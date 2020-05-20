package models

import java.util.Date

case class Reservation(id: Long, eventDate: Date, userId: Long, offerId: Long) extends Model
