package services.Claim;

import database.Connexion;
import entities.reclamation;
import entities.ReponseReclamation;
import entities.User;
import services.User.UserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClaimResponseService {

    public boolean add(ReponseReclamation fieldownerResponse) {
        if (fieldownerResponse == null)
            return false;
        Connection connection = Connexion.getInstance();
        int rowsAffected = 0;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO claimresponse(idClaim, idUser, description, closureDate) VALUES (?, ?, ?, ?)");
            preparedStatement.setInt(1, fieldownerResponse.getClaim().getId());
            preparedStatement.setInt(2, fieldownerResponse.getUser().getId());
            preparedStatement.setString(3, fieldownerResponse.getDescription());
            preparedStatement.setDate(4, fieldownerResponse.getClosureDate());

            rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("Insertion de la réponse Fieldowner terminée avec succès");

                return true;
            } else {
                System.out.println("Aucune réponse Fieldowner n'a été insérée");
            }

        } catch (SQLException e) {
            System.out.println("L'ajout de la réponse Fieldowner a échoué : " + e.getMessage());
        }
        return false;
    }



    public boolean update(ReponseReclamation fieldownerResponse) {
        if (fieldownerResponse == null)
            return false;
        Connection connection = Connexion.getInstance();
        int rowsAffected = 0;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "UPDATE claimresponse SET idUser = ?, description = ?, closureDate = ? WHERE idClaim = ?");
            preparedStatement.setInt(1, fieldownerResponse.getUser().getId());
            preparedStatement.setString(2, fieldownerResponse.getDescription());
            preparedStatement.setDate(3, fieldownerResponse.getClosureDate());
            preparedStatement.setInt(4, fieldownerResponse.getClaim().getId());

            rowsAffected = preparedStatement.executeUpdate();
            preparedStatement.close();
            if (rowsAffected == 1) {
                System.out.println("Mise à jour de la réponse Fieldowner terminée avec succès");

                // Mettre à jour le statut de la réclamation associée si nécessaire
                ClaimService claimDAO = new ClaimService();
                reclamation relatedReclamation = claimDAO.findById(fieldownerResponse.getClaim().getId());
                System.out.println(relatedReclamation);
                if (relatedReclamation != null && relatedReclamation.getStatus().equals("en attente")) {
                    relatedReclamation.setStatus("repondue");
                    claimDAO.update(relatedReclamation); // Mettre à jour la réclamation dans la base de données
                    System.out.println("Statut de la réclamation mis à jour avec succès");
                }
                return true;
            } else {
                System.out.println("Aucune réponse Fieldowner n'a été mise à jour");
            }

        } catch (SQLException e) {
            System.out.println("La mise à jour de la réponse Fieldowner a échoué : " + e.getMessage());
        }
        return false;
    }



    public boolean delete(int idClaim) {
        Connection connection = Connexion.getInstance();
        int rowsAffected = 0;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "DELETE FROM fieldownerresponse WHERE idClaim = ?");
            preparedStatement.setInt(1, idClaim);

            rowsAffected = preparedStatement.executeUpdate();
            preparedStatement.close();
            if (rowsAffected == 1) {
                System.out.println("Suppression de la réponse Fieldowner terminée avec succès");
                return true;
            } else {
                System.out.println("Aucune réponse Fieldowner n'a été supprimée");
            }

        } catch (SQLException e) {
            System.out.println("La suppression de la réponse Fieldowner a échoué : " + e.getMessage());
        }
        return false;
    }

    public ReponseReclamation findById(int idClaim) {
        Connection connection = Connexion.getInstance();
        PreparedStatement preparedStatement = null;
        ReponseReclamation fieldownerResponse = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM claimresponse WHERE idClaim = ?");
            preparedStatement.setInt(1, idClaim);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {

                ClaimService cdao1=new ClaimService();
                UserDAO usdao1=new UserDAO();
               reclamation reclamation =cdao1.findById(resultSet.getInt(1));
               User user=usdao1.getUserById(resultSet.getInt(2));

                fieldownerResponse = new ReponseReclamation(reclamation,user,resultSet.getString(3),resultSet.getDate(4));

            }
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("La recherche de la réponse Fieldowner a échoué : " + e.getMessage());
        }
        return fieldownerResponse;
    }


    public ReponseReclamation findResponseByClaimId(int claimId) {
       ReponseReclamation responses=new ReponseReclamation();
        Connection connection = Connexion.getInstance();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM claimresponse WHERE idClaim = ?");
            preparedStatement.setInt(1, claimId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                responses = new ReponseReclamation();
                ClaimService claimDAO = new ClaimService();
                UserDAO userDAO = new UserDAO();
                reclamation reclamation = claimDAO.findById(resultSet.getInt("idClaim"));
                User user = userDAO.getUserById(resultSet.getInt("idUser"));
                responses.setClaim(reclamation);
                responses.setUser(user);
                responses.setDescription(resultSet.getString("description"));
                responses.setClosureDate(resultSet.getDate("closureDate"));

            }
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("La recherche des réponses par ID de réclamation a échoué : " + e.getMessage());
        }
        return responses;
    }
}
