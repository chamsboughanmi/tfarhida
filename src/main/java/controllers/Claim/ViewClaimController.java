package controllers.Claim;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import controllers.SharedData;
import entities.reclamation;
import entities.Role;
import entities.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.Claim.ClaimService;
import services.User.UserDAO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewClaimController {
    @FXML
    private VBox claimCardsContainer;
    @FXML
    private TableView<reclamation> claimTableView;
    @FXML
    private TableColumn<reclamation, Date> dateColumn;
    @FXML
    private TableColumn<reclamation, String> descriptionColumn;
    @FXML
    private TableColumn<reclamation, String> typeColumn;
    @FXML
    private TableColumn<reclamation, String> statusColumn;
    @FXML
    private TableColumn<reclamation, String> satisfactionColumn;
    @FXML
    private TableColumn<reclamation, Image> imageColumn;
    @FXML
    private TableColumn<reclamation, String> userColumn;
    @FXML
    private TableColumn<reclamation, Void> actionsColumn;
    @FXML
    private Label labelType;
    @FXML
    private Button addButton;
    @FXML
    private Button responseButton;
    private ClaimService claimDAO;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private Button ViewResponseButton;
    @FXML
    private PieChart claimPieChart;
    @FXML
    private Button analyzeButton;
    @FXML
    private Button downloadPDF;
    //private User currentUser = SessionManager.getInstance().getCurrentUser();
    //hedha baad ywali f blasit hedha user static || twali tekho f blasto current user
    private int userId = 19;
    private User user;
    private List<String> types = Arrays.asList("site", "service","reservation" );
    private List<String> status = Arrays.asList("en attente", "repondue");
    private List<reclamation> allReclamations;
    private int claimId;
    private UserDAO userDAO;
    private ObservableList<String> typeOptions;
    private int selectedClaimId;


    // Méthode pour récupérer l'ID de la réclamation sélectionnée
    public ViewClaimController() {
        claimDAO = new ClaimService();
        userDAO = new UserDAO();
    }
    private void initializeComboBoxes() {
        user = userDAO.getUserById(userId);
        typeOptions = FXCollections.observableArrayList(types);
        if (user.getRole() == Role.client) {
            addButton.setVisible(true);
            analyzeButton.setVisible(false);
        } else if (user.getRole() == Role.proprietaire) {
            addButton.setVisible(false);
            analyzeButton.setVisible(true);
        }
        ObservableList<String> statusOptions = FXCollections.observableArrayList(status);
        typeComboBox.setItems(typeOptions);
        statusComboBox.setItems(statusOptions);
        if (user.getRole() == Role.client) {
            responseButton.setVisible(false); // Hide the "Response" button for players
        } else if (user.getRole() == Role.proprietaire) {
            responseButton.setVisible(true);
        }
    }

    @FXML
    public void initialize() {
        allReclamations = new ArrayList<>();
        typeOptions = FXCollections.observableArrayList();
        // Assurez-vous d'initialiser vos ComboBoxes
        initializeComboBoxes();
        populateTableView(); // Appelez cette méthode pour remplir votre table avec les données
        typeComboBox.setOnAction(event -> populateTableView());
        statusComboBox.setOnAction(event -> populateTableView());

        // Assurez-vous que les colonnes sont correctement liées aux propriétés de vos objets de réclamation
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        satisfactionColumn.setCellValueFactory(new PropertyValueFactory<>("satisfaction"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));

        userColumn.setCellValueFactory(cellData -> {
            reclamation reclamation = cellData.getValue();
            String userName = reclamation.getUser().getFirstName(); // Assuming User class has getName() method
            return new SimpleStringProperty(userName);
        });
        // Gérez les actions des boutons dans la colonne des actions
        actionsColumn.setCellFactory(createActionCellFactory());

        claimTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                ViewResponseButton.setOnAction(event -> {
                    System.out.println(newSelection);
                    claimId = newSelection.getId();
                    SharedData.setClaimId(claimId);
                    System.out.println(claimId);
                    // Ouvrez la vue de réponse avec l'ID de la réclamation sélectionnée
                    openViewResponse(claimId);
                });
                responseButton.setOnAction(event -> {
                    claimId = newSelection.getId();
                    SharedData.setClaimId(claimId);
                    // Récupérez l'ID de la réclamation sélectionnée
                    // Ouvrez la vue de réponse avec l'ID de la réclamation sélectionnée
                    openViewClaim();
                });
            }
        });
        claimTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Mettez à jour l'ID de la réclamation sélectionnée
                selectedClaimId = newValue.getId();
                if (user.getRole() == Role.client) {
                    // Afficher le bouton "View Response" lorsque une réclamation est sélectionnée
                    ViewResponseButton.setVisible(true);
                } else {
                    ViewResponseButton.setVisible(false);
                }
            }
        });
        claimTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Mettez à jour l'ID de la réclamation sélectionnée
                selectedClaimId = newValue.getId();
                if (user.getRole() == Role.proprietaire) {
                    // Afficher le bouton "View Response" lorsque une réclamation est sélectionnée
                    responseButton.setVisible(true);
                } else {
                    responseButton.setVisible(false);
                }
            }
        });
        addButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewClaim/NewClaim.fxml"));
                Parent root = loader.load();
                // Mettre à jour le captcha


                Scene scene = new Scene(root) ;
                Stage stage = (Stage) addButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        typeComboBox.setOnAction(event -> {
            System.out.println("ComboBox clicked");
            // Add more code here if needed
        });
        System.out.println("Type Options: " + typeOptions);
    }



    @FXML
    private void resetFilters() {
        // Reset ComboBoxes
        typeComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        // Refresh TableView to display all claims
        populateTableView();
    }

    @FXML
    private void filterClaims() {
        String selectedType = typeComboBox.getValue();
        String selectedStatus = statusComboBox.getValue();
        List<reclamation> filteredReclamations;

        if (selectedType != null || selectedStatus != null) {
            claimTableView.getItems().clear(); // Clear previous results

            if (user.getRole().equals("player")) {
                // For player role
                if (selectedType != null && selectedStatus != null) {
                    filteredReclamations = claimDAO.findAllbyStatusFieldTypeFieldPlayer(selectedStatus, selectedType,user.getId());
                } else if (selectedType != null) {
                    filteredReclamations = claimDAO.findAllbyTypeFieldPlayer(selectedType, user.getId());
                } else if (selectedStatus != null) {
                    filteredReclamations = claimDAO.findAllbyStatusFieldPlayer(selectedStatus, user.getId());
                }
                else
                {
                    filteredReclamations = claimDAO.findAllbyUser( user.getId());
                }

            } else if (user.getRole().equals("fieldOwner")) {
                // For field owner role
                if (selectedStatus != null) {
                    filteredReclamations = claimDAO.findAllbyStatusFieldOwner(selectedStatus);
                } else  {
                    filteredReclamations = claimDAO.findAllbyFieldOwner();
                }
            } else {
                // For admin role
                if (selectedType != null && selectedStatus != null) {
                    filteredReclamations = claimDAO.findAllbystatusAndType(selectedStatus, selectedType);
                } else if (selectedType != null) {
                    filteredReclamations = claimDAO.findAllbyType(selectedType);
                } else if (selectedStatus !=null){
                    filteredReclamations = claimDAO.findAllbyStatusAdmin(selectedStatus);
                }
                else
                    filteredReclamations = claimDAO.findAllbyTypeAdmin();
            }

            claimTableView.getItems().addAll(filteredReclamations);
        } else {
            // No filters selected, show all claims
            claimTableView.getItems().addAll(allReclamations);
        }
    }


        private void populateTableView() {
            claimTableView.getItems().clear(); // Clear the table view
            if (user.getRole() == Role.client) {
                allReclamations = claimDAO.findAllbyUser(user.getId()); // Filter claims by UserId for players
            } else {
                allReclamations = claimDAO.findAll();
            }
            // Neither ComboBox is selected, show all claims
            claimTableView.getItems().addAll(allReclamations);
        }
    private void openViewResponse(int claimId) {

        try {
            System.out.println(claimId);
            // Chargez la vue de réponse associée à la réclamation ici
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewResponse/ViewResponse.fxml"));
            Parent root = loader.load();

            // Si nécessaire, passez l'identifiant de la réclamation au contrôleur de vue de la réponse


            // Affichez la vue dans une nouvelle fenêtre ou comme vous le souhaitez dans votre application
            Scene scene = new Scene(root);
            Stage stage = (Stage) ViewResponseButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Callback<TableColumn<reclamation, Void>, TableCell<reclamation, Void>> createActionCellFactory() {
        return new Callback<TableColumn<reclamation, Void>, TableCell<reclamation, Void>>() {
            @Override
            public TableCell<reclamation, Void> call(TableColumn<reclamation, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");

                    {
                        editButton.setOnAction(event -> {
                            reclamation reclamation = getTableView().getItems().get(getIndex());
                            editClaim(reclamation);
                        });

                        deleteButton.setOnAction(event -> {
                            reclamation reclamation = getTableView().getItems().get(getIndex());
                            deleteClaim(reclamation);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            if (user.getRole() == Role.client) {
                                editButton.setVisible(true);
                            } else {
                                editButton.setVisible(false);
                            }
                            setGraphic(new HBox(editButton, deleteButton));
                        }

                    }

                };
            }
        };
    }
    private void deleteClaim(reclamation reclamation) {
        boolean isDeleted = claimDAO.delete(reclamation);
        if (isDeleted) {
            claimTableView.getItems().remove(reclamation);
            showAlert("Success", "Claim deleted successfully.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Failed to delete claim.", Alert.AlertType.ERROR);
        }
    }

    private void editClaim(reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewClaim/NewClaim.fxml"));
            Parent root = loader.load();
            NewClaimController newClaimController = loader.getController();
            newClaimController.populateFieldsWithClaim(reclamation.getId());
            Scene scene = new Scene(root, 1320.0, 660.0);
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openViewClaim() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Response/Response.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) responseButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void analyzeClaimsByType() {
        // Collecte des données pour l'analyse
        int siteCount = 0;
        int serviceCount = 0;
        int reservationCount = 0;

        for (reclamation reclamation : allReclamations) {
            String type = reclamation.getType();
            if (type.equalsIgnoreCase("site")) {
                siteCount++;
            } else if (type.equalsIgnoreCase("service")) {
                serviceCount++;
            } else if (type.equalsIgnoreCase("reservation")) {
                reservationCount++;
            }
        }

        // Création du PieChart
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Site", siteCount),
                        new PieChart.Data("services", serviceCount),
                        new PieChart.Data("Reservation", reservationCount)
                );

        // Affichage du PieChart dans une nouvelle fenêtre
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Claims by Type");
        Stage stage = new Stage();
        Scene scene = new Scene(pieChart);
        stage.setScene(scene);
        stage.show();
    }
    /*
    @FXML
    private void downloadPDF() {
        Claim selectedClaim = claimTableView.getSelectionModel().getSelectedItem();
        if (selectedClaim != null) {
            // Créez un sélecteur de fichiers pour choisir l'emplacement de sauvegarde du PDF
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                // Générez le PDF et enregistrez-le au chemin spécifié
                try {
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(file));
                    document.open();

                    // Mise en forme du titre "Claim Details"
                    Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
                    Paragraph titleParagraph = new Paragraph("Détails du réclamation", titleFont);
                    titleParagraph.setAlignment(Element.ALIGN_CENTER);
                    titleParagraph.setSpacingAfter(20); // Espacement après le titre
                    document.add(titleParagraph);

                    // Ajout des détails de la réclamation
                    Font detailFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
                    addDetailToPDF(document, "Date", selectedClaim.getDate().toString(), detailFont);
                    addDetailToPDF(document, "Description", selectedClaim.getDescription(), detailFont);
                    addDetailToPDF(document, "Type", selectedClaim.getType(), detailFont);
                    addDetailToPDF(document, "Status", selectedClaim.getStatus(), detailFont);
                    addDetailToPDF(document, "Satisfaction", String.valueOf(selectedClaim.getSatisfaction()), detailFont);
                    // Ajoutez d'autres détails au besoin



                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    // Gérer les erreurs d'écriture du fichier PDF
                } catch (Exception e) {
                    e.printStackTrace();
                    // Gérer les autres exceptions lors de la génération du PDF
                }
            }
        } else {
            // Aucune réclamation sélectionnée, affichez un message d'avertissement ou gérez-le selon vos besoins
        }
    }
    */

    private void addDetailToPDF(Document document, String label, String value, Font font) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        paragraph.setFont(font);
        paragraph.add(new Chunk(label + ": ", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, BaseColor.BLUE)));
        paragraph.add(new Chunk(value, FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK)));
        document.add(paragraph);
    }


    @FXML
    private void downloadPDF() {
        reclamation selectedReclamation = claimTableView.getSelectionModel().getSelectedItem();
        if (selectedReclamation != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                try {
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(file));
                    document.open();

                    // Mise en forme du titre "Claim Details"
                    Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
                    Paragraph titleParagraph = new Paragraph("Détails du réclamation", titleFont);
                    titleParagraph.setAlignment(Element.ALIGN_CENTER);
                    titleParagraph.setSpacingAfter(20); // Spacing after the title
                    document.add(titleParagraph);

// Ajout des détails de la réclamation
                    Font detailFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
                    addDetailToPDF(document, "Date", selectedReclamation.getDate().toString(), detailFont);
                    addDetailToPDF(document, "Description", selectedReclamation.getDescription(), detailFont);
                    addDetailToPDF(document, "Type", selectedReclamation.getType(), detailFont);
                    addDetailToPDF(document, "Status", selectedReclamation.getStatus(), detailFont);
                    addDetailToPDF(document, "Satisfaction", String.valueOf(selectedReclamation.getSatisfaction()), detailFont);
// Add other details as needed

// Add spacing before the image
                    document.add(new Paragraph("\n\n\n"));

                    // Add claim image to PDF
                    if (selectedReclamation.getImage() != null && !selectedReclamation.getImage().isEmpty()) {
                        addImageToPDF(document, selectedReclamation.getImage());
                    }

                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Handle case where no claim is selected
        }
    }

    private void addImageToPDF(Document document, String imagePath) throws IOException, DocumentException {
        // Create a new paragraph for the image
        Paragraph imageParagraph = new Paragraph();
        // Add spacing before the image paragraph
        imageParagraph.setSpacingBefore(20);
        // Center the image
        imageParagraph.setAlignment(Element.ALIGN_CENTER);

        // Load the image from the file path
        com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(imagePath);
        // Set the scale of the image to fit the document
        image.scaleToFit(200, 200); // Adjust size here
        // Add the image to the paragraph
        imageParagraph.add(new Chunk(image, 0, 0));

        // Add the image paragraph to the document
        document.add(imageParagraph);
    }



}

