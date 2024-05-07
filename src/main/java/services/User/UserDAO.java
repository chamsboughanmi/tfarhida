package services.User;

import entities.*;
import entities.User;
import database.Connexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDAO {

    private Connection con;
    private Statement ste;
    private PreparedStatement pst;
    public UserDAO() {
        con = Connexion.getInstance();
    }

    public boolean deleteUserById(int userId) {
        // Validate input: Ensure userId is valid
        if (userId <= 0) {
            System.out.println("Invalid user ID.");
            return false;
        }

        // Check user existence
        if (getUserById(userId).getId()!= userId) {
            System.out.println("User with ID " + userId + " does not exist.");
            return false;
        }

        String sql = "DELETE FROM user WHERE id = ?";
        try {
            pst = con.prepareStatement(sql);
            pst.setInt(1, userId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("User with ID " + userId + " deleted successfully.");
                return true;
            } else {
                System.out.println("Failed to delete user with ID " + userId + ".");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserById(int userId) {
        User user = null;
        String sql = "SELECT * FROM user WHERE id = ?";
        try {
            pst = con.prepareStatement(sql);
            pst.setInt(1,userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                // Assuming role is stored as string in the database and mapped to an enum in the User class
                user.setRole(Role.valueOf(rs.getString("role")));
                user.setImage(rs.getString("image"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();

        String sql = "SELECT * FROM user";
        try {
            pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(Role.valueOf(rs.getString("role")));
                user.setImage(rs.getString("image"));
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }


    public User getUserByEmail(String email) {
        User user = null;
        String sql = "SELECT * FROM user WHERE email = ?";
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password")); // Assuming password is stored hashed
                // Assuming role is stored as string in the database and mapped to an enum in the User class
                user.setRole(Role.valueOf(rs.getString("role")));
                user.setImage(rs.getString("image"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}

