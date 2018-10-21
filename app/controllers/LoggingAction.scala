package controllers

import play.api.mvc._
import play.api.Logger
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LoggingAction @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext)
    extends ActionBuilderImpl(parser) {

    override def invokeBlock[A]( request: Request[A], block: (Request[A])=>Future[Result] ) = {
        Logger.info(s"${request.path}")
        block(request)
    }

}