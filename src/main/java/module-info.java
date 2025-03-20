module org.example.megachat5 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.megachat to javafx.fxml;
    exports org.example.megachat;
}