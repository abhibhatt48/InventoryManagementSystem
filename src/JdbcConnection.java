import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class JdbcConnection {
	
	JdbcConnection(){

    }
	public static void main(String[] args) {

        // Get my identity information

        final String username = "abhishekb";
        final String password = "B00933993";
        final List<String> VALID_EXTENSIONS  = Arrays.asList(".txt", ".csv", ".xml", ".doc", ".pdf");
        
        // Do the actual database work now
        Connection connect = null;
        Statement statement = null;

        Scanner scanner = new Scanner(System.in);
        
        // Take user input for start date, end date and file name.
        System.out.println("Enter start date (YYYY-MM-DD):");
        String startDate = scanner.nextLine();
        System.out.println("Enter end date (YYYY-MM-DD):");
        String endDate = scanner.nextLine();
        System.out.println("Enter a file name you want to generate output in: ");
        String fileName = scanner.nextLine();
        // if file name contain any extension validation
        for (String ext : VALID_EXTENSIONS )
        if (fileName.endsWith(ext) ) {
        	System.out.println("Please don't specify file extensions.");        	
        }

        // Connect the database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connect = DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306?serverTimezone=UTC&useSSL=false", username, password );
            statement = connect.createStatement();
            statement.execute("use csci3901;");       
            
            // Call the CustomerListQuery class to process the query
            CustomerListQuery customerListQuery = new CustomerListQuery(connect);
            ResultSet resultSet = customerListQuery.execute(startDate, endDate);
                        
            // Create a Document object to build the XML file
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            
            // Create the root element and add it to the document
            Element rootElement = doc.createElement("activity_summary");
            doc.appendChild(rootElement);
            
            // Create the time_span element and add it to the root element
            Element timeSpanElement = doc.createElement("time_span");
            rootElement.appendChild(timeSpanElement);
            
            // Create the start_date element and add it to the time_span element
            Element startDateElement = doc.createElement("start_date");
            startDateElement.appendChild(doc.createTextNode(startDate));
            timeSpanElement.appendChild(startDateElement);
            
            // Create the end_date element and add it to the time_span element
            Element endDateElement = doc.createElement("end_date");
            endDateElement.appendChild(doc.createTextNode(endDate));
            timeSpanElement.appendChild(endDateElement);
            
            // Create the customer_list element and add it to the root element
            Element customerListElement = doc.createElement("customer_list");
            rootElement.appendChild(customerListElement);
            
            // Loop through the ResultSet and create the XML elements
            while (resultSet.next()) {
            	
            // Create the customer element and add it to the customer_list element
            Element customerElement = doc.createElement("customer");
            customerListElement.appendChild(customerElement);
            
            // Add the customer name element to the customer element
            Element customerNameElement = doc.createElement("customer_name");
            String customerName = resultSet.getString("first_name") + " " + resultSet.getString("last_name");
            customerNameElement.appendChild(doc.createTextNode(customerName));
            customerElement.appendChild(customerNameElement);
            
            // Add the address element to the customer element
            Element addressElement = doc.createElement("address");
            customerElement.appendChild(addressElement);
            
            // Add the street_address element to the address element
            Element streetAddressElement = doc.createElement("street_address");
            streetAddressElement.appendChild(doc.createTextNode(resultSet.getString("street")));
            addressElement.appendChild(streetAddressElement);
            
            // Add the city element to the address element
            Element cityElement = doc.createElement("city");
            cityElement.appendChild(doc.createTextNode(resultSet.getString("city")));
            addressElement.appendChild(cityElement);
            
            // Add the state element to the address element
            Element stateElement = doc.createElement("state");
            stateElement.appendChild(doc.createTextNode(resultSet.getString("state")));
            addressElement.appendChild(stateElement);
            
            // Add the zip_code element to the address element
            Element zipCodeElement = doc.createElement("zip_code");
            zipCodeElement.appendChild(doc.createTextNode(resultSet.getString("zip_code")));
            addressElement.appendChild(zipCodeElement);
            
            // Add the order_value element to the customer element
            Element orderValueElement = doc.createElement("order_value");
            orderValueElement.appendChild(doc.createTextNode(resultSet.getString("order_value")));
            customerElement.appendChild(orderValueElement);
            
            // Add the bicycles_purchased element to the customer element
            Element bicyclesPurchasedElement = doc.createElement("bicycles_purchased");
            bicyclesPurchasedElement.appendChild(doc.createTextNode(resultSet.getString("bicycles_purchased")));
            customerElement.appendChild(bicyclesPurchasedElement);
            }
            
            // Call the ProductListQuery class to process the query
            ProductListQuery productListQuery = new ProductListQuery(connect);
            ResultSet resultSet1 = productListQuery.execute1(startDate, endDate);
            
            // Create the product_list element and add it to the root element
            Element productListElement = doc.createElement("product_list");
            rootElement.appendChild(productListElement);
            
            // iterate through the products ResultSet and add them to the product_list element
            while (resultSet1.next()) {
            	// Create the product element and add it to the product_list element
                Element productElement = doc.createElement("product");
                productListElement.appendChild(productElement);
                
                // Add the product_name element to product element 
                Element productNameElement = doc.createElement("product_name");
                productNameElement.appendChild(doc.createTextNode(resultSet1.getString("product_name")));
                productElement.appendChild(productNameElement);
                
                // Add the brand element to product element 
                Element brandNameElement = doc.createElement("brand");
                brandNameElement.appendChild(doc.createTextNode(resultSet1.getString("brand_name")));
                productElement.appendChild(brandNameElement);
                
                // Add the category_name element to product element 
                String[] categoriesElement = resultSet1.getString("category_name").split(",");
                for (String category_name : categoriesElement) {
                	// Add the category element to product category_name element 
                   Element categoryElement = doc.createElement("category");
                   categoryElement.appendChild(doc.createTextNode(category_name.trim()));
                   productElement.appendChild(categoryElement);
                }

                // Add the store_sales element to product element 
                Element storeSaleElement = doc.createElement("store_sales");
                productElement.appendChild(storeSaleElement);
                
                // Add the store_name element to store_sales element 
                Element storeNameElement = doc.createElement("store_name");
                storeNameElement.appendChild(doc.createTextNode(resultSet1.getString("store_name")));
                storeSaleElement.appendChild(storeNameElement);
                
                // Add the unit_sold element to store_sales element 
                Element unitsSoldElement = doc.createElement("units_sold");
                unitsSoldElement.appendChild(doc.createTextNode(resultSet1.getString("total_units_sold")));
                storeSaleElement.appendChild(unitsSoldElement);
            }
            
            // Call the StoreListQuery class to process the query
            StoreListQuery storeListQuery = new StoreListQuery(connect);
            ResultSet resultSet2 = productListQuery.execute1(startDate, endDate);
			
            
            // Create store_list element and add to the root elements
            Element storeListElement = doc.createElement("store_list");
            rootElement.appendChild(storeListElement);
            
            while (resultSet2.next()) {
            	// Create the store element and add it to the root element
                Element storeElement = doc.createElement("store");
                storeListElement.appendChild(storeElement);
                
                // Add the store_name element to store element 
                Element storeNameElement = doc.createElement("store_name");
                storeNameElement.appendChild(doc.createTextNode(resultSet2.getString("store_name")));
                storeElement.appendChild(storeNameElement);
                
                // Add the city element to store element
                Element cityElement = doc.createElement("city");
                cityElement.appendChild(doc.createTextNode(resultSet2.getString("city")));
                storeElement.appendChild(cityElement);
                
                // Add the employee_count element to store element
                Element employeeCountElement = doc.createElement("employee_count");
                employeeCountElement.appendChild(doc.createTextNode(resultSet2.getString("employee_count")));
                storeElement.appendChild(employeeCountElement);
                
                // Add the customers_served element to store element
                Element customersServedElement = doc.createElement("customers_served");
                customersServedElement.appendChild(doc.createTextNode(resultSet2.getString("customers_served")));
                storeElement.appendChild(customersServedElement);
                
                // Add the avg_sales_per_customer element to store element
                Element avgSalesPerCustomerElement = doc.createElement("avg_sales_per_customer");
                avgSalesPerCustomerElement.appendChild(doc.createTextNode(resultSet2.getString("avg_sales_per_customer")));
                storeElement.appendChild(avgSalesPerCustomerElement);
                
                // Add the customer_list element to store element
                Element customerListElement1 = doc.createElement("customer_list");
                storeElement.appendChild(customerListElement1);
                
                // Add Add the customer element to customer_list element
                do {
                    Element customerElement = doc.createElement("customer");
                    customerListElement.appendChild(customerElement);
                    
                    // Add the first_name element to customer element
                    Element firstNameElement = doc.createElement("first_name");
                    firstNameElement.appendChild(doc.createTextNode(resultSet2.getString("first_name")));
                    customerElement.appendChild(firstNameElement);
                    
                    // Add the last_name element to customer element
                    Element lastNameElement = doc.createElement("last_name");
                    lastNameElement.appendChild(doc.createTextNode(resultSet2.getString("last_name")));
                    customerElement.appendChild(lastNameElement);
                    
                    // Add the sales_value element to customer element
                    Element salesValueElement = doc.createElement("sales_value");
                    salesValueElement.appendChild(doc.createTextNode(resultSet2.getString("customer_sales_value")));
                    customerElement.appendChild(salesValueElement);
                    
                } while (resultSet2.next() && resultSet2.getString("store_name").equals(storeNameElement.getTextContent()) && resultSet2.getString("city").equals(cityElement.getTextContent()));
            }
            
            // Write the document to an XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fileName+".xml"));
            System.out.print("file generated");
            transformer.transform(source, result);
            
            connect.close();

        }catch (Exception e) {
        	System.out.println("Connection failed");
        	System.out.println(e.getMessage());
        	}
	}
}
