module org.example.antrauzd1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;


    opens org.example.antrauzd1 to javafx.fxml;
    exports org.example.antrauzd1;
}