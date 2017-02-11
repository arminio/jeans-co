package services

import armin.jeans.shared._
import com.github.tototoshi.csv._

import scala.io.Source


class ApiService extends Api {

  var orders = getAllOrders("2016")

  override def getAllOrders(year: String): List[Sale] = {
    val vals = CSVReader.open(Source.fromURL(getClass.getResource(s"/orders-$year.csv"))).allWithHeaders()
    vals.map(x => Sale(x))
  }


}
