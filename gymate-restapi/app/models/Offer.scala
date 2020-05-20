package models

import java.util.Date

case class Offer(id: Long, name: String, description: Option[String], singlePrice: BigDecimal, isFirstFree: Boolean, dates: Array[Date], providerId: Long) extends Model
