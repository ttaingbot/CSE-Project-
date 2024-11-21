package project;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;


class AutoTest {

	@Test
	public void SN1() {
		//create test user
		User testUser = new User();	
		testUser.username = "user";
		testUser.password = "password";
		testUser.id = 1;
		testUser.roles.add('a');
		testUser.groups.add("general");
		
		assertEquals("general", getGroups(testUser));
	}
	
	@Test
	public void SN2() {
		//create test user
		User testUser = new User();	
		testUser.username = "user";
		testUser.password = "password";
		testUser.id = 1;
		testUser.roles.add('s');
		testUser.groups.add("general, Special, Intermediate");
		
		assertEquals("general, Special, Intermediate", getGroups(testUser));
	}
	
	@Test
	public void SN3() {
		//create test user
		User testUser = new User();	
		testUser.username = "user";
		testUser.password = "password";
		testUser.id = 1;
		testUser.roles.add('i');
		testUser.groups.add("general, Advanced");
		
		assertEquals("general, Special, Advanced", getGroups(testUser));
	}
	
	public String getGroups(User users) {
		String temp = "";
		for(int i = 0; i < users.groups.size(); i++) {
			if(users.groups.size() > 1) {
				temp = temp + users.groups.get(i) + ", ";
			}
			else {
				temp = temp + users.groups.get(i);
			}
		}
		return temp;
	}
}

