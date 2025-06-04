package gui;

import db.DbException;
import gui.listeners.DataChangeListeners;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class SellerFormController implements Initializable {

    private Seller sel;

    private SellerService service;

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
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldBirthDate;

    @FXML
    private TextField textFieldBaseSalary;

    @FXML
    private ComboBox<Department> comboBoxDepartment;

    @FXML
    private Label errorLabel;

    @FXML
    public void addSellerAction(ActionEvent event){
        if (service == null){
            throw new IllegalStateException("service was null");
        }
        if (sel == null){
            throw new IllegalStateException("Seller was null");
        } try {
            sel = getFormData();
            service.insertOrUpdate(sel);
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

    private Seller getFormData() {
        Seller sel = new Seller();

        ValidationException exception = new ValidationException("Validation error");

        sel.setId(Utils.tryParseToInt(textFieldId.getText()));
        if (textFieldName.getText() == null || textFieldName.getText().trim().equals("")){
            exception.addError("name", "O campo não pode ser vazio");
        }
        sel.setName(textFieldName.getText());
        sel.setEmail(textFieldEmail.getText());
        sel.setBirthDate(LocalDate.parse(textFieldBirthDate.getText()));
        sel.setBaseSalary(Double.parseDouble(textFieldBaseSalary.getText()));
        sel.setDepartment(comboBoxDepartment.getValue());

        if (exception.getErrors().size() > 0){
            throw exception;
        }
        return sel;
    }

    public void setService(SellerService service){
        this.service = service;
    }

    public void setSeller(Seller sel){
        this.sel = sel;
    }

    public void subscribeDataChangerListeners(DataChangeListeners listener){
        dataChangeListeners.add(listener);
    }
    private void notifyDataChangerListeners() {
        dataChangeListeners.forEach(DataChangeListeners::onDataChanged);
    }

    public void updateFormData(){
        if (sel == null){
            throw new IllegalStateException("Department was null");
        }
        textFieldId.setText(String.valueOf(sel.getId()));
        textFieldName.setText(sel.getName());
        textFieldEmail.setText(sel.getEmail());
        textFieldBirthDate.setText(sel.getBirthDate().toString());
        textFieldBaseSalary.setText(String.valueOf(sel.getBaseSalary()));
        comboBoxDepartment.setValue(sel.getDepartment());
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
        setComboBoxDepartment();
    }

    private void initializeNodes(){
        Constraints.setTextFieldInteger(textFieldId);
        Constraints.setTextFieldMaxLength(textFieldName, 30);
        Constraints.setTextFieldMaxLength(textFieldEmail, 40);
        Constraints.setTextFieldDouble(textFieldBaseSalary);
        Constraints.setTextFieldMaxLength(textFieldBirthDate, 10);
    }

    private void setComboBoxDepartment(){
        DepartmentService service = new DepartmentService();
        List<Department> list = service.findAll();
        comboBoxDepartment.setItems(FXCollections.observableArrayList(list));
    }
}
