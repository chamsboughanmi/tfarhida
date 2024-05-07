package controllers.Claim;

import entities.reclamation;
import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import services.Claim.ClaimService;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.User.UserDAO;

import java.io.IOException;

public class NewClaimController {

    @FXML
    private TextField descriptionField;
    @FXML
    private  Label titleLabel;
    @FXML
    private TextField imageField;
    @FXML
    private RadioButton siteRadioButton;
    @FXML
    private RadioButton reservationRadioButton;
    @FXML
    private RadioButton serviceRadioButton;
    @FXML
    private Button addButton;
    @FXML
    private Button returnButton;
    @FXML
    private Button browseButton;
    @FXML
    private ImageView imageView;

    private File selectedFile;
    private reclamation reclamation;
    private reclamation cp;
    //private User currentUser = SessionManager.getInstance().getCurrentUser();
    private int userId = 19;
    private User user;
    private ClaimService claimDAO;
    private UserDAO userDAO;
    private final ToggleGroup radioButtonGroup = new ToggleGroup();
    public NewClaimController() {
        claimDAO = new ClaimService();
        userDAO = new UserDAO();
    }

    public void initialize() {
        user = userDAO.getUserById(userId);
        descriptionField.setText("");
        imageField.setText("");

        // Ajout des RadioButtons au ToggleGroup
        siteRadioButton.setToggleGroup(radioButtonGroup);
        reservationRadioButton.setToggleGroup(radioButtonGroup);
        serviceRadioButton.setToggleGroup(radioButtonGroup);

        // Add listener for ComboBox selection


        returnButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewClaim/ViewClaim.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) addButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleAddButtonAction() {
        if (addButton.getText().equals("Update")) {
            String des = descriptionField.getText();
            reclamation.setDescription(des);
            claimDAO.update(reclamation);
            redirectToViewClaim();
        } else {
            if (validateFields()) {
                String des = descriptionField.getText();
                // Utilisation de la date système
                LocalDate localDate = LocalDate.now();
                Date sqlDate = Date.valueOf(localDate);
                String statue = "en attente";
                String type = getSelectedType();
                String imageUrl = (selectedFile != null) ? selectedFile.toURI().toString() : "";
                String sat = "pas encore";


                    reclamation reclamation = new reclamation( user, des, sqlDate, statue, type, imageUrl, sat);
                    if (claimDAO.addWithAutoResponse(reclamation)) {
                        System.out.println("Claim added successfully!");
                        redirectToViewClaim();
                    } else {
                        System.out.println("Failed to add claim!");
                    }

            }
        }
    }
    @FXML
    private void handleCancelButtonAction() {
        redirectToViewClaim();
    }

    public void populateFieldsWithClaim(int id) {
        reclamation = claimDAO.findById(id);
        System.out.println(id);
        System.out.println(reclamation);
        descriptionField.setText(reclamation.getDescription());

        if (reclamation.getImage() != null && !reclamation.getImage().isEmpty()) {
            // Load the image and set it to the ImageView
            Image image = new Image(reclamation.getImage());
            imageView.setImage(image);
        }
        addButton.setText("Modifier");
        titleLabel.setText("Modifier votre réclamation");
    }

    @FXML
    private void handleBrowseButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );
        selectedFile = fileChooser.showOpenDialog(browseButton.getScene().getWindow());
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
            imageField.setText(selectedFile.getName());
        }
    }

    private boolean validateFields() {
        if (descriptionField.getText().isEmpty() || imageField.getText().isEmpty() || getSelectedType() == null) {
            showAlert("Veuillez remplir tous les champs et sélectionner un type.");
            return false;
        } else

        return true;
    }

    private String getSelectedType() {
        if (serviceRadioButton.isSelected()) {
            return "service";
        } else if (reservationRadioButton.isSelected()) {
            return "reservation";
        } else if (siteRadioButton.isSelected()) {
            return "site";
        }
        return null;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void redirectToViewClaim() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ViewClaim/ViewClaim.fxml"));
            Scene scene = new Scene(root, 1100, 600);
            Stage stage = (Stage) addButton.getScene().getWindow(); // Assuming saveButton is present in NewClub.fxml
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
