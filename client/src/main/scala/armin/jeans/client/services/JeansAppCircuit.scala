package armin.jeans.client.services

import java.util.Date

import armin.jeans.shared.Types._
import armin.jeans.shared.{Api, Sale, SaleFilter}
import autowire._
import diode._
import diode.data._
import diode.react.ReactConnector
//!@ intellij removes these imports :-(
//import boopickle.Default._
//import grouper.SalesGrouper
//import armin.jeans.shared.Types._
import boopickle.Default._
import armin.jeans.client.grouper.SalesGrouper
import armin.jeans.shared.Types._

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

// Actions
case object RefreshSales extends Action
case class UpdateAllSales(sales: Seq[Sale]) extends Action
case class UpdatedSalesFilter(salesFilter: SaleFilter) extends Action
case object ResetSalesFilter extends Action

case class SalesAndFilter(sales: Sales, saleFilter: SaleFilter)

// The base model of our application
case class RootModel(salesAndFilter: SalesAndFilter)



case class Sales(items: Seq[Sale]) {
  val allDeliveryCountries: List[Country] = items.groupBy(_.deliveryCountry).keys.toList.sorted
  val allStyles: List[Style] = items.groupBy(_.style).keys.toList.sorted
  val allColours: List[Colour] = items.groupBy(_.colour).keys.toList.sorted
  val allOrderDates: List[Date] = items.groupBy(_.orderDate).keys.toList.sorted
  val allGenders: List[Gender] = items.groupBy(_.gender).keys.toList.sorted
  val allManufacturers: List[Manufacturer] = items.groupBy(_.manufacturer).keys.toList.sorted
  val allSizes: List[Size] = items.groupBy(_.size).keys.toList.sorted
}



/**
  * Handles actions related to sales (loading/reloading)
  *
  * @param modelRW Reader/Writer to access the model
  */
class SalesHandler[M](modelRW: ModelRW[M, Sales]) extends ActionHandler(modelRW) {
  override def handle = {
    case RefreshSales =>
      effectOnly(Effect(AjaxClient[Api].getAllOrders().call().map(UpdateAllSales)))
    case UpdateAllSales(sales) =>
      // got new sales, update model
      updated(Sales(sales))
  }
}
/**
  * Handles actions related to sales filter selections on pages
  *
  * @param modelRW Reader/Writer to access the model
  */
class FilterUpdateHandler[M](modelRW: ModelRW[M, SaleFilter]) extends ActionHandler(modelRW) {
  override def handle = {
    case UpdatedSalesFilter(filter) =>
      updated(filter)
    case ResetSalesFilter =>
      updated(SaleFilter.empty)
  }
}


object JeansAppCircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  // initial application model
  override protected def initialModel = RootModel(SalesAndFilter(Sales(Seq.empty), SaleFilter.empty))
  // combine all handlers into one
  override protected val actionHandler = composeHandlers(
    new SalesHandler(zoomRW(_.salesAndFilter.sales)((m, v) => m.copy(salesAndFilter = m.salesAndFilter.copy(sales = v)))),
    new FilterUpdateHandler(zoomRW(_.salesAndFilter.saleFilter)((m, v) => m.copy(salesAndFilter = m.salesAndFilter.copy(saleFilter = v))))
  )
}