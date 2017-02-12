package armin.jeans.client.util

import scala.scalajs.js
import scala.language.implicitConversions


object DateUtils {

  import java.time.{Instant, LocalDate, LocalDateTime, ZoneId}
  import java.util.Date

  val monthNames = List(
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December")

  implicit def toJavaDate(jsDate: js.Date) = {
    new Date(jsDate.getTime().toLong)
  }

  implicit def asDate(localDate:LocalDate): Date = {
//    Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant)
    new Date(localDate.toEpochDay)
  }

  def asDate(localDateTime: LocalDateTime ): Date = {
    Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant)
  }

  def asLocalDate(date: Date): LocalDate = {
    Instant.ofEpochMilli(date.getTime).atZone(ZoneId.systemDefault()).toLocalDate
  }

  def asLocalDateTime(date: Date): LocalDateTime = {
    Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime
  }
}
