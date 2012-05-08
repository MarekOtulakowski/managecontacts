/*
 * build 009
 */
package com.blogspot.marekotulakowski.managecontacts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import com.blogspot.marekotulakowski.managecontacts.Contact.TypeContact;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class ManageContacts extends Activity {
	
	//Variables  
	private Button Button_Import;
	private Button Button_Export;  
	private Button Button_Close;  
	private Spinner Spinner_KindOfContacts;
	private Spinner Spinner_ContactSource;
	private TextView TextView_Result;
	private Cursor cursor_ContactsFromAndroid;  
	private Cursor cursor_ContactsFromSIM;  
	private ArrayList<Contact> arrayList_Contacts;  
	private Contact contact; 
	private String string_FileNamePrefix = "ExportContacts";
	private String string_FileNameSurfix = ".cvs";
	private Boolean bool_IsCsvFormat = true;
	private Integer int_ContactSourceType = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //assign controls
        this.Button_Import = (Button)this.findViewById(R.id.button_ImpContacs);
        this.Button_Export = (Button)this.findViewById(R.id.button_ExpContacs);
        this.Button_Close = (Button)this.findViewById(R.id.button_CloseApplication);
        this.Spinner_KindOfContacts = (Spinner)this.findViewById(R.id.spinner_kindOfContact);
        this.TextView_Result = (TextView)this.findViewById(R.id.textView_Result);
        this.Spinner_ContactSource = (Spinner)this.findViewById(R.id.spinner_contactSource);
        
        //assign events
        this.Button_Import.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	showDialog("clicked import button!\nNot implemed function, yet!");
            	
            	writeContactsToPhone();
            	//showDialog("success add new contact!");
            }
        });
        this.Button_Export.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {     
            	
            	//user choose output format
            	if (Spinner_KindOfContacts.getSelectedItemPosition() == 0) {            		
            		//CSV, default value
            		bool_IsCsvFormat = true;             		
            	} else if (Spinner_KindOfContacts.getSelectedItemPosition() == 1) {            		
            		//VCF
            		bool_IsCsvFormat = false;             		
            	} 
            	
            	//user choose type of contact
            	if (Spinner_ContactSource.getSelectedItemPosition() == 0) {            		
            		//0 only Android Contacts
            		int_ContactSourceType = 0;            		
            	} else if (Spinner_ContactSource.getSelectedItemPosition() == 1) {            		
            		//1 only SIM Card Contacts
            		int_ContactSourceType = 1;            		
            	} else if (Spinner_ContactSource.getSelectedItemPosition() == 2) {            		
            		//2 both Android and SIM Card Contacts
            		int_ContactSourceType = 2;            		
            	}            
            	
            	//run selected function
            	if (bool_IsCsvFormat) {            		
            		try {       
            			writeContacts();
            		}
            		catch (Exception e) {            			
            			TextView_Result.setText("Export to CVS unsuccessfully completed!");
            			showDialog("Error export, detail:\n" + e.getMessage());            			
            		}
            	} else {            		
            		try {            			
            			writeContacts();         			
            		}
            		catch (Exception e) {            			
            			TextView_Result.setText("Export to VCF unsuccessfully completed!");
            			showDialog("Error export, detail:\n" + e.getMessage());            			
            		}
            	}
            }
        });
        this.Button_Close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	showQuestionCloseApp("Are you sure, close this application?");
            }
        });
        
        //set properties
        String[] spinnerItems = new String[] {"CVS",
        									  "VCF"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, 
												  android.R.layout.simple_spinner_item, 
												  spinnerItems);
	    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    Spinner_KindOfContacts.setAdapter(spinnerAdapter);
	    
	    String[] spinnerItemsContactSource = new String[] {"Only Android Contact",
	    		        								   "Only SIM Card Contact",
	    		        								   "Both Android and SIM Contact"};
ArrayAdapter<String> spinnerAdapterContactSource = new ArrayAdapter<String>(this, 
												  android.R.layout.simple_spinner_item, 
												  spinnerItemsContactSource);
		spinnerAdapterContactSource.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner_ContactSource.setAdapter(spinnerAdapterContactSource);
    }
    
    private void writeContacts() {
		if (int_ContactSourceType == 0) {
			writeContactsToSdCard(SourceContact.AndroidContact);    
		} else if (int_ContactSourceType == 1) {
			writeContactsToSdCard(SourceContact.SIMcardContact);
		} else if (int_ContactSourceType == 2) {
			writeContactsToSdCard(SourceContact.SIMandAndroid);
		}   	
    }
    
    private void showDialog(String message) {  
    	new AlertDialog.Builder(this).setTitle("Program Info").setMessage(message).
    	setIcon(android.R.drawable.ic_dialog_info).setPositiveButton("OK", null).show();  
    }  
    
    private void showQuestionCloseApp(String questionMessage) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Program Question").setMessage(questionMessage)
			   .setPositiveButton("Yes", showDialogQuestionListener)
			   .setNegativeButton("No", showDialogQuestionListener)
			   .show();
    }
    
    DialogInterface.OnClickListener showDialogQuestionListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
            case DialogInterface.BUTTON_POSITIVE:
            	finish();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                break;
            }
        }
    };
    
    private Cursor getContactsFromAndroid() {  
        Uri uri = ContactsContract.Contacts.CONTENT_URI;  
        String[] projection = new String[] {  
								  ContactsContract.Contacts._ID,  
								  ContactsContract.Contacts.DISPLAY_NAME  
        };  
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1'";   
        //1 invisible  
        //0 visible  
          
        String[] selectionArgs = null;  
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";  
  
        return managedQuery(uri, 
							projection, 
							selection, 
							selectionArgs, 
							sortOrder);  
    }  
    
    private Cursor getContactsFromSIMcard() {  
    	String simUrl = "content://icc/adn";  
    	Intent intent = new Intent();  
    	intent.setData(Uri.parse(simUrl));  
    	Uri uri = intent.getData();  
    	Cursor mCursor = this.getContentResolver().query(uri, 
														 null, 
														 null, 
														 null, 
														 null);  
    	return mCursor;  
    }  
    
    private ArrayList<Contact> getArrayListContactsFromAndroid() {  
        ArrayList<Contact> arContactFromAndroid = new ArrayList<Contact>();  
        int iContactsCountFromAndroid = getContactsFromAndroid().getCount();  
        cursor_ContactsFromAndroid = getContactsFromAndroid();  
          
        if (cursor_ContactsFromAndroid.moveToFirst()) {  
	        String contactId, phoneNumber, contactName  = "";  
	        while (iContactsCountFromAndroid > 0) {  
				contactId = phoneNumber = contactName = "";  
				contactId = cursor_ContactsFromAndroid.getString(cursor_ContactsFromAndroid.getColumnIndex(   
														   ContactsContract.Contacts._ID));  
					
				Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
														   null,   
														   ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, 
														   null, 
														   null);   
				while (phones.moveToNext()) {   
					phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));                   
				}   
				phones.close();  
					 
				contactName = cursor_ContactsFromAndroid.getString(cursor_ContactsFromAndroid.getColumnIndex(  
															 ContactsContract.Contacts.DISPLAY_NAME));  
					
				contact = new Contact(contactName, 
										 phoneNumber, 
										 TypeContact.ContactFromAndroid);  
				arContactFromAndroid.add(contact);  
				contact = null;  
					
				iContactsCountFromAndroid--;  
				cursor_ContactsFromAndroid.moveToNext();  
	        }  
        }  
        cursor_ContactsFromAndroid.close();       
          
        return arContactFromAndroid;  
    }  
     
    private ArrayList<Contact> getArrayListContactsFromSIM() {  
		ArrayList<Contact> arContactFromSIM = new ArrayList<Contact>();  
		int iCountContactFromSIMcard = getContactsFromSIMcard().getCount();  
		cursor_ContactsFromSIM = getContactsFromSIMcard();  
	  
		if (cursor_ContactsFromSIM.moveToFirst()) {       
			 String contactName, phoneNumber = "";  
			 while (iCountContactFromSIMcard  > 0) {  
				contactName = phoneNumber = "";  
					
				contactName = cursor_ContactsFromSIM.getString(0);  
				phoneNumber = cursor_ContactsFromSIM.getString(1);  
				// 0 - name  
				// 1 - number  
				// 2 - email  
				// 3 - _id  
					
				contact = new Contact(contactName, phoneNumber, TypeContact.ContactFromSIMcard);  
				arContactFromSIM.add(contact);  
				contact = null;  
					
				iCountContactFromSIMcard--;  
				cursor_ContactsFromSIM.moveToNext();  
			}  
		}  
		cursor_ContactsFromSIM.close();   
		  
		return arContactFromSIM;  
	}  
    
    private void writeContactsToPhone() {
    	
    	/*
    	Intent intent = new Intent(ContactsContract.Intents.SHOW_OR_CREATE_CONTACT, Uri.parse("tel:" + "0123456789"));
    	intent.putExtra(ContactsContract.Intents.Insert.NAME, "New Contact");
    	intent.putExtra(ContactsContract.Intents.Insert.NOTES, "Notes");
    	intent.putExtra(ContactsContract.Intents.Insert.FULL_MODE, "Full Name");
    	intent.putExtra(ContactsContract.Intents.Insert.EMAIL, "nick@domain.com");
    	intent.putExtra(ContactsContract.Intents.Insert.PHONE, "0123456789");
    	intent.putExtra(ContactsContract.Intents.EXTRA_FORCE_CREATE, true);
    	startActivity(intent);
    	setResult(RESULT_OK); */
    	
    	//to fix above code, always display confirm for user (edit new contact "done" and "revert")
    }
	
    private void writeContactsToSdCard(SourceContact sourceContact) { 
		arrayList_Contacts = new ArrayList<Contact>();
		  
		switch (sourceContact) {
			case SIMcardContact: {
				arrayList_Contacts.addAll(getArrayListContactsFromSIM());    
			}
			case AndroidContact: {
				arrayList_Contacts.addAll(getArrayListContactsFromAndroid());
			}
			case SIMandAndroid: {
				arrayList_Contacts.addAll(getArrayListContactsFromSIM());
				arrayList_Contacts.addAll(getArrayListContactsFromAndroid());
			}
		}
		  
		if (arrayList_Contacts.size() < 1) {
			showDialog("Not found any contacts on SIM and Android, exit");
			return;
		}
			 
		File sdCard = Environment.getExternalStorageDirectory(); 
		File dir = new File (sdCard.getAbsolutePath() + 
							 File.separator + 
							 "ManageContacts"); 
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		if (bool_IsCsvFormat) { //CVS format
			
			//delete old file if exists
			File oldFile = new File(dir + 
									File.separator + 
									string_FileNamePrefix + 
									string_FileNameSurfix);
			if(oldFile.exists()) {
				oldFile.delete();
			}
			
			try {
				File file = new File(dir + 
									 File.separator + 
									 string_FileNamePrefix + 
									 string_FileNameSurfix);
				file.createNewFile();
			  
				if (!file.exists()) {
					showDialog("Cannot create/override file on sdcard, exit");
					return;
				} 
				  
				FileOutputStream f = new FileOutputStream(file);
				//header in CVS file
				f.write("Contact name,Phone number,Source".getBytes());
				f.write("\r\n".getBytes());
			   
				for(Contact Contact_OneContact: arrayList_Contacts) {
					//CVS file (semicolon separate)
					f.write(Contact_OneContact.getName().getBytes());
					f.write(",".getBytes());
					f.write(Contact_OneContact.getPhoneNumber().getBytes());
					f.write(",".getBytes());
					f.write(Contact_OneContact.getTypeContact().toString().getBytes());
					f.write("\r\n".getBytes());
				}
				f.flush();
				f.close();
			} catch (IOException e) {
				showDialog("Error during writing file on SD card, exit");
				e.printStackTrace();
			} finally {
				TextView_Result.setText("Export done successfull!\r\n(Output file is in\r\n" + 
										dir.toString() + "/ "+
										string_FileNamePrefix + 
										string_FileNameSurfix + 
										")");
				showDialog("Export done successfull! (" + 
						   dir.toString() + "/"+
						   string_FileNamePrefix + 
						   string_FileNameSurfix + 
						   ")");
			}
		} else { //VCF format		
			try {	
				Integer noContact = 1;
				for (Contact Contact_OneContact: arrayList_Contacts) {
					String fileName = dir + 
							      	  File.separator + 
							      	  (noContact++).toString() + 
							      	  ".vcf";
					File file = new File(fileName);
					if (file.exists()) file.delete();
					file.createNewFile();				  
					FileOutputStream f = new FileOutputStream(file);
					
					//header in VCF file
					f.write("BEGIN:VCARD".getBytes());
					f.write("\r\n".getBytes());
					f.write("VERSION:2.1".getBytes());
					f.write("\r\n".getBytes());
					
					//body in VCF file
					f.write("N:".getBytes());
					f.write(Contact_OneContact.getName().getBytes());
					f.write(";;;;".getBytes());
					
					f.write("\r\n".getBytes());
					
					f.write("FN:".getBytes());
					f.write(Contact_OneContact.getName().getBytes());
					f.write("\r\n".getBytes());
					
					f.write("TEL".getBytes());
					f.write(";".getBytes());
					
					f.write("CELL:".getBytes());
					f.write(Contact_OneContact.getPhoneNumber().getBytes());
					f.write("\r\n".getBytes());
					
					f.write("EMAIL;HOME".getBytes());
					f.write("\r\n".getBytes());
					
					//footer in VCF file
					f.write("END:VCARD".getBytes());
					f.write("\r\n".getBytes());
					
					//blank line
					f.write("\r\n".getBytes());
					
					f.flush();
					f.close();
					file = null;
				}				
			} catch (IOException e) {
				showDialog("Error during writing file on SD card, exit");
				e.printStackTrace();
			} finally {
				TextView_Result.setText("Export done successfull!\r\n(Output files is in /sdcard/ManageContacts/" + 
										"*.vcf" +
										")");

				showDialog("Export done successfull! (check file " + 
						   "/sdcard/ManageContacts/ " + 
						   "*.vcf" +
						   " on you sdcard");
			}
		}
    }
}
