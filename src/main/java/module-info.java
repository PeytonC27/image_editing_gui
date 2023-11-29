module com.rammble.viperion {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.rammble.viperion to javafx.fxml;
    exports com.rammble.viperion;
}