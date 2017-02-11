package spatutorial.client.modules

import diode.react.ReactPot._
import diode.react._
import grouper.SalesGrouper
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.components.Bootstrap._
import spatutorial.client.components.Chart
import spatutorial.client.components.popup.ChartPopup
import spatutorial.client.services._
import spatutorial.shared.TodoItem

import scala.util.Random
import scalacss.ScalaCssReact._

object TopSellingCountries extends TopSellingGenericComponent{

  class Backend($: BackendScope[Props, State]) {

    def mounted(props: Props) =
      Callback.when(props.proxy().sales.isEmpty)(props.proxy.dispatchCB(RefreshSales))

    def chartCloseHandler(cancelled: Boolean): CallbackTo[Unit] = {
      // hide the dialog
      $.modState(s => s.copy(showChartPopup = false))
    }


    def render(p: Props, s: State) = {
      val proxy = p.proxy()
      Panel(Panel.Props("These are the top selling countires"), <.div(
        proxy.sales.renderFailed(ex => "Error loading"),
        proxy.sales.renderPending(_ > 500, _ => "Loading..."),
        proxy.sales.render { sales =>
          val saleFilter = proxy.saleFilter
          val topCountriesFiltered = SalesGrouper.topSellingCountries(sales.items, saleFilter)
          val chartProps1 = chartProps(Random.nextString(10), Random.nextString(10), topCountriesFiltered.map(_.country), topCountriesFiltered.map(_.count.toDouble))
//          val chart = Chart(chartProps1)

          <.div(
            createFilterSelectionArea(sales, saleFilter, $.props),
            <.a("Chart", ^.onClick --> showChartPopup),
//                        chart,
            //            if (s.showChartPopup) TodoForm(TodoForm.Props(s.selectedItem, todoEdited))
            //            else
            //              Seq.empty[ReactElement])
            if (s.showChartPopup) {
              ChartPopup(ChartPopup.Props(chartProps1, chartCloseHandler))
            } else
              Seq.empty[ReactElement],
            <.ul(style.listGroup)(topCountriesFiltered map { (s) => <.li(s.toString)})
          )
          
        }
      ))
    }

    def showChartPopup = {
      $.modState(_.copy(showChartPopup = true))
    }
  }



  val component = ReactComponentB[Props]("TopSellingCountries")
    .initialState_P((p: Props) => State(salesFilter = p.proxy().saleFilter))
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(proxy: ModelProxy[SalesAndFilter]) = component(Props(proxy))
}

