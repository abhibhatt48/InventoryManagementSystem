import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StoreListQuery {
private Connection connection;
    
    public StoreListQuery(Connection connection) {
        this.connection = connection;
    }

    // Create the resultSet for Store
	public ResultSet execute2(String startDate, String endDate) throws SQLException {
	    	Statement statement = connection.createStatement();
	    	ResultSet resultSet2 = statement.executeQuery("SELECT s.store_name, s.city, COUNT(DISTINCT st.staff_id) AS employee_count,\r\n"
            		+ "       COUNT(DISTINCT o.customer_id) AS customers_served,\r\n"
            		+ "       SUM(oi.list_price * oi.quantity * (1 - oi.discount)) / COUNT(DISTINCT o.customer_id) AS avg_sales_per_customer,\r\n"
            		+ "       c.first_name, c.last_name, oi.list_price * oi.quantity * (1 - oi.discount) AS customer_sales_value\r\n"
            		+ "FROM stores s\r\n"
            		+ "JOIN staffs st ON s.store_id = st.store_id\r\n"
            		+ "JOIN orders o ON s.store_id = o.store_id\r\n"
            		+ "JOIN order_items oi ON o.order_id = oi.order_id\r\n"
            		+ "JOIN customers c ON o.customer_id = c.customer_id\r\n"
            		+ "WHERE o.order_status = 4 AND o.order_date BETWEEN '" + startDate + "' AND '" + endDate + "'\r\n"
            		+ "GROUP BY s.store_id, c.customer_id\r\n"
            		+ "ORDER BY s.store_name, s.city, customer_sales_value DESC;\r\n"
            		+ "");
	    	return resultSet2;
	    }
	

}
