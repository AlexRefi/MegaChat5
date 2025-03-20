package Heart

import akka.actor.{ActorSystem, Props}
import javafx.application.{Application, Platform}
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.{Button, TextArea, TextField}
import javafx.scene.layout.{HBox, Priority, VBox}
import javafx.stage.Stage

object ChatApp {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[ChatApp], args: _*)
  }
}

class ChatApp extends Application {
  private var username: String = _
  private var chatActor: akka.actor.ActorRef = _
  private var userActor: akka.actor.ActorRef = _
  private var chatArea: TextArea = _
  private var system: ActorSystem = _

  override def start(primaryStage: Stage): Unit = {
    // Создаём систему акторов
    system = ActorSystem("ChatSystem")
    chatActor = system.actorOf(Props[ChatActor], "chatActor")

    // Окно для ввода имени
    val nameField = new TextField {
      setPromptText("Enter your name")
    }
    val nameButton = new Button("Join Chat") {
      setOnAction(_ => {
        username = nameField.getText
        if (username.nonEmpty) {
          primaryStage.setScene(createChatScene())
        }
      })
    }
    val nameScene = new Scene(new VBox(nameField, nameButton), 300, 200)

    // Настройка главного окна
    primaryStage.setTitle("Scala Chat")
    primaryStage.setScene(nameScene)
    primaryStage.show()
  }

  private def createChatScene(): Scene = {
    // Основное окно чата
    chatArea = new TextArea {
      setEditable(false)
      setWrapText(true)
    }

    val messageField = new TextField {
      setPromptText("Type your message")
    }

    val sendButton = new Button("Send") {
      setOnAction(_ => {
        val message = messageField.getText
        if (message.nonEmpty) {
          chatActor ! SendMessage(username, message)
          messageField.setText("")
        }
      })
    }

    // Создаём контейнер для поля ввода и кнопки
    val inputContainer = new HBox(messageField, sendButton) {
      setSpacing(10)
    }

    // Создаём главный контейнер
    val root = new VBox(chatArea, inputContainer) {
      setSpacing(10)
      setPadding(new Insets(10))
    }

    // Настраиваем растягивание
    VBox.setVgrow(chatArea, Priority.ALWAYS)

    // Создаём актор пользователя
    userActor = system.actorOf(Props(new UserActor(username, chatActor, appendMessage _)))

    // Обработка сообщений для отображения в чате
    chatActor ! BroadcastMessage(s"$username joined the chat.")

    // Создаём сцену
    new Scene(root, 400, 300)
  }

  // Метод для добавления сообщения в ленту
  private def appendMessage(message: String): Unit = {
    Platform.runLater(() => {
      chatArea.appendText(message + "\n")
    })
  }
}