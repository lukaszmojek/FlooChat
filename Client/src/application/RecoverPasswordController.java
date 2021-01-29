package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RecoverPasswordController implements Initializable{

    @FXML
    private Button backButton;

    @FXML
    private TextField emailField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField lastnameField;

    @FXML
    private Button recoverPasswordButton;

    @FXML
    private Label recoverPasswordLabel;
    
    @FXML
    void backButtonClick(ActionEvent event) throws IOException {
    	ScreenController control = new ScreenController(this.backButton);
    	control.switchScene("LoginPanel.fxml");
    }

    @FXML
    void recoverPasswordButtonClick(ActionEvent event) throws IOException {
    	
    	if((emailField.getText() != "") && (nameField.getText() != "") && (lastnameField.getText() != ""))
    	{
    		String email=emailField.getText();
    		String name=nameField.getText();
    		String lastName=lastnameField.getText();
    		
    		if (!Main.client.connect()) 
                System.err.println("Polaczenie zakonczone niepowodzeniem");
            else
                System.out.println("Polaczenie zakonczone powodzeniem");
    		
    		
    		String response=Main.client.recoverPassword(email, name, lastName);
		
    		recoverPasswordLabel.setText(response);
    	}	
    	else
    	{
        	Alert alert = new Alert(AlertType.WARNING);
        	alert.setTitle("Warning Dialog");
        	alert.setHeaderText("Puste pola");
        	alert.setContentText("Wprowadz dane");
        	alert.showAndWait();
    	}	
    }
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
		System.out.println("recover password initialize");
		
	}

}
