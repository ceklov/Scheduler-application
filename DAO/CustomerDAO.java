package scheduler.DAO;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import scheduler.DBHandler;
import scheduler.DateHandler;
import scheduler.Scheduler;
import scheduler.model.Customer;

public class CustomerDAO {
    
    public static String getCustomerName(int customerID) throws SQLException {
        String customerName = null;
        try (Connection conn = DBHandler.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT customerName FROM customer WHERE customerId = ?") ) {
            
            ps.setString(1, String.valueOf(customerID));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    customerName = rs.getString("customerName");
                }
            }
        } catch (Exception e) {
            System.out.println("Problem in getCustomerName()");
        }
        return customerName;
    }
    
    public static Customer getCustomer(int customerID) throws SQLException {
        /*CE: as you can see, this method is burdensome due to the poor design of the database.
        * Keep in mind that because of how I have added customers to the database, customerId, addressId, cityId, and countryId are all identical for a given customer
        */
        
        Customer customer = new Customer();
        customer.setCustomerID(customerID);
        
        try (Connection conn = DBHandler.getConnection();
            PreparedStatement customerPS = conn.prepareStatement("SELECT customerName FROM customer WHERE customerId = ?"); 
            PreparedStatement addressPS = conn.prepareStatement("SELECT address, postalCode, phone FROM address WHERE addressId = ?");
            PreparedStatement cityPS = conn.prepareStatement("SELECT city FROM city WHERE cityId = ?");
            PreparedStatement countryPS = conn.prepareStatement("SELECT country FROM country WHERE countryId = ?") ) {
            
            customerPS.setString(1, String.valueOf(customerID));
            addressPS.setString(1, String.valueOf(customerID));
            cityPS.setString(1, String.valueOf(customerID));
            countryPS.setString(1, String.valueOf(customerID));
        
            try (ResultSet rs = customerPS.executeQuery() ) {
                 while (rs.next()) {
                    customer.setCustomerName(rs.getString("customerName"));
                 }
            }
            try (ResultSet rs = addressPS.executeQuery()) {
                while (rs.next()) {
                    customer.setCustomerAddress(rs.getString("address"));
                    customer.setCustomerZip(rs.getString("postalCode"));
                    customer.setCustomerPhone(rs.getString("phone"));
                }
            }
            try (ResultSet rs = cityPS.executeQuery(); ) {
                while (rs.next()) {
                    customer.setCustomerCityState(rs.getString("city"));
                }
            }   
            try (ResultSet rs = countryPS.executeQuery(); ) {
                while (rs.next()) {
                    customer.setCustomerCountry(rs.getString("country"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not find customer");
        }
        return customer;
    }
    
    public static void updateCustomer(int customerID, Customer newCustomer) throws SQLException {
        //CE: find existing customer based on the old customer ID, extract createDate and createdBy for each table
        String currentDate = DateHandler.format(LocalDateTime.now());
        String fCreateDate = null;
        String createdBy = null;
                
        try (Connection conn = DBHandler.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT createDate, createdBy FROM customer WHERE customerId = ?") ) {
        
            ps.setString(1, String.valueOf(customerID));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp("createDate");
                    LocalDateTime createDate = DateHandler.convert(timestamp);
                    fCreateDate = DateHandler.format(createDate);
                    createdBy = rs.getString("createdBy");
                    System.out.println(createdBy);
                }
            }
        } catch (Exception e) {
            System.out.println("Problem in updateCustomer()");
        }
            updateCustomerTable(newCustomer, currentDate, fCreateDate, createdBy);
            updateAddressTable(newCustomer, currentDate, fCreateDate, createdBy);
            updateCityTable(newCustomer, currentDate, fCreateDate, createdBy);
            updateCountryTable(newCustomer, currentDate, fCreateDate, createdBy);
    }
     
    public static void deleteCustomer(Customer customer) throws SQLException {
        
        int id = customer.getCustomerID(); //CE: the following work because adding employees sets customerId, addressId, cityId, and countryId to the same value
        
        try (Connection conn = DBHandler.getConnection();
            PreparedStatement customerStatement = conn.prepareStatement("DELETE FROM customer WHERE customerId = ?");
            PreparedStatement addressStatement = conn.prepareStatement("DELETE FROM address WHERE addressId = ?"); 
            PreparedStatement cityStatement = conn.prepareStatement("DELETE FROM city WHERE cityId = ?");
            PreparedStatement countryStatement = conn.prepareStatement("DELETE FROM country WHERE countryId = ?") ) {
            
            customerStatement.setString(1, String.valueOf(id));
            customerStatement.executeUpdate();     

            addressStatement.setString(1, String.valueOf(id));
            addressStatement.executeUpdate();     

            cityStatement.setString(1, String.valueOf(id));
            cityStatement.executeUpdate();     

            countryStatement.setString(1, String.valueOf(id));
            countryStatement.executeUpdate();     
        }  catch (Exception e) {
            System.out.println("Problem in deleteCustomer()");
        }
    }
    
    public static void addCustomer(Customer newCustomer) throws SQLException {                    
        String currentDate = DateHandler.format(LocalDateTime.now());
        
        updateCustomerTable(newCustomer, currentDate, null, null);
        updateAddressTable(newCustomer, currentDate, null, null);
        updateCityTable(newCustomer, currentDate, null, null);
        updateCountryTable(newCustomer, currentDate, null, null);     
    }
    
    public static void loadCustomerData() throws SQLException {

        //CE: ArrayLists (because we are adding to them in rapid succession): the first two contain arrays because multiple values are selected while the last two contain only values because a single value is selected
        ArrayList<String[]> customerValues = getCustomerValues();
        ArrayList<String[]> addressValues = getAddressValues();
        ArrayList<String> cityValues = getCityValues();
        ArrayList<String> countryValues = getCountryValues();

        /*CE: must enable assertions for the following to work
        int customerSize = customerValues.size();
        assert customerSize == addressValues.size(): "Error comparing with addressValues";
        assert customerSize == cityValues.size(): "Error comparing with cityValues";
        assert customerSize == countryValues.size(): "Error comparing with countryValues";
        */

        for (int i = 0; i < customerValues.size(); i++) {
            Customer customer = new Customer();

            String[] customerArray = customerValues.get(i);
            customer.setCustomerID(Integer.parseInt(customerArray[0]));
            customer.setCustomerName(customerArray[1]);

            String[] addressArray = addressValues.get(i);
            customer.setCustomerAddress(addressArray[0]);
            customer.setCustomerZip(addressArray[1]);
            customer.setCustomerPhone(addressArray[2]);

            customer.setCustomerCityState(cityValues.get(i));
            customer.setCustomerCountry(countryValues.get(i));

            Scheduler.customerData.add(customer);
        }
    }
        
    private static ArrayList<String[]> getCustomerValues() throws SQLException {
        ArrayList<String[]> customerValues = new ArrayList<>();
        
        try (Connection conn = DBHandler.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT customerId, customerName FROM customer") ) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] array = {String.valueOf(rs.getInt("customerId")), rs.getString("customerName")};
                    customerValues.add(array);
                }
            }
        }  catch (Exception e) {
            System.out.println("Problem in getCustomerValues()");
        }
        return customerValues;
    }
    
    private static ArrayList<String[]> getAddressValues() throws SQLException {
        ArrayList<String[]> addressValues = new ArrayList<>();

        try (Connection conn = DBHandler.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT address, postalCode, phone FROM address") ) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] array = {rs.getString("address"), rs.getString("postalCode"), rs.getString("phone")};
                    addressValues.add(array);
                }
            }
        } catch (Exception e) {
            System.out.println("Problem in getAddressValues()");
        }
        return addressValues;
    }

    private static ArrayList<String> getCityValues() throws SQLException {
        ArrayList<String> cityValues = new ArrayList<>();
        
        try (Connection conn = DBHandler.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT city FROM city") ) {
            try (ResultSet rs = ps.executeQuery(); ) {
                while (rs.next()) {
                    cityValues.add(rs.getString("city"));
                }
            }
        } catch (Exception e) {
            System.out.println("Problem in getCityValues()");
        }
        return cityValues;
    }

    private static ArrayList<String> getCountryValues() throws SQLException {
        ArrayList<String> countryValues = new ArrayList<>();
        
        try (Connection conn = DBHandler.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT country FROM country") ) {
            try (ResultSet rs = ps.executeQuery(); ) {
                while (rs.next()) {
                    countryValues.add(rs.getString("country"));
                }
            }
        } catch (Exception e) {
            System.out.println("Problem in getCountryValues()");
        }
        return countryValues;
    }
    
    /*CE: The following methods update the necessary tables and all their fields.
    *These methods do so in a cumbersome manner due to the poor design of the database, which is not in 3rd Normalized Form.
    *Although certainly not ideal, numerous primary and foreign ids are identical, and it should work just fine.
    */
    
    //CE: for each of the following methods: if we are adding a new customer, createDate and createdBy will be null; however, if we are editing an existing customer, they will have values
    
    private static void updateCustomerTable(Customer newCustomer, String updateDate, String createDate, String createdBy) throws SQLException {
        
        String sql = null;
        if (createDate == null && createdBy == null) {        
            sql = "INSERT INTO customer VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE customer SET customerId = ?, customerName = ?, addressId = ?, active = ?, createDate = ?, createdBy = ?, lastUpdate = ?, lastUpdateBy = ? WHERE customerId = " + newCustomer.getCustomerID();
        }

        try (Connection conn = DBHandler.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql) ) {

            ps.setString(1, String.valueOf(newCustomer.getCustomerID()));
            ps.setString(2, newCustomer.getCustomerName());
            ps.setString(3, String.valueOf(newCustomer.getCustomerID())); //CE: thus customerID and addressID will be identical
            ps.setString(4, String.valueOf(newCustomer.getCustomerActive()));
            if (createDate == null && createdBy == null) {
                ps.setString(5, updateDate);
                ps.setString(6, Scheduler.currentUser);
            } else {
                ps.setString(5, createDate);
                ps.setString(6, createdBy);
            }
            ps.setString(7, updateDate);
            ps.setString(8, Scheduler.currentUser);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Problem in updateCustomerTable()");
        }
    }
    
    private static void updateAddressTable(Customer newCustomer, String updateDate, String createDate, String createdBy) throws SQLException {

        String sql = null;
        if (createDate == null && createdBy == null) {        
            sql = "INSERT INTO address VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE address SET addressId = ?, address = ?, address2 = ?, cityId = ?, postalCode = ?, phone = ?, createDate = ?, createdBy = ?, lastUpdate = ?, lastUpdateBy = ? WHERE addressId = " + newCustomer.getCustomerID();
        }
        
        try (Connection conn = DBHandler.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql) ) {

            ps.setString(1, String.valueOf(newCustomer.getCustomerID())); //CE: to repeat, customerID and addressID will be identical
            ps.setString(2, newCustomer.getCustomerAddress());
            ps.setString(3, ""); //CE: no second address line necessary--all can be entered in the first line
            ps.setString(4, String.valueOf(newCustomer.getCustomerID())); //CE: thus customerID, addressID, and cityID will all be the same--I know, this is not ideal; however, the database is poorly structured
            ps.setString(5, newCustomer.getCustomerZip());
            ps.setString(6, newCustomer.getCustomerPhone());
            if (createDate == null && createdBy == null) {
                ps.setString(7, updateDate);
                ps.setString(8, Scheduler.currentUser);
            } else {
                ps.setString(7, createDate);
                ps.setString(8, createdBy);
            }
            ps.setString(9, updateDate);
            ps.setString(10, Scheduler.currentUser);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Problem in updateAddressTable()");
        }
    }
    
    private static void updateCityTable(Customer newCustomer, String updateDate, String createDate, String createdBy) throws SQLException {
        
        String sql = null;
        if (createDate == null && createdBy == null) {        
            sql = "INSERT INTO city VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE city SET cityId = ?, city = ?, countryId = ?, createDate = ?, createdBy = ?, lastUpdate = ?, lastUpdateBy = ? WHERE cityId = " + newCustomer.getCustomerID();
        }
      
        try (Connection conn = DBHandler.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql) ) {

            ps.setString(1, String.valueOf(newCustomer.getCustomerID())); //CE: again, cityID is same as customerID and addressID
            ps.setString(2, newCustomer.getCustomerCityState());
            ps.setString(3, String.valueOf(newCustomer.getCustomerID())); //CE: countryID is identical to cityID, customerID, and addressID
            if (createDate == null && createdBy == null) {
                ps.setString(4, updateDate);
                ps.setString(5, Scheduler.currentUser);
            } else {
                ps.setString(4, createDate);
                ps.setString(5, createdBy);
            }
            ps.setString(6, updateDate);
            ps.setString(7, Scheduler.currentUser);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Problem in updateCityTable()");
        }
    }
    
    private static void updateCountryTable(Customer newCustomer, String updateDate, String createDate, String createdBy) throws SQLException {
        
        String sql = null;
        if (createDate == null && createdBy == null) {        
            sql = "INSERT INTO country VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE country SET countryId = ?, country = ?, createDate = ?, createdBy = ?, lastUpdate = ?, lastUpdateBy = ? WHERE countryId = " + newCustomer.getCustomerID();
        }

        try (Connection conn = DBHandler.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql) ) {

            ps.setString(1, String.valueOf(newCustomer.getCustomerID())); //CE: this countryID is also equivalent to cityID, customerID, and addressID
            ps.setString(2, newCustomer.getCustomerCountry());
            if (createDate == null && createdBy == null) {
                ps.setString(3, updateDate);
                ps.setString(4, Scheduler.currentUser);
            } else {
                ps.setString(3, createDate);
                ps.setString(4, createdBy);
            }
            ps.setString(5, updateDate);
            ps.setString(6, Scheduler.currentUser);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Problem in updateCountryTable()");
        }
    }    
}
