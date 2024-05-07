package design;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
       Parent root = FXMLLoader.load(getClass().getResource("/ViewClaim/ViewClaim.fxml"));
        Scene scene = new Scene(root, 1180.0, 655.0);

       /* scene.getStylesheets().add(getClass().getResource("/ViewClaim/styles.css").toExternalForm());*/


        // Load the CSS file
      //  scene.getStylesheets().add(getClass().getResource("/ViewClub/styles.css").toExternalForm());

        primaryStage.setTitle("Tfarhidaa");
        Image icon = new Image(getClass().getResourceAsStream("/Images/tfarhida logo.png"));

        // Set the icon
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

