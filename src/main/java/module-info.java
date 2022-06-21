module fr.metropolis.gestion {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports fr.metropolis.gestion.gui;
    opens fr.metropolis.gestion.gui to javafx.fxml;
}