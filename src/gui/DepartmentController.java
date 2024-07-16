package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.util.Alerts;
import gui.util.UtilDialog;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.entities.Department;
import model.service.DepartmentService;

public class DepartmentController implements Initializable, DataListener {

    private DepartmentService service;

    @FXML
    private TableView<Department> tvDepartment;
    @FXML
    private TableColumn<Department, Integer> tbColumnId;
    @FXML
    private TableColumn<Department, String> tbColumnName;
    @FXML
    private TableColumn<Department, Department> tbColumnEdit;
    @FXML
    private TableColumn<Department, Department> tbColumnDelete;
    @FXML    
    private Button btNew;

    private ObservableList<Department> obsList;

    @FXML
    public void onBtNewAction(ActionEvent event) {
        Stage parentStage = UtilDialog.currentStage(event);
        Department obj = new Department();
        FormDialog(obj, "/gui/DepartmentForm.fxml", parentStage);
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

        initEditButtons();
        initDeleteButtons();
    }

    public void showTableList() {
        if (service == null) {
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
            controller.subscribeDataListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Department Title");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();

        } catch (IOException e) {
            Alerts.showMessage("IOException", "Error", e.getMessage(), AlertType.ERROR);
        }
    }

    @Override
    public void onDataChanged() {
        updateTableView();
    }

    private void updateTableView() {
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }
        List<Department> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tvDepartment.setItems(obsList);
        initDeleteButtons();
    }
    
    private void initEditButtons() {
        tbColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tbColumnEdit.setCellFactory(new Callback<TableColumn<Department, Department>, TableCell<Department, Department>>() {
            @Override
            public TableCell<Department, Department> call(TableColumn<Department, Department> param) {
                return new TableCell<Department, Department>() {
                    private final Button button = new Button("edit");   

                    @Override
                    protected void updateItem(Department obj, boolean empty) {
                        super.updateItem(obj, empty);

                        if (obj == null || empty) {
                            setGraphic(null);
                            return;
                        }

                        setGraphic(button);
                        button.setOnAction(
                                event -> createDialogForm(
                                        obj, "/gui/DepartmentForm.fxml", UtilDialog.currentStage(event)));
                    }
                };
            }
        });
    }
    
    
    private void initDeleteButtons() {
        tbColumnDelete.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tbColumnDelete.setCellFactory(param -> new TableCell<Department, Department>() {
            private final Button button = new Button("delete");

            @Override
            protected void updateItem(Department obj, boolean empty) {
                super.updateItem(obj, empty);

                if (obj == null || empty) {
                    setGraphic(null);
                    return;
                }

                setGraphic(button);
                button.setOnAction(event -> removeEntity(obj));
            }
        });
    }

    private void removeEntity(Department obj) {
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure you want to delete this department?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (service == null) {
                throw new IllegalStateException("Service was null");
            }
            try {
                service.delete(obj);     
                updateTableView(); 
            } catch (DbIntegrityException e) {
                Alerts.showMessage("Error removing department: ", null, e.getMessage(), AlertType.ERROR);
            }
        }
    }

    private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

            DepartmentFormController controller = loader.getController();
            controller.setDepartment(obj);
            controller.setDepartmentService(new DepartmentService());
            controller.subscribeDataListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Department data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
        } catch (IOException e) {
            Alerts.showMessage("IOException", "Error", e.getMessage(), AlertType.ERROR);
        }
    }
}
