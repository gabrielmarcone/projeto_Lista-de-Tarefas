package view;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;


public class App extends Application{

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load (getClass().getResource("/fxml/telaInicial.fxml"));
            Scene scene = new Scene(root);
            
            stage.setTitle("Inicio");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
