package spatutorial.client.modules

import diode.react.ReactPot._
import diode.react._
import grouper.SalesGrouper
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.components.Bootstrap._
import spatutorial.client.components._
import spatutorial.client.services._
import spatutorial.shared._

import scalacss.ScalaCssReact._

trait GenericComponent {
  case class Props(proxy: ModelProxy[SalesAndFilter])
  case class State(selectedItem: Option[Sale] = None, showTodoForm: Boolean = false, salesFilter: SaleFilter = SaleFilter.empty)

  @inline private def bss = GlobalStyles.bootstrapStyles
  val style = bss.listGroup

  def newSelectedValue(e: ReactEventI) =
    if (e.currentTarget.value.toLowerCase().startsWith("select "))
      None
    else
      Some(e.currentTarget.value)

}


object TopManufacturers extends GenericComponent {

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      Callback.when(props.proxy().sales.isEmpty)(props.proxy.dispatchCB(RefreshSales))

    def render(p: Props, s: State) = {
      val proxy = p.proxy()
      Panel(Panel.Props("What needs to be done"), <.div(
        proxy.sales.renderFailed(ex => "Error loading"),
        proxy.sales.renderPending(_ > 500, _ => "Loading..."),
        proxy.sales.render { sales =>
          val saleFilter = proxy.saleFilter
          <.div(
            <.div(
              <.select(^.id := "colour", ^.onChange ==> colourFilterSelected(saleFilter), makeOptions("Colour", sales.allColours, saleFilter.colour)),
              <.select(^.id := "country", ^.onChange ==> countryFilterSelected(saleFilter), makeOptions("Country", sales.allDeliveryCountries, saleFilter.deliveryCountry)),
              <.select(^.id := "gender", ^.onChange ==> genderFilterSelected(saleFilter), makeOptions("Gender", sales.allGenders, saleFilter.gender)),
              <.select(^.id := "size", ^.onChange ==> sizeFilterSelected(saleFilter), makeOptions("Size", sales.allSizes, saleFilter.size)),
              <.select(^.id := "style", ^.onChange ==> styleFilterSelected(saleFilter), makeOptions("Style", sales.allStyles, saleFilter.style)),
              <.button("Rest Filters", ^.onClick --> resetFilters)
            ),

            <.ul(style.listGroup)(SalesGrouper.topSellingManufacturer(sales.items, saleFilter) map { (s) => <.li(s.toString) })
          )

        }
      ))
    }

    def makeOptions[T](fieldType: String, things: List[T], selectedItem: Option[T]) = {
      <.option(^.id := "top-select", s"Select $fieldType", selectedItem.isEmpty ?= (^.selected := true)) +:
        things.map(t => <.option(selectedItem.fold(false)(_ == t.toString) ?= (^.selected := true), t.toString))
    }

    def colourFilterSelected(saleFilter: SaleFilter)(e: ReactEventI) = {
      val props: CallbackTo[Props] = $.props
      props >>= (p => p.proxy.dispatchCB(UpdatedSalesFilter(saleFilter.copy(colour = newSelectedValue(e)))))
    }

    def countryFilterSelected(saleFilter: SaleFilter)(e: ReactEventI) =
      $.props >>= (p => p.proxy.dispatchCB(UpdatedSalesFilter(saleFilter.copy(deliveryCountry = newSelectedValue(e)))))

    def genderFilterSelected(saleFilter: SaleFilter)(e: ReactEventI) =
      $.props >>= (p => p.proxy.dispatchCB(UpdatedSalesFilter(saleFilter.copy(gender = newSelectedValue(e)))))

    def sizeFilterSelected(saleFilter: SaleFilter)(e: ReactEventI) =
      $.props >>= (p => p.proxy.dispatchCB(UpdatedSalesFilter(saleFilter.copy(size = newSelectedValue(e)))))

    def styleFilterSelected(saleFilter: SaleFilter)(e: ReactEventI) =
      $.props >>= (p => p.proxy.dispatchCB(UpdatedSalesFilter(saleFilter.copy(style = newSelectedValue(e)))))

    def resetFilters() =
      $.props >>= (p => p.proxy.dispatchCB(ResetSalesFilter))

  }

  // create the React component for To Do management
  val component = ReactComponentB[Props]("TODO")
    .initialState_P((p: Props) => State(salesFilter = p.proxy().saleFilter)) // initial state from TodoStore
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(proxy: ModelProxy[SalesAndFilter]) = component(Props(proxy))
}

