import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductListQuery {
private Connection connection;
    
    public ProductListQuery(Connection connection) {
        this.connection = connection;
    }
    
    // Create the resultset for products 
    public ResultSet execute1(String startDate, String endDate) throws SQLException {
    	Statement statement = connection.createStatement();
    	ResultSet resultSet1 = statement.executeQuery("SELECT p.product_name, b.brand_name, c.category_name, s.store_name, SUM(oi.quantity) as total_units_sold\r\n"
        		+ "FROM products p\r\n"
        		+ "INNER JOIN brands b ON b.brand_id = p.brand_id\r\n"
        		+ "INNER JOIN categories c ON c.category_id = p.category_id\r\n"
        		+ "INNER JOIN order_items oi ON oi.product_id = p.product_id\r\n"
        		+ "INNER JOIN orders o ON o.order_id = oi.order_id\r\n"
        		+ "INNER JOIN stores s ON s.store_id = o.store_id\r\n"
        		+ "WHERE p.product_id IN (\r\n"
        		+ "    SELECT MIN(oi2.product_id)\r\n"
        		+ "    FROM order_items oi2\r\n"
        		+ "    INNER JOIN orders o2 ON o2.order_id = oi2.order_id\r\n"
        		+ "    WHERE o2.order_date BETWEEN '" + startDate + "' AND '" + endDate + "'\r\n"
        		+ "    GROUP BY oi2.product_id\r\n"
        		+ ")\r\n"
        		+ "GROUP BY p.product_id, s.store_id\r\n"
        		+ "ORDER BY p.product_name, s.store_name;\r\n"
        		+ "");
    	return resultSet1;
    	
    }

}
