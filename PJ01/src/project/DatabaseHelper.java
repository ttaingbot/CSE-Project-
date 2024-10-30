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
		String userTable = "CREATE TABLE IF NOT EXISTS PJArticles ("
				+ "id LONG AUTO_INCREMENT PRIMARY KEY, "
				+ "header VARCHAR(255) UNIQUE, "
				+ "title VARCHAR(255), "
				+ "keywords VARCHAR(255), "
				+ "body VARCHAR(255), "
				+ "references VARCHAR(255), "
				+ "other VARCHAR(255))";
		statement.execute(userTable);
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM PJArticles";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	public void register(String header, String title, String keywords, String body, String references, String other) throws Exception {
		
		String insertArticle = "INSERT INTO PJArticles (header, title, keywords, body, references, other) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
			pstmt.setString(1, header);
			pstmt.setString(2, title);
			pstmt.setString(3, keywords);
			pstmt.setString(4, body);
			pstmt.setString(5, references);
			pstmt.setString(6, other);
			pstmt.executeUpdate();
		}
	}


	
	public void displayArticles() throws Exception{
		String sql = "SELECT * FROM PJArticles"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			long id  = rs.getInt("id"); 
			String  header = rs.getString("header");
			String  title = rs.getString("title");  
			String keys = rs.getString("keywords"); 
			String bod = rs.getString("body"); 
			String refer = rs.getString("references"); 
			String other = rs.getString("other");
			

			// Display values 
			System.out.print("\n ID: " + id); 
			System.out.print("\n Header: " + header); 
			System.out.print("\n Title: " + title); 
			System.out.print("\n Keyword(s): " + keys); 
			System.out.print("\n Body: " + bod); 
			System.out.print("\n Reference(s): " + refer + "\n"); 
			System.out.print("\n Other(s): " + other + "\n");
			
		} 
	}
	
	//delete selected article
	public void deleteArticle(long id) throws SQLException {
	    String deleteSQL = "DELETE FROM PJArticles WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
	        pstmt.setLong(1, id); // Set the id parameter in the SQL query
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Article with ID " + id + " has been deleted.");
	        } else {
	            System.out.println("No article found with ID " + id + ".");
	        }
	    }
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
