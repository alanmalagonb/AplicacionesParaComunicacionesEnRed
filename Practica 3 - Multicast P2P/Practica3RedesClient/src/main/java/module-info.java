module com.ipn.practica3redes {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.ipn.practica3redes to javafx.fxml;
    exports com.ipn.practica3redes;
}