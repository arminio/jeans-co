package armin.jeans.client.modules

import armin.jeans.client.components.GlobalStyles
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._
import armin.jeans.client.SPAMain._
import armin.jeans.client.components.Bootstrap.CommonStyle
import armin.jeans.client.components.Icon._
import armin.jeans.client.components._
import armin.jeans.client.services._

import scalacss.ScalaCssReact._

object MainMenu {
  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(router: RouterCtl[Loc], currentLoc: Loc, proxy: ModelProxy[Option[Int]])

  private case class MenuItem(idx: Int, label: (Props) => ReactNode, icon: Icon, location: Loc)

  // build the Todo menu item, showing the number of open todos
  private def buildTodoMenu(props: Props): ReactElement = {
    val todoCount = props.proxy().getOrElse(0)
    <.span(
      <.span("Todo "),
      todoCount > 0 ?= <.span(bss.labelOpt(CommonStyle.danger), bss.labelAsBadge, todoCount)
    )
  }

  private def buildTopManufacturersMenu(props: Props): ReactElement = {
    <.span("Top Manufacturers")
  }

  private val menuItems = Seq(
    
    MenuItem(1, _ => "Top Manufacturers", Icon.train, TopManufacturersLoc),
    MenuItem(2, _ => "Top Selling Sizes", Icon.sitemap, TopSellingSizesLoc),
    MenuItem(3, _ => "Top Selling Months", Icon.birthdayCake, TopSellingMonthsLoc),
    MenuItem(4, _ => "Top Selling Countries", Icon.flag, TopSellingCountriesLoc),
    MenuItem(5, _ => "Top Selling Colours", Icon.reddit, TopSellingColoursLoc),
    MenuItem(6, _ => "Top Selling Styles", Icon.star, TopSellingStylesLoc)
  )

  private class Backend($: BackendScope[Props, Unit]) {
    def mounted(props: Props) =
      // dispatch a message to refresh the todos
      Callback.when(props.proxy.value.isEmpty)(props.proxy.dispatchCB(RefreshTodos))

    def render(props: Props) = {
      <.ul(bss.navbar)(
        // build a list of menu items
        for (item <- menuItems) yield {
          <.li(^.key := item.idx, (props.currentLoc == item.location) ?= (^.className := "active"),
            props.router.link(item.location)(item.icon, " ", item.label(props))
          )
        }
      )
    }
  }

  private val component = ReactComponentB[Props]("MainMenu")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(ctl: RouterCtl[Loc], currentLoc: Loc, proxy: ModelProxy[Option[Int]]): ReactElement =
    component(Props(ctl, currentLoc, proxy))
}
