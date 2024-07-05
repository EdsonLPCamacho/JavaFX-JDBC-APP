module Javafxjdbc {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; 

    opens gui to javafx.fxml; 
    opens application to javafx.graphics; 

    exports gui; 
}
