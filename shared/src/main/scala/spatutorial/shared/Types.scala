package armin.jeans.shared


import boopickle.Default._


object Types {
  type YearMonth = String
  type Country = String
  type Manufacturer = String
  type Gender = String
  type Size = String
  type Colour = String
  type Style = String
  type Count = Int

  implicit val datePickler = transformPickler((t: Long) => new java.util.Date(t))(_.getTime)
}
