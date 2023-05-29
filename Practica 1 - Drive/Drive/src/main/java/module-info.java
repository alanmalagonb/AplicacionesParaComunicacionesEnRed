module com.ipn.drive {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    exports com.ipn.drive;
    opens com.ipn.drive to javafx.fxml, javafx.graphics;
}