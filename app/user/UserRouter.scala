package user

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class UserRouter @Inject()(controller: UserController) extends SimpleRouter {
    val prefix = "/users"

    def link(id: Long): String = {
        import com.netaporter.uri.dsl._
        val url = prefix / id.toString
        url.toString()
    }

    override def routes: Routes = {
        case GET(p"/") =>
            controller.index
        
        case GET(p"/${long(id)}") =>
            controller.show(id)

        case POST(p"/") =>
            controller.add

        case DELETE(p"/${long(id)}") =>
            controller.delete(id)

    }


}