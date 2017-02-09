package spatutorial.shared

object DateUtils {

  import java.time.Instant
  import java.time.LocalDate
  import java.time.LocalDateTime
  import java.time.ZoneId
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
