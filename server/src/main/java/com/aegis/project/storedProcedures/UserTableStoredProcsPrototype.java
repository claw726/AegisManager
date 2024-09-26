package com.aegis.project.storedProcedures;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserTableStoredProcsPrototype{
    public static void main(String[] args){
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:server/src/main/java/com/aegis/project/databases/Users.db");
            try {
                GetNumAccounts(conn);
            } catch (SQLException e) {
                CreateUserDatabase(conn);
            }

            String username = "test";
            String email = "test@gmail.com";
            String pwhash = "Password1234!";
            InsertUser(conn, username, email, pwhash);
            PrintUserDatabase(conn);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void CreateUserDatabase(Connection conn) throws SQLException {
        String query = "CREATE TABLE users "
                        + "(UserID INTEGER PRIMARY KEY NOT NULL,"
                        + "UserName TEXT NO NULL,"
                        + "Email TEXT NOT NULL,"
                        + "OrgIDTableName TEXT NOT NULL,"
                        + "PWHash TEXT NOT NULL,"
                        + "TwoFactorAuthInfo TEXT NOT NULL,"
                        + "PasswordResetToken TEXT NOT NULL,"
                        + "IsLoggedIn INTEGER NOT NULL);";
        Statement statement = conn.createStatement();
        statement.executeUpdate(query);
    }

    public static void PrintUserDatabase(Connection conn) throws SQLException {
        String query = "SELECT * FROM users;";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            int userID = resultSet.getInt("UserID");
            String username = resultSet.getString("UserName");
            String email = resultSet.getString("Email");
            System.out.println("userID: " + userID + ", username: " + username + ", email: " + email);
        }
    }

    private static int GetNumAccounts(Connection conn) throws SQLException {
        String query = "SELECT COUNT() AS total FROM users;";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet.getInt("total");
    }

    public static void InsertUser(Connection conn, String username, String email, String pwhash) throws SQLException {
        if (!IsEmailUsed(conn, email)) {
            int newIndex = GetNumAccounts(conn) + 1;
            String orgTableName = String.format("%s_%d_OrgTable", username, newIndex);
            String query = String.format("INSERT INTO users (UserID, UserName, Email, OrgIDTableName, "
                            + "PWHash, TwoFactorAuthInfo, PasswordResetToken, IsLoggedIn) VALUES"
                            + "(%d, '%s', '%s', '%s', '%s', '%s', '%s', %d);",
                            newIndex, username, email, orgTableName, pwhash, "Placeholder 2FA", "Placeholder Token", 0);
            Statement statement = conn.createStatement();
            statement.executeUpdate(query);
        }
    }

    private static boolean IsEmailUsed(Connection conn, String email) throws SQLException {
        String query = String.format("SELECT 1 FROM users WHERE Email = '%s' LIMIT 1", email);
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet.next();
    }

    public static void DropTable(Connection conn) throws SQLException {
        String query = "DROP TABLE IF EXISTS users";
        Statement statement = conn.createStatement();
        statement.executeUpdate(query);
    }
}