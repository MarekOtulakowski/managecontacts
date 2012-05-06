/*
 * build 008
 */
package com.blogspot.marekotulakowski.managecontacts;

public class Contact {  
   
	 //Variables  
	 private String name;  
	 private String phoneNumber;  
	 private TypeContact typeContact;  
	   
	 public enum TypeContact { 
		 ContactFromSIMcard,  
		 ContactFromAndroid  
	 };  
	   
	 public String getName() {  
		 return name;  
	 }  
	   
	 public String getPhoneNumber() {  
		 return phoneNumber;  
	 }  
	   
	 public TypeContact getTypeContact() {  
		 return typeContact;  
	 }  
	   
	 public void setName(String _name) {  
		 name = _name;  
	 }  
	   
	 public void setPhoneNumber(String _telephone) {  
		 phoneNumber = _telephone;  
	 }  
	   
	 public void setTypeContact(TypeContact _typeContact) {  
		 typeContact = _typeContact;  
	 }  
	   
	 Contact(String _name, String _telephone, TypeContact _typeContact) {  
		  name = _name;  
		  phoneNumber = _telephone;  
		  typeContact = _typeContact;  
	 }   
}  
