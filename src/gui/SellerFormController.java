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
import java.time.format.DateTimeFormatter;
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
        sel.setName(textFieldName.getText());
        sel.setEmail(textFieldEmail.getText());
        sel.setBirthDate(LocalDate.parse(textFieldBirthDate.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        sel.setBaseSalary(Utils.tryParseSalaryfForDouble(textFieldBaseSalary));
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
            throw new IllegalStateException("Seller was null");
        }
        textFieldId.setText(String.valueOf(sel.getId()));
        textFieldName.setText(sel.getName());
        textFieldEmail.setText(sel.getEmail());
        textFieldBirthDate.setText("");
        if (sel.getBirthDate() != null) {
            textFieldBirthDate.setText(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(sel.getBirthDate()));
        }
        textFieldBaseSalary.setText(Utils.defineDecimalFormat(sel.getBaseSalary()));
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

    public void initializeFormForNewSeller() {
        this.sel = new Seller(); // Garante que a instância 'sel' não seja null
        updateFormData(); // Preenche com os valores padrão (ID 0, salário 0.0, etc.)
    }

    private void initializeNodes(){
        Constraints.setTextFieldInteger(textFieldId);
        Constraints.setTextFieldMaxLength(textFieldName, 30);
        Constraints.setTextFieldMaxLength(textFieldEmail, 40);
        Constraints.setTextFieldSalaryDynamic(textFieldBaseSalary);
        Constraints.setTextFieldMaxLength(textFieldBaseSalary, 12);
        Constraints.setTextFieldDate(textFieldBirthDate);
    }

    private void setComboBoxDepartment(){
        DepartmentService service = new DepartmentService();
        List<Department> list = service.findAll();
        comboBoxDepartment.setItems(FXCollections.observableArrayList(list));
    }
}
