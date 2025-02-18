package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.UtilDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidateException;
import model.service.DepartmentService;
import model.service.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;
	private SellerService service;
	private DepartmentService departmentService;

	private List<DataListener> dataListener = new ArrayList<>();

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	@FXML
	private TextField txtBaseSalary;
	@FXML
	private DatePicker dbBirthDate;
	@FXML
	private ComboBox<Department> comboBoxDepartment;
	@FXML
	private Label lblErrorName;
	@FXML
	private Label lblErrorEmail;
	@FXML
	private Label lblErrorBaseSalary;
	@FXML
	private Label lblErrorBirthDate;
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;

	private ObservableList<Department> obsList;

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	public void subscribeDataListener(DataListener listener) {
		dataListener.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		try {
			entity = getFormData();
			service.SaveUpdate(entity);
			notifyDataListener();
			UtilDialog.currentStage(event).close();
		} catch (ValidateException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showMessage("Saving error", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataListener() {
		for (DataListener listener : dataListener) {
			listener.onDataChanged();
		}
	}

	private Seller getFormData() {
		Seller obj = new Seller();

		ValidateException exception = new ValidateException("Validate Error!");

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("Name", "The field can't be empty");
		}
		
		
		obj.setId(UtilDialog.tryParseToInt(txtId.getText()));
		obj.setName(txtName.getText());
		

		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addError("Email", "The field can't be empty");
		}
		
		obj.setEmail(txtEmail.getText());
		
		if(dbBirthDate.getValue() == null ) {
			exception.addError("BirthDate", "The field can't be empty");
		}else {
			Instant instant = Instant.from(dbBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}
		

		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			exception.addError("baseSalary", "The field can't be empty");
		}
		
		obj.setBaseSalary(UtilDialog.tryParseToDouble(txtBaseSalary.getText()));
		
		obj.setDepartment(comboBoxDepartment.getValue());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		UtilDialog.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	public void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 35);
		Constraints.setTextFieldMaxLength(txtEmail, 50);
		Constraints.setTextFieldDouble(txtBaseSalary);
		UtilDialog.formatDatePicker(dbBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("entity is null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));

		if (entity.getBirthDate() != null) {
			dbBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}

		if (entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartment.setValue(entity.getDepartment());
		}
	}

	public void loadAssociatedObjects() {

		if (departmentService == null) {
		    throw new IllegalStateException("Department Service is null!");
		}


		List<Department> list = departmentService.findAll();

		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);

	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("Name")) {
			lblErrorName.setText(errors.get("Name"));
		}
		
		lblErrorName.setText((fields.contains("Name") ? errors.get("Name") : ""));
		lblErrorEmail.setText((fields.contains("Email") ? errors.get("Email") : ""));
		lblErrorBirthDate.setText((fields.contains("birthDate") ? errors.get("birthDate") : ""));
		lblErrorBaseSalary.setText((fields.contains("baseSalary") ? errors.get("baseSalary") : ""));
		
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

}
