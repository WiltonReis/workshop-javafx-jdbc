package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML
    private MenuItem menuItemVendedor;

    @FXML
    private MenuItem menuItemDepartamento;

    @FXML
    private MenuItem menuItemAbout;

    @FXML
    public void onMenuItemVendedorAction(){
        System.out.println("Ok");
    }

    @FXML
    public void onMenuItemDepartamentoAction(){
        System.out.println("Ok");
    }

    @FXML
    public void onMenuItemAboutAction(){
        System.out.println("Ok");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
