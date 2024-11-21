package project;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class User {
	// user account information
	String email;
	String username;
	String password; // says non-string data type in requirements but not sure what the project means by that
	boolean oneTimePassword;
	DateTimeFormatter oneTimeValid;
	String firstName;
	String middleName;
	String lastName;
	String preferredName;
	String topic;
	int id; //id to be used to update users
	ArrayList<String> groups = new ArrayList<String>();
	
	// going to arrange roles by char - a=admin, s=student, i=instructor
	ArrayList<Character> roles = new ArrayList<Character>();
	char currentRole;
	
	// boolean to quickly check if they have an preferred name
	boolean prefName;
	
	// constructor
	public User() {
		topic = "Intermediate";
		prefName = false;
	}
	
}
