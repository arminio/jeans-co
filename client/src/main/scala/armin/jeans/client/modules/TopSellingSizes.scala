package armin.jeans.client.modules

import armin.jeans.client.components.Bootstrap._
import armin.jeans.client.components._
import armin.jeans.client.components.ChartPopup
import armin.jeans.client.services._
import diode.react.ReactPot._
import diode.react._
import grouper.SalesGrouper
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object TopSellingSizes extends TopSellingGenericComponent {

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      Callback.when(props.proxy().sales.isEmpty)(props.proxy.dispatchCB(RefreshSales))

    def render(p: Props, s: State) = {
      val proxy = p.proxy()
      Panel(Panel.Props("Top Selling Sizes"), <.div(
        proxy.sales.renderFailed(ex => "Error loading"),
        proxy.sales.renderPending(_ > 500, _ => "Loading..."),
        proxy.sales.render { sales =>
          val saleFilter = proxy.saleFilter
          val topSizesFiltered = SalesGrouper.topSellingSizes(sales.items, saleFilter)
          <.div(
            createFilterSelectionArea(sales, saleFilter, $.props),
            Button(Button.Props(showChartPopup($), addStyles = Seq(bss.buttonXSml)), Icon.pieChart, " Chart"),

            if (s.showChartPopup) {
              ChartPopup(ChartPopup.Props(
                chartProps(
                  dataLabels = topSizesFiltered.map(_.size),
                  data = topSizesFiltered.map(_.count.toDouble)), chartCloseHandler($)))
            } else
              Seq.empty[ReactElement],
            listItems(topSizesFiltered)
          )
        }
      ))
    }
  }



  val component = ReactComponentB[Props]("Sizes")
    .initialState_P((p: Props) => State(salesFilter = p.proxy().saleFilter))
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(proxy: ModelProxy[SalesAndFilter]) = component(Props(proxy))
}

