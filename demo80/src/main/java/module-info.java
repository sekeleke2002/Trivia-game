module com.example.demo80 {
    requires javafx.controls;
    requires javafx.media;


    opens com.example.demo80 to javafx.fxml;
    exports com.example.demo80;
}