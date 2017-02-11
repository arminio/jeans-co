package armin.jeans.client.components.popup

import armin.jeans.client.components.GlobalStyles
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import armin.jeans.client.components.Bootstrap._
import armin.jeans.client.components.Chart.ChartProps
import armin.jeans.client.components._

import scalacss.ScalaCssReact._


object ChartPopup {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(chartProps: ChartProps, closeHandler: Boolean => Callback)

  case class State(chartProps: ChartProps)

  class Backend(t: BackendScope[Props, State]) {

    def render(p: Props, s: State) = {
      println("rendering Chartpopup")
      val headerText = p.chartProps.name
      Modal(Modal.Props(
        // header contains a cancel button (X)
        header = hide => <.span(<.button(^.tpe := "button", bss.close, ^.onClick --> hide, Icon.close), <.h4(headerText)),
        // footer has the OK button that submits the form before hiding it
        footer = hide => <.span(Button(Button.Props(hide), "Close")),
        // this is called after the modal has been hidden (animation is completed)
        closed = formClosed(s, p)),
        Chart(p.chartProps))
    }

    def formClosed(state: State, props: Props): Callback = {
      props.closeHandler(true)
    }
  }

  val component = ReactComponentB[Props]("ChartPopup")
    .initialState_P(p => State(p.chartProps))
    .renderBackend[Backend]
    .build

  def apply(props: Props) = component(props)
}