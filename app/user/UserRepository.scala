package user

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile

import scala.concurrent.{ Future, ExecutionContext }
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import play.api.Logger

/**
 * A repository for users.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class UserRepository @Inject() (@NamedDatabase("mysql") dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val logger: Logger = Logger(this.getClass())

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
   * Here we define the table. It will have a name of users
   */
  private class UsersTable(tag: Tag) extends Table[User](tag, "users") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The name column */
    def name = column[String]("name")

    /** The age column */
    def age = column[Int]("age")

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the User object.
     *
     * In this case, we are simply passing the id, name and page parameters to the User case classes
     * apply and unapply methods.
     */
    def * = (id, name, age).mapTo[User]
  }

  

  /**
   * The starting point for all queries on the users table.
   */
  private val users = TableQuery[UsersTable]
  //exec(users.schema.create)
  /**
   * Create a user with the given name and age.
   *
   * This is an asynchronous operation, it will return a future of the created user, which can be used to obtain the
   * id for that user.
   */

   // Helper method for running a query in this example file:
  def exec[T](program: DBIO[T]): T = Await.result(db.run(program), 2 seconds)
  def await[T](f: Future[T]): T = Await.result(f, 2 seconds)
  /**
   * List all the users in the database.
   */
  def list(): Future[Seq[User]] = db.run {
    users.result
  }

 
  def create(u:User): Future[User] = db.run {
    val usersReturningRow =
    users returning users.map(_.id) into { (user, id) =>
      user.copy(id = id)
    }

    usersReturningRow += u
  }


  def find(id:Long) : Future[Option[User]] = db.run {
    val q = for {
      u <- users if u.id === id
    } yield(u)
    
    q.result.headOption
  }

  def delete(id:Long):Future[Int] = db.run {
    users.filter(_.id === id).delete
  }
  
  def update(user: User): Future[Option[User]] = db.run {
    users.filter(_.id === user.id).update(user).map {
      case 0 => None
      case _ => Some(user)
    }
  }
  
}

object UserRepository {
  /**
   * Used to get a ref to the UserRepository from the console
   */
  def fromConsole(app: play.api.Application) = {
    val c = Class.forName("user.UserRepository")
    app.injector.instanceOf(c).asInstanceOf[user.UserRepository]
  }
}