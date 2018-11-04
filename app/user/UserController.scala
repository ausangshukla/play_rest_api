package user

import javax.inject.Inject

import play.api.Logger
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc._
import controllers._

import io.kanaka.monadic.dsl._

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(jwtAction: AuthAction.JWTAction, cc: ControllerComponents, repo: UserRepository, jwtUtils: JwtUtils)(implicit ec: ExecutionContext) 
extends AbstractController(cc) {

    private val logger:Logger = Logger(this.getClass())
    import UserSerialization._

    def index = Action.async { request =>

        //logger.debug(s"Requested by ${request.user}")
        for {
            users      <- repo.list()    ?| (ue => NotFound)
        } yield Ok(Json.toJson(users))
    }

    def show(id:Long) = Action.async { request =>
        logger.info(s"show $id")
        for {
            user      <- repo.find(id)    ?| (ue => NotFound)
        } yield Ok(Json.toJson(user))
        
    }

    def add = Action.async (parse.json[User]) { request =>
        val u:User = request.body
        logger.info(s"add $u")
        
        for {
            addedUser  <- repo.create(u)    ?| (ue => InternalServerError)
        } yield Created(Json.toJson(addedUser))
    }

    def delete(id:Long) = jwtAction.async { request =>
        logger.info(s"delete $id")
        repo.delete(id).map { x =>
            logger.info(s"delete $x")
            x match {
                case 1 => Status(204)
                case 0 => NotFound
            }
            
        }
        
    }
    

}
