module Javafxjdbc {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; 
    requires transitive javafx.base;

    opens gui to javafx.fxml; 
    opens application to javafx.graphics; 

    exports gui; 
}
