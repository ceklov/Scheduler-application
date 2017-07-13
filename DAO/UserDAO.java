package scheduler.DAO;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import scheduler.DBHandler;
import scheduler.DateHandler;

public class UserDAO {
    
     private static SimpleDateFormat mysqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         
     public static boolean validateUser(String userName, String userPassword) throws SQLException {
        boolean validated = false;
        String sql = "SELECT userName, password FROM user WHERE userName = ?";

        try (Connection conn = DBHandler.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql) ) {

            ps.setString(1, userName);
            try(ResultSet resultSet = ps.executeQuery();) {

                while (resultSet.next()) {
                    String foundUserName = resultSet.getString("userName");
                    String foundUserPassword = resultSet.getString("password");

                    if (foundUserName.equals(userName) && foundUserPassword.equals(userPassword)) {
                        validated = true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Problem in validateUser()");
        }
        return validated;           
     }
     
     public static void insertTestUser() throws SQLException {
       String sql = "INSERT INTO user VALUES (1, 'test', 'test', 1, 'Cody Eklov', ?, ?, 'Cody Eklov')";

        try (Connection conn = DBHandler.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql) ) {

            LocalDateTime createDate = LocalDateTime.now();
            String fCreateDate = DateHandler.format(createDate);
            ps.setString(1, fCreateDate);
            ps.setString(2, fCreateDate);

            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Problem in insertTestUser()");
        }
     }
    
}
