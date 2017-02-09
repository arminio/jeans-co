package spatutorial.client.modules

import diode.react.ReactPot._
import diode.react._
import grouper.SalesGrouper
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.components.Bootstrap._
import spatutorial.client.services._

import scalacss.ScalaCssReact._


object TopManufacturers extends TopSellingGenericComponent {

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      Callback.when(props.proxy().sales.isEmpty)(props.proxy.dispatchCB(RefreshSales))

    def render(p: Props, s: State) = {
      val proxy = p.proxy()
      Panel(Panel.Props("These are the Top Manufacturers"), <.div(
        proxy.sales.renderFailed(ex => "Error loading"),
        proxy.sales.renderPending(_ > 500, _ => "Loading..."),
        proxy.sales.render { sales =>
          val saleFilter = proxy.saleFilter
          <.div(
            createFilterSelectionArea(sales, saleFilter, $.props),
            <.ul(style.listGroup)(SalesGrouper.topSellingManufacturer(sales.items, saleFilter) map { (s) => <.li(s.toString) })
          )

        }
      ))
    }
  }

  // create the React component for To Do management
  val component = ReactComponentB[Props]("TopManufacturers")
    .initialState_P((p: Props) => State(salesFilter = p.proxy().saleFilter)) // initial state from TodoStore
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  
  def apply(proxy: ModelProxy[SalesAndFilter]) = component(Props(proxy))
}

