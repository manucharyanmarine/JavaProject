import java.sql.*;

public class DatabaseExample {
    public static void main(String[] args) {
        try {
            // Load the database driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/supermarket", "root", "");

            // Create a Statement
            Statement statement = connection.createStatement();

            // Execute a query
            ResultSet resultSet = statement.executeQuery("SELECT * FROM staff");

            // Process the ResultSet
            while (resultSet.next()) {
                // Retrieve data from the result set
                String data = resultSet.getString("FName");
                // Do something with the data
                System.out.println(data);
            }

            // Close the resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
