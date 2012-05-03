/*
 * build 001
 */
package com.blogspot.marekotulakowski.managecontacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
    	builder.setTitle("Program Question").setMessage(questionMessage).setPositiveButton("Yes", showDialogQuestionListener)
    	    .setNegativeButton("No", showDialogQuestionListener).show();
    }
    
    DialogInterface.OnClickListener showDialogQuestionListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                //Yes button clicked
            	finish();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                //No button clicked
                break;
            }
        }
    };
}