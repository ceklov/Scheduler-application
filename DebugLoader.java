package scheduler;

import java.sql.SQLException;
import scheduler.DAO.UserDAO;

public class DebugLoader {
    
    public static void loadDebugUser() throws SQLException {   
        
        //CE: this class ended up much smaller than expected because I found adding information automatically through pre-loaded code more burdensome than simply taking the extra few seconds to manually add a user and an appointment
        
        try {
            UserDAO.insertTestUser();
        } catch (SQLException e) {
            System.out.println("Could not load test user");
            e.printStackTrace();
        }
    }

}
