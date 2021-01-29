package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChangeDataPanelController implements Initializable {

    @FXML
    private Button backButton;

    @FXML
    private Button changeDataButton;

    @FXML
    private TextField nameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private void changeDataButtonClick(ActionEvent event) throws IOException, InterruptedException {

        if(nameField.getText().equals(Main.user.imie) && lastNameField.getText().equals(Main.user.nazwisko))
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText("Dane nie zmienione");
            alert.setContentText("Musisz zmienić imie lub nazwisko");
            alert.showAndWait();
        }
        else
        {
            String response="";
            if(!nameField.getText().equals(Main.user.imie))
            {
                String newName = nameField.getText();
                response = Main.client.changeData(Main.user.email, Main.user.haslo, "imie", newName);
                Main.user.imie = newName;
            }

            if(!lastNameField.getText().equals(Main.user.nazwisko))
            {
                String newLastName = lastNameField.getText();
                response = Main.client.changeData(Main.user.email, Main.user.haslo, "nazwisko", newLastName);
                Main.user.nazwisko = newLastName;
            }

            if(response.equals("OK"))
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(null);
                alert.setHeaderText("Dane zmienione pomyslnie");
                alert.setContentText("Przeniose cie na glowana strone aplikacji");
                alert.showAndWait();

                ScreenController control = new ScreenController(this.changeDataButton);
                control.switchScene("MainAppPanel.fxml");
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(null);
                alert.setHeaderText("Nie udalo sie zmienic hasla");
                alert.setContentText("Cos poszlo nie tak :(");
                alert.showAndWait();

                ScreenController control = new ScreenController(this.changeDataButton);
                control.switchScene("MainAppPanel.fxml");
            }
        }
    }

    @FXML
    private void backButtonClick(ActionEvent event) throws IOException, InterruptedException {

        ScreenController control = new ScreenController(this.backButton);
        control.switchScene("MainAppPanel.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        nameField.setText(Main.user.imie);
        lastNameField.setText(Main.user.nazwisko);

    }
}
