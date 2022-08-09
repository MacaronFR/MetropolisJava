module fr.metropolis.gestion {
	uses fr.metropolis.gestion.Plugin;
	requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
	requires org.jetbrains.annotations;

	exports fr.metropolis.gestion.gui;
    opens fr.metropolis.gestion.gui to javafx.fxml;
}