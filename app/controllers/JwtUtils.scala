package controllers

import pdi.jwt.{JwtJson, JwtAlgorithm, Jwt}
import javax.inject.{ Inject, Singleton }
import play.api.Configuration

@Singleton
class JwtUtils @Inject() (config: Configuration) {

  val key = config.get[String]("jwt.secret")
  val algo = JwtAlgorithm.HS256
  
  def createToken(claim: play.api.libs.json.JsObject): String = {
    JwtJson.encode(claim, key, algo)
  }
  
  def isValidToken(token:String) = {
    Jwt.isValid(token, key, Seq(algo))
  }

  def decodePayload(token: String) =
    JwtJson.decodeJson(token, key, Seq(JwtAlgorithm.HS256))
  
}