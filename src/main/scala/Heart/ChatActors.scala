package Heart

import akka.actor.{Actor, ActorRef}

// Сообщения для акторов
case class JoinChat(username: String)
case class LeaveChat(username: String)
case class SendMessage(username: String, message: String)
case class BroadcastMessage(message: String)

// Актор чата
class ChatActor extends Actor {
  var users: Map[String, ActorRef] = Map()

  def receive: Receive = {
    case JoinChat(username) =>
      users += (username -> sender())
      broadcast(s"$username joined the chat.")
    case LeaveChat(username) =>
      users -= username
      broadcast(s"$username left the chat.")
    case SendMessage(username, message) =>
      broadcast(s"$username: $message")
    case BroadcastMessage(message) =>
      users.values.foreach(_ ! message)
  }

  private def broadcast(message: String): Unit = {
    users.values.foreach(_ ! message)
  }
}

// Актор пользователя
class UserActor(username: String, chatActor: ActorRef, appendMessage: String => Unit) extends Actor {
  chatActor ! JoinChat(username)

  def receive: Receive = {
    case message: String =>
      appendMessage(message) // Обновляем ленту сообщений
  }

  override def postStop(): Unit = {
    chatActor ! LeaveChat(username)
  }
}