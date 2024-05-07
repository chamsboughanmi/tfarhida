package controllers.Claim;

import controllers.SharedData;
import entities.reclamation;
import entities.ReponseReclamation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import services.Claim.ClaimResponseService;
import services.Claim.ClaimService;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class ClaimResponseController {

    @FXML
    private TextField date;

    @FXML
    private TextField Description;

    @FXML
    private TextField UserName;

    @FXML
    private Button backButton;

    @FXML
    private BorderPane borderPane;

    @FXML
    private FlowPane imageFlowPane;

    @FXML
    private Button responseButton;

    @FXML
    private TextArea responseTextArea;
    @FXML
    private ImageView claimImageView;
    @FXML
    private Label titleLabel;
    @FXML
    private Button submitButton;
    @FXML
    private Button returnButton;

    private reclamation reclamation;
    private ClaimService claimDAO;
    private ClaimResponseService claimResponseDAO;
    private int idClaim ;

    public ClaimResponseController() {
        claimDAO = new ClaimService();
        claimResponseDAO = new ClaimResponseService();
    }

    @FXML
    public void initialize() {
        idClaim = SharedData.getClaimId();
        System.out.println("eee"+idClaim);
        reclamation = claimDAO.findById(idClaim);
        // Populate the UI elements with claim data
         Description.setText(reclamation.getDescription());
        UserName.setText(reclamation.getUser().getFirstName());

        // Set image only if the claim has one, assuming getImage() returns image path
        if (reclamation.getImage() != null && !reclamation.getImage().isEmpty()) {
            // Load the image and set it to the ImageView
            Image image = new Image(reclamation.getImage());
            claimImageView.setImage(image);
        }
        date.setText(reclamation.getDate().toString());

        submitButton.setOnAction(event -> handleResponseSubmission());
    }

    private void handleResponseSubmission() {
        String responseText = responseTextArea.getText();
        Date d1 = Date.valueOf(LocalDate.now());
        if (!responseText.isEmpty()) {
            try {
                // Create a ClaimResponse object
                ReponseReclamation reponseReclamation = new ReponseReclamation(reclamation, reclamation.getUser(), responseText, d1);

                // Add the response to the database
                boolean responseAdded = claimResponseDAO.update(reponseReclamation);

                if (responseAdded) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewClaim/ViewClaim.fxml"));
                            Parent root = loader.load();
                            Scene scene = new Scene(root) ;
                            Stage stage = (Stage) submitButton.getScene().getWindow();
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                } else {
                    // Handle response addition failure
                    System.out.println("Failed to add the claim response.");
                }
            } catch (Exception e) {
                // Handle exception
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void handleReturnButtonAction() {
        redirectToViewClaim();
    }

    private void redirectToViewClaim() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewClaim/ViewClaim.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) returnButton.getScene().getWindow(); // Assuming returnButton is present in ClaimResponse.fxml
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
