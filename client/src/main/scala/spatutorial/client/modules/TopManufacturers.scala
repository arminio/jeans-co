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

object TopManufacturers {
  @inline private def bss = GlobalStyles.bootstrapStyles
  val style = bss.listGroup
  
  case class Props(proxy: ModelProxy[SalesAndFilter])

  case class State(selectedItem: Option[Sale] = None, showTodoForm: Boolean = false, salesFilter: SaleFilter = SaleFilter.empty)

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

    def makeOptions(sales: Sales) = {
       sales.allColours.map(s => <.option(s))
    }

    def colourFilterSelected(saleFilter: SaleFilter)(e: ReactEventI) = {
//      $.modState(s => s.copy(item = s.item.copy(priority = newPri)))
//      $.props >>= (_.proxy.dispatchCB(UpdateTodo(item)))

      val newColour = e.currentTarget.value

      $.props >>= (p => p.proxy.dispatchCB(UpdatedSalesFilter(saleFilter.copy(colour = Some(newColour)))))

    }

    def render(p: Props, s: State) = {
      val proxy = p.proxy()
      Panel(Panel.Props("What needs to be done"), <.div(
        proxy.sales.renderFailed(ex => "Error loading"),
        proxy.sales.renderPending(_ > 500, _ => "Loading..."),
        proxy.sales.render { sales =>
          <.div(
            <.div(
              <.select(^.id := "colour", ^.onChange ==> colourFilterSelected(proxy.saleFilter), sales.allColours.map(<.option(_))),
              <.select(sales.allDeliveryCountries.map(<.option(_))),
              <.select(sales.allGenders.map(<.option(_))),
              <.select(sales.allSizes.map(<.option(_))),
              <.select(sales.allStyles.map(<.option(_)))
              
            ),
            <.ul(style.listGroup)(SalesGrouper.topSellingManufacturer(sales.items, proxy.saleFilter) map { (s) => <.li(s.toString)})
          )

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

//object TopManufacturersForm {
//  // shorthand for styles
//  @inline private def bss = GlobalStyles.bootstrapStyles
//
//  case class Props(item: Option[TodoItem], submitHandler: (TodoItem, Boolean) => Callback)
//
//  case class State(item: TodoItem, cancelled: Boolean = true)
//
//  class Backend(t: BackendScope[Props, State]) {
//    def submitForm(): Callback = {
//      // mark it as NOT cancelled (which is the default)
//      t.modState(s => s.copy(cancelled = false))
//    }
//
//    def formClosed(state: State, props: Props): Callback =
//      // call parent handler with the new item and whether form was OK or cancelled
//      props.submitHandler(state.item, state.cancelled)
//
//    def updateDescription(e: ReactEventI) = {
//      val text = e.target.value
//      // update TodoItem content
//      t.modState(s => s.copy(item = s.item.copy(content = text)))
//    }
//
//    def updatePriority(e: ReactEventI) = {
//      // update TodoItem priority
//      val newPri = e.currentTarget.value match {
//        case p if p == TodoHigh.toString => TodoHigh
//        case p if p == TodoNormal.toString => TodoNormal
//        case p if p == TodoLow.toString => TodoLow
//      }
//      t.modState(s => s.copy(item = s.item.copy(priority = newPri)))
//    }
//
//    def render(p: Props, s: State) = {
//      log.debug(s"User is ${if (s.item.id == "") "adding" else "editing"} a todo or two")
//      val headerText = if (s.item.id == "") "Add new todo" else "Edit todo"
//      Modal(Modal.Props(
//        // header contains a cancel button (X)
//        header = hide => <.span(<.button(^.tpe := "button", bss.close, ^.onClick --> hide, Icon.close), <.h4(headerText)),
//        // footer has the OK button that submits the form before hiding it
//        footer = hide => <.span(Button(Button.Props(submitForm() >> hide), "OK")),
//        // this is called after the modal has been hidden (animation is completed)
//        closed = formClosed(s, p)),
//        <.div(bss.formGroup,
//          <.label(^.`for` := "description", "Description"),
//          <.input.text(bss.formControl, ^.id := "description", ^.value := s.item.content,
//            ^.placeholder := "write description", ^.onChange ==> updateDescription)),
//        <.div(bss.formGroup,
//          <.label(^.`for` := "priority", "Priority"),
//          // using defaultValue = "Normal" instead of option/selected due to React
//          <.select(bss.formControl, ^.id := "priority", ^.value := s.item.priority.toString, ^.onChange ==> updatePriority,
//            <.option(^.value := TodoHigh.toString, "High"),
//            <.option(^.value := TodoNormal.toString, "Normal"),
//            <.option(^.value := TodoLow.toString, "Low")
//          )
//        )
//      )
//    }
//  }
//
//  val component = ReactComponentB[Props]("TodoForm")
//    .initialState_P(p => State(p.item.getOrElse(TodoItem("", 0, "", TodoNormal, completed = false))))
//    .renderBackend[Backend]
//    .build
//
//  def apply(props: Props) = component(props)
//}