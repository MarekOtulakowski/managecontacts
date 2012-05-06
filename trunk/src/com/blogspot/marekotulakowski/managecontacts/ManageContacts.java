/*
 * build 003
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
	private TextView TextView_Result;
	private Cursor cContactsFromAndroid;  
	private Cursor cContactsFromSIM;  
	private ArrayList<Contact> arContacts;  
	private Contact newContact; 
	private String sFileNamePrefix = "ExportContacts";
	private String sFileNameSurfix = ".cvs";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //assign controls
        this.Button_Import = (Button)this.findViewById(R.id.button_ImpContacs);
        this.Button_Export = (Button)this.findViewById(R.id.button_ExpContacs);
        this.Button_Close = (Button)this.findViewById(R.id.button_CloseApplication);
        this.Spinner_KindOfContacts = (Spinner)this.findViewById(R.id.spinner_kindOfContact);
        this.TextView_Result = (TextView)this.findViewById(R.id.TextView_Result);
        
        //assign events
        this.Button_Import.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	ShowDialog("clicked import button!");
            }
        });
        this.Button_Export.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	ShowDialog("clicked export button!");
            }
        });
        this.Button_Close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	ShowQuestionCloseApp("Are you sure, close this application?");
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
    }
    
    public void ShowDialog(String message) {  
    	new AlertDialog.Builder(this).setTitle("Program Info").setMessage(message).
    	setIcon(android.R.drawable.ic_dialog_info).setPositiveButton("OK", null).show();  
    }  
    
    public void ShowQuestionCloseApp(String questionMessage) {
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
    
    public Cursor getContactsFromAndroid() {  
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
    
    public Cursor getContactsFromSIMcard() {  
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
    
    public ArrayList<Contact> getArrayListContactsFromAndroid() {  
        ArrayList<Contact> arContactFromAndroid = new ArrayList<Contact>();  
        int iContactsCountFromAndroid = getContactsFromAndroid().getCount();  
        cContactsFromAndroid = getContactsFromAndroid();  
          
        if (cContactsFromAndroid.moveToFirst()) {  
	        String contactId, phoneNumber, contactName  = "";  
	        while (iContactsCountFromAndroid > 0) {  
				contactId = phoneNumber = contactName = "";  
				contactId = cContactsFromAndroid.getString(cContactsFromAndroid.getColumnIndex(   
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
					 
				contactName = cContactsFromAndroid.getString(cContactsFromAndroid.getColumnIndex(  
															 ContactsContract.Contacts.DISPLAY_NAME));  
					
				newContact = new Contact(contactName, 
										 phoneNumber, 
										 TypeContact.ContactFromAndroid);  
				arContactFromAndroid.add(newContact);  
				newContact = null;  
					
				iContactsCountFromAndroid--;  
				cContactsFromAndroid.moveToNext();  
	        }  
        }  
        cContactsFromAndroid.close();       
          
        return arContactFromAndroid;  
    }  
     
	public ArrayList<Contact> getArrayListContactsFromSIM() {  
		ArrayList<Contact> arContactFromSIM = new ArrayList<Contact>();  
		int iCountContactFromSIMcard = getContactsFromSIMcard().getCount();  
		cContactsFromSIM = getContactsFromSIMcard();  
	  
		if (cContactsFromSIM.moveToFirst()) {       
			 String contactName, phoneNumber = "";  
			 while (iCountContactFromSIMcard  > 0) {  
				contactName = phoneNumber = "";  
					
				contactName = cContactsFromSIM.getString(0);  
				phoneNumber = cContactsFromSIM.getString(1);  
				// 0 - name  
				// 1 - number  
				// 2 - email  
				// 3 - _id  
					
				newContact = new Contact(contactName, phoneNumber, TypeContact.ContactFromSIMcard);  
				arContactFromSIM.add(newContact);  
				newContact = null;  
					
				iCountContactFromSIMcard--;  
				cContactsFromSIM.moveToNext();  
			}  
		}  
		cContactsFromSIM.close();   
		  
		return arContactFromSIM;  
	}  
	
	public void writeContactsToSdCard(SourceContact sourceContact) { 
		arContacts = new ArrayList<Contact>();
		  
		switch (sourceContact) {
			case SIMcardContact: {
				arContacts.addAll(getArrayListContactsFromSIM());    
			}
			case AndroidContact: {
				arContacts.addAll(getArrayListContactsFromAndroid());
			}
			case SIMandAndroid: {
				arContacts.addAll(getArrayListContactsFromSIM());
				arContacts.addAll(getArrayListContactsFromAndroid());
			}
		}
		  
		if (arContacts.size() < 1) {
			ShowDialog("Not found any contacts on SIM and Android, exit");
			return;
		}
			 
		File sdCard = Environment.getExternalStorageDirectory(); 
		File dir = new File (sdCard.getAbsolutePath() + ""); 
		if (!dir.exists()) {
			dir.mkdirs();
		}
			 
		try {
			File file = new File(dir + "/" + sFileNamePrefix + sFileNameSurfix);
			file.createNewFile();
		  
			if (!file.exists()) {
				ShowDialog("Cannot create/override file on sdcard, exit");
				return;
			} 
			  
			FileOutputStream f = new FileOutputStream(file);
			//header in CVS file
			f.write("Contact name,Phone number,Source".getBytes());
			f.write("\r\n".getBytes());
		   
			for(Contact Contact_OneContact: arContacts) {
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
			ShowDialog("Error during writing file on SD card, exit");
			e.printStackTrace();
		} finally {
			TextView_Result.setText("Export done successfull!\r\n(Output file is in /sdcard/" + 
									sFileNamePrefix + 
									sFileNameSurfix + 
									")");
			ShowDialog("Export done successfull! (check file " + 
					    sFileNamePrefix + 
						sFileNameSurfix + 
						" on you sdcard");
		}
    }
}