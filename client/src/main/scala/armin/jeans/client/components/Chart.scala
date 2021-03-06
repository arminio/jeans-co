package armin.jeans.client.components

import japgolly.scalajs.react.CompScope.DuringCallbackM
import japgolly.scalajs.react.vdom.prefix_<^._
import japgolly.scalajs.react.{BackendScope, Callback, LifecycleInput, ReactComponentB}
import org.scalajs.dom.raw.HTMLCanvasElement

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSName

@js.native
trait ChartDataset extends js.Object {
  def label: String = js.native
  def data: js.Array[Double] = js.native
  def fillColor: String = js.native
  def strokeColor: String = js.native
}

object ChartDataset {
  def apply(data: Seq[Double],
            label: String, backgroundColor: Seq[String] = Seq("#8080FF"), borderColor: String = "#404080"): ChartDataset = {
    js.Dynamic.literal(
      label = label,
      data = data.toJSArray,
      backgroundColor = backgroundColor.toJSArray,
      borderColor = borderColor
    ).asInstanceOf[ChartDataset]
  }
}

@js.native
trait ChartData extends js.Object {
  def labels: js.Array[String] = js.native
  def datasets: js.Array[ChartDataset] = js.native
}

object ChartData {
  def apply(labels: Seq[String], datasets: Seq[ChartDataset]): ChartData = {
    js.Dynamic.literal(
      labels = labels.toJSArray,
      datasets = datasets.toJSArray
    ).asInstanceOf[ChartData]
  }
}

@js.native
trait ChartOptions extends js.Object {
  def responsive: Boolean = js.native
}

object ChartOptions {
  def apply(responsive: Boolean = true): ChartOptions = {
    js.Dynamic.literal(
      responsive = responsive
    ).asInstanceOf[ChartOptions]
  }
}

@js.native
trait ChartConfiguration extends js.Object {
  def `type`: String = js.native
  def data: ChartData = js.native
  def options: ChartOptions = js.native
}

object ChartConfiguration {
  def apply(`type`: String, data: ChartData, options: ChartOptions = ChartOptions(false)): ChartConfiguration = {
    js.Dynamic.literal(
      `type` = `type`,
      data = data,
      options = options
    ).asInstanceOf[ChartConfiguration]
  }
}

// define a class to access the Chart.js component
@js.native
@JSName("Chart")
class JSChart(ctx: js.Dynamic, config: ChartConfiguration) extends js.Object {
  def update(duration:Int, `lazy`:Boolean): Unit = js.native
}

object Chart {

  // available chart types
  sealed trait ChartType

  case object LineChart extends ChartType
  case object BarChart extends ChartType
  case object PieChart extends ChartType
  case object DoughnutChart extends ChartType

  case class ChartProps(name: String, chartType: ChartType, data: ChartData, width: Int = 500, height: Int = 300)

  case class State(data: ChartData)

  class Backend($: BackendScope[ChartProps, State]) {

    def render(p: ChartProps, state: State) = {
      println(s"rendering the chart: $p, ${state.data.datasets}")
      <.canvas("width".reactAttr := p.width, "height".reactAttr := p.height)
    }
  }

  val Chart = ReactComponentB[ChartProps]("Chart")
    .initialState_P((p: ChartProps) => State(p.data))
    .renderBackend[Backend]
    .domType[HTMLCanvasElement]
    .componentDidMount(scope => Callback {
      // access context of the canvas
      val ctx = scope.getDOMNode().getContext("2d")
      // create the actual chart using the 3rd party component
      val chart = scope.props.chartType match {
        case LineChart => new JSChart(ctx, ChartConfiguration("line", scope.props.data))
        case BarChart => new JSChart(ctx, ChartConfiguration("bar", scope.props.data))
        case DoughnutChart => new JSChart(ctx, ChartConfiguration("doughnut", scope.props.data))
        case PieChart => new JSChart(ctx, ChartConfiguration("pie", scope.props.data))
        case _ => throw new IllegalArgumentException
      }
      chart
    })
    .componentWillReceiveProps(x => x.$.modState(_.copy(data = x.nextProps.data)))
    .build

  def apply(props: ChartProps) = {
    println(s"Making a new chart: $props")
    Chart(props)
  }
}


