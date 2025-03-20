package Heart

object HelloWorld extends App {
  println("Hello World")
}

import akka.actor.{Actor, ActorSystem, Props}

// Определение актора
class HelloActor extends Actor {
  def receive: Receive = {
    case "hello" => println("Hello from Akka!")
    case _       => println("Unknown message")
  }
}

// Главный объект для запуска
object AkkaExample extends App {
  // Создаём систему акторов
  val system = ActorSystem("HelloSystem")

  // Создаём актор
  val helloActor = system.actorOf(Props[HelloActor], name = "helloactor")

  // Отправляем сообщение актору
  helloActor ! "hello"
  helloActor ! "unknown"

  // Завершаем систему акторов
  system.terminate()
}