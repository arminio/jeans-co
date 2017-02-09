package spatutorial.shared

import java.text.SimpleDateFormat
import java.util.Date

import boopickle.Default._
import spatutorial.shared.Types._

import scala.util.Try


case class Sale(orderDate: Date,
                deliveryCountry: Country,
                manufacturer: Manufacturer,
                gender: Gender,
                size: Size,
                colour: Colour,
                style: Style,
                count: Count)


object Sale {
  def apply(values: Map[String, String]): Sale = {
    val triedSale = Try(
      Sale(
      new SimpleDateFormat("d-M-yyyy").parse(values("OrderDate")),
      values("DeliveryCountry"),
      values("Manufacturer"),
      values("Gender"),
      values("Size"),
      values("Colour"),
      values("Style"),
      values("Count").toInt))
    triedSale.get
  }
}

case class SaleFilter(orderDate: Option[Date] = None,
                      deliveryCountry: Option[Country] = None,
                      manufacturer: Option[Manufacturer] = None,
                      gender: Option[Gender] = None,
                      size: Option[Size] = None,
                      colour: Option[Colour] = None,
                      style: Option[Style] = None)


object SaleFilter {
  val empty = SaleFilter()
}



sealed trait TodoPriority

case object TodoLow extends TodoPriority

case object TodoNormal extends TodoPriority

case object TodoHigh extends TodoPriority

case class TodoItem(id: String, timeStamp: Int, content: String, priority: TodoPriority, completed: Boolean)

object TodoPriority {
  implicit val todoPriorityPickler: Pickler[TodoPriority] = generatePickler[TodoPriority]
}
