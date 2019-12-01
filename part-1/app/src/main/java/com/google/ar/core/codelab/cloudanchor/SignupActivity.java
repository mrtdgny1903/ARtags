package com.google.ar.core.codelab.cloudanchor;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText email_signup;
    EditText password_signup;
    EditText name_signup;
    EditText lastname_signup;
    EditText phone_signup;
    EditText country_signup;
    EditText city_signup;
    Button button_signup;
    TextView textview_login;

    private FirebaseAuth mAuth;
    FirebaseFirestore DB;
    String userEmail,userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        DB = FirebaseFirestore.getInstance();

        email_signup = (EditText) findViewById(R.id.input_email);
        password_signup = (EditText) findViewById(R.id.input_password);
        name_signup = (EditText) findViewById(R.id.input_name);
        lastname_signup = (EditText) findViewById(R.id.input_lastname);
        phone_signup = (EditText) findViewById(R.id.input_mobile);
        country_signup = (EditText) findViewById(R.id.input_country);
        city_signup = (EditText) findViewById(R.id.input_city);
        button_signup = (Button) findViewById(R.id.btn_signup);
        textview_login = (TextView) findViewById(R.id.link_login);

        textview_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent= new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);
            }

        });


        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userEmail = email_signup.getText().toString().trim();
                String userPassword = password_signup.getText().toString().trim();
                String userName = name_signup.getText().toString().trim();
                String userLastname = lastname_signup.getText().toString().trim();
                String userPhone = phone_signup.getText().toString().trim();
                String userCity = city_signup.getText().toString().trim();
                String userCountry = country_signup.getText().toString().trim();



                if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword) || TextUtils.isEmpty(userName) || TextUtils.isEmpty(userLastname) || TextUtils.isEmpty(userPhone) || TextUtils.isEmpty(userCountry) || TextUtils.isEmpty(userCity)) {

                    Toast.makeText(getApplicationContext(), "Error! Please check your information.", Toast.LENGTH_SHORT).show();

                } else if (password_signup.length() < 6){
                    Toast.makeText(SignupActivity.this, "Password must have 6 characters!", Toast.LENGTH_SHORT).show();

                }else{
                    registerFunc(userEmail, userPassword, userName, userLastname, userPhone, userCity, userCountry);
                }

            }
        });
    }

    public void registerFunc(String userEmail, String userPassword, String userName, String userLastname, String userPhone, String userCity, String userCountry) {
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Success", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            Map<String, String> map = new HashMap<>();
                            map.put("Email", email_signup.getText().toString().trim());
                            map.put("Password", password_signup.getText().toString().trim());
                            map.put("Name", name_signup.getText().toString().trim());
                            map.put("Lastname", lastname_signup.getText().toString().trim());
                            map.put("Number", phone_signup.getText().toString().trim());
                            map.put("Country", country_signup.getText().toString().trim());
                            map.put("City", city_signup.getText().toString().trim());

                            DB.collection("User").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("Success", "DocumentSnapshot added with ID: " + documentReference.getId());
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Error", "Error adding document", e);
                                        }
                                    });

                            Intent i = new Intent(SignupActivity.this,MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Log.w("Error", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser currentUser) {

    }



}
