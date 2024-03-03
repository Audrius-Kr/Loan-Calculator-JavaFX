module org.example.antrauzd1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.antrauzd1 to javafx.fxml;
    exports org.example.antrauzd1;
}