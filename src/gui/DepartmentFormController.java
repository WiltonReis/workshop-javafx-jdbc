package gui;

import db.DbException;
import gui.listeners.DataChangeListeners;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

import java.net.URL;
import java.util.*;

public class DepartmentFormController implements Initializable {

    private Department dep;

    private DepartmentService service;

    private List<DataChangeListeners> dataChangeListeners = new ArrayList<>();

    @FXML
    private Button save;

    @FXML
    private Button cancel;

    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldId;

    @FXML
    private Label errorLabel;

    @FXML
    public void addDepartmentAction(ActionEvent event){
        if (service == null){
            throw new IllegalStateException("service was null");
        }
        if (dep == null){
            throw new IllegalStateException("Department was null");
        } try {
            dep = getFormData();
            service.insertOrUpdate(dep);
            notifyDataChangerListeners();
            Utils.currentStage(event).close();
        } catch (DbException e){
            Alerts.showAlert("Erro ao salvar departamento", null, e.getMessage(), Alert.AlertType.ERROR);
        } catch (ValidationException e){
            setErrorMessages(e.getErrors());
        }
    }

    @FXML
    public void cancelAction(ActionEvent event){
        Utils.currentStage(event).close();
    }

    private Department getFormData() {
        Department dep = new Department();

        ValidationException exception = new ValidationException("Validation error");

        dep.setId(Utils.tryParseToInt(textFieldId.getText()));
        if (textFieldName.getText() == null || textFieldName.getText().trim().equals("")){
            exception.addError("name", "O campo não pode ser vazio");
        }
        dep.setName(textFieldName.getText());

        if (exception.getErrors().size() > 0){
            throw exception;
        }
        return dep;
    }

    public void setService(DepartmentService service){
        this.service = service;
    }

    public void setDepartment(Department dep){
        this.dep = dep;
    }

    public void subscribeDataChangerListeners(DataChangeListeners listener){
        dataChangeListeners.add(listener);
    }
    private void notifyDataChangerListeners() {
        dataChangeListeners.forEach(DataChangeListeners::onDataChanged);
    }

    public void updateFormData(){
        if (dep == null){
            throw new IllegalStateException("Department was null");
        }
        textFieldId.setText(String.valueOf(dep.getId()));
        textFieldName.setText(dep.getName());
    }

    private void setErrorMessages(Map<String, String> errors){
        Set<String> fields = errors.keySet();

        if(fields.contains("name")){
            errorLabel.setText(errors.get("name"));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes(){
        Constraints.setTextFieldInteger(textFieldId);
        Constraints.setTextFieldMaxLength(textFieldName, 30);
    }
}
