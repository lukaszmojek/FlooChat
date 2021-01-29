package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

	public static class Uzytkownik{
		
		public Uzytkownik()
		{
			this.imie="";
			this.nazwisko="";
			this.dom="";
			this.email="";
			this.haslo="";
		}
		public String imie;
		public String nazwisko;
		public String dom;
		public String email;
		public String haslo;	
		public String znajomy;
	}
	
	public static Uzytkownik user;
	public static ChatClient client;
	
	public static void main(String[] args) {
		
		user = new Uzytkownik();
		
        client = new ChatClient("localhost", 8818);

        Main.client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("Online: " + login);
            }
            @Override
            public void offline(String login) {
                System.out.println("Offline: " + login);
            }
        });
		
        Application.launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
        Parent root = FXMLLoader.load(getClass().getResource("LoginPanel.fxml"));
        
        Scene scene = new Scene(root, 800, 800);
       
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
	}
}

