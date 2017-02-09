package spatutorial.client.modules

import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.components._
import spatutorial.client.services._
import spatutorial.shared._

trait TopSellingGenericComponent {
  case class Props(proxy: ModelProxy[SalesAndFilter])
  case class State(selectedItem: Option[Sale] = None, showTodoForm: Boolean = false, salesFilter: SaleFilter = SaleFilter.empty)

  @inline private def bss = GlobalStyles.bootstrapStyles
  val style = bss.listGroup

  def newSelectedValue(e: ReactEventI) =
    if (e.currentTarget.value.toLowerCase().startsWith("select "))
      None
    else
      Some(e.currentTarget.value)

  def makeOptions[T](fieldType: String, things: List[T], selectedItem: Option[T]) = {
    <.option(^.id := "top-select", s"Select $fieldType", selectedItem.isEmpty ?= (^.selected := true)) +:
      things.map(t => <.option(selectedItem.fold(false)(_ == t.toString) ?= (^.selected := true), t.toString))
  }

  def createFilterSelectionArea(sales: Sales, saleFilter: SaleFilter, props: CallbackTo[Props]) = {
    <.div(
      <.select(^.id := "colour", ^.onChange ==> colourFilterSelected(saleFilter, props), makeOptions("Colour", sales.allColours, saleFilter.colour)),
      <.select(^.id := "country", ^.onChange ==> countryFilterSelected(saleFilter, props), makeOptions("Country", sales.allDeliveryCountries, saleFilter.deliveryCountry)),
      <.select(^.id := "gender", ^.onChange ==> genderFilterSelected(saleFilter, props), makeOptions("Gender", sales.allGenders, saleFilter.gender)),
      <.select(^.id := "size", ^.onChange ==> sizeFilterSelected(saleFilter, props), makeOptions("Size", sales.allSizes, saleFilter.size)),
      <.select(^.id := "style", ^.onChange ==> styleFilterSelected(saleFilter, props), makeOptions("Style", sales.allStyles, saleFilter.style)),
      <.button("Rest Filters", ^.onClick --> resetFilters(props))
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




