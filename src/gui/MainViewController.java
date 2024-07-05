package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable {

    @FXML
    private MenuItem menuItemSeller;

    @FXML
    private MenuItem menuItemDepartment;

    @FXML
    private MenuItem menuItemAbout;

    @FXML
    public void onMenuItemSellerAction(ActionEvent event) {
        System.out.println("Seller");
    }

    @FXML
    public void onMenuItemDepartmentAction(ActionEvent event) {
    	loadView("/gui/Department.fxml");
    }

    @FXML
    public void onMenuItemAboutAction(ActionEvent event) {
        loadView("/gui/About.fxml");
        
        
    }

    @Override
    public void initialize(URL uri, ResourceBundle rb) {
        // Initialization code if needed
    }

    private void loadView(String namePath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(namePath));
            VBox newVBox = fxmlLoader.load();

            Scene mainScene = Main.getMainScene();

            VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

            Node mainMenu = mainVBox.getChildren().get(0);
            mainVBox.getChildren().clear();
            mainVBox.getChildren().add(mainMenu);
            mainVBox.getChildren().addAll(newVBox.getChildren());
        } catch (IOException e) {
            System.err.println("Error FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
