package spatutorial.client.modules

import diode.react.ReactPot._
import diode.react._
import grouper.SalesGrouper
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.components.Bootstrap._
import spatutorial.client.components.Icon
import spatutorial.client.components.popup.ChartPopup
import spatutorial.client.services._

import scalacss.ScalaCssReact._

object TopSellingCountries extends TopSellingGenericComponent{

  class Backend($: BackendScope[Props, State]) {

    def mounted(props: Props) =
      Callback.when(props.proxy().sales.isEmpty)(props.proxy.dispatchCB(RefreshSales))

    def render(p: Props, s: State) = {
      val proxy = p.proxy()
      Panel(Panel.Props("These are the top selling countires"), <.div(
        proxy.sales.renderFailed(ex => "Error loading"),
        proxy.sales.renderPending(_ > 500, _ => "Loading..."),
        proxy.sales.render { sales =>
          val saleFilter = proxy.saleFilter
          val topCountriesFiltered = SalesGrouper.topSellingCountries(sales.items, saleFilter)
          <.div(
            createFilterSelectionArea(sales, saleFilter, $.props),
            Button(Button.Props(showChartPopup($)), Icon.pieChart, " Chart"),

            if (s.showChartPopup) {
              ChartPopup(ChartPopup.Props(
                chartProps(
                  dataLabels = topCountriesFiltered.map(_.country),
                  data = topCountriesFiltered.map(_.count.toDouble)), chartCloseHandler($)))
            } else
              Seq.empty[ReactElement],
            <.ul(style.listGroup)(topCountriesFiltered map { (s) => <.li(s.toString)})
          )
          
        }
      ))
    }


  }



  val component = ReactComponentB[Props]("TopSellingCountries")
    .initialState_P((p: Props) => State(salesFilter = p.proxy().saleFilter))
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(proxy: ModelProxy[SalesAndFilter]) = component(Props(proxy))
}

