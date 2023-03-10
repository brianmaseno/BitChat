package com.example.bitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signup1 extends AppCompatActivity {


            private static final String TAG = "Signup1";
            TextInputEditText fullnames,emailing,phonenumber1,password1,confirm;
            MaterialButton submitbtn;
            TextView login1;
            private FirebaseAuth firebaseAuth;
            private FirebaseDatabase firebaseDatabase;
            private DatabaseReference databaseReference;
            private FirebaseUser user;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_signup1);
                fullnames = findViewById(R.id.fullnames);
                emailing =findViewById(R.id.emailing);
                phonenumber1 = findViewById(R.id.phonenumber1);
                password1 = findViewById(R.id.password1);
                confirm = findViewById(R.id.confirm);
                submitbtn =findViewById(R.id.submit1);
                login1 =findViewById(R.id.log);
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseDatabase = FirebaseDatabase.getInstance();

                submitbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (firebaseAuth.getCurrentUser() == null){
                            registerUser();
                        }
                    }
                });
                login1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Signup1.this, login1.class));
                    }
                });
            }

            private void registerUser() {
                user = firebaseAuth.getCurrentUser();
                if (user == null){
                    String fullname = fullnames.getText().toString().trim();
                    String lEmail = emailing.getText().toString().trim();
                    String phonenumber = phonenumber1.getText().toString().trim();
                    String lpassword = password1.getText().toString().trim();
                    String lconfirmation = confirm.getText().toString().trim();
                    if (TextUtils.isEmpty(fullname)){
                        fullnames.setError("Enter Fullnames");
                    }
                    else if (TextUtils.isEmpty(lEmail)){
                        emailing.setError("Enter Email");
                    }
                    else if (TextUtils.isEmpty(phonenumber)){
                        phonenumber1.setError("Enter Phonenumber");
                    }
                    else if (TextUtils.isEmpty(lpassword)){
                        password1.setError("Enter Password");
                    }
                    else if (TextUtils.isEmpty(lconfirmation)){
                        confirm.setError("Confirm Password");
                    }
                    else if (!lconfirmation.equalsIgnoreCase(lpassword)){
                        confirm.setError("Passwords do not match");
                    }
                    else {
                        submitbtn.setEnabled(false);
                        firebaseAuth.createUserWithEmailAndPassword(lEmail, lpassword)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            sendData();

                                        }
                                        else {
                                            Toast.makeText(Signup1.this, "Registration Unsuccessful, Check network Connection", Toast.LENGTH_SHORT).show();
                                        }
                                        task.addOnFailureListener(Signup1.this, new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i(TAG, "onFailure: "+ e.getMessage());
                                            }
                                        });
                                    }

                                });
                    }
                }

            }

            private void sendData() {
                String fullname = fullnames.getText().toString().trim();
                String lEmail = emailing.getText().toString().trim();
                String phonenumber = phonenumber1.getText().toString().trim();

                if (TextUtils.isEmpty(fullname)){
                    fullnames.setError("Enter Fullname");
                }
                else if (TextUtils.isEmpty(lEmail)){
                    emailing.setError("Enter Email");
                }
                else if (TextUtils.isEmpty(phonenumber)){
                    phonenumber1.setError("Enter Phone number");
                }
                else {
                    databaseReference = firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid());
                    Users users = new Users(fullname,lEmail,phonenumber);
                    databaseReference.setValue(users);
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            submitbtn.setEnabled(true);
                            if(snapshot.exists()){
                                startActivity(new Intent(Signup1.this, MainActivity1.class));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }
                            else {
                                Toast.makeText(Signup1.this,"User not Created",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            submitbtn.setEnabled(true);
                            Log.i(TAG, "onCancelled: " + error);
                        }
                    });

                }
            }



        }
