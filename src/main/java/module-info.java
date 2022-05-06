module com.vitalytyrenko.pigeonmail {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.vitalytyrenko.pigeonmail to javafx.fxml;
    exports com.vitalytyrenko.pigeonmail;
}