package armin.jeans.client.modules

import diode.react.ReactPot._
import diode.react._
import grouper.SalesGrouper
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import armin.jeans.client.components.Bootstrap._
import armin.jeans.client.components.Icon
import armin.jeans.client.components.popup.ChartPopup
import TopManufacturers.{chartCloseHandler, chartProps, showChartPopup}
import armin.jeans.client.services._

import scalacss.ScalaCssReact._

object TopSellingColours extends TopSellingGenericComponent {



  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      Callback.when(props.proxy().sales.isEmpty)(props.proxy.dispatchCB(RefreshSales))

    def render(p: Props, s: State) = {
      val proxy = p.proxy()
      Panel(Panel.Props("These are the Top Colours"), <.div(
        proxy.sales.renderFailed(ex => "Error loading"),
        proxy.sales.renderPending(_ > 500, _ => "Loading..."),
        proxy.sales.render { sales =>
          val saleFilter = proxy.saleFilter
          val topColoursFiltered = SalesGrouper.topSellingColours(sales.items, saleFilter)
          <.div(
            createFilterSelectionArea(sales, saleFilter, $.props),
            Button(Button.Props(showChartPopup($)), Icon.pieChart, " Chart"),

            if (s.showChartPopup) {
              ChartPopup(ChartPopup.Props(
                chartProps(
                  dataLabels = topColoursFiltered.map(_.colour),
                  data = topColoursFiltered.map(_.count.toDouble)), chartCloseHandler($)))
            } else
              Seq.empty[ReactElement],
            <.ul(style.listGroup)(topColoursFiltered map { (s) => <.li(s.toString)})
          )

        }
      ))
    }
  }


  val component = ReactComponentB[Props]("TopSellingColours")
    .initialState_P((p: Props) => State(salesFilter = p.proxy().saleFilter))
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(proxy: ModelProxy[SalesAndFilter]) = component(Props(proxy))
}

