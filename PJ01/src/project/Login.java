package project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.sql.*;

public class Login extends Application{
	//initiallize global variables
	User tem = new User();
	int sceneIndex = 0;
	long tempid;
	double OTP;
	char otpR;
	boolean kill = false;
	private static ArrayList<User> users;
	
	private static DatabaseHelper databaseHelper;
	
	//Method to backup the database to a file
	public static void backupData() {
	    try {
	        databaseHelper = new DatabaseHelper();
	        databaseHelper.connectToDatabase();
	        System.out.println("Initiating data backup...");
	        databaseHelper.backupToFile("backupData.txt");
	        System.out.println("Data backup completed successfully.");
	    } catch (Exception e) {
	        System.err.println("Error during backup: " + e.getMessage());
	    } finally {
	        databaseHelper.closeConnection();
	    }
	}

	//Method to restore the database from a file
	public static void restoreData() {
	    try {
	        databaseHelper = new DatabaseHelper();
	        databaseHelper.connectToDatabase();
	        System.out.println("Initiating data restore...");
	        databaseHelper.restoreFromFile("backupData.txt", true); // true = merge, false = replace
	        System.out.println("Data restore completed successfully.");
	    } catch (Exception e) {
	        System.err.println("Error during restore: " + e.getMessage());
	    } finally {
	        databaseHelper.closeConnection();
	    }
	}
	
	public static void setPassword(Scanner scanner, User user) {
		// variables to hold two passwords for comparison - if they are equal then change the password
		String s1;
		String s2;
		
		// collecting input for first and second password attempts
		System.out.println("Hello " + user.username + " , please enter in a password below: ");
		s1 = scanner.nextLine();
		
		System.out.println("Please enter your password again: ");
		s2 = scanner.nextLine();
		
		// if the strings are not equal, call the function again
		if(!s1.equals(s2)) {
			System.out.println("Error, passwords do not match: ");
			while (!s1.equals(s2)) {
				System.out.println("Error, passwords do not match. Please try again.");
				System.out.println("Enter password: ");
				s1 = scanner.nextLine();
				System.out.println("Please enter your password again: ");
				s2 = scanner.nextLine();
			}
			user.password = s1;
		}
		// otherwise, set the password to either string
		else {
			user.password = s1;
			return;
		}
		
	}
	
	
	public static void finishSettingUp(Scanner scanner, User user) {
		System.out.println("Finish setting up your account: ");
		
		// input for email
		System.out.println("Enter email: ");
		user.email = scanner.nextLine();
		
		// getting user inputs for first, middle, last name
		System.out.println("Enter first name: ");
		user.firstName = scanner.nextLine();
		
		System.out.println("Enter middle name: ");
		user.middleName = scanner.nextLine();
		
		System.out.println("Enter last name: ");
		user.lastName = scanner.nextLine();
		
		// preferred name is optional, so first collect input 
		System.out.println("Enter preferred name (optional - leave blank if none): ");
		String temp = scanner.nextLine();
		
		// if it is not empty, then set user's pref name to temp and change boolean
		if(!temp.isEmpty()) {
			user.preferredName = temp;
			user.prefName = true;
		}
		
		
	}
	
	
	public static boolean checkPassword(Scanner scanner, User user) {
		System.out.println("Please Enter Password");
		// storing the input in string s
		String s = scanner.nextLine();
		String input;
		
		// if s is equal to the suspected username's password, the user is valid
		if(s.equals(user.password)) {
			return true;
		}
		
		// if s is not equal to the suspected password, return false
		else {
			System.out.println("Error, incorrect password");
			return false;
		}
	}
	
	public static boolean checkChar(Scanner scanner, String allowed, String input) {
		
		while (!input.matches("[A-Za-z]")) {
			System.out.println("Error, input is not a single char");
			input = scanner.nextLine();
		}
		
		// first check if the input is more than one char
		// if it is not, pass the checkChar function again and then return to avoid recursive loops
		if(!input.matches("[A-Za-z]{1}")) {
			System.out.println("Error, input is not a single char");
			return checkChar(scanner, allowed, input);
		}
		
		// a boolean flag to check if the inputted string is in the allowed list of chars
		boolean flag = false;
		
		for(int i = 0;i<allowed.length();i++) {
			if(input.charAt(0) == allowed.charAt(i)) {
				flag = true;
			}
		}
		
		// final check to see if the flag is true - if it is not 
		// then the user has inputted a random char
		if(flag == false) {
			System.out.println("Error, that is not an existing command");
			return checkChar(scanner, allowed, input);
		}
		
		
		return true;
	}
	
	// this function prints a greeting to the user after they log in
	// it also displays their name/username and role
	public static void printWelcome(User user) {
		// if the user has a preferred name, use it
		if(user.prefName) {
			System.out.print("Welcome " + user.preferredName + " | ");
		}
		// if the user does not have a preferred name, do not use it
		else {
			System.out.print("Welcome " + user.username + " | ");
		}
		
		// switch statement to find what role the user is and output their role
		switch(user.currentRole) {
			case 'a':
				System.out.println("Admin");
				return;
			case 's':
				System.out.println("Student");
				return;
			case 'i':
				System.out.println("Instructor");
				return;
			default:
				return;
		
		}
	}
	
	
	
	public static void loginScreen(Scanner scanner) {
		String s;
		
		// loop runs until a username is inputted
		while(true) {
			System.out.println("Enter Username: ");
			s = scanner.nextLine();
			int currentUser;
			
			// check to see if username is in current list of usernames
			boolean match = false;
			for(currentUser = 0;currentUser<users.size();currentUser++) {
				if(s.equals(users.get(currentUser).username)) {
					// if the username is, save what user it belongs to in currentUser integer
					// eventually want to compare saved user's password to inputted password
					match = true;
					break;
				}
			}
			
			if(!match) {
				System.out.println("Username not recognized");
				continue;
			}
			
			// check to see if a match was found
			
			
			// if password is wrong, refresh the login screen.
			if(!checkPassword(scanner, users.get(currentUser))) {
				continue;
			}
			
			// creating a user variable to avoid having to type users.get(currentUser) every time
			User user = users.get(currentUser);
			
			// check what role(s) 
			if(user.roles.size()>1){
				System.out.println("You have multiple roles, enter a letter depending on what role you wish you access the program in.");
				System.out.println("Admin = (a), Student = (s), Instructor = (i)");
				
				// calling checkChar to verify that the user has inputted either a, s, or i
				s = scanner.nextLine();
				checkChar(scanner, "asi", s);
				
				// set user's current role depending on input
				user.currentRole = s.charAt(0);
			}
			// if user only has one role, set their current role to their only one
			else {
				user.currentRole = user.roles.get(0);
				printWelcome(user);
				
			}
			
			
			
		}
		
	}
	

	//Scene logic for going between screens
	public void start(Stage MainScreen) {
		Stage startScreen = new Stage();
		//initial startup screen
			if(sceneIndex == 0) {
				initial(startScreen);
			}
			else if(sceneIndex == 1) {
				login(startScreen);
			}
			else if(sceneIndex == 2) {
				menu(startScreen);
			}
			else if(sceneIndex == 3) {
				adminMenu(startScreen);
			}
			else if(sceneIndex == 4) {
				userMenu(startScreen);
			}
			else if(sceneIndex == 5) {
				OTPMenu(startScreen);
			}
			else if(sceneIndex == 6) {
				artMenu(startScreen);
			}
			else if(sceneIndex == 7) {
				instrMenu(startScreen);
			}
			else if(sceneIndex == 8) {
				//stuMenu(startScreen);
			}
			else if(sceneIndex == 9) {
				createArMenu(startScreen);
			}
			else if(sceneIndex == 10) {
				updArMenu(startScreen);
			}
			else {
				System.out.print("goodbye!");
			}
		
        
    }
	
	
	//very first login screen to establish the 
	public void initial(Stage startScreen) {
    	System.out.println("Starting Login");
    	startScreen.setTitle("Login Screen");
        
        Button btn = new Button();
        btn.setText("Create admin");
        btn.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
                sceneIndex = 1;
                start(startScreen);
                startScreen.close();
            }
        });
        //create the arraylist of users
    	users = new ArrayList<User>();
    	
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        startScreen.setScene(new Scene(root, 300, 250));
        startScreen.show();
	}
	
	//admin login screen for the very first user
	public void login(Stage startScreen) {
		System.out.println("Admin Login");
		startScreen.setTitle("Admin login");
		
		//initialize a grid setup for the windows
		BorderPane bPane = new BorderPane();
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		
		gridPane.setPadding(new Insets(5,5,5,5));
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		bPane.setCenter(gridPane);
		
		//create a new textfield for admin username
		TextField adminUser = new TextField("");
		
		//create a new textfield for admin password
		TextField adminPass = new TextField("");
		
		TextField adminPassConf = new TextField("");
		
		// create a stack pane
        StackPane adminWindow = new StackPane();
        
        // add textfields
        adminWindow.getChildren().add(adminUser);
        adminWindow.getChildren().add(adminPass);
        
        Scene sc = new Scene(bPane, 900, 500);
        
      //Create Labels
        Label User = new Label("Username");
        Label Pass = new Label("Password");
        Label Pass2 = new Label("Password Confirm");
        
        Button back = new Button("Back");
        Button sub = new Button("Submit");
        

        //Add all controls to Grid
        gridPane.add(User, 0, 0);
        gridPane.add(Pass, 0, 1);
        gridPane.add(Pass2, 0, 2);
        gridPane.add(adminUser, 1, 0);
        gridPane.add(adminPass, 1, 1);
        gridPane.add(adminPassConf,1, 2);
        gridPane.add(back, 0, 3);
        gridPane.add(sub, 2, 3);
        
        // set the scene
        startScreen.setScene(sc);
 
        startScreen.show();
		
		Admin firstAdmin = new Admin();
		
		sub.setOnAction(new EventHandler<ActionEvent>()
	    {
	      @Override      
	      //when the submit button is pressed
	      public void handle(ActionEvent e)
	      {
	    	  //check if passwords match and username isnt empty
			if(adminPass.getText().equals(adminPassConf.getText()) && adminUser.getText() != null && !adminUser.getText().isEmpty()) {
	        	//set username to text
	    		firstAdmin.username = adminUser.getText();
	    		//set password to text
	    		firstAdmin.password = adminPass.getText();
	    		// adding 2 role (admin) to newly created user
	    		firstAdmin.roles.add('a');
	    		// adding first user to list of usernames in system
	    		users.add(firstAdmin);
	    		// displaying log out message and sending program to loginScreen
	    		System.out.println("New Account Created! Logging you out.");
	    		sceneIndex = 2;
	    		start(startScreen);
                startScreen.close();
	        }
	        else
	        {
	        	System.out.print("Passwords don't match or username is empty!");
	        }
	      }
	    });
		
	}
	
	//main menu screen with login with username or login with code
	public void menu(Stage startScreen) {
		System.out.println("Menu");
		startScreen.setTitle("Menu login");
		
		//initialize a grid setup for the windows
		BorderPane bPane = new BorderPane();
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		
		gridPane.setPadding(new Insets(5,5,5,5));
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		bPane.setCenter(gridPane);
		
		//create a new textfield for admin username
		TextField user = new TextField("");
		
		//create a new textfield for admin password
		TextField pass = new TextField("");
		
		TextField code = new TextField("");
		
		// create a stack pane
        StackPane adminWindow = new StackPane();
        
        // add textfields
        adminWindow.getChildren().add(user);
        adminWindow.getChildren().add(pass);
        adminWindow.getChildren().add(code);
        
        Scene sc = new Scene(bPane, 900, 500);
        
        //Create Labels
        Label User = new Label("Username");
        Label Pass = new Label("Password");
        Label Pass2 = new Label("One Time Code");
        
        Button close = new Button("Close");
        Button sub = new Button("Submit");

        gridPane.add(User, 0, 0);
        gridPane.add(Pass, 0, 1);
        gridPane.add(Pass2, 0, 2);
        gridPane.add(user, 1, 0);
        gridPane.add(pass, 1, 1);
        gridPane.add(code,1, 2);
        gridPane.add(close, 0, 3);
        gridPane.add(sub, 2, 3);
        
        // set the scene
        startScreen.setScene(sc);
 
        startScreen.show();
		
		
		sub.setOnAction(new EventHandler<ActionEvent>()
	    {
	      @Override      
	      //when the submit button is pressed
	      public void handle(ActionEvent e)
	      {
	    	  //checks if user is in array
	    	  for (int i = 0; i < users.size(); i++) {
	    		  //if the user is found
	    		  if(users.get(i).username.equals(user.getText())){
	    			  //check if passwords match
	    			  if(users.get(i).password.equals(pass.getText())) {
	    				  System.out.print(users.get(i).roles.get(0));
	    				  //check if the user is an admin and proceed to admin login
	    				  if((users.get(i).roles.contains('a')))
	    				  {
	    					tem = users.get(i);
	    					sceneIndex = 3;
		    				start(startScreen);
		    				startScreen.close();
	    				  }
	    				  //check if they are an instructor
	    				  else if(users.get(i).roles.contains('i')){
	    					  tem=users.get(i);
		    				  sceneIndex = 7;
		    				  start(startScreen);
		    				  startScreen.close();
	    				  }
	    				  //else go to normal login
	    				  else {
	    					  tem=users.get(i);
		    				  sceneIndex = 4;
		    				  start(startScreen);
		    				  startScreen.close();
	    				  }
	    			  }
	    		  }
	  	        else if(code.getText().equals(String.valueOf(OTP)) && OTP != 0)
		        {
	  	        	OTP = 0;
		  	        sceneIndex = 5;
	  				start(startScreen);
	  				startScreen.close();
		        }
	    		  else
		    	  {
	    			  //user not found
		    		  System.out.print("Invalid User!");
		    	  }
	    	  }
	      }
	    });
		//close the window
		close.setOnAction((ActionEvent e) ->
	    {
	        startScreen.close();      
	    }); 
	}
	
	//Admin menu screen
		public void adminMenu(Stage startScreen) {
			System.out.println("Admin Menu");
			startScreen.setTitle(String.format("Admin Menu: Logged in as, %s", tem.username));
			
			
			//initialize a grid setup for the windows
			BorderPane bPane = new BorderPane();
			GridPane gridPane = new GridPane();
			gridPane.setAlignment(Pos.CENTER);
			
			gridPane.setPadding(new Insets(9,9,9,9));
			gridPane.setHgap(10);
			gridPane.setVgap(10);
			bPane.setCenter(gridPane);
			
			//create a new textfield for user select
			TextField user = new TextField("");
			
			//create a new textfield for a OTP
			TextField code = new TextField("");
			
			//create a new textfield for a OTP role
			TextField role = new TextField("Roles: a, s or i");
			
			//create a new textfield for a response
			TextField response = new TextField("Awaiting Input");
			
			//text area for display
			TextArea area = new TextArea(""); 
			
			// create a stack pane
	        StackPane adminWindow = new StackPane();
	        
	        // add textfields
	        adminWindow.getChildren().add(user);
	        adminWindow.getChildren().add(code);
	        adminWindow.getChildren().add(role);
	        
	        Scene sc = new Scene(bPane, 1000, 700);
	        
	        //Create Labels
	        Label t = new Label("User Select");
	        Label one = new Label("One Time Code Gen");
	        
	        Button close = new Button("log out");
	        Button gen = new Button("Generate");
	        Button del = new Button("Delete");
	        Button list = new Button("List Users");
	        Button find = new Button("Find");
	        Button art = new Button("Articles");
	        Button upd = new Button("Update Role");
	        Button rem = new Button("Remove Role");
	        
	     

	        gridPane.add(t, 1, 0);
	        gridPane.add(response, 3, 0);
	        gridPane.add(one, 1, 3);
	        gridPane.add(user, 1, 2);
	        gridPane.add(find, 2, 2);
	        gridPane.add(code,1, 4);
	        gridPane.add(close, 0, 4);
	        gridPane.add(gen, 2, 5);
	        gridPane.add(role, 4, 3);
	        gridPane.add(del, 2, 3);
	        gridPane.add(list, 3, 3);
	        gridPane.add(art, 4, 4);
	        gridPane.add(area, 1, 5);
	        gridPane.add(upd, 2, 4);
	        gridPane.add(rem, 3, 4);
	        
	        
	        // set the scene
	        startScreen.setScene(sc);
	 
	        startScreen.show();
	        
	     
			
			
			gen.setOnAction(new EventHandler<ActionEvent>()
		    {
		      @Override      
		      //when the generate button is pressed
		      public void handle(ActionEvent e)
		      {
		    	  //set the one time password
		    	  if(role.getText().equals("a") || role.getText().equals("i") || role.getText().equals("s")) {
		    		  //System.out.print("otpppppp");
		    	  OTP = Math.random()*1000000;
		    	  code.setText(String.valueOf(OTP));
				  otpR = role.getText().charAt(0);
		    	  }
		      }
		    });
			//find button
			find.setOnAction((ActionEvent e) ->
		    {
		    	//checks if user is in array
		    	  for (int i = 0; i < users.size(); i++) {
		    		  //if the user is found
		    		  if(users.get(i).username.equals(user.getText())){
		    			  kill = true;
		    			  tem = users.get(i);
		    			  tem.id = i;
		    			  response.setText("User Found! Type Yes here to confirm");
		    		  }
		    		  if(kill == false) {
		    			  response.setText("User not found");
		    		  }
		    	  }
		    }); 
			
			//delete selected user
			del.setOnAction((ActionEvent e) ->
		    {
		    	if(response.getText().equals("Yes")) {
			        if( kill == true) {
			        	users.remove(users.indexOf(tem));
			        	kill = false;
			        	response.setText("User Deleted");
			        }
		    	}
		    });
			
			//update selected user's roles
			upd.setOnAction((ActionEvent e) ->
		    {
		    	boolean check = false;
		    	if(role.getText().equals("a") || role.getText().equals("i") || role.getText().equals("s")) {
		    		for (int i = 0; i < tem.roles.size(); i++) {
		    		      if(role.getText().equals(tem.roles.get(i))) {
		    		    	  response.setText("User already has role");
		    		    	  check = true;
		    		      }
		    		    }
		    		if(check == false) {
		    			users.get(tem.id).roles.add(role.getText().charAt(0));
		    			response.setText("Roles updated!");
		    		}
		    	}
		    });
			
			//remove selected user's roles
			rem.setOnAction((ActionEvent e) ->
		    {
		    	if(role.getText().equals("a") || role.getText().equals("i") || role.getText().equals("s")) {
		    		for (int i = 0; i < tem.roles.size(); i++) {
		    		      if(tem.roles.get(i).equals(role.getText().charAt(0))) {
		    		    	  users.get(tem.id).roles.remove(i);
		    		    	  response.setText("Role Removed!");
		    		      }
		    		    }
		    	}
		    });
			
			//close the window
			close.setOnAction((ActionEvent e) ->
		    {
		    	sceneIndex = 2;
		    	start(startScreen);
		        startScreen.close();      
		    }); 
			//list users
			list.setOnAction((ActionEvent e) ->
		    {
		    	String temp = "";
		    	for(int i = 0; i < users.size(); i++){
		    		String tmp = "User: " + users.get(i).username + " ,Role(s): ";
		    		if(users.get(i).roles.size() > 1) {
			    		for(int j = 0; j < users.get(i).roles.size()-1; j++) {
			    			tmp  = tmp + users.get(i).roles.get(j) + ", ";
			    		}
			    		tmp  = tmp + users.get(i).roles.get(users.get(i).roles.size()-1) + "\n";
		    		}
		    		else {
		    			tmp  = tmp + users.get(i).roles.get(0) + "\n";
		    		}
		    		temp = temp + tmp;
		    		System.out.print(temp);
		    	}
		    	area.setText(temp);
		    }); 
			//change to article window
			art.setOnAction((ActionEvent e) ->
		    {
		    	sceneIndex = 6;
		    	start(startScreen);
		        startScreen.close();      
		    });
		}
		
		//OTP login screen for the very first user
		public void OTPMenu(Stage startScreen) {
			System.out.println("OTP Login");
			startScreen.setTitle("OTP login");
			
			//initialize a grid setup for the windows
			BorderPane bPane = new BorderPane();
			GridPane gridPane = new GridPane();
			gridPane.setAlignment(Pos.CENTER);
			
			gridPane.setPadding(new Insets(5,5,5,5));
			gridPane.setHgap(10);
			gridPane.setVgap(10);
			bPane.setCenter(gridPane);
			
			//create a new textfield for admin username
			TextField uUser = new TextField("");
			
			//create a new textfield for admin password
			TextField uPass = new TextField("");
			
			TextField uPassConf = new TextField("");
			
			// create a stack pane
	        StackPane uWindow = new StackPane();
	        
	        // add textfields
	        uWindow.getChildren().add(uUser);
	        uWindow.getChildren().add(uPass);
	        
	        Scene sc = new Scene(bPane, 900, 500);
	        
	      //Create Labels
	        Label User = new Label("Username");
	        Label Pass = new Label("Password");
	        Label Pass2 = new Label("Password Confirm");
	        
	        Button back = new Button("Back");
	        Button sub = new Button("Submit");
	        
	        
	        //Add all controls to Grid
	        gridPane.add(User, 0, 0);
	        gridPane.add(Pass, 0, 1);
	        gridPane.add(Pass2, 0, 2);
	        gridPane.add(uUser, 1, 0);
	        gridPane.add(uPass, 1, 1);
	        gridPane.add(uPassConf,1, 2);
	        gridPane.add(back, 0, 3);
	        gridPane.add(sub, 2, 3);
	        
	        // set the scene
	        startScreen.setScene(sc);
	 
	        startScreen.show();
			
			User userer = new User();
			
			sub.setOnAction(new EventHandler<ActionEvent>()
		    {
		      @Override      
		      //when the submit button is pressed
		      public void handle(ActionEvent e)
		      {
		    	  //check if passwords match and username isnt empty
				if(uPass.getText().equals(uPassConf.getText()) && uUser.getText() != null && !uUser.getText().isEmpty()) {
		        	//set username to text
		        	userer.username = uUser.getText();
		    		//set password to text
		        	userer.password = uPass.getText();
		    		// adding the otp role to newly created user
					userer.roles.add(otpR);
		    		// adding first user to list of usernames in system
		    		users.add(userer);
		    		// displaying log out message and sending program to loginScreen
		    		System.out.println("New Account Created! Logging you out.");
		    		sceneIndex = 2;
		    		start(startScreen);
	                startScreen.close();
		        }
		        else
		        {
		        	System.out.print("Passwords don't match or username is empty!");
		        }
		      }
		    });
			
		}
		
		//basic user menu
				public void userMenu(Stage startScreen) {
					System.out.println("User Menu");
					startScreen.setTitle(String.format("Student Menu: Logged in as, %s", tem.username));
					
					//initialize a grid setup for the windows
					BorderPane bPane = new BorderPane();
					GridPane gridPane = new GridPane();
					gridPane.setAlignment(Pos.CENTER);
					
					gridPane.setPadding(new Insets(5,5,5,5));
					gridPane.setHgap(10);
					gridPane.setVgap(10);
					bPane.setCenter(gridPane);
					
					
					// create a stack pane
			        StackPane uWindow = new StackPane();
			        
			        
			        Scene sc = new Scene(bPane, 900, 500);
			        
			      //Create Labels
			        Label User = new Label(String.format("Welcome Student: %s", tem.username));
			        //logout button
			        Button back = new Button("logout");
			        //prefences button
			        Button prof = new Button("Edit Profile");
			        //article buttons
			        Button view = new Button("View All Articles");
			        Button ser = new Button("Search by Keyword");
			        
			        //textfields
			        TextField tex = new TextField("Type Here");
			        TextArea texArea = new TextArea("");
			        texArea.setWrapText(true);

			        //Add all controls to Grid
			        gridPane.add(User, 0, 0);
			        gridPane.add(ser, 3, 1);
			        gridPane.add(view, 2, 0);
			        gridPane.add(tex, 2, 1);
			        gridPane.add(texArea, 2, 2);
			        //to be added, edit user email, pref name, etc
			        //gridPane.add(prof, 1, 0);
			        gridPane.add(back, 3, 0);
			        
			        // set the scene
			        startScreen.setScene(sc);
			 
			        startScreen.show();
					
					User userer = new User();
					
					back.setOnAction(new EventHandler<ActionEvent>()
				    {
				      @Override      
				      //when the submit button is pressed
				      public void handle(ActionEvent e)
				      {
				    	  System.out.println("Logging you out.");
				    		sceneIndex = 2;
				    		start(startScreen);
			                startScreen.close();
				      }
				    });
					
					prof.setOnAction(new EventHandler<ActionEvent>()
				    {
				      @Override      
				      //when the submit button is pressed
				      public void handle(ActionEvent e)
				      {
				    		sceneIndex = 8;
				    		start(startScreen);
			                startScreen.close();
				      }
				    });
					
					view.setOnAction(new EventHandler<ActionEvent>()
				    {
				      @Override      
				      //when the submit button is pressed
				      public void handle(ActionEvent e)
				      {
				    	  databaseHelper = new DatabaseHelper();
				  		try { 
				  			
				  			databaseHelper.connectToDatabase();  // Connect to the database

				  			// Check if the database is empty
				  			if (databaseHelper.isDatabaseEmpty()) {
				  				System.out.println( "In-Memory Database  is empty" );
				  				
				  				
				  			}
				  			else {
				  				texArea.setText(databaseHelper.displayArticles());
				  			}
				  		}
						 catch (Exception e1) {
							System.err.println("Database error: " + e1.getMessage());
							e1.printStackTrace();
						}
						finally {
							//System.out.println("Good Bye!!");
							databaseHelper.closeConnection();
						}
				      
				      }	  
				     });
					
					ser.setOnAction(new EventHandler<ActionEvent>()
				    {
				      @Override      
				      //when the submit button is pressed
				      public void handle(ActionEvent e)
				      {
				    	  databaseHelper = new DatabaseHelper();
				  		try { 
				  			
				  			databaseHelper.connectToDatabase();  // Connect to the database

				  			// Check if the database is empty
				  			if (databaseHelper.isDatabaseEmpty()) {
				  				System.out.println( "In-Memory Database  is empty" );
				  				
				  				
				  			}
				  			else {
				  				texArea.setText(databaseHelper.displayByKeyword(tex.getText()));
				  			}
				  		}
						 catch (Exception e1) {
							System.err.println("Database error: " + e1.getMessage());
							e1.printStackTrace();
						}
						finally {
							//System.out.println("Good Bye!!");
							databaseHelper.closeConnection();
						}
				      
				      }	  
				     });
					
				}
				
				//basic instructor menu
				public void instrMenu(Stage startScreen) {
					System.out.println("Instructor Menu");
					startScreen.setTitle(String.format("Instructor Menu: Logged in as, %s", tem.username));
					
					//initialize a grid setup for the windows
					BorderPane bPane = new BorderPane();
					GridPane gridPane = new GridPane();
					gridPane.setAlignment(Pos.CENTER);
					
					gridPane.setPadding(new Insets(5,5,5,5));
					gridPane.setHgap(10);
					gridPane.setVgap(10);
					bPane.setCenter(gridPane);
					
					
					// create a stack pane
			        StackPane uWindow = new StackPane();
			        
			        
			        Scene sc = new Scene(bPane, 900, 500);
			        
			      //Create Labels
			        Label User = new Label(String.format("Welcome Instructor: %s", tem.username));
			        //logout button
			        Button back = new Button("logout");
			        Button ar = new Button("Articles");
			        

			        //Add all controls to Grid
			        gridPane.add(User, 0, 0);
			        gridPane.add(ar, 1, 0);
			        gridPane.add(back, 2, 0);
			        
			        // set the scene
			        startScreen.setScene(sc);
			 
			        startScreen.show();
					
					User userer = new User();
					
					back.setOnAction(new EventHandler<ActionEvent>()
				    {
				      @Override      
				      //when the submit button is pressed
				      public void handle(ActionEvent e)
				      {
				    	  System.out.println("Logging you out.");
				    		sceneIndex = 2;
				    		start(startScreen);
			                startScreen.close();
				      }
				    });
					
					ar.setOnAction(new EventHandler<ActionEvent>()
				    {
				      @Override      
				      //when the submit button is pressed
				      public void handle(ActionEvent e)
				      {
				    		sceneIndex = 6;
				    		start(startScreen);
			                startScreen.close();
				      }
				    });
					
				}
				
				//main article menu to select what to do
				public void artMenu(Stage startScreen) {
					System.out.println("Article Menu");
					startScreen.setTitle(String.format("Article Menu: Logged in as, %s", tem.username));
					
					//initialize a grid setup for the windows
					BorderPane bPane = new BorderPane();
					GridPane gridPane = new GridPane();
					gridPane.setAlignment(Pos.CENTER);
					
					gridPane.setPadding(new Insets(5,5,5,5));
					gridPane.setHgap(10);
					gridPane.setVgap(10);
					bPane.setCenter(gridPane);
					
					
					// create a stack pane
			        StackPane uWindow = new StackPane();
			        
			        
			        Scene sc = new Scene(bPane, 900, 500);
			        
			      //Create Labels
			        Label Article = new Label("Article Menu");
			        //logout button
			        Button back = new Button("Back");
			        Button article = new Button("Create Article");
			        Button view = new Button("View All Articles");
			        Button up = new Button("Update Article");
			        Button del = new Button("Delete Article(s)");
			        Button ser = new Button("Search by Keyword");
			        
			        TextField tex = new TextField("Type Here");
			        TextArea texArea = new TextArea("");
			        texArea.setWrapText(true);
			        
			     // Add new Backup and Restore buttons for Phase 2 requirements
				    Button backupBtn = new Button("Backup Data");
				    Button restoreBtn = new Button("Restore Data");

			        

			        //Add all controls to Grid
			        gridPane.add(Article, 0,0);
			        gridPane.add(article, 1, 0);
			        gridPane.add(view, 2, 0);
			        gridPane.add(up, 0, 1);
			        gridPane.add(del, 1, 1);
			        gridPane.add(tex, 2, 1);
			        gridPane.add(back, 3, 0);
			        gridPane.add(ser, 3, 1);
			        gridPane.add(texArea, 2, 2);
			        gridPane.add(backupBtn, 3, 2);  // Add Backup button
				    gridPane.add(restoreBtn, 3, 3);  // Add Restore button

			        
			        // set the scene
			        startScreen.setScene(sc);
			 
			        startScreen.show();
					
					User structor = new User();
					
				    // Define actions for Backup and Restore buttons
				    backupBtn.setOnAction((ActionEvent e) -> backupData());
				    restoreBtn.setOnAction((ActionEvent e) -> restoreData());

						view.setOnAction(new EventHandler<ActionEvent>()
					    {
					      @Override      
					      //when the submit button is pressed
					      public void handle(ActionEvent e)
					      {
					    	  databaseHelper = new DatabaseHelper();
					  		try { 
					  			
					  			databaseHelper.connectToDatabase();  // Connect to the database

					  			// Check if the database is empty
					  			if (databaseHelper.isDatabaseEmpty()) {
					  				System.out.println( "In-Memory Database  is empty" );
					  				
					  				
					  			}
					  			else {
					  				texArea.setText(databaseHelper.displayArticles());
					  			}
					  		}
							 catch (Exception e1) {
								System.err.println("Database error: " + e1.getMessage());
								e1.printStackTrace();
							}
							finally {
								//System.out.println("Good Bye!!");
								databaseHelper.closeConnection();
							}
					      
					      }	  
					     });
						
						del.setOnAction(new EventHandler<ActionEvent>()
					    {
					      @Override      
					      //when the submit button is pressed
					      public void handle(ActionEvent e)
					      {
					    	  databaseHelper = new DatabaseHelper();
					  		try { 
					  			
					  			databaseHelper.connectToDatabase();  // Connect to the database

					  			// Check if the database is empty
					  			if (databaseHelper.isDatabaseEmpty()) {
					  				System.out.println( "In-Memory Database  is empty" );
					  				
					  				
					  			}
					  			else {
					  				databaseHelper.deleteArticle((Long.parseLong(tex.getText().trim())));
					  			}
					  		}
							 catch (Exception e1) {
								System.err.println("Database error: " + e1.getMessage());
								e1.printStackTrace();
							}
							finally {
								//System.out.println("Good Bye!!");
								databaseHelper.closeConnection();
							}
					      
					      }	  
					     });
						
						up.setOnAction(new EventHandler<ActionEvent>()
					    {
					      @Override      
					      //when the submit button is pressed
					      public void handle(ActionEvent e)
					      {
					    	  databaseHelper = new DatabaseHelper();
					  		try { 
					  			
					  			databaseHelper.connectToDatabase();  // Connect to the database

					  			// Check if the database is empty
					  			if (databaseHelper.isDatabaseEmpty()) {
					  				System.out.println( "In-Memory Database  is empty" );
					  				
					  				
					  			}
					  			else {
					  				if(databaseHelper.articleExists(Long.parseLong(tex.getText().trim()))) {
					  					tempid = Long.parseLong(tex.getText().trim());
							    		//change to update screen
							    		    sceneIndex = 10;
								    		start(startScreen);
							                startScreen.close();
							                databaseHelper.closeConnection();
							    	  }
					  				else {
					  					System.out.println("Please input a valid ID");
					  				}
					  			}
					  		}
							 catch (Exception e1) {
								System.err.println("Database error: " + e1.getMessage());
								e1.printStackTrace();
							}
							finally {
								//System.out.println("Good Bye!!");
								databaseHelper.closeConnection();
							}
					      
					      }	  
					     });
						
							
						ser.setOnAction(new EventHandler<ActionEvent>()
					    {
					      @Override      
					      //when the submit button is pressed
					      public void handle(ActionEvent e)
					      {
					    	  databaseHelper = new DatabaseHelper();
					  		try { 
					  			
					  			databaseHelper.connectToDatabase();  // Connect to the database

					  			// Check if the database is empty
					  			if (databaseHelper.isDatabaseEmpty()) {
					  				System.out.println( "In-Memory Database  is empty" );
					  				
					  				
					  			}
					  			else {
					  				texArea.setText(databaseHelper.displayByKeyword(tex.getText()));
					  			}
					  		}
							 catch (Exception e1) {
								System.err.println("Database error: " + e1.getMessage());
								e1.printStackTrace();
							}
							finally {
								//System.out.println("Good Bye!!");
								databaseHelper.closeConnection();
							}
					      
					      }	  
					     });	
						
					      
					
					back.setOnAction(new EventHandler<ActionEvent>()
				    {
				      @Override      
				      //when the submit button is pressed
				      public void handle(ActionEvent e)
				      {
				    	  //check if user is an admin
				    	  if(tem.roles.contains('a')) {
				    		//back to admin screen
				    		    sceneIndex = 3;
					    		start(startScreen);
				                startScreen.close();
				    		  
				    	  }
				    	  //they are an instructor without admin roles
				    	  else {
				    		  //back to instructor screen
				    		    sceneIndex = 7;
					    		start(startScreen);
				                startScreen.close();
				    	  }
				      }
				    });
					
					article.setOnAction(new EventHandler<ActionEvent>()
				    {
				      @Override      
				      //when the submit button is pressed
				      public void handle(ActionEvent e)
				      {
				    	  //change to create article screen
				    	  sceneIndex = 9;
				    		start(startScreen);
			                startScreen.close();
				      }
				    });
					
					
					
				}
				
				// article creation menu 
				public void createArMenu(Stage startScreen) {
					System.out.println("Article Creation");
					startScreen.setTitle(String.format("Article Creation: Logged in as, %s", tem.username));
					
					//initialize a grid setup for the windows
					BorderPane bPane = new BorderPane();
					GridPane gridPane = new GridPane();
					gridPane.setAlignment(Pos.TOP_CENTER);
					
					gridPane.setPadding(new Insets(5,5,5,5));
					gridPane.setHgap(10);
					gridPane.setVgap(10);
					bPane.setCenter(gridPane);
					
					
					// create a stack pane
			        StackPane uWindow = new StackPane();
			        
			        
			        Scene sc = new Scene(bPane, 900, 500);
			        
			      //Create Labels
			        Label head = new Label("Header");
			        Label titl = new Label("Title");
			        Label des = new Label("Description");
			        Label keys = new Label("Keywords");
			        Label bod = new Label("Body");
			        Label ref = new Label("References");
			        Label oth = new Label("Other");
			        
			        //all the textfields
			        TextField headt = new TextField("");
			        TextField titlt = new TextField("");
			        TextField dest = new TextField("");
			        TextField keyst = new TextField("");
			        TextField bodt = new TextField("");
			        TextField reft = new TextField("");
			        TextField otht = new TextField("");
			        
			        //logout button
			        Button back = new Button("Back");
			        Button sub = new Button("Submit");
			        
			        

			        //Add all controls to Grid
			        //left side
			        gridPane.add(head, 0,0);
			        gridPane.add(titl, 0,1);
			        gridPane.add(des, 0,2);
			        gridPane.add(keys, 0,3);
			        gridPane.add(bod, 0,4);
			        gridPane.add(ref, 0,5);
			        gridPane.add(oth, 0,6);
			        
			        //right side
			        gridPane.add(headt, 1,0);
			        gridPane.add(titlt, 1,1);
			        gridPane.add(dest, 1,2);
			        gridPane.add(keyst, 1,3);
			        gridPane.add(bodt, 1,4);
			        gridPane.add(reft, 1,5);
			        gridPane.add(otht, 1,6);
			        
			        gridPane.add(back, 7, 0);
			        gridPane.add(sub, 7, 1);
			        
			        // set the scene
			        startScreen.setScene(sc);
			 
			        startScreen.show();
				
					
					back.setOnAction(new EventHandler<ActionEvent>()
				    {
				      @Override      
				      //when the submit button is pressed
				      public void handle(ActionEvent e)
				      {
		    		    sceneIndex = 6;
			    		start(startScreen);
		                startScreen.close();
				      }
				    });
					
					sub.setOnAction(new EventHandler<ActionEvent>()
				    {
				      @Override      
				      //when the submit button is pressed
				      public void handle(ActionEvent e)
				      {
				    	  databaseHelper = new DatabaseHelper();
				  		try { 
				  			
				  			databaseHelper.connectToDatabase();  // Connect to the database

				  			// Check if the database is empty
				  			if (databaseHelper.isDatabaseEmpty()) {
				  				System.out.println( "In-Memory Database  is empty, Adding article" );
				  				//create article and add to new database
				  				if(headt.getText().isBlank()) {
				  					System.out.println( "Please Include a Header" );
				  				}
				  				else {
				  					databaseHelper.register(headt.getText(), titlt.getText(), dest.getText(), keyst.getText(), bodt.getText(), reft.getText(), otht.getText());
				  					System.out.println( "Article Added!" );
				  					sceneIndex = 6;
						    		start(startScreen);
					                startScreen.close();
				  				}

				  			}
				  			//add to existing database
				  			else {
				  				if(headt.getText().isBlank()) {
				  					System.out.println( "Please Include a Header" );
				  				}
				  				else {
				  					databaseHelper.register(headt.getText(), titlt.getText(), dest.getText(), keyst.getText(), bodt.getText(), reft.getText(), otht.getText());
				  					System.out.println( "Article Added!" );
				  					sceneIndex = 6;
						    		start(startScreen);
					                startScreen.close();
				  				}
				  			}
				  		}
						 catch (Exception e1) {
							System.err.println("Database error: " + e1.getMessage());
							e1.printStackTrace();
						}
						finally {
							//System.out.println("Good Bye!!");
							databaseHelper.closeConnection();
						}
				      
				      }	  
				     });
					
				}
				
				// article update menu 
				public void updArMenu(Stage startScreen) {
					System.out.println("Article Update");
					startScreen.setTitle(String.format("Article Update: Logged in as, %s", tem.username));
					
					//initialize a grid setup for the windows
					BorderPane bPane = new BorderPane();
					GridPane gridPane = new GridPane();
					gridPane.setAlignment(Pos.TOP_CENTER);
					
					gridPane.setPadding(new Insets(5,5,5,5));
					gridPane.setHgap(10);
					gridPane.setVgap(10);
					bPane.setCenter(gridPane);
					
					
					// create a stack pane
			        StackPane uWindow = new StackPane();
			        
			        
			        Scene sc = new Scene(bPane, 900, 500);
			        
			      //Create Labels
			        Label head = new Label("Header");
			        Label titl = new Label("Title");
			        Label des = new Label("Description");
			        Label keys = new Label("Keywords");
			        Label bod = new Label("Body");
			        Label ref = new Label("References");
			        Label oth = new Label("Other");
			        
			        //all the textfields
			        TextField headt = new TextField("");
			        TextField titlt = new TextField("");
			        TextField dest = new TextField("");
			        TextField keyst = new TextField("");
			        TextField bodt = new TextField("");
			        TextField reft = new TextField("");
			        TextField otht = new TextField("");
			        
			        //logout button
			        Button back = new Button("Back");
			        Button sub = new Button("Submit");
			        
			        

			        //Add all controls to Grid
			        //left side
			        gridPane.add(head, 0,0);
			        gridPane.add(titl, 0,1);
			        gridPane.add(des, 0,2);
			        gridPane.add(keys, 0,3);
			        gridPane.add(bod, 0,4);
			        gridPane.add(ref, 0,5);
			        gridPane.add(oth, 0,6);
			        
			        //right side
			        gridPane.add(headt, 1,0);
			        gridPane.add(titlt, 1,1);
			        gridPane.add(dest, 1,2);
			        gridPane.add(keyst, 1,3);
			        gridPane.add(bodt, 1,4);
			        gridPane.add(reft, 1,5);
			        gridPane.add(otht, 1,6);
			        
			        gridPane.add(back, 7, 0);
			        gridPane.add(sub, 7, 1);
			        
			        // set the scene
			        startScreen.setScene(sc);
			 
			        startScreen.show();
				
					
					back.setOnAction(new EventHandler<ActionEvent>()
				    {
				      @Override      
				      //when the submit button is pressed
				      public void handle(ActionEvent e)
				      {
		    		    sceneIndex = 6;
			    		start(startScreen);
		                startScreen.close();
				      }
				    });
					
					sub.setOnAction(new EventHandler<ActionEvent>()
				    {
				      @Override      
				      //when the submit button is pressed
				      public void handle(ActionEvent e)
				      {
				    	  databaseHelper = new DatabaseHelper();
				  		try { 
				  			
				  			databaseHelper.connectToDatabase();  // Connect to the database

				  			// Check if the database is empty
				  			if (databaseHelper.isDatabaseEmpty()) {
				  				System.out.println( "In-Memory Database  is empty" );

				  			}
				  			else {
				  				if(headt.getText().isBlank()) {
				  					System.out.println( "Please Include a Header" );
				  				}
				  				else {
				  					databaseHelper.updateArticle(tempid, headt.getText(), titlt.getText(), dest.getText(), keyst.getText(), bodt.getText(), reft.getText(), otht.getText());
				  				}
				  			}
				  		}
						 catch (Exception e1) {
							System.err.println("Database error: " + e1.getMessage());
							e1.printStackTrace();
						}
						finally {
							//System.out.println("Good Bye!!");
							databaseHelper.closeConnection();
						}
				      
				      }	  
				     });
					
				}
	
	public static void main(String[] args) {
		launch(args);
	
	}
	
}
