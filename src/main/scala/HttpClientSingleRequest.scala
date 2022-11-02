/*
 * Copyright (C) 2020-2022 Lightbend Inc. <https://www.lightbend.com>
 */

package docs.http.scaladsl

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object HttpClientSingleRequest {
  def main(args: Array[String]): Unit = {
    /*
    * Input: Gets the start_time & time delta as command line args (in that order)
    *
    * Connects to the lambda API on AWS
    * Requests the logs between the start_time & time_delta as HTTP POST request
    * Prints the results from the lambda
    * */
    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext
    val start_time = args(0)
    val time_delta = args(1)

    val responseFuture: Future[HttpResponse] = Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = "https://nvnwt4ripg.execute-api.us-east-1.amazonaws.com/Prod/getLogs",
        entity = HttpEntity(
          ContentTypes.`application/json`,
          s"{\"start_time\":" + s"\"$start_time\"" + ",\"time_delta\":" + s"\"$time_delta\"" + "}"
        )
      )
    )

    val timeout = 5.second
    val responseAsString = Await.result(
      responseFuture.flatMap(resp => Unmarshal(resp.entity).to[String]),
      timeout
    )

    println(responseAsString)
  }
}