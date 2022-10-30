/*
 * Copyright (C) 2020-2022 Lightbend Inc. <https://www.lightbend.com>
 */

package docs.http.scaladsl

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

import scala.concurrent.Future
import scala.util.{ Failure, Success }

object HttpClientSingleRequest {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val responseFuture: Future[HttpResponse] = Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = "https://iglc5ilpn7.execute-api.us-east-1.amazonaws.com/Prod/checktimewithinbounds",
        entity = HttpEntity(ContentTypes.`application/json`, "{\"start_time\":\"22-45\",\"time_delta\":\"10\"}")
      )
    )

    responseFuture
      .onComplete {
        case Success(res) => println(res)
        case Failure(_)   => sys.error("something wrong")
      }
  }
}