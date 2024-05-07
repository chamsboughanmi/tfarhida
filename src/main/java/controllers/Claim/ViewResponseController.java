package controllers.Claim;

import controllers.SharedData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.Claim.ClaimService;
import services.Claim.ClaimResponseService;
import entities.ReponseReclamation;
import javafx.event.ActionEvent;

import java.io.IOException;

public class ViewResponseController {

    @FXML
    private TextField response;
    @FXML
    private Button sat1;
    @FXML
    private Button sat2;
    @FXML
    private Button sat3;
    @FXML
    private Button sat4;
    @FXML
    private Button sat5;
    @FXML
    private Label responseLabel; // Label pour afficher la réponse
    private ReponseReclamation cl;
    private int claimId= SharedData.getClaimId(); // ID de la réclamation pour laquelle la réponse est affichée
    private ClaimResponseService claimResponseDAO;
    public ViewResponseController() {
        claimResponseDAO = new ClaimResponseService();
    }

    public void initialize() {
        cl = new ReponseReclamation();
        cl =  claimResponseDAO.findResponseByClaimId(claimId);
        responseLabel.setText("Contenu de la réponse");
       // response.setText(cl.getDescription());

        System.out.println(cl);
        // Charger la réponse associée à la réclamation
        loadResponse();
    }
    private void loadResponse() {
        // Vérifiez si la réponse n'est pas null avant de l'afficher
        if (cl != null) {
            // Mettez à jour le label avec la réponse
            responseLabel.setText(cl.getDescription());
        } else {
            // Si aucune réponse n'est trouvée, affichez un message approprié ou laissez le label vide
            responseLabel.setText("Aucune réponse n'a été trouvée pour cette réclamation.");
        }
    }

    @FXML
    private void closeWindow() {
        // Ferme la fenêtre de la vue de réponse
        Stage stage = (Stage) responseLabel.getScene().getWindow();
        stage.close();
    }


    @FXML
    void Asat1(ActionEvent event) {
        ClaimService Cd = new ClaimService();
        Cd.updateClaimSatisfaction(claimId,"Très insatisfait");
        try {
            System.out.println(claimId);
            // Chargez la vue de réponse associée à la réclamation ici
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewClaim/ViewClaim.fxml"));
            Parent root = loader.load();
            // Affichez la vue dans une nouvelle fenêtre ou comme vous le souhaitez dans votre application
            Scene scene = new Scene(root);
            Stage stage = (Stage) sat1.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Asat2(ActionEvent event) {
        ClaimService Cd = new ClaimService();
        Cd.updateClaimSatisfaction(claimId,"plutôt insatisfait");
        try {
            System.out.println(claimId);
            // Chargez la vue de réponse associée à la réclamation ici
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewClaim/ViewClaim.fxml"));
            Parent root = loader.load();
            // Affichez la vue dans une nouvelle fenêtre ou comme vous le souhaitez dans votre application
            Scene scene = new Scene(root);
            Stage stage = (Stage) sat2.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Asat3(ActionEvent event) {
        ClaimService Cd = new ClaimService();
        Cd.updateClaimSatisfaction(claimId,"Neutre");
        try {
            System.out.println(claimId);
            // Chargez la vue de réponse associée à la réclamation ici
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewClaim/ViewClaim.fxml"));
            Parent root = loader.load();
            // Affichez la vue dans une nouvelle fenêtre ou comme vous le souhaitez dans votre application
            Scene scene = new Scene(root);
            Stage stage = (Stage) sat3.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Asat4(ActionEvent event) {
        ClaimService Cd = new ClaimService();
        Cd.updateClaimSatisfaction(claimId,"satisfait");
        try {
            System.out.println(claimId);
            // Chargez la vue de réponse associée à la réclamation ici
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewClaim/ViewClaim.fxml"));
            Parent root = loader.load();
            // Affichez la vue dans une nouvelle fenêtre ou comme vous le souhaitez dans votre application
            Scene scene = new Scene(root);
            Stage stage = (Stage) sat4.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Asat5(ActionEvent event) {
        ClaimService Cd = new ClaimService();
        Cd.updateClaimSatisfaction(claimId,"Tres satisfait");
        try {
            System.out.println(claimId);
            // Chargez la vue de réponse associée à la réclamation ici
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewClaim/ViewClaim.fxml"));
            Parent root = loader.load();

            // Affichez la vue dans une nouvelle fenêtre ou comme vous le souhaitez dans votre application
            Scene scene = new Scene(root);
            Stage stage = (Stage) sat5.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
