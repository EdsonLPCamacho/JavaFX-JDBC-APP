package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.service.DepartmentService;

public class DepartmentController implements Initializable {
	
	private DepartmentService service;
	
	@FXML
	private TableView<Department> tvDepartment;
	@FXML
	private TableColumn<Department, Integer> tbColumnId;
	@FXML
	private TableColumn<Department, String> tbColumnName;
	@FXML
	private  Button btNew;
	
	private ObservableList<Department> obsList;
	
	//Button click event
	public void onBtNewAction() {
		System.out.println("Button worked!");
		
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
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
	
	public void showTableList() {
		if(service==null) {
			throw new IllegalStateException("Service error");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tvDepartment.setItems(obsList);
	}

}
