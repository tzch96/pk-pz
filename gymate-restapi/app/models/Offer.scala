package models

import java.sql.Timestamp

case class Offer(id: Long, name: String, description: Option[String], singlePrice: BigDecimal, isFirstFree: Boolean,
                 dates: Array[Timestamp], providerId: Long, latitude: BigDecimal, longitude: BigDecimal, sportId: Long) extends Model
