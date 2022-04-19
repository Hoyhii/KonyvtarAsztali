module com.example.konyvtarasztali {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.konyvtarasztali to javafx.fxml;
    exports com.example.konyvtarasztali;
}