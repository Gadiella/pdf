module org.example.demojavapdf {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io;
    requires kernel;
    requires layout;
    requires java.desktop;


    opens org.example.demojavapdf to javafx.fxml;
    exports org.example.demojavapdf;

    opens org.example.demojavapdf.controller to javafx.fxml;
    exports org.example.demojavapdf.controller;

    opens org.example.demojavapdf.interfaces to javafx.fxml;
    exports org.example.demojavapdf.interfaces;


    opens org.example.demojavapdf.models to javafx.fxml;
    exports org.example.demojavapdf.models ;

    opens org.example.demojavapdf.DBConfig to javafx.fxml;
    exports org.example.demojavapdf.DBConfig ;
}