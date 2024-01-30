module com.example.projetplante {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.fasterxml.jackson.annotation;
    requires org.json;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires javafx.swing;
    requires com.google.gson;

    opens com.example.projetplante to javafx.fxml, com.fasterxml.jackson.databind;

    exports com.example.projetplante;
}
