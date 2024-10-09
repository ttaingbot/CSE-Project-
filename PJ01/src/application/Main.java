package application;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	private static ArrayList<User> users;
	
	
	
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
			setPassword(scanner, user);
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
	
	// the goal of this function is to check that the inputted line is a single char of a type allowed
	// returns a boolean if the char follows all of required conditions
	public static boolean checkChar(Scanner scanner, String allowed, String input) {
		
		
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
	
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		users = new ArrayList<User>();
		Admin firstAdmin = new Admin();
		
		// initial login to set first user as admin
		// reading first input, which is username
		System.out.println("Hello first user, please enter in a username below: ");
		firstAdmin.username = scanner.nextLine();
		
		// getting password
		setPassword(scanner, firstAdmin);
		finishSettingUp(scanner, firstAdmin);
		
		// adding 0 role (admin) to newly created user
		firstAdmin.roles.add('a');
		
		// adding first user to list of usernames in system
		users.add(firstAdmin);
		
		// displaying log out message and sending program to loginScreen
		System.out.println("New Account Created! Logging you out.");
		loginScreen(scanner);
		
		
		
		
		
		scanner.close();
	}
	
}
