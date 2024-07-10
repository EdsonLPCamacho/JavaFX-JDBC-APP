package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.UtilDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
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
	public void onBtNewAction(ActionEvent event) {
		
		Stage parentStage = UtilDialog.currentStage(event);
		Department obj = new Department();
		FormDialog(obj, "/gui/DepartmentForm.fxml", parentStage );
		
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

	private void FormDialog(Department obj, String namePath, Stage parentStage) {
		
		try {
			
			 FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(namePath));
			 Pane pane = fxmlLoader.load();
			 
			 DepartmentFormController controller = fxmlLoader.getController();
			 controller.setDepartment(obj);
			 controller.setDepartmentService(new DepartmentService());
			 controller.updateFormData();
			 
			 Stage dialogStage = new Stage();
			 dialogStage.setTitle("Enter Department Title");
			 dialogStage.setScene(new Scene(pane));
			 dialogStage.setResizable(false);
			 dialogStage.initOwner(parentStage);
			 dialogStage.initModality(Modality.WINDOW_MODAL);
			 dialogStage.showAndWait();
				 
		}
		catch(IOException e) {
			
			Alerts.showMessage("IOException", "Error", e.getMessage(), AlertType.ERROR);
			
		}
		
	}
	
}
