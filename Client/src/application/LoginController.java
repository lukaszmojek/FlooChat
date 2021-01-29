package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable{

    @FXML
    private Pane loginPane;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink recoverPasswordHyperlink;

    @FXML
    private Hyperlink registerHyperlink;
    
    @FXML
    private ImageView muggles_view;
    
    @FXML
    private Label test;

    @FXML
    void loginButtonClick(ActionEvent event) throws IOException, InterruptedException {
    	
        String email;
        String haslo;
        
        email=emailField.getText();
        haslo=passwordField.getText();
        
        if(email.equals("") || haslo.equals(""))
        {
        	Alert alert = new Alert(AlertType.WARNING);
        	alert.setTitle("Warning Dialog");
        	alert.setHeaderText("Email/haslo nie moze byc puste");
        	alert.setContentText("Wprowadz dane");
        	alert.showAndWait();
        	
        	emailField.clear();
        	passwordField.clear();
        }
        else
        {
            if (!Main.client.connect()) {
                System.err.println("Polaczenie zakonczone niepowodzeniem");
            }else{
                System.out.println("Polaczenie zakonczone powodzeniem");
                if(Main.client.login(email,haslo)){
                    System.out.println("Zalogowano");
                    Main.user.email=email;
                    Main.user.haslo=haslo;
                    
                    Main.client.getData(email);

                	ScreenController control = new ScreenController(this.loginButton);
                	control.switchScene("MainAppPanel.fxml");
                }else{
                    System.err.println("Nie zalogowano");
                    
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setHeaderText("Zly email/haslo");
                    alert.setContentText("Wprowadz jeszcze raz");
                    alert.showAndWait();
                	
                	emailField.clear();
                	passwordField.clear();
                }
            }     	
        }
    }

    @FXML
    void recoverPasswordHyperlinkClick(ActionEvent event) throws IOException {
    	
    	ScreenController control = new ScreenController(this.recoverPasswordHyperlink);
    	control.switchScene("RecoverPasswordPanel.fxml");
    	
    }

    @FXML
    void registerHyperlinkClick(ActionEvent event) throws IOException {
    	ScreenController control = new ScreenController(this.registerHyperlink);
    	control.switchScene("RegisterPanel.fxml");
    }

    void loadData()
    {
        File file = new File("src/images/noMuggles.png");
        Image image = new Image(file.toURI().toString());
        muggles_view.setImage(image);
        
        Font font = Font.loadFont("file:src/fonts/ParryHotter.ttf", 100);
        test.setFont(font);
        test.setLayoutX(220);
        test.setLayoutY(-100);
    }
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		loadData();   
		
		Main.user.imie="";
		Main.user.nazwisko="";
		Main.user.dom="";
		Main.user.email="";
		Main.user.haslo="";
		System.out.println("login initialize");
	}
}
