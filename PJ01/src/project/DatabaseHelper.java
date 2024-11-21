package project;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


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
	
	// Encryption key for sensitive data
    private static final String ENCRYPTION_KEY = "1234567890123456"; // 16-byte key
	
    /**
     * Encrypts a given text using AES encryption.
     *
     * @param plainText The text to encrypt.
     * @return Encrypted text.
     */
	private String encrypt(String plainText) throws Exception {
	    Cipher cipher = Cipher.getInstance("AES");
	    SecretKeySpec keySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "AES");
	    cipher.init(Cipher.ENCRYPT_MODE, keySpec);
	    byte[] encrypted = cipher.doFinal(plainText.getBytes());
	    return Base64.getEncoder().encodeToString(encrypted);
	}

	/**
	 * Decrypts a given text using AES encryption.
	 *
	 * @param encryptedText The encrypted text.
	 * @return Decrypted text.
	 */
	private String decrypt(String encryptedText) throws Exception {
	    Cipher cipher = Cipher.getInstance("AES");
	    SecretKeySpec keySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "AES");
	    cipher.init(Cipher.DECRYPT_MODE, keySpec);
	    byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
	    return new String(cipher.doFinal(decodedBytes));
	}


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
		String userTable = "CREATE TABLE IF NOT EXISTS databased ("
				+ "id LONG AUTO_INCREMENT PRIMARY KEY, "
				+ "header VARCHAR(255) UNIQUE, "
				+ "title VARCHAR(255), "
				+ "description VARCHAR(255), "
				+ "keywords VARCHAR(255), "
				+ "body VARCHAR(255), "
				+ "references VARCHAR(255), "
				+ "groups VARCHAR(255), "
				+ "other VARCHAR(255))";
		try (PreparedStatement pstmt = connection.prepareStatement(userTable)) {
			pstmt.execute();
		}
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM DataBased";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	public void register(String header, String title, String description, String keywords, String body, String references, String groups, String other) throws Exception {
		String encryptedBody = encrypt(body);
		String insertArticle = "INSERT INTO DataBased (header, title, description, keywords, body, references, groups, other) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
			pstmt.setString(1, header);
			pstmt.setString(2, title);
			pstmt.setString(3, description);
			pstmt.setString(4, keywords);
			pstmt.setString(5, encryptedBody);
			pstmt.setString(6, references);
			pstmt.setString(7, groups);
			pstmt.setString(8, other);
			pstmt.executeUpdate();
		}
	}


	//encrypted article display for testing
	public String displayEncryptedArticles() throws Exception{
		String sql = "SELECT * FROM DataBased"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 
		String ret = "";
		String temp;

		while(rs.next()) { 
			// Retrieve by column name 
			long id  = rs.getLong("id"); 
			String  header = rs.getString("header");
			String  title = rs.getString("title");  
			String  description = rs.getString("description"); 
			String keys = rs.getString("keywords"); 
			String bod = rs.getString("body"); 
			String refer = rs.getString("references"); 
			String groups = rs.getString("groups");
			String other = rs.getString("other");
			

			// Display values 
			System.out.print("\n ID: " + id); 
			System.out.print("\n Header: " + header); 
			System.out.print("\n Title: " + title); 
			System.out.print("\n Description: " + description); 
			System.out.print("\n Keyword(s): " + keys); 
			System.out.print("\n Body: " + bod); 
			System.out.print("\n Reference(s): " + refer); 
			System.out.print("\n Groups(s): " + groups);
			System.out.print("\n Other(s): " + other + "\n");
			
			temp = "ID: " + id + "\n" +
					"Header: " + header + "\n" +
					"Title: " + title + "\n" +
					"Description: " + description + "\n" +
					"Keyword(s): " + keys + "\n" + 
					"Body: " + bod + "\n" + 
					"Reference(s): " + refer + "\n" +
					"Group(s): " + groups + "\n" +
					"Other: " + other + "\n";
					
					ret = ret + temp;
			
		} 
		return ret;
	}
	
	//decrypted article display for testing
	public String displayDecryptedArticles() throws Exception{
		String sql = "SELECT * FROM DataBased"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 
		String ret = "";
		String temp;

		while(rs.next()) { 
			// Retrieve by column name 
			long id  = rs.getLong("id"); 
			String  header = rs.getString("header");
			String  title = rs.getString("title");  
			String  description = rs.getString("description"); 
			String keys = rs.getString("keywords"); 
			String bod = rs.getString("body"); 
			String refer = rs.getString("references"); 
			String groups = rs.getString("groups");
			String other = rs.getString("other");
			

			// Display values 
			System.out.print("\n ID: " + id); 
			System.out.print("\n Header: " + header); 
			System.out.print("\n Title: " + title); 
			System.out.print("\n Description: " + description); 
			System.out.print("\n Keyword(s): " + keys); 
			System.out.print("\n Body: " + decrypt(bod)); 
			System.out.print("\n Reference(s): " + refer); 
			System.out.print("\n Groups(s): " + groups);
			System.out.print("\n Other(s): " + other + "\n");
			
			temp = "ID: " + id + "\n" +
					"Header: " + header + "\n" +
					"Title: " + title + "\n" +
					"Description: " + description + "\n" +
					"Keyword(s): " + keys + "\n" + 
					"Body: " + decrypt(bod) + "\n" + 
					"Reference(s): " + refer + "\n" +
					"Group(s): " + groups + "\n" +
					"Other: " + other + "\n";
					
					ret = ret + temp;
			
		} 
		return ret;
	}
	
	public String displayByKeyword(String keyword, ArrayList<String> groups) throws SQLException {
	    String sql = "SELECT * FROM DataBased WHERE (keywords LIKE ? OR title LIKE ? OR references LIKE ?) AND groups LIKE ?";
	    String ret = "";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	    	for(int i = 0; i < groups.size(); i++) {
	    		//checks if the keyword is in title, references, OR keywords
		        pstmt.setString(1, "%" + keyword + "%"); 
		        pstmt.setString(2, "%" + keyword + "%");
		        pstmt.setString(3, "%" + keyword + "%");
		        pstmt.setString(4, "%" + groups.get(i) + "%");
		        ResultSet rs = pstmt.executeQuery();
		        
				String temp;
	
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
		            try {
						System.out.print("\n Body: " + decrypt(bod));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
		            System.out.print("\n Reference(s): " + refer); 
		            System.out.print("\n Other(s): " + other + "\n");
		            temp = "";
		            try {
						temp = "ID: " + id + "\n" +
								"Header: " + header + "\n" +
								"Title: " + title + "\n" +
								"Description: " + description + "\n" +
								"Keyword(s): " + keys + "\n" + 
								"Body: " + decrypt(bod) + "\n" + 
								"Reference(s): " + refer + "\n" +
								"Other: " + other + "\n";
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
							
							ret = ret + temp;
		        }
	
		        if (!hasResults) {
		            System.out.println("No articles found with: '" + groups + "' in groups.");
		        }
	    	}
	    	return ret;
	    }
	    
	}
	
	//checks the groups that the user has and displays them
	public String displayByGroups(ArrayList<String> groups) throws SQLException {
	    String sql = "SELECT * FROM DataBased WHERE groups LIKE ?";
	    String ret = "";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	    	for(int i = 0; i < groups.size(); i++) {
		        pstmt.setString(1, "%" + groups.get(i) + "%"); // Use LIKE to search for the keyword in the keywords column
		        ResultSet rs = pstmt.executeQuery();
		        
				String temp;
	
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
		            try {
						System.out.print("\n Body: " + decrypt(bod));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
		            System.out.print("\n Reference(s): " + refer); 
		            System.out.print("\n Other(s): " + other + "\n");
		            temp = "";
		            try {
						temp = "ID: " + id + "\n" +
								"Header: " + header + "\n" +
								"Title: " + title + "\n" +
								"Description: " + description + "\n" +
								"Keyword(s): " + keys + "\n" + 
								"Body: " + decrypt(bod) + "\n" + 
								"Reference(s): " + refer + "\n" +
								"Other: " + other + "\n";
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
							
							ret = ret + temp;
		        }
	
		        if (!hasResults) {
		            System.out.println("No articles found with: '" + groups + "' in groups.");
		        }
	    	}
	    	return ret;
	    }
	    
	}
	
	//delete selected article
	public void deleteArticle(long id) throws SQLException {
	    String deleteSQL = "DELETE FROM DataBased WHERE id = ?";
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
	
	//function to select the article based on ID and then update that particular article's contents
	public void updateArticle(long id, String header, String title, String description, String keywords, String body, String references, String groups, String other) throws SQLException {
	    String updateSQL = "UPDATE DataBased SET header = ?, title = ?, description = ?, keywords = ?, body = ?, references = ?, groups = ?,other = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
	        pstmt.setString(1, header);
	        pstmt.setString(2, title);
	        pstmt.setString(3, description);
	        pstmt.setString(4, keywords);
	        try {
				pstmt.setString(5, encrypt(body));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        pstmt.setString(6, references);
	        pstmt.setString(7, groups);
	        pstmt.setString(8, other);
	        pstmt.setLong(9, id); // Set the id parameter in the SQL query
	        
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Article with ID " + id + " has been updated.");
	        } else {
	            System.out.println("No article found with ID " + id + ".");
	        }
	    }
	}
	
	//function to check if the desired article exists within the database
	public boolean articleExists(long id) throws SQLException {
	    String query = "SELECT COUNT(*) AS count FROM DataBased WHERE id = ?";
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
	
	// Method to back up articles to a file
	public void backupToFile(String fileName) throws IOException, SQLException {
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
	        String query = "SELECT * FROM DataBased";
	        ResultSet rs = statement.executeQuery(query);
	        while (rs.next()) {
	            writer.write(rs.getLong("id") + "," +
	                         rs.getString("header") + "," +
	                         rs.getString("title") + "," +
	                         rs.getString("description") + "," +
	                         rs.getString("keywords") + "," +
	                         rs.getString("body") + "," +
	                         rs.getString("references") + "," +
	                         rs.getString("groups") + "," +
	                         rs.getString("other") + "\n");
	        }
	        System.out.println("Data backed up to " + fileName);
	    }
	}

	// Method to restore articles from a file
	public void restoreFromFile(String fileName, boolean merge) throws IOException, SQLException {
	    if (!merge) {
	        statement.executeUpdate("DELETE FROM DataBased"); // Clear existing data if not merging
	    }

	    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            String[] fields = line.split(",");
	            long id = Long.parseLong(fields[0]);
	            String header = fields[1];
	            String title = fields[2];
	            String description = fields[3];
	            String keywords = fields[4];
	            String body = fields[5];
	            String references = fields[6];
	            String groups = fields[7];
	            String other = fields[8];

	            if (!merge || !articleExists(id)) {
	                String insertSQL = "INSERT INTO DataBased (id, header, title, description, keywords, body, references, groups, other) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	                try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
	                    pstmt.setLong(1, id);
	                    pstmt.setString(2, header);
	                    pstmt.setString(3, title);
	                    pstmt.setString(4, description);
	                    pstmt.setString(5, keywords);
	                    pstmt.setString(6, body);
	                    pstmt.setString(7, references);
	                    pstmt.setString(8, groups);
	                    pstmt.setString(9, other);
	                    pstmt.executeUpdate();
	                }
	            }
	        }
	        System.out.println("Data restored from " + fileName);
	    }
	}


}
