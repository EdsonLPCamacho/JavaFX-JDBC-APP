package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;

public class DepartmentController implements Initializable {
	
	@FXML
	private TableView<Department> tvDepartment;
	@FXML
	private TableColumn<Department, Integer> tbColumnId;
	@FXML
	private TableColumn<Department, String> tbColumnName;
	@FXML
	private  Button btNew;
	
	//Button click event
	public void onBtNewAction() {
		System.out.println("Button worked!");
		
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	
		initializeNodes();
		
	}
	
	

	private void initializeNodes() {
		
		tbColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tbColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tvDepartment.prefHeightProperty().bind(stage.heightProperty());
		
	}

}
