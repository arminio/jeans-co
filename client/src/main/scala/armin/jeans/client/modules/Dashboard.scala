package armin.jeans.client.modules

import armin.jeans.client.SPAMain._
import armin.jeans.client.components.{Chart, ChartData, ChartDataset}
import diode.data.Pot
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import util.RandomColourGenerator

import scala.language.existentials
import scala.util.Random

object Dashboard {

  case class Props(router: RouterCtl[Loc])

//  case class State(motdWrapper: ReactConnectProxy[Pot[String]])


  // create dummy data for the chart
  val cp = Chart.ChartProps(
    "Test chart",
    Chart.PieChart,
    ChartData(
      Seq("Armin", "Keyvanloo", "Scala.js", "React", "Diode"),
      Seq(ChartDataset(Iterator.continually(Random.nextDouble() * 100).take(5).toSeq, "Data1",
        RandomColourGenerator.of(10)
))
    )
  )

  // create the React component for Dashboard
  private val component = ReactComponentB[Props]("Dashboard")
    // create and store the connect proxy in state for later use
//    .initialState_P(props => State(props.proxy.connect(m => m)))
    .renderPS { (_, props, state) =>
      <.div(
        <.h2("Dashboard"),
        Chart(cp),
        <.h3("Check your:"),
        // create a link to the pages
        <.div(props.router.link(TopManufacturersLoc)("Top selling Manufacturers")),
        <.div(props.router.link(TopSellingSizesLoc)("Top selling Sizes")),
        <.div(props.router.link(TopSellingMonthsLoc)("Top selling Months")),
        <.div(props.router.link(TopSellingCountriesLoc)("Top selling Countries")),
        <.div(props.router.link(TopSellingColoursLoc)("Top selling Colours")),
        <.div(props.router.link(TopSellingStylesLoc)("Top selling Styles"))
      )
    }
    .build

  def apply(router: RouterCtl[Loc]) = component(Props(router))
}
