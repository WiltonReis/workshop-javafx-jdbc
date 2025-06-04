package gui;

import application.Main;
import db.DbException;
import gui.listeners.DataChangeListeners;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SellerListController implements Initializable, DataChangeListeners {

    private SellerService service;

    @FXML
    private Button addSeller;

    @FXML
    private TableView<Seller> tableViewSeller;

    @FXML
    private TableColumn<Seller, Long> tableColumId;

    @FXML
    private TableColumn<Seller, String> tableColumnName;

    @FXML
    private TableColumn<Seller, String> tableColumnEmail;

    @FXML
    private TableColumn<Seller, LocalDate> tableColumnBirthDate;

    @FXML
    private TableColumn<Seller, Double> tableColumnBaseSalary;

    @FXML
    private TableColumn<Seller, Department> tableColumnDepartment;

    @FXML
    private TableColumn<Seller, Seller> tableColumnEdit;

    @FXML
    private TableColumn<Seller, Seller> tableColumnDelete;

    private ObservableList<Seller> obsList;

    @FXML
    public void onAddSellerAction(ActionEvent event){
        Seller sel = new Seller();
        createDialogForm(sel,"SellerForm.fxml", Utils.currentStage(event));
    }

    public void setService(SellerService service){
        this.service = service;
    }

    public void updateTableView(){
        if(service == null){
            throw new IllegalStateException("Service was null");
        }
        List<Seller> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tableViewSeller.setItems(obsList);
        /*initEditButtons();
        initDeleteButtons();*/
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes() {
        tableColumId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        formatDate(tableColumnBirthDate, "dd/MM/yyyy");
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        formatSalary(tableColumnBaseSalary);
        tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        formatDepartment(tableColumnDepartment);
        tableColumnDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
    }

    private void createDialogForm(Seller sel, String absolutName, Stage parentStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
            Pane pane = loader.load();
            SellerFormController controller = loader.getController();
            controller.setSeller(sel);
            controller.setService(new SellerService());

            controller.subscribeDataChangerListeners(this);

            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Entre com o dados no novo funcionário");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();

        } catch (IOException e) {
            Alerts.showAlert("Erro!", null, e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /*private void initEditButtons() {
        tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEdit.setCellFactory(param -> new TableCell<Seller, Seller>() {
            private final Button button = new Button("editar");
            @Override
            protected void updateItem(Seller obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(
                                obj, "/gui/DepartmentForm.fxml",Utils.currentStage(event)));
            }
        });
    }

    private void initDeleteButtons() {
        tableColumnDelete.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnDelete.setCellFactory(param -> new TableCell<Department, Department>() {
            private final Button button = new Button("deletar");
            @Override
            protected void updateItem(Department obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> deleteDepartment(obj));
            }
        });
    }

    private void deleteDepartment(Department dep) {
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmar", "Tem certeza que quer deletar?");

        if (result.get() == ButtonType.OK){
            if (service == null){
                throw new IllegalStateException("Service was null");
            }
            try {
                service.delete(dep);
                updateTableView();
            } catch (DbException e){
                Alerts.showAlert("Erro!", null, e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }*/

    private void formatDate(TableColumn<Seller, LocalDate> column, String format) {
        column.setCellFactory(col -> new TableCell<Seller, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(DateTimeFormatter.ofPattern(format).format(item));
                }
            }
        });
    }
    private void formatSalary(TableColumn<Seller, Double> column) {
        column.setCellFactory(col -> new TableCell<Seller, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(NumberFormat.getCurrencyInstance().format(item));
                }
            }
        });
    }

    private void formatDepartment(TableColumn<Seller, Department> column) {
        column.setCellFactory(col -> new TableCell<Seller, Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
    }

    @Override
    public void onDataChanged() {
        updateTableView();
    }
}
