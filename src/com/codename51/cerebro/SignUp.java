package com.codename51.cerebro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SignUp extends Activity implements OnClickListener
{
	Button create;
	EditText txtName,txtPassword,cnfmPassword;
	// alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        
        txtName = (EditText) findViewById(R.id.fname);
        txtPassword = 	(EditText) findViewById(R.id.pass);
        cnfmPassword = (EditText) findViewById(R.id.confirm_pass);
        create= (Button) findViewById(R.id.create);
        create.setOnClickListener(this);		
    }
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.create:
			//Toast.makeText(this, "Account Created", Toast.LENGTH_LONG);
			// Read EditText dat
            String name = txtName.getText().toString();
            String password = txtPassword.getText().toString();
            String cPassword = cnfmPassword.getText().toString();
            
            // Check if user filled the form
            if(name.trim().length() > 0 && password.trim().length() > 0 && cPassword.trim().length() > 0){
            	if(password.equals(cPassword)){
            		// Launch Tabbed View
                    Intent i = new Intent(getApplicationContext(), Tabbed.class);
                    i.putExtra("name", name);
                    i.putExtra("password", password);
                    startActivity(i);
            	}
            	else{
            		alert.showAlertDialog(SignUp.this, "Registration Error!", "Please put same passwords", false);
            	}
            }
            else{
            	alert.showAlertDialog(SignUp.this, "Registration Error!", "Please fill up complete details", false);
            }
			
			break;
		}
	}
}
