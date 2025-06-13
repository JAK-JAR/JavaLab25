module pl.umcs.oop.drugiekolokwium2023 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens pl.umcs.oop.drugiekolokwium2023 to javafx.fxml;
    exports pl.umcs.oop.drugiekolokwium2023;
}