package spatutorial.client.components.popup

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.components.Bootstrap._
import spatutorial.client.components.Chart.ChartProps
import spatutorial.client.components._
import spatutorial.client.logger._
import spatutorial.shared._

import scalacss.ScalaCssReact._


object ChartPopup {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

//  case class Props(item: Option[TodoItem], submitHandler: (TodoItem, Boolean) => Callback)
  case class Props(chartProps: ChartProps, closeHandler: Boolean => Callback)

  case class State(chartProps: ChartProps)

  class Backend(t: BackendScope[Props, State]) {

//    def submitForm(): Callback = {
//      // mark it as NOT cancelled (which is the default)
//      t.modState(s => s.copy(cancelled = false))
//    }



//    def updateDescription(e: ReactEventI) = {
//      val text = e.target.value
//      // update TodoItem content
//      t.modState(s => s.copy(item = s.item.copy(content = text)))
//    }

//    def updatePriority(e: ReactEventI) = {
//      // update TodoItem priority
//      val newPri = e.currentTarget.value match {
//        case p if p == TodoHigh.toString => TodoHigh
//        case p if p == TodoNormal.toString => TodoNormal
//        case p if p == TodoLow.toString => TodoLow
//      }
//      t.modState(s => s.copy(item = s.item.copy(priority = newPri)))
//    }
def formClosed(state: State, props: Props): Callback =     {
  props.closeHandler(true)
}
// call parent handler with the new item and whether form was OK or cancelled


    def render(p: Props, s: State) = {
      println("rendering Chartpopup")
//      log.debug(s"User is ${if (s.item.id == "") "adding" else "editing"} a todo or two")
      val headerText = "Chart"
      Modal(Modal.Props(
        // header contains a cancel button (X)
        header = hide => <.span(<.button(^.tpe := "button", bss.close, ^.onClick --> hide, Icon.close), <.h4(headerText)),
        // footer has the OK button that submits the form before hiding it
        footer = hide => <.span(Button(Button.Props(hide), "OK")),
        // this is called after the modal has been hidden (animation is completed)
        closed = formClosed(s, p)),
        Chart(p.chartProps))
    }
  }

  val component = ReactComponentB[Props]("ChartPopup")
    .initialState_P(p => State(p.chartProps))
    .renderBackend[Backend]
    .build

  def apply(props: Props) = component(props)
}