package armin.jeans.client.modules

import armin.jeans.client.components.Bootstrap.{Button, Panel}
import armin.jeans.client.components.{ChartPopup, Icon}
import armin.jeans.client.services.{RefreshSales, SalesAndFilter}
import diode.react._
import armin.jeans.client.grouper.SalesGrouper
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._


object TopManufacturers extends TopSellingGenericComponent {

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      Callback.when(props.proxy().sales.items.isEmpty)(props.proxy.dispatchCB(RefreshSales))

    def render(p: Props, s: State) = {
      val proxy = p.proxy()
      val saleFilter = proxy.saleFilter
      val topManufacturerFiltered = SalesGrouper.topSellingManufacturer(proxy.sales.items, saleFilter)

      Panel(Panel.Props("Top Manufacturers"), <.div(
          <.div(
            createFilterSelectionArea(proxy.sales, saleFilter, $.props),
            Button(Button.Props(showChartPopup($), addStyles = Seq(bss.buttonXSml)), Icon.pieChart, " Chart"),

            if (s.showChartPopup) {
              ChartPopup(ChartPopup.Props(
                chartProps(
                  dataLabels = topManufacturerFiltered.map(_.manufacturer),
                  data = topManufacturerFiltered.map(_.count.toDouble)), chartCloseHandler($)))
            } else
              Seq.empty[ReactElement],
            listItems(topManufacturerFiltered)
          )

//        }
      ))
    }
  }

  val component = ReactComponentB[Props]("TopManufacturers")
    .initialState_P((p: Props) => State(salesFilter = p.proxy().saleFilter))
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  
  def apply(proxy: ModelProxy[SalesAndFilter]) = component(Props(proxy))
}

