package project;
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;


class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt
	

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS Project ("
				+ "id LONG AUTO_INCREMENT PRIMARY KEY, "
				+ "header VARCHAR(255) UNIQUE, "
				+ "title VARCHAR(255), "
				+ "description VARCHAR(255), "
				+ "keywords VARCHAR(255), "
				+ "body VARCHAR(255), "
				+ "references VARCHAR(255), "
				+ "other VARCHAR(255))";
		statement.execute(userTable);
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM Project";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	public void register(String header, String title, String description, String keywords, String body, String references, String other) throws Exception {
		
		String insertArticle = "INSERT INTO Project (header, title, description, keywords, body, references, other) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
			pstmt.setString(1, header);
			pstmt.setString(2, title);
			pstmt.setString(3, description);
			pstmt.setString(4, keywords);
			pstmt.setString(5, body);
			pstmt.setString(6, references);
			pstmt.setString(7, other);
			pstmt.executeUpdate();
		}
	}


	
	public void displayArticles() throws Exception{
		String sql = "SELECT * FROM Project"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			long id  = rs.getLong("id"); 
			String  header = rs.getString("header");
			String  title = rs.getString("title");  
			String  description = rs.getString("description"); 
			String keys = rs.getString("keywords"); 
			String bod = rs.getString("body"); 
			String refer = rs.getString("references"); 
			String other = rs.getString("other");
			

			// Display values 
			System.out.print("\n ID: " + id); 
			System.out.print("\n Header: " + header); 
			System.out.print("\n Title: " + title); 
			System.out.print("\n Description: " + description); 
			System.out.print("\n Keyword(s): " + keys); 
			System.out.print("\n Body: " + bod); 
			System.out.print("\n Reference(s): " + refer); 
			System.out.print("\n Other(s): " + other + "\n");
			
		} 
	}
	
	public void displayByKeyword(String keyword) throws SQLException {
	    String sql = "SELECT * FROM Project WHERE keywords LIKE ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, "%" + keyword + "%"); // Use LIKE to search for the keyword in the keywords column
	        ResultSet rs = pstmt.executeQuery();

	        boolean hasResults = false; // Flag to check if any articles are found
	        while (rs.next()) { 
	            hasResults = true; // Set the flag to true if we have results

	            // Retrieve by column name 
	            long id = rs.getLong("id"); 
	            String header = rs.getString("header");
	            String title = rs.getString("title");  
	            String description = rs.getString("description"); 
	            String keys = rs.getString("keywords"); 
	            String bod = rs.getString("body"); 
	            String refer = rs.getString("references"); 
	            String other = rs.getString("other");

	            // Display values 
	            System.out.print("\n ID: " + id); 
	            System.out.print("\n Header: " + header); 
	            System.out.print("\n Title: " + title); 
	            System.out.print("\n Description: " + description); 
	            System.out.print("\n Keyword(s): " + keys); 
	            System.out.print("\n Body: " + bod); 
	            System.out.print("\n Reference(s): " + refer); 
	            System.out.print("\n Other(s): " + other + "\n");
	        }

	        if (!hasResults) {
	            System.out.println("No articles found with: '" + keyword + "' in keywords.");
	        }
	    }
	}
	
	//delete selected article
	public void deleteArticle(long id) throws SQLException {
	    String deleteSQL = "DELETE FROM Project WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
	    	System.out.println("Article with ID " + id);
	        pstmt.setLong(1, id); // Set the id parameter in the SQL query
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Article with ID " + id + " has been deleted.");
	        } else {
	            System.out.println("No article found with ID " + id + ".");
	        }
	    }
	}
	
	public void updateArticle(long id, String header, String title, String description, String keywords, String body, String references, String other) throws SQLException {
	    String updateSQL = "UPDATE Project SET header = ?, title = ?, description = ?, keywords = ?, body = ?, references = ?, other = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
	        pstmt.setString(1, header);
	        pstmt.setString(2, title);
	        pstmt.setString(3, description);
	        pstmt.setString(4, keywords);
	        pstmt.setString(5, body);
	        pstmt.setString(6, references);
	        pstmt.setString(7, other);
	        pstmt.setLong(8, id); // Set the id parameter in the SQL query
	        
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Article with ID " + id + " has been updated.");
	        } else {
	            System.out.println("No article found with ID " + id + ".");
	        }
	    }
	}
	
	public boolean articleExists(long id) throws SQLException {
	    String query = "SELECT COUNT(*) AS count FROM Project WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setLong(1, id);
	        ResultSet resultSet = pstmt.executeQuery();
	        if (resultSet.next()) {
	            return resultSet.getInt("count") > 0; // Return true if count > 0
	        }
	    }
	    return false; // Return false if no rows found
	}


	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

}
