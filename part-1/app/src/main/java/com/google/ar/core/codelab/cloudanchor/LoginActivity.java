package com.google.ar.core.codelab.cloudanchor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email_login;
    private EditText password_login;
    private Button button_login;
    private TextView textview_login;

    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button_login = (Button) findViewById(R.id.btn_login);
        textview_login =  (TextView) findViewById(R.id.link_signup);

        textview_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
            }

        });


        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        email_login = (EditText) findViewById(R.id.input_email);
        password_login = (EditText) findViewById(R.id.input_password);
        textview_login = (TextView) findViewById(R.id.link_signup);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() !=null){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                }

            }
        };

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignIn();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void startSignIn(){

        String email = email_login.getText().toString();
        String password = password_login.getText().toString();

        if (TextUtils.isEmpty(email)|| TextUtils.isEmpty(password)){

            Toast.makeText(LoginActivity.this, "Please enter a email and password!", Toast.LENGTH_SHORT).show();

        }else {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(!task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Please check your information!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }


    }

}
