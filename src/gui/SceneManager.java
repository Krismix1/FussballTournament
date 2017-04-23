package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by Chris on 23-Apr-17.
 */
public class SceneManager {
    private static SceneManager instance;

    private SceneManager(){}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    private Stage primaryStage;

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    void loadLoginScene() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/gui/Login.fxml"));
        primaryStage.setScene(new Scene(root, 300, 275));
    }

    void loadPlayerScene() throws IOException{
        Parent playerRoot = FXMLLoader.load(getClass().getResource("/gui/playerView.fxml"));
        Scene playerScene = new Scene(playerRoot, 900, 575);
        primaryStage.setScene(playerScene);
    }

    void loadAdministratorScene() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/gui/mainScene.fxml"));
        Scene adminScene = new Scene(root, 900, 575);
        primaryStage.setScene(adminScene);
    }



    ///////////////////////////////////////////////////HELPER METHODS////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////HELPER METHODS////////////////////////////////////////////////////////

    /**
     * Display an error alert with the given information.
     *
     * @param title   the title of the error window.
     * @param header  the message which will be showed in the header. If headers is not wanted,
     *                a null value can be sent as parameter.
     * @param content the message of the error window.
     */
    void displayError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    /**
     * Display an information alert with the given information.
     *
     * @param title   the title of the information window.
     * @param header  the message which will be showed in the header. If headers is not wanted,
     *                a null value can be sent as parameter.
     * @param content the message of the information window.
     */
    void displayInformation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    /**
     * Display a warning alert with the given information.
     *
     * @param title   the title of the warning window.
     * @param header  the message which will be showed in the header. If headers is not wanted,
     *                a null value can be sent as parameter.
     * @param content the message of the warning window.
     */
    void displayWarning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    boolean displayConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(null);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }
}
