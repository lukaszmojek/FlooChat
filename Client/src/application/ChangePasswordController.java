package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChangePasswordController implements Initializable {


    @FXML
    private Button backButton;

    @FXML
    private PasswordField actualPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    void changePasswordButtonClick(ActionEvent event) throws IOException, InterruptedException {


        if(actualPasswordField.getText() == "" || newPasswordField.getText() == "" || confirmPasswordField.getText() == "")
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText("Zadne pole nie moze byc puste!");
            alert.setContentText("Wprowadz dane");
            alert.showAndWait();
        }
        else {
            if (!Main.user.haslo.equals(actualPasswordField.getText())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(null);
                alert.setHeaderText("Wprowadz poprawne aktualne haslo!");
                alert.setContentText("Wprowadz dane");
                alert.showAndWait();

                actualPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();
            } else {
                if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle(null);
                    alert.setHeaderText("Wprowadz nowe hasla poprawnie!");
                    alert.setContentText("Wprowadz dane");
                    alert.showAndWait();

                    actualPasswordField.clear();
                    newPasswordField.clear();
                    confirmPasswordField.clear();
                } else {
                    String newPassword = newPasswordField.getText();
                    String response = Main.client.changeData(Main.user.email, Main.user.haslo, "haslo", newPassword);

                    if (response.equals("OK")) {
                        Main.user.haslo = newPassword;
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle(null);
                        alert.setHeaderText("Haslo zmienione pomyslnie!");
                        alert.setContentText("Przeniose cie na glowna strone aplikacji");
                        alert.showAndWait();

                        ScreenController control = new ScreenController(this.backButton);
                        control.switchScene("MainAppPanel.fxml");
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle(null);
                        alert.setHeaderText("Nie udalo sie zmienic hasla");
                        alert.setContentText("Cos poszlo nie tak :(");
                        alert.showAndWait();
                    }
                }
            }
        }
    }

    @FXML
    void backButtonClick(ActionEvent event) throws IOException, InterruptedException {

        ScreenController control = new ScreenController(this.backButton);
        control.switchScene("MainAppPanel.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
