package gui;

import db.DbException;
import gui.listeners.DataChangeListeners;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SellerFormController implements Initializable {

    private Seller sel;

    private SellerService sellerService;

    private DepartmentService departmentService;

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
    private DatePicker datePickerBirthDate;

    @FXML
    private TextField textFieldBaseSalary;

    @FXML
    private ComboBox<Department> comboBoxDepartment;

    @FXML
    private Label errorLabelName;

    @FXML
    private Label errorLabelEmail;

    @FXML
    private Label errorLabelBaseSalary;

    @FXML
    private Label errorLabelBirthDate;

    @FXML
    private Label errorLabelDepartment;

    private ObservableList<Department> obsList;


    @FXML
    public void addSellerAction(ActionEvent event){
        if (sellerService == null){
            throw new IllegalStateException("service was null");
        }
        if (sel == null){
            throw new IllegalStateException("Seller was null");
        } try {
            sel = getFormData();
            sellerService.insertOrUpdate(sel);
            notifyDataChangerListeners();
            Utils.currentStage(event).close();
        } catch (DbException e){
            Alerts.showAlert("Erro ao salvar departamento", null, e.getMessage(), Alert.AlertType.ERROR);
            System.out.println(e.getMessage());
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
        System.out.println("chegou aqui");
        ValidationException exception = new ValidationException("Validation error");

        sel.setId(Utils.tryParseToInt(textFieldId.getText()));
        if (textFieldName.getText() == null || textFieldName.getText().trim().equals("")){
            exception.addError("name", "O campo não pode ser vazio");
        }
        if (textFieldEmail.getText() == null || textFieldEmail.getText().trim().equals("")){
            exception.addError("email", "O campo não pode ser vazio");
        }
        if (textFieldBaseSalary.getText() == null || textFieldBaseSalary.getText().trim().equals("R$ 0,00")){
            exception.addError("baseSalary", "O campo não pode ser vazio");
        }
        if (datePickerBirthDate.getValue() == null){
            exception.addError("birthDate", "O campo não pode ser vazio");
        }
        if (comboBoxDepartment.getValue() == null){
            exception.addError("department", "O campo não pode ser vazio");
        }
        sel.setName(textFieldName.getText());
        sel.setEmail(textFieldEmail.getText());
        sel.setBirthDate(datePickerBirthDate.getValue());
        sel.setBaseSalary(Utils.tryParseSalaryfForDouble(textFieldBaseSalary));
        sel.setDepartment(comboBoxDepartment.getValue());

        if (exception.getErrors().size() > 0){
            throw exception;
        }
        return sel;
    }

    public void setServices(SellerService sellerService, DepartmentService departmentService){
        this.sellerService = sellerService;
        this.departmentService = departmentService;
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
            throw new IllegalStateException("Seller was null");
        }
        textFieldId.setText(String.valueOf(sel.getId()));
        textFieldName.setText(sel.getName());
        textFieldEmail.setText(sel.getEmail());
        datePickerBirthDate.setValue(sel.getBirthDate());
        textFieldBaseSalary.setText(Utils.defineDecimalFormat(sel.getBaseSalary()));
        comboBoxDepartment.setValue(sel.getDepartment());
    }

    private void setErrorMessages(Map<String, String> errors){
        Set<String> fields = errors.keySet();

        errorLabelName.setText(fields.contains("name") ? errors.get("name") : "");
        errorLabelEmail.setText(fields.contains("email") ? errors.get("email") : "");
        errorLabelBaseSalary.setText(fields.contains("baseSalary") ? errors.get("baseSalary") : "");
        errorLabelBirthDate.setText(fields.contains("birthDate") ? errors.get("birthDate") : "");
        errorLabelDepartment.setText(fields.contains("department") ? errors.get("department") : "");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    public void initializeFormForNewSeller() {
        this.sel = new Seller();
        updateFormData();
    }

    private void initializeNodes(){
        Constraints.setTextFieldInteger(textFieldId);
        Constraints.setTextFieldMaxLength(textFieldName, 30);
        Constraints.setTextFieldMaxLength(textFieldEmail, 40);
        Constraints.setTextFieldSalaryDynamic(textFieldBaseSalary);
        Constraints.setTextFieldMaxLength(textFieldBaseSalary, 12);
    }

    public void loadAssociatedObjects(){
        if (departmentService == null){
            throw new IllegalStateException("Service was null");
        }
        obsList = FXCollections.observableArrayList(departmentService.findAll());
        comboBoxDepartment.setItems(FXCollections.observableArrayList(obsList));
    }
}
