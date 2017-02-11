package armin.jeans.shared

trait Api {

  def getAllOrders(year: String = "2016"): List[Sale]
  
}
