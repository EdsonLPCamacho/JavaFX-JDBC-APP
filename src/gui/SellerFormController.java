package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.UtilDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidateException;
import model.service.SellerService;

public class SellerFormController implements Initializable {

    private Seller entity;
    private SellerService service;
    private List<DataListener> dataListener = new ArrayList<>();

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private Label lblErrorName;
    @FXML
    private Button btSave;
    @FXML
    private Button btCancel;

    public void setSeller(Seller entity) {
        this.entity = entity;
    }

    public void setSellerService(SellerService service) {
        this.service = service;
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
        }
        catch(ValidateException e) {
        	setErrorMessages(e.getErrors());
        }
        catch (DbException e) {
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
        
        if(txtName.getText() == null || txtName.getText().trim().equals("")) {
        	exception.addError("Name", "The field can't be empty");
        }
        
        obj.setId(UtilDialog.tryParseToInt(txtId.getText()));
        obj.setName(txtName.getText());
        
        if(exception.getErrors().size() > 0) {
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
    }

    public void updateFormData() {
        if (entity == null) {
            throw new IllegalStateException("entity is null");
        }
        txtId.setText(String.valueOf(entity.getId()));
        txtName.setText(entity.getName());
    }
    
    private void setErrorMessages(Map<String, String> errors) {
    	Set<String> fields = errors.keySet();
    	
    	if(fields.contains("Name")) {
    		lblErrorName.setText(errors.get("Name"));
    	}
    	
    }
}
