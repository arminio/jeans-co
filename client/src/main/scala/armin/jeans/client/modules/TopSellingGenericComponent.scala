package armin.jeans.client.modules

import armin.jeans.client.components.Bootstrap.{Button, CommonStyle}
import armin.jeans.client.components._
import armin.jeans.client.modules.TopSellingMonths.showChartPopup
import armin.jeans.client.services.{ResetSalesFilter, Sales, SalesAndFilter, UpdatedSalesFilter}
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import armin.jeans.shared._
import grouper.TopSelling
import util.RandomColourGenerator

trait TopSellingGenericComponent {
  case class Props(proxy: ModelProxy[SalesAndFilter])
  case class State(selectedItem: Option[Sale] = None, showChartPopup: Boolean = false, salesFilter: SaleFilter = SaleFilter.empty)

  @inline protected def bss = GlobalStyles.bootstrapStyles
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



  def listItems[T](items: Seq[TopSelling[T]]) = {
    val label = items.headOption.fold("No Data")(i => i.getLabel)

    <.div(^.`class` := "top-selling-list",
      <.table(^.`class` := "table",
        <.thead(
          <.tr(
          <.th(label, ^.`class` := "col-md-3"), <.th("Count", ^.`class` := "col-md-3")
          )
        ),
        <.tbody(
          items.map { i =>
            <.tr(
              <.td(i.getCategory.toString),
              <.td(i.count)
            )
          }
        )
      )
    )
  }


  def createFilterSelectionArea(sales: Sales, saleFilter: SaleFilter, props: CallbackTo[Props]) = {
    val colourKey = "Colour"
    val countryKey = "Country"
    val genderKey = "Gender"
    val sizeKey = "Size"
    val styleKey = "Style"
    <.div(
      <.div(^.`class` := "form-group filter-area",
        <.h2("Use these filters to get more targeted statistics:"),
        
        <.div(^.`class` := "col-sm-10", Icon.filter, " Filters: ",
          <.select(^.id := "colour", ^.value := saleFilter.colour.getOrElse(defaultSelectValue(colourKey)),^.onChange ==> colourFilterSelected(saleFilter, props), makeSelectOptions(colourKey, sales.allColours, saleFilter.colour)),
          <.select(^.id := "country", ^.value := saleFilter.deliveryCountry.getOrElse(defaultSelectValue(countryKey)), ^.onChange ==> countryFilterSelected(saleFilter, props), makeSelectOptions(countryKey, sales.allDeliveryCountries, saleFilter.deliveryCountry)),
          <.select(^.id := "gender", ^.value := saleFilter.gender.getOrElse(defaultSelectValue(genderKey)), ^.onChange ==> genderFilterSelected(saleFilter, props), makeSelectOptions(genderKey, sales.allGenders, saleFilter.gender)),
          <.select(^.id := "size", ^.value := saleFilter.size.getOrElse(defaultSelectValue(sizeKey)), ^.onChange ==> sizeFilterSelected(saleFilter, props), makeSelectOptions(sizeKey, sales.allSizes, saleFilter.size)),
          <.select(^.id := "style", ^.value := saleFilter.style.getOrElse(defaultSelectValue(styleKey)), ^.onChange ==> styleFilterSelected(saleFilter, props), makeSelectOptions(styleKey, sales.allStyles, saleFilter.style)),
          Button(Button.Props(resetFilters(props), addStyles = Seq(bss.buttonXSml)), Icon.refresh, " Reset")
        )
      )

    )
  }

  def makeSelectOptions[T](fieldType: String, things: List[T], selectedItem: Option[T]) = {
    <.option(defaultSelectValue(fieldType)) +:
      things.map(t => <.option(selectedItem.fold(false)(_ == t.toString) ?= (^.selected := true), t.toString))
  }

  private def defaultSelectValue[T](fieldType: String) = {
    s"Select $fieldType"
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




