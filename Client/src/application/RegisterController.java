package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


//import javafx.scene.input.ContextMenuEvent;


public class RegisterController implements Initializable{
	
    @FXML
    private Label nameLabel; 
    
    @FXML
    private Label lastNameLabel; 
    
    @FXML
    private Label emailLabel;  
    
    @FXML
    private Label passwordLabel;
    
    @FXML
    private Label confirmPasswordLabel;
    
    @FXML
    private Label houseLabel;
    
	@FXML
    private Pane registerMainPane;
	
	@FXML
	private Pane registerPane;
	
    @FXML
    private TextField firstNameField;
    
    @FXML
    private TextField lastNameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private TextField userNameField;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Button backButton;
    
    @FXML
    private TextArea descriptionOfHouse;
    
    @FXML
    private ImageView imageOfHouse;
    
    @FXML
    private ComboBox<String> choiceHouse = new ComboBox<>();
    
    @FXML
    void backButtonClick(ActionEvent event) throws IOException {
    	ScreenController control = new ScreenController(this.backButton);
    	control.switchScene("LoginPanel.fxml");
    }
      
    @FXML
    void registerButtonClick(ActionEvent event) throws IOException {
    	
    	if(firstNameField.equals("") || lastNameField.equals("") || emailField.equals("") || passwordField.equals("") || confirmPasswordField.equals("") || choiceHouse.getSelectionModel().getSelectedItem() == null)
    	{
        	Alert alert = new Alert(AlertType.WARNING);
        	alert.setTitle("Warning Dialog");
        	alert.setHeaderText("Puste pola");
        	alert.setContentText("Wprowadz dane");
        	alert.showAndWait();
    	}
    	else
    	{
    		String name=firstNameField.getText();
    		String lastName=lastNameField.getText();
    		String email=emailField.getText();
    		String password=passwordField.getText();
    		String confirmPassword=confirmPasswordField.getText();
    		String house=choiceHouse.getSelectionModel().getSelectedItem();
    	
    		
    		if(!password.equals(confirmPassword))
    		{
            	Alert alert = new Alert(AlertType.WARNING);
            	alert.setTitle("Warning Dialog");
            	alert.setHeaderText("Hasla musza byc takie same");
            	alert.setContentText("Wprowadz haslo jeszcze raz");
            	alert.showAndWait();
            	
            	passwordField.clear();
            	confirmPasswordField.clear();
    		}
    		else
    		{
                if (!Main.client.connect()) 
                    System.err.println("Polaczenie zakonczone niepowodzeniem");
                else
                    System.out.println("Polaczenie zakonczone powodzeniem");
                
    			String response=Main.client.register(name, lastName, email, password, house);
    			
    			if(response.equals("TAK"))
    			{
    				Alert alert = new Alert(AlertType.INFORMATION);
    				alert.setTitle("Information Dialog");
    				alert.setHeaderText(null);
    				alert.setContentText("Rejestracja przebiegla pomylsnie, przeniose cie na strone logowania");
    				alert.showAndWait();
    				
    		    	ScreenController control = new ScreenController(this.backButton);
    		    	control.switchScene("LoginPanel.fxml");
    			}
    					
    			if(response.equals("NIE"))
    			{
                	Alert alert = new Alert(AlertType.WARNING);
                	alert.setTitle("Warning Dialog");
                	alert.setHeaderText("Email zajety");
                	alert.setContentText("Uzytkownik " + email + " juz istnieje, wprowadz inny email");
                	alert.showAndWait();
                	
                	emailField.clear();
                	passwordField.clear();
                	confirmPasswordField.clear();
    			}		
    		}
    	}
    }
    
    void choiceHouseSystem()
    {
    	SingleSelectionModel<String> comboSelModel = choiceHouse.getSelectionModel();
    	comboSelModel.selectedItemProperty().addListener(new ChangeListener<String>()
    	{
			@Override
			public void changed(ObservableValue<? extends String> changed, String oldVal, String newVal) {
				// TODO Auto-generated method stub
				if(newVal == "Gryffindor")
				{
					descriptionOfHouse.setText(opisGryffindor);
			        File fileGryffindor = new File("src/images/gryffindor_sign.png");
			        Image gryffindorSign = new Image(fileGryffindor.toURI().toString());
			        imageOfHouse.setImage(gryffindorSign);
			        centerImage();
			        changeColor("#FFFFFF", "#FFFFFF", "#FF0808", "../images/mainGryfon.png", "../images/registerGryfon.png");
				}
				else if(newVal == "Slytherin")
				{
					descriptionOfHouse.setText(opisSlytherin);
			        File fileSlytherin = new File("src/images/slytherin_sign.png");
			        Image slytherinSign = new Image(fileSlytherin.toURI().toString());	     
			        imageOfHouse.setImage(slytherinSign);
			        centerImage();
			        changeColor("#FFFFFF", "#FFFFFF", "#009F00", "../images/mainSlyth.png", "../images/registerSlytherin.png");
				}
				else if(newVal == "Ravenclaw")
				{
					descriptionOfHouse.setText(opisRavenclaw);
			        File fileRavenclaw = new File("src/images/ravenclaw_sign.png");
			        Image ravenclawSign = new Image(fileRavenclaw.toURI().toString());
			        imageOfHouse.setImage(ravenclawSign);
			        centerImage();
			        changeColor("#FFFFFF", "#FFFFFF", "#4169E1", "../images/mainRaven.png", "../images/registerRaven.png");
				}
				else if(newVal == "Hufflepuff")
				{
					descriptionOfHouse.setText(opisHufflepuff);
			        File fileHufflepuff = new File("src/images/hufflepuff_sign.png");
			        Image hufflepuffSign = new Image(fileHufflepuff.toURI().toString());
			        imageOfHouse.setImage(hufflepuffSign);
			        centerImage();
			        changeColor("#FFFFFF", "#FFFFFF", "#870000", "../images/mainHafel.png", "../images/registerHafel.png");
				}
				else
				{
					descriptionOfHouse.setText("");
			        File fileTiara = new File("src/images/tiara.png");
			        Image tiara = new Image(fileTiara.toURI().toString());
			        imageOfHouse.setImage(tiara); 
			        centerImage();
				}
			}
    	});
    }
    
    public void centerImage() {
        Image img = imageOfHouse.getImage();
        if (img != null) {
            double w = 0;
            double h = 0;

            double ratioX = imageOfHouse.getFitWidth() / img.getWidth();
            double ratioY = imageOfHouse.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if(ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            imageOfHouse.setX((imageOfHouse.getFitWidth() - w) / 2);
            imageOfHouse.setY((imageOfHouse.getFitHeight() - h) / 2);
        }
    }
    
    ObservableList<String> housesOfHogwart = FXCollections.observableArrayList("Gryffindor", "Slytherin","Ravenclaw", "Hufflepuff");
    String opisDomow="W Hogwarcie kazdy rocznik danego domu dzielony zostawal na klasy \n Domy konkurowaly ze soba przez caly rok szkolny "
    		+ "zdobywajac i tracac punkty za rozne dzialania";
    String opisGryffindor="Wychowankowie domu cechuja sie odwaga rycerskoscia i determinacja smialoscia "
    		+ "jak rowniez umiejetnoscia zachowania zimnej krwi w trudnych sytuacjach";
    String opisSlytherin="Slytherin jest kojarzony przede wszystkim jako dom uczniow sprytnych "
    		+ "przebieglych i zadnych wladzy ale nie wszyscy Slizgoni charakteryzuja sie tymi cechami";
    String opisHufflepuff="Puchoni byli osobami pracowitymi uczynnymi i sprawiedliwymi tolerancyjnymi "
    		+ "uczciwymi i spokojnymi oraz o czym malo osob wie rowniez kreatywnymi";
    String opisRavenclaw="Ravenclaw byl domem nauki madrosci i intelektu Cenilo sie tutaj rowniez indywidualizm oryginalnosc i kreatywnosc "
    		+ "Tak wiec wielu Krukonow bylo naukowo zmotywowanymi i zdolnymi uczniami czesto wyrozniajacymi sie na tle innych";
    				

    void loadData()
    {
    	choiceHouse.setPromptText("Wybierz dom");
    	choiceHouse.setItems(housesOfHogwart);
    	choiceHouse.setItems(housesOfHogwart);
    	choiceHouseSystem();
    	
    	Font font = Font.loadFont("file:src/fonts/HarryPotter-ov4z.ttf", 20);
    	descriptionOfHouse.setText(opisDomow);
    	descriptionOfHouse.setFont(font);
    	
        File fileTiara = new File("src/images/tiara.png");
        Image tiara = new Image(fileTiara.toURI().toString());
        imageOfHouse.setImage(tiara);	
    }
        
    void changeColor(String labelColor, String textColor, String buttonColor, String mainBackground, String registerBackground)
    {
    	nameLabel.setTextFill(Color.web(labelColor));
    	lastNameLabel.setTextFill(Color.web(labelColor));
    	emailLabel.setTextFill(Color.web(labelColor));
    	passwordLabel.setTextFill(Color.web(labelColor));
    	confirmPasswordLabel.setTextFill(Color.web(labelColor));
    	houseLabel.setTextFill(Color.web(labelColor));

    	descriptionOfHouse.setStyle("-fx-text-fill: " + textColor + " ;");   	
    	
        String backgroundUrl = RegisterController.class.getResource(mainBackground).toExternalForm();
        String registerUrl = RegisterController.class.getResource(registerBackground).toExternalForm();
        
    	registerMainPane.setStyle("-fx-background-image: url('" + backgroundUrl + "')");
    	registerPane.setStyle("-fx-background-image: url('" + registerUrl + "')");
    	
    	registerButton.setStyle("-fx-background-color: " + buttonColor + "; -fx-font-weight: bold;");
    	
    	backButton.setStyle("-fx-background-color: " + buttonColor + "; -fx-font-weight: bold;");
    }
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {

        loadData();
		System.out.println("register initialize");
		
	}

}

