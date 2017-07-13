package scheduler;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHandler {
    
     private static Connection conn;
     private static DBHandler dbHandler;
     private static final String DRIVER = "com.mysql.jdbc.Driver";
     private static final String DB = "U04Dfe";
     private static final String URL = "jdbc:mysql://52.206.157.109/" + DB;
     private static final String USER = "U04Dfe";
     private static final String PASS = "53688206675";
     
     public static Connection getConnection() {
         try {
             Class.forName(DRIVER);
             conn = (Connection) DriverManager.getConnection(URL, USER, PASS);
             System.out.println("Connection established: " + conn);
         } catch (ClassNotFoundException | SQLException e) {
             if (e instanceof SQLException) {
                System.out.println("SQLException: " + e.getMessage());
             } else {
                System.out.println("ClassNotFoundException: Could not find driver (" + DRIVER + ")");
            }        
         }         
         return conn;
     }
 
   
}
