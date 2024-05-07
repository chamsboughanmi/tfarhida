package services.Claim;
import database.Connexion;
import entities.*;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import services.User.UserDAO;


public class ClaimService {
    UserDAO usrdao = new UserDAO();
    ClaimResponseService claimResponseDAO = new ClaimResponseService();

    public reclamation findById(int id) {
        Connection cnx = Connexion.getInstance();
        PreparedStatement pstmt = null;
        reclamation c = null;

        try {
            pstmt = cnx.prepareStatement("select * from claim where id=?");
            pstmt.setInt(1, id);
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                User u1 = usrdao.getUserById(res.getInt(7));

                c = new reclamation(res.getInt(1), u1, res.getString(3), res.getDate(2), res.getString(5), res.getString(4), res.getString(8), res.getString(6));
            }
        } catch (Exception e) {
            System.out.println("La requête n'a pas pu être exécutée" + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }

    public boolean update(reclamation c) {
        if (c == null)
            return false;
        Connection cnx = Connexion.getInstance();
        int n = 0;
        PreparedStatement pstmt = null;
        try {
            pstmt = cnx.prepareStatement(
                    "update claim set idUser=?, description=?, date=?, status=?, Type=?, satisfaction=?, image=? where id=?");
            pstmt.setInt(1, c.getUser().getId());
            pstmt.setString(2, c.getDescription());
            pstmt.setDate(3, c.getDate());
            pstmt.setString(4, c.getStatus());
            pstmt.setString(5, c.getType());
            pstmt.setString(6, c.getSatisfaction());
            pstmt.setString(7, c.getImage());
            pstmt.setInt(8, c.getId());

            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("Mise à jour du claim terminée avec succès");
                return true;
            } else
                System.out.println("Aucun claim n'a été mis à jour");
        } catch (SQLException e) {
            System.out.println("La mise à jour du claim " + c.getId() + " a échoué");
        }
        return false;
    }

    public boolean addWithAutoResponse(reclamation c) {
        if (c == null)
            return false;

        Connection cnx = Connexion.getInstance();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Ajout de la Claim
            pstmt = cnx.prepareStatement(
                    "INSERT INTO claim(idUser, description, date, status, type, satisfaction,image) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, c.getUser().getId());
            pstmt.setString(2, c.getDescription());
            pstmt.setDate(3, c.getDate());
            pstmt.setString(4, c.getStatus()); // Mettre le statut initial de la réclamation
            pstmt.setString(5, c.getType());
            pstmt.setString(6, c.getSatisfaction());
            pstmt.setString(7, c.getImage());

            // Ajout de la réclamation
            int n = pstmt.executeUpdate();

// Récupération de l'ID généré
            if (n == 1) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int lastInsertedClaimId = rs.getInt(1);
                    System.out.println("eeee" + lastInsertedClaimId);
                    reclamation c1 = findById(lastInsertedClaimId);
                    System.out.println(c1);
                    // Création de la réponse à la réclamation
                    ReponseReclamation reponseReclamation = new ReponseReclamation(c1, c1.getUser(), null, null);

                    // Envoi de l'e-mail de confirmation de réclamation
                    String userEmail = c1.getUser().getEmail();  // Get email from user object
                    String claimDescription = c1.getDescription();
                    EmailSender.sendEmail(userEmail, "Confirmation de réclamation",
                            "Votre réclamation a été enregistrée avec succès.\n\n" +
                                    "Description : " + claimDescription + "\n\n" +
                                    "Nous traiterons votre réclamation dans les plus brefs délais. Merci.");



                    //System.out.println(claimResponse);
                   // System.out.println("dddddddddddddddddddd" + c1.getId());
                    // Ajout de la réponse à la réclamation
                    claimResponseDAO.add(reponseReclamation);


                    System.out.println("Insertion du claim avec réponse automatique terminée avec succès");
                    return true;
                }
            }

        } catch (SQLException e1) {
            System.out.println("L'ajout du claim a échoué : " + e1.getMessage());
        } finally {
            // Fermer les ressources
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Méthode pour mettre à jour le statut de la réclamation
    private void updateClaimStatus(int claimId, String newStatus) {
        Connection cnx = Connexion.getInstance();
        PreparedStatement pstmt = null;

        try {
            pstmt = cnx.prepareStatement("UPDATE claim SET status = ? WHERE id = ?");
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, claimId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du statut de la réclamation : " + e.getMessage());
        } finally {
            // Fermer les ressources
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    // Méthode pour envoyer l'e-mail de confirmation de la réclamation
    private void sendClaimConfirmationEmail(String userEmail, String claimDescription) {
        String subject = "Confirmation de réclamation";
        String messageBody = "Votre réclamation a été enregistrée avec succès. Voici les détails de votre réclamation :\n\n"
                + "Description : " + claimDescription + "\n\n"
                + "Nous traiterons votre réclamation dans les plus brefs délais. Merci.";

        EmailSender.sendEmail(userEmail, subject, messageBody);
    }


    public boolean delete(reclamation c) {
        if (c == null)
            return false;
        Connection cnx = Connexion.getInstance();
        PreparedStatement pstmt = null;
        int n = 0;
        try {
            pstmt = cnx.prepareStatement("delete from Claim  where id=?");
            pstmt.setInt(1, c.getId());
            n = pstmt.executeUpdate();
            pstmt.close();
            if (n == 1) {
                System.out.println("Suppression logique du Claim terminée avec succès");
                return true;
            } else {
                System.out.println("Aucun Claim n'a été supprimé");
                return false;
            }

        } catch (Exception e) {
            System.out.println("La requête n'a pas pu être exécutée");
            e.printStackTrace();
            return false;
        }
    }
    public List<reclamation> findAll() {
        Connection cnx = Connexion.getInstance();
        List<reclamation> c = new ArrayList<>();


        try {
            PreparedStatement pstmt = cnx.prepareStatement("select * from claim");
            ResultSet res = pstmt.executeQuery();


            while (res.next()) {
                c.add(new reclamation(res.getInt(1), usrdao.getUserById(res.getInt(7)), res.getString(3), res.getDate(2), res.getString(5), res.getString(4), res.getString(8), res.getString(6)));

            }
        } catch (Exception e) {
            System.out.println("La requête n'a pas pu être exécutée" + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }

    public List<reclamation> findAllbyStatueandType(String type, String status) {
        Connection cnx = Connexion.getInstance();
        List<reclamation> c = new ArrayList<>();
        String sql = null;
        if (type.equals("reservation"))
            sql = "SELECT * FROM claim WHERE status=? AND type=?";
        else
            sql = "SELECT * FROM claim WHERE status=? AND type <> ?";

        try {
            PreparedStatement pstmt = cnx.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setString(2, "reservation");
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                c.add(new reclamation(res.getInt(1), usrdao.getUserById(res.getInt(7)), res.getString(3), res.getDate(2), res.getString(5), res.getString(4), res.getString(8), res.getString(6)));

            }
        } catch (Exception e) {
            System.out.println("La requête n'a pas pu être exécutée" + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }

    public List<reclamation> findAllbyUser(int id) {
        Connection cnx = Connexion.getInstance();
        List<reclamation> c = new ArrayList<>();
        String sql = "SELECT * FROM claim WHERE idUser=?";
        try {
            PreparedStatement pstmt = cnx.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet res = pstmt.executeQuery();
            while (res.next()) {
                c.add(new reclamation(res.getInt(1), usrdao.getUserById(id), res.getString(3), res.getDate(2), res.getString(5), res.getString(4), res.getString(8), res.getString(6)));

            }
        } catch (Exception e) {
            System.out.println("La requête n'a pas pu être exécutée" + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }
    public List<reclamation> findAllbyTypeFieldPlayer(String type , int id) {
        Connection cnx = Connexion.getInstance();
        List<reclamation> c = new ArrayList<>();


        try {
            PreparedStatement pstmt = cnx.prepareStatement("select * from claim where type=? and idUser = ?");
            pstmt.setString(1, type);
            pstmt.setInt(2,id);
            ResultSet res = pstmt.executeQuery();


            while (res.next()) {
                c.add(new reclamation(res.getInt(1), usrdao.getUserById(res.getInt(7)), res.getString(3), res.getDate(2), res.getString(5), res.getString(4), res.getString(8), res.getString(6)));

            }
        } catch (Exception e) {
            System.out.println("La requête n'a pas pu être exécutée" + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }
    public List<reclamation> findAllbyStatusFieldPlayer(String status , int id) {
        Connection cnx = Connexion.getInstance();
        List<reclamation> c = new ArrayList<>();


        try {
            PreparedStatement pstmt = cnx.prepareStatement("select * from claim where status=? and idUser = ?");
            pstmt.setString(1, status);
            pstmt.setInt(2,id);
            ResultSet res = pstmt.executeQuery();


            while (res.next()) {
                c.add(new reclamation(res.getInt(1), usrdao.getUserById(res.getInt(7)), res.getString(3), res.getDate(2), res.getString(5), res.getString(4), res.getString(8), res.getString(6)));

            }
        } catch (Exception e) {
            System.out.println("La requête n'a pas pu être exécutée" + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }

    public List<reclamation> findAllbyStatusFieldTypeFieldPlayer(String status , String type , int id) {
        Connection cnx = Connexion.getInstance();
        List<reclamation> c = new ArrayList<>();


        try {
            PreparedStatement pstmt = cnx.prepareStatement("select * from claim where status=? and type=? and idUser = ?");
            pstmt.setString(1, status);
            pstmt.setString(2, type);
            pstmt.setInt(3,id);
            ResultSet res = pstmt.executeQuery();


            while (res.next()) {
                c.add(new reclamation(res.getInt(1), usrdao.getUserById(res.getInt(7)), res.getString(3), res.getDate(2), res.getString(5), res.getString(4), res.getString(8), res.getString(6)));

            }
        } catch (Exception e) {
            System.out.println("La requête n'a pas pu être exécutée" + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }
    public List<reclamation> findAllbyTypeAdmin() {
        Connection cnx = Connexion.getInstance();
        List<reclamation> c = new ArrayList<>();


        try {
            PreparedStatement pstmt = cnx.prepareStatement("select * from claim where type <> ?");
            pstmt.setString(1, "reservation");
            ResultSet res = pstmt.executeQuery();


            while (res.next()) {
                c.add(new reclamation(res.getInt(1), usrdao.getUserById(res.getInt(7)), res.getString(3), res.getDate(2), res.getString(5), res.getString(4), res.getString(8), res.getString(6)));

            }
        } catch (Exception e) {
            System.out.println("La requête n'a pas pu être exécutée" + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }
    public List<reclamation> findAllbyStatusAdmin(String status) {
        Connection cnx = Connexion.getInstance();
        List<reclamation> c = new ArrayList<>();


        try {
            PreparedStatement pstmt = cnx.prepareStatement("select * from claim where type <> ? and status = ?");
            pstmt.setString(1, "reservation");
            pstmt.setString(2,status);
            ResultSet res = pstmt.executeQuery();


            while (res.next()) {
                c.add(new reclamation(res.getInt(1), usrdao.getUserById(res.getInt(7)), res.getString(3), res.getDate(2), res.getString(5), res.getString(4), res.getString(8), res.getString(6)));

            }
        } catch (Exception e) {
            System.out.println("La requête n'a pas pu être exécutée" + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }
    public List<reclamation> findAllbystatusAndType(String status, String type) {
        Connection cnx = Connexion.getInstance();
        List<reclamation> c = new ArrayList<>();


        try {
            PreparedStatement pstmt = cnx.prepareStatement("select * from claim where type= ?and status = ?");
            pstmt.setString(1, type);
            pstmt.setString(2,status);
            ResultSet res = pstmt.executeQuery();


            while (res.next()) {
                c.add(new reclamation(res.getInt(1), usrdao.getUserById(res.getInt(7)), res.getString(3), res.getDate(2), res.getString(5), res.getString(4), res.getString(8), res.getString(6)));

            }
        } catch (Exception e) {
            System.out.println("La requête n'a pas pu être exécutée" + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }
    public List<reclamation> findAllbyType(String type) {
        Connection cnx = Connexion.getInstance();
        List<reclamation> c = new ArrayList<>();


        try {
            PreparedStatement pstmt = cnx.prepareStatement("select * from claim where type= ?");
            pstmt.setString(1, type);
            ResultSet res = pstmt.executeQuery();


            while (res.next()) {
                c.add(new reclamation(res.getInt(1)
                        , usrdao.getUserById(res.getInt(7)), res.getString(3), res.getDate(2), res.getString(5), res.getString(4), res.getString(8), res.getString(6)));

            }
        } catch (Exception e) {
            System.out.println("La requête n'a pas pu être exécutée" + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }
    public List<reclamation> findAllbyStatusFieldOwner(String status ) {
        Connection cnx = Connexion.getInstance();
        List<reclamation> c = new ArrayList<>();


        try {
            PreparedStatement pstmt = cnx.prepareStatement("select * from claim where status=? and type = ?");
            pstmt.setString(1, status);
            pstmt.setString(2,"reservation");
            ResultSet res = pstmt.executeQuery();


            while (res.next()) {
                c.add(new reclamation(res.getInt(1), usrdao.getUserById(res.getInt(7)), res.getString(3), res.getDate(2), res.getString(5), res.getString(4), res.getString(8), res.getString(6)));

            }
        } catch (Exception e) {
            System.out.println("La requête n'a pas pu être exécutée" + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }
    public List<reclamation> findAllbyFieldOwner() {
        Connection cnx = Connexion.getInstance();
        List<reclamation> c = new ArrayList<>();


        try {
            PreparedStatement pstmt = cnx.prepareStatement("select * from claim where type = ?");
            pstmt.setString(1,"reservation");
            ResultSet res = pstmt.executeQuery();


            while (res.next()) {
                c.add(new reclamation(res.getInt(1), usrdao.getUserById(res.getInt(7)), res.getString(3), res.getDate(2), res.getString(5), res.getString(4), res.getString(8), res.getString(6)));

            }
        } catch (Exception e) {
            System.out.println("La requête n'a pas pu être exécutée" + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }


    public void updateClaimSatisfaction(int claimId, String sat) {
        Connection cnx = Connexion.getInstance();
        PreparedStatement pstmt = null;

        try {
            pstmt = cnx.prepareStatement("UPDATE claim SET satisfaction = ? WHERE id = ?");
            pstmt.setString(1, sat);
            pstmt.setInt(2, claimId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du statut de la réclamation : " + e.getMessage());
        } finally {
            // Fermer les ressources
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Méthode pour envoyer l'e-mail de confirmation de la réclamation



}


