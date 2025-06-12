module pl.umcs.oop.threadshomework {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens pl.umcs.oop.threadshomework to javafx.fxml;
    exports pl.umcs.oop.threadshomework;
}