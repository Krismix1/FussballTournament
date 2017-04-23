package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        SceneManager.getInstance().setStage(primaryStage);
        primaryStage.setTitle("Fu\u00DFball bitches");
        SceneManager.getInstance().loadLoginScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
