package de.cognicrypt.codegenerator.crysl.templates.userauthenticationauth;


import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;


public class UserAuthentication {
	
//	static Scanner sc = new Scanner(System.in);
	//for this to work, sql must be installed and put in path
	@SuppressWarnings("null")
	public static void userAuth(String username, String pwd) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
	    String databaseUsername = "root";
	    String databasePassword = "test";
	    String databaseURL = "jdbc:mysql://localhost:3306/mydb";
	    String tableName = " mytb ";
	    
	    String usernameInDB = "";
	    String passwordInDB = "";
	    int iterationCount = 65536;
	    int keysize = 128;
	    
	    byte[] salt = new byte[32];
	    
	    Object encryptionKey = null;
	    
	    char[] charPwd = pwd.toCharArray();
	    
		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(salt, "next").includeClass("javax.crypto.spec.PBEKeySpec")
		.addParameter(charPwd, "password").addParameter(keysize, "keylength").addParameter(iterationCount, "iterationCount").includeClass("javax.crypto.SecretKeyFactory").includeClass("javax.crypto.SecretKey")
		.addParameter(encryptionKey, "this").generate();
		
	    //this may cause error when mysql is not in the path
	    try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		Connection connection=DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);  

	    // Create SQL Query
	    Statement stmt = connection.createStatement();
	    String SQL = "SELECT * FROM" + tableName + "WHERE USERNAME='" + username + "' && PASSWORD='" + pwd + "'";

	    ResultSet queryResult = stmt.executeQuery(SQL);
	     // Check Username and Password
	    while (queryResult.next()) {
	    	usernameInDB = queryResult.getString("username");
	    	passwordInDB = queryResult.getString("password");
	    }

	    if (username.equals(usernameInDB) && encryptionKey.equals(passwordInDB)) {
	        System.out.println("Successful Login!\n----");
	    } else {
	        System.out.println("Incorrect Password\n----");
	    }
	}
	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
		try {
			userAuth("test", "test");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
