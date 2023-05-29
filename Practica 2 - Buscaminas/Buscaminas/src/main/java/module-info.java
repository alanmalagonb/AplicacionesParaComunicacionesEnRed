module com.ipn.buscaminas {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.ipn.buscaminas to javafx.fxml;
    exports com.ipn.buscaminas;
}