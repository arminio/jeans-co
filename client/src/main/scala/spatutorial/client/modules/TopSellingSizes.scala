package spatutorial.client.modules

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import grouper.SalesGrouper
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import spatutorial.client.components.Bootstrap._
import spatutorial.client.components._
import spatutorial.client.logger._
import spatutorial.client.services._
import spatutorial.shared._

import scalacss.ScalaCssReact._

object TopSellingSizes {
  @inline private def bss = GlobalStyles.bootstrapStyles
  val style = bss.listGroup
  
  case class Props(proxy: ModelProxy[SalesAndFilter])

  case class State(selectedItem: Option[Sale] = None, showTodoForm: Boolean = false)

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props) =
      Callback.when(props.proxy().sales.isEmpty)(props.proxy.dispatchCB(RefreshSales))

    def editTodo(item: Option[Sale]) =
      // activate the edit dialog
      $.modState(s => s.copy(selectedItem = item, showTodoForm = true)) //!@

    def todoEdited(item: TodoItem, cancelled: Boolean) = {
      val cb = if (cancelled) {
        // nothing to do here
        Callback.log("Todo editing cancelled")
      } else {
        Callback.log(s"Todo edited: $item") >>
          $.props >>= (_.proxy.dispatchCB(UpdateTodo(item)))
      }
      // hide the edit dialog, chain callbacks
      cb >> $.modState(s => s.copy(showTodoForm = false))
    }

    def render(p: Props, s: State) = {
      val proxy = p.proxy()
      Panel(Panel.Props("What needs to be done"), <.div(
        proxy.sales.renderFailed(ex => "Error loading"),
        proxy.sales.renderPending(_ > 500, _ => "Loading..."),
        proxy.sales.render { sales =>

          <.ul(style.listGroup)(SalesGrouper.topSellingSizes(sales.items, SaleFilter.empty) map { (s) => <.li(s.toString)})

        },
        Button(Button.Props(editTodo(None)), Icon.plusSquare, " New")))
        // if the dialog is open, add it to the panel
//        if (s.showTodoForm) TodoForm(TodoForm.Props(s.selectedItem, todoEdited))
//        else // otherwise add an empty placeholder
//          Seq.empty[ReactElement])
    }
  }

  // create the React component for To Do management
  val component = ReactComponentB[Props]("TODO")
    .initialState(State()) // initial state from TodoStore
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  /** Returns a function compatible with router location system while using our own props */
  def apply(proxy: ModelProxy[SalesAndFilter]) = component(Props(proxy))
}

