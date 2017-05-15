package org.chicken_ar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by chicken on 2017-05-10.
 */

public class NewAccount extends AppCompatActivity {

    EditText idInput, passwordInput, passwordConfirm;
    Button buttonAssign, buttonCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_account);
        initializeVariable();

        buttonAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void initializeVariable() {
        buttonAssign = (Button)findViewById(R.id.buttonLogin);
        buttonCancel = (Button)findViewById(R.id.buttonSignUp);
        idInput = (EditText)findViewById(R.id.idInput);
        passwordInput = (EditText)findViewById(R.id.passwordInput);
        passwordConfirm = (EditText)findViewById(R.id.passwordConfirm);
    }
}
