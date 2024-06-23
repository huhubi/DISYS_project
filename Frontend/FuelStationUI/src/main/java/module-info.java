module org.example.fuelstationui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
   // requires itextpdf; TODO: solve dependeny issue

    opens org.example.fuelstationui to javafx.fxml;
    opens org.example.fuelstationui.model to javafx.base;
    exports org.example.fuelstationui.service to javafx.base;
    exports org.example.fuelstationui;
}