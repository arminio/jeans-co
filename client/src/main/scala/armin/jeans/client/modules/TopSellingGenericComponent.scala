package armin.jeans.client.modules

import armin.jeans.client.components.Bootstrap.Button
import armin.jeans.client.components._
import armin.jeans.client.modules.TopSellingMonths.showChartPopup
import armin.jeans.client.services.{ResetSalesFilter, Sales, SalesAndFilter, UpdatedSalesFilter}
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import armin.jeans.shared._
import util.RandomColourGenerator

trait TopSellingGenericComponent {
  case class Props(proxy: ModelProxy[SalesAndFilter])
  case class State(selectedItem: Option[Sale] = None, showChartPopup: Boolean = false, salesFilter: SaleFilter = SaleFilter.empty)

  @inline private def bss = GlobalStyles.bootstrapStyles
  val style = bss.listGroup

  def newSelectedValue(e: ReactEventI) =
    if (e.currentTarget.value.toLowerCase().startsWith("select "))
      None
    else
      Some(e.currentTarget.value)

  def chartProps(name: String = "", label: String = "", dataLabels: Seq[String], data: Seq[Double]) =
    Chart.ChartProps(
      name,
      Chart.PieChart,
      ChartData(dataLabels, Seq(ChartDataset(data, label, RandomColourGenerator.of(data.size)))
      )
    )

  def chartCloseHandler(childBackend: BackendScope[Props,State])(cancelled: Boolean): CallbackTo[Unit] = {
    // hide the dialog
    childBackend.modState(s => s.copy(showChartPopup = false))
  }

  def showChartPopup(childBackend: BackendScope[Props,State]) = {
    childBackend.modState(_.copy(showChartPopup = true))
  }

  def makeSelectOptions[T](fieldType: String, things: List[T], selectedItem: Option[T]) = {
    <.option(s"Select $fieldType", selectedItem.isEmpty ?= (^.selected := true)) +:
      things.map(t => <.option(selectedItem.fold(false)(_ == t.toString) ?= (^.selected := true), t.toString))
  }

  def createFilterSelectionArea(sales: Sales, saleFilter: SaleFilter, props: CallbackTo[Props]) = {
    <.div(^.`class` := "form-group",
      <.div( ^.`class` := "col-sm-10",
        <.select(^.id := "colour" ,^.onChange ==> colourFilterSelected(saleFilter, props), makeSelectOptions("Colour", sales.allColours, saleFilter.colour)),
        <.select(^.id := "country", ^.onChange ==> countryFilterSelected(saleFilter, props), makeSelectOptions("Country", sales.allDeliveryCountries, saleFilter.deliveryCountry)),
        <.select(^.id := "gender", ^.onChange ==> genderFilterSelected(saleFilter, props), makeSelectOptions("Gender", sales.allGenders, saleFilter.gender)),
        <.select(^.id := "size", ^.onChange ==> sizeFilterSelected(saleFilter, props), makeSelectOptions("Size", sales.allSizes, saleFilter.size)),
        <.select(^.id := "style", ^.onChange ==> styleFilterSelected(saleFilter, props), makeSelectOptions("Style", sales.allStyles, saleFilter.style)),
//        <.button("Rest Filters", ^.onClick --> resetFilters(props)),
      Button(Button.Props(resetFilters(props)), Icon.refresh, " Reset")
      )
    )
  }

  def colourFilterSelected(saleFilter: SaleFilter, props: CallbackTo[Props])(e: ReactEventI) =
    props >>= (p => p.proxy.dispatchCB(UpdatedSalesFilter(saleFilter.copy(colour = newSelectedValue(e)))))

  def countryFilterSelected(saleFilter: SaleFilter, props: CallbackTo[Props])(e: ReactEventI) =
    props >>= (p => p.proxy.dispatchCB(UpdatedSalesFilter(saleFilter.copy(deliveryCountry = newSelectedValue(e)))))

  def genderFilterSelected(saleFilter: SaleFilter, props: CallbackTo[Props])(e: ReactEventI) =
    props >>= (p => p.proxy.dispatchCB(UpdatedSalesFilter(saleFilter.copy(gender = newSelectedValue(e)))))

  def sizeFilterSelected(saleFilter: SaleFilter, props: CallbackTo[Props])(e: ReactEventI) =
    props >>= (p => p.proxy.dispatchCB(UpdatedSalesFilter(saleFilter.copy(size = newSelectedValue(e)))))

  def styleFilterSelected(saleFilter: SaleFilter, props: CallbackTo[Props])(e: ReactEventI) =
    props >>= (p => p.proxy.dispatchCB(UpdatedSalesFilter(saleFilter.copy(style = newSelectedValue(e)))))

  def resetFilters(props: CallbackTo[Props]) =
    props >>= (p => p.proxy.dispatchCB(ResetSalesFilter))

}




