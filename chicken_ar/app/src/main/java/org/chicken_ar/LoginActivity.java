package org.chicken_ar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    Button buttonLogin, buttonSignUp;
    EditText editTextID, editTextPassword;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        initializeVariable();
        clickLoginButton();
        clickSignUpButton();
    }

    void initializeVariable() {
        buttonLogin = (Button)findViewById(R.id.buttonLogin);
        buttonSignUp = (Button)findViewById(R.id.buttonSignUp);
        editTextID = (EditText)findViewById(R.id.editTextID);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
    }

    void clickLoginButton() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputID, inputPassword;
                inputID = editTextID.getText().toString();
                inputPassword = editTextPassword.getText().toString();

                if(isLoginInputValid(inputID, inputPassword)) {
                    Intent intent = new Intent(getApplicationContext(), DrawerMenu.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"invaild login",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void clickSignUpButton() {
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewAccount.class);
                startActivity(intent);
            }
        });
    }

    private boolean isLoginInputValid(String email, String password) {
        /*if(email.contains("@"))
            if(passward.length()>4)
                return true;*/
        if(email.equals("chicken") || email.equals("chicken2"))
            if(password.equals("clzls") || password.equals("clzls2"))
                return true;
        return false;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mID;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mID = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mID)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }
/*
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
        */
    }

}

