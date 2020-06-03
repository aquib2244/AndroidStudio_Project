package com.example.loginregisterpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText mFullName,mEmail,mPassword,mPhone;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fstore;
    String USERID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName=findViewById(R.id.account);
        mEmail=findViewById(R.id.Email);
        mPassword=findViewById(R.id.Password);
        mPhone=findViewById(R.id.Phonenumber);
        mRegisterBtn=findViewById(R.id.registerbtn);
        mLoginBtn=findViewById(R.id.createtext);
        fstore=FirebaseFirestore.getInstance();
        fAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);


        if(fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
            mRegisterBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final String email = mEmail.getText().toString().trim();
                    String password = mPassword.getText().toString().trim();
                    final String fullname=mFullName.getText().toString();
                    final String phone=mPhone.getText().toString();

                    if(TextUtils.isEmpty(email)){
                        mEmail.setError("Email is Required.");
                        return;
                    }
                    if(TextUtils.isEmpty(password)){
                        mPassword.setError("Passowrd is Required.");
                        return;
                    }
                    if(password.length()<6){
                        mPassword.setError("Password must be >= 6 characters");
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);

                    fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                               Toast.makeText(Register.this,"user Created.", Toast.LENGTH_LONG).show();
                               USERID=fAuth.getCurrentUser().getUid();
                                DocumentReference documentReference=fstore.collection("user").document(USERID);
                                Map<String,Object> user=new HashMap<>();
                                user.put("fullName",fullname);
                                user.put("email",email);
                                user.put("Phone number",phone);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG","onsuccess: User profile is created for "+USERID);
                                    }
                                });
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }
                            else
                            {
                                Toast.makeText(Register.this,"error!" + task.getException().getMessage(),Toast.LENGTH_SHORT);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });
            mLoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),Login.class));
                }
            });
        }
    }
