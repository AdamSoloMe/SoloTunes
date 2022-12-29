module com.csc229labfiles.finalaudioplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.base;
    requires javafx.media;
    requires java.sql;

  

    opens com.csc229labfiles.finalaudioplayer to javafx.fxml,com.google.gson;
    exports com.csc229labfiles.finalaudioplayer;
}
