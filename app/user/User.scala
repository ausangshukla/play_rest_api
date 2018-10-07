package user

import play.api.libs.json._

final case class User(id: Long = 0L, name: String, age: Int)

object UserSerialization {  
  implicit val userFormat = Json.format[User]
}