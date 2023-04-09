import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerListQuery {

private Connection connection;
    
    public CustomerListQuery(Connection connection) {
        this.connection = connection;
    }
    
    // Create the resultSet for Customer
    public ResultSet execute(String startDate, String endDate) throws SQLException {
    	Statement statement = connection.createStatement();
    	ResultSet resultSet = statement.executeQuery("SELECT c.first_name, c.last_name, c.street, c.city, c.state, c.zip_code,\r\n"
        		+ "    SUM(oi.list_price * oi.quantity - oi.list_price * oi.quantity * oi.discount) AS order_value,\r\n"
        		+ "    SUM(oi.quantity) AS bicycles_purchased, b.brand_name\r\n"
        		+ "FROM customers c\r\n"
        		+ "INNER JOIN orders o ON c.customer_id = o.customer_id\r\n"
        		+ "INNER JOIN order_items oi ON o.order_id = oi.order_id\r\n"
        		+ "INNER JOIN products p ON oi.product_id = p.product_id\r\n"
        		+ "INNER JOIN brands b ON p.brand_id = b.brand_id\r\n"
        		+ "WHERE o.order_date BETWEEN '" + startDate + "' AND '" + endDate + "'\r\n"
        		+ "AND NOT EXISTS (\r\n"
        		+ "    SELECT 1 FROM orders o2 WHERE c.customer_id = o2.customer_id AND o2.order_date < '2016-02-12'\r\n"
        		+ ")\r\n"
        		+ "GROUP BY c.customer_id, b.brand_id\r\n"
        		+ "ORDER BY c.last_name, c.first_name, b.brand_name;\r\n"
        		+ "" );
    	return resultSet;
    }
}
