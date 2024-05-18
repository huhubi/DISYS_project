module org.example.fuelstationui {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.fuelstationui to javafx.fxml;
    exports org.example.fuelstationui;
}