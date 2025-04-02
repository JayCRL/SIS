module org.example.sis {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    opens org.example.sis.Entity to javafx.base;
    opens org.example.sis to javafx.fxml;
    exports org.example.sis;
}