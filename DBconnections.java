package db.bank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

    public class DBconnections {
        public static void main(String[] args) {
            String url = "jdbc:mysql://localhost:3306/bankingdb"; // database name
            String user = "root"; // your MySQL username
            String password = "Ashraf@2005"; // your MySQL password

            try {
                Connection conn = DriverManager.getConnection(url, user, password);
                System.out.println("✅ Connected successfully!");
            } catch (SQLException e) {
                System.out.println("❌ Connection failed!");
                e.printStackTrace();
            }
        }
    }


