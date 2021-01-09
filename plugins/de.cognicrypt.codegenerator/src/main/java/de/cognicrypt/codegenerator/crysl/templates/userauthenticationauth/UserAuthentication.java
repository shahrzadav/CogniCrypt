package de.cognicrypt.codegenerator.crysl.templates.userauthenticationauth;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;


public class UserAuthentication {
	//hash and salt will be stored as string
	//for this to work, sql must be installed and put in env path
	public static void userAuth(String username, char[] pwd) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
	    String databaseUsername = "root";
	    String databasePassword = "test";
	    String databaseURL = "jdbc:mysql://localhost:3306/myDatabase";
	    String tableName = " mytb ";
	    
	    String usernameInDB = "";
	    String salt = "" ;
	    String hash = "";
	    
	    int iterationCount = 65536;
	    int keysize = 128;
	    
	    byte[] hashedPass = null;
	    //this may cause error when mysql is not in the path
	    try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		Connection connection=DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);  

	    Statement stmt = connection.createStatement();
	    String SQL = "SELECT * FROM" + tableName + "WHERE USERNAME='" + username + "'";
	    ResultSet queryResult = stmt.executeQuery(SQL);
	    while (queryResult.next()) {
	    	usernameInDB = queryResult.getString("username");
	    	salt = queryResult.getString("salt");
	    	hash = queryResult.getString("hash");
	    	
	    }

	    byte [] bytedSalt = Base64.getDecoder().decode(salt);
		
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.PBEKeySpec")
		.addParameter(pwd, "password").addParameter(keysize, "keylength").addParameter(iterationCount, "iterationCount")
		.addParameter(bytedSalt, "salt").includeClass("javax.crypto.SecretKeyFactory").includeClass("javax.crypto.SecretKey")
		.addParameter(hashedPass, "this").generate();

		String hashedPassString = Base64.getEncoder().encodeToString(hashedPass);

	    if (username.equals(usernameInDB) && hash.equals(hashedPassString)) {
	    	
	        System.out.println("Successful Login!\n----");
	    } else {
	        System.out.println("Incorrect Password\n----");
	    }
	}
	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
		try {
			String pass = "non";
			char[] charPass = pass.toCharArray();
			userAuth("test", charPass);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
