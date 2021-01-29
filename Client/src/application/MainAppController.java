package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class MainAppController implements Initializable{

    @FXML
    private Button chatButton;

    @FXML
    private Button groupChatButton;

    @FXML
    private Button addFriendButton;

    @FXML
    private Button refreshFriendButton;

    @FXML
    private Button deleteAccountButton;

    @FXML
    private Pane mainAppPane;

    @FXML
    private Button changeDataButton;

    @FXML
    private Button changePasswordButton;

    @FXML
    private Button deleteFriendButton;

    @FXML
    private ListView<String> friendListView = new ListView();

    @FXML
    private TextField addFriendsField;

    @FXML
    private Button logoutButton;
    
    @FXML
    private Label welcomeLabel;

    @FXML
    void chatButtonClick(ActionEvent event) throws IOException {

        Main.user.znajomy = friendListView.getSelectionModel().getSelectedItem();

        ScreenController control = new ScreenController(this.chatButton);
        control.switchScene("ChatPanel.fxml");
    }

    @FXML
    void groupChatButtonClick(ActionEvent event) throws IOException {

        ScreenController control = new ScreenController(this.chatButton);
        control.switchScene("ChatHousePanel.fxml");
    }

    @FXML
    void deleteAccountButtonClick(ActionEvent event) throws IOException {


        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Usuwanie konta");
        alert.setHeaderText("Klikając OK usuniesz konto");
        alert.setContentText("Jestes pewien?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){

            String response = Main.client.deleteAccount(Main.user.email, Main.user.haslo);
            if(response.equals("OK"))
            {
                Alert alert2 = new Alert(Alert.AlertType.WARNING);
                alert2.setTitle(null);
                alert2.setHeaderText("Konto zostalo usuniete");
                alert2.setContentText("Zostaniesz przeniesiony do strony logowania");
                alert2.showAndWait();
                ScreenController control = new ScreenController(this.deleteAccountButton);
                control.switchScene("LoginPanel.fxml");
            }
            else
            {
                Alert alert2 = new Alert(Alert.AlertType.WARNING);
                alert2.setTitle(null);
                alert2.setHeaderText("Cos poszlo nie tak");
                alert2.setContentText("Zostaniesz przeniesiony do glownej strony aplikacji");
                alert2.showAndWait();
                ScreenController control = new ScreenController(this.deleteAccountButton);
                control.switchScene("MainAppPanel.fxml");
            }
        } else {

        }
    }

    @FXML
    void changeDataButtonClick(ActionEvent event) throws IOException {

        ScreenController control = new ScreenController(this.changeDataButton);
        control.switchScene("ChangeDataPanel.fxml");
    }

    
    @FXML
    void addFriendButtonClick(ActionEvent event) throws IOException {
    	
    	if(addFriendsField.getText()=="")
    	{
        	Alert alert = new Alert(AlertType.WARNING);
        	alert.setTitle(null);
        	alert.setHeaderText(null);
        	alert.setContentText("Podaj email znajomego");
        	alert.showAndWait();
    	}
    	else if(addFriendsField.getText().equals(Main.user.email))
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Nice try byczku");
            alert.showAndWait();
            addFriendsField.clear();
        }
    	else
    	{
    		String znajomy=addFriendsField.getText();
    		String response=Main.client.addFriends(Main.user.email, Main.user.haslo, znajomy);

    		if(response.equals("JUZ"))
            {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("Jestescie juz znajomymi");
                alert.showAndWait();
                addFriendsField.clear();
            }
    		else if(response.equals("OK"))
            {
                friendListView.getItems().add(znajomy);
                addFriendsField.clear();
            }
    		else if(response.equals("BRAK"))
            {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("Uzytkownik o takim loginie nie istnieje");
                alert.showAndWait();
                addFriendsField.clear();
            }
    		else
            {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("Blad - nieudane dodanie znajomego");
                alert.showAndWait();
                addFriendsField.clear();
            }
    	}
    }


    @FXML
    void deleteFriendButtonClick(ActionEvent event) throws IOException {


    	String loginZnajomy=friendListView.getSelectionModel().getSelectedItem();
        String response=Main.client.deleteFriend(Main.user.email, Main.user.haslo, loginZnajomy);
        if(response.equals("OK"))
        {
            friendListView.getItems().remove(friendListView.getSelectionModel().getSelectedIndex());
        }
        else
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Blad przy usuwaniu znajomego");
            alert.showAndWait();
        }

        deleteFriendButton.setDisable(true);
        friendListView.getSelectionModel().select(-1);//deselect all
    }

    @FXML
    void logoutButtonClick(ActionEvent event) throws IOException {

    	Main.client.logoff();
    	ScreenController control = new ScreenController(this.logoutButton);
    	control.switchScene("LoginPanel.fxml");
    }

    @FXML
    void refreshFriendButtonClick(ActionEvent event) throws IOException {

        List<String> emaile = Main.client.getFriends(Main.user.email);
        friendListView.getItems().clear();

        for(int i=0; i<emaile.size(); i++)
        {
            friendListView.getItems().add(emaile.get(i));
        }

        deleteFriendButton.setDisable(true);
        friendListView.getSelectionModel().select(-1);//deselect all
    }


    @FXML
    void changePasswordButtonClick(ActionEvent event) throws IOException {
        ScreenController control = new ScreenController(this.changePasswordButton);
        control.switchScene("ChangePassword.fxml");
    }


    public void loadData() throws IOException {

        List<String> emaile = Main.client.getFriends(Main.user.email);

        for(int i=0; i<emaile.size(); i++)
        {
            friendListView.getItems().add(emaile.get(i));
        }

        deleteFriendButton.setDisable(true);
        chatButton.setDisable(true);
    }


    private void loadBackground()
    {
        String dom = Main.user.dom;

        switch(dom)
        {
            case "Gryffindor":
                changeBackground("../images/gryffBackground.png");
                changeHouseButton("src/images/gryffindor_sign.png");
                break;
            case "Slytherin":
                changeBackground("../images/slythBackground.png");
                changeHouseButton("src/images/slytherin_sign.png");
                break;
            case "Hufflepuff":
                changeBackground("../images/huffBackground.png");
                changeHouseButton("src/images/hufflepuff_sign.png");
                break;
            case "Ravenclaw":
                changeBackground("../images/ravenBackground.png");
                changeHouseButton("src/images/ravenclaw_sign.png");
                break;
            default:
                changeBackground("../images/gryffBackground.png");
                changeHouseButton("src/images/gryffindor_sign.png");
                break;
        }
    }

    private void changeBackground(String background)
    {
        String backgroundUrl = RegisterController.class.getResource(background).toExternalForm();
        mainAppPane.setStyle("-fx-background-image: url('" + backgroundUrl + "')");
    }

    private void changeHouseButton(String button)
    {
        File file = new File(button);
        Image image = new Image(file.toURI().toString());

        ImageView view = new ImageView(image);
        view.setFitHeight(400);
        view.setPreserveRatio(true);
        groupChatButton.setGraphic(view);
    }



    void choiceListViewSystem()
    {
        friendListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        MultipleSelectionModel<String> comboSelModel = friendListView.getSelectionModel();
        comboSelModel.selectedItemProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> changed, String oldVal, String newVal) {
                // TODO Auto-generated method stub
                if(newVal != oldVal)
                {

                    if((friendListView.getSelectionModel().getSelectedIndex() != -1) || (friendListView.getSelectionModel().getSelectedItem() != ""))
                    {
                        deleteFriendButton.setDisable(false);
                        chatButton.setDisable(false);
                    }
                    else
                    {
                        deleteFriendButton.setDisable(true);
                        chatButton.setDisable(true);
                    }
                }
            }
        });
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
        loadBackground();
		welcomeLabel.setText("Witaj " + Main.user.imie + " " + Main.user.nazwisko + " (" + Main.user.email + ")");

        try {
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        choiceListViewSystem();
		System.out.println("MainAppController initialize");
	}
}

