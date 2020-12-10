package de.cognicrypt.codegenerator.crysl.templates.userauthenticationauth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


public class UserAuthentication {
	
//	static Scanner sc = new Scanner(System.in);
	//for this to work, sql must be installed and put in path
	public static void userAuth(String username, String password) throws SQLException {
	    String databaseUsername = "root";
	    String databasePassword = "test";
	    
	    String usernameInDB = "";
	    String passwordInDB = "";
	    
	    String tableName = " mytb ";
	    //this may cause error when mysql is not in the path
	    try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		Connection connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", databaseUsername, databasePassword);  
	    // Check Username and Password
		
//	    System.out.print("Enter Username: ");
//	    String name = sc.next();
//	    System.out.print("Enter Password: ");
//	    String password = sc.next();

	    // Create SQL Query
	    Statement stmt = connection.createStatement();
	    String SQL = "SELECT * FROM" + tableName + "WHERE USERNAME='" + username + "' && PASSWORD='" + password + "'";

	    ResultSet queryResult = stmt.executeQuery(SQL);
	     // Check Username and Password
	    while (queryResult.next()) {
	    	usernameInDB = queryResult.getString("username");
	    	passwordInDB = queryResult.getString("password");
	    }

	    if (username.equals(usernameInDB) && password.equals(passwordInDB)) {
	        System.out.println("Successful Login!\n----");
	    } else {
	        System.out.println("Incorrect Password\n----");
	    }
	}
	public static void main(String[] args) {
		try {
			userAuth("test", "test");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
