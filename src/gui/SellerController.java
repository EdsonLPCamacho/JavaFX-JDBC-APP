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
import model.entities.Seller;
import model.service.SellerService;

public class SellerController implements Initializable, DataListener {

    private SellerService service;

    @FXML
    private TableView<Seller> tvSeller;
    @FXML
    private TableColumn<Seller, Integer> tbColumnId;
    @FXML
    private TableColumn<Seller, String> tbColumnName;
    @FXML
    private TableColumn<Seller, Seller> tbColumnEdit;
    @FXML
    private TableColumn<Seller, Seller> tbColumnDelete;
    @FXML    
    private Button btNew;

    private ObservableList<Seller> obsList;

    @FXML
    public void onBtNewAction(ActionEvent event) {
        Stage parentStage = UtilDialog.currentStage(event);
        Seller obj = new Seller();
        FormDialog(obj, "/gui/SellerForm.fxml", parentStage);
    }

    public void setSellerService(SellerService service) {
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
        tvSeller.prefHeightProperty().bind(stage.heightProperty());

        initEditButtons();
        initDeleteButtons();
    }

    public void showTableList() {
        if (service == null) {
            throw new IllegalStateException("Service error");
        }
        List<Seller> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tvSeller.setItems(obsList);
    }

    private void FormDialog(Seller obj, String namePath, Stage parentStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(namePath));
            Pane pane = fxmlLoader.load();

            SellerFormController controller = fxmlLoader.getController();
            controller.setSeller(obj);
            controller.setSellerService(new SellerService());
            controller.subscribeDataListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Seller Title");
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
        List<Seller> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tvSeller.setItems(obsList);
        initDeleteButtons();
    }
    
    private void initEditButtons() {
        tbColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tbColumnEdit.setCellFactory(new Callback<TableColumn<Seller, Seller>, TableCell<Seller, Seller>>() {
            @Override
            public TableCell<Seller, Seller> call(TableColumn<Seller, Seller> param) {
                return new TableCell<Seller, Seller>() {
                    private final Button button = new Button("edit");   

                    @Override
                    protected void updateItem(Seller obj, boolean empty) {
                        super.updateItem(obj, empty);

                        if (obj == null || empty) {
                            setGraphic(null);
                            return;
                        }

                        setGraphic(button);
                        button.setOnAction(
                                event -> createDialogForm(
                                        obj, "/gui/SellerForm.fxml", UtilDialog.currentStage(event)));
                    }
                };
            }
        });
    }
    
    
    private void initDeleteButtons() {
        tbColumnDelete.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tbColumnDelete.setCellFactory(param -> new TableCell<Seller, Seller>() {
            private final Button button = new Button("delete");

            @Override
            protected void updateItem(Seller obj, boolean empty) {
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

    private void removeEntity(Seller obj) {
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
    


    private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
       /*
    	try {
        	
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

            SellerFormController controller = loader.getController();
            controller.setSeller(obj);
            controller.setSellerService(new SellerService());
            controller.subscribeDataListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Seller data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
        } catch (IOException e) {
            Alerts.showMessage("IOException", "Error", e.getMessage(), AlertType.ERROR);
        }
        */
    }
    
}
