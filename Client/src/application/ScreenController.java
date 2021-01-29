package application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.io.IOException;


public class ScreenController {
	
	private Button button;
	private Hyperlink hyperlink;
	public ScreenController(Button button)
	{
		this.button=button;
		this.hyperlink=null;
	}
	
	public ScreenController(Hyperlink hyperlink)
	{
		this.hyperlink=hyperlink;
		this.button=null;
	}
	
	public void switchScene(String nameOfPage) throws IOException
	{
		Stage stage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource(nameOfPage));
		if(button == null)
	        stage = (Stage) hyperlink.getScene().getWindow();
		else
	        stage = (Stage) button.getScene().getWindow();
		
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);

	}

}
