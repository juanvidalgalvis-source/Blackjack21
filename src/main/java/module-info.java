// Declara los módulos JavaFX y los paquetes usados por la aplicación.
module com.example.blackjack21 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.blackjack21 to javafx.fxml;
    exports com.example.blackjack21;
    exports com.example.blackjack21.controller;
    opens com.example.blackjack21.controller to javafx.fxml;
}