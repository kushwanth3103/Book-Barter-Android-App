package com.example.bookbarter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText mName,mMail,mPassword,mRePassword,mPhoneNumber;
    Button mRegister;
    TextView mLoginButton;
    String userID;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ArrayList<String> arr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mName=findViewById(R.id.name);
        mMail=findViewById(R.id.email);
        mPassword=findViewById(R.id.enter_password);
        mRePassword=findViewById(R.id.re_enter_password);

        mRegister=findViewById(R.id.registerbutton);
        mLoginButton=findViewById(R.id.LoginButton);
        mPhoneNumber=findViewById(R.id.PhoneNumber);
        fAuth= FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        /*if (fAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }*/
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mMail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();
                String re_password=mRePassword.getText().toString().trim();
                String phonenumber=mPhoneNumber.getText().toString().trim();
                String name=mName.getText().toString().trim();
                if (TextUtils.isEmpty(email))
                {
                    mMail.setError("E-Mail is Required");
                    return;
                }
                if (TextUtils.isEmpty(password))
                {
                    mPassword.setError("Password is Required");
                    return;
                }
                if (TextUtils.isEmpty(re_password))
                {
                    mRePassword.setError("This field is Required");
                    return;
                }
                if (TextUtils.equals(password,re_password))
                {
                }
                else {
                    mRePassword.setError("Password should match");
                }
                if (password.length()<6)
                {
                    mPassword.setError("Password must contain at least 6 Characters");
                    return;
                }
                //register user in firebase
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            //send verification email
                            FirebaseUser user=fAuth.getCurrentUser();
                            assert user != null;
                            userID=user.getUid();
                            DocumentReference documentReference=fStore.collection("users").document(userID);
                            Map<String,Object> user_item=new HashMap<>();
                            user_item.put("Name",name);
                            user_item.put("E-Mail",email);
                            user_item.put("Phone Number",phonenumber);
                            user_item.put("Wish List",arr);
                            documentReference.set(user_item).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Register.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Register.this, "Verification E-mail has been Sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Register.this, "Error! E-mail not semt"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            Toast.makeText(Register.this,"Sucessfully Registered",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Login.class));
                        }
                        else{
                            Toast.makeText(Register.this,"Error Occurred",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });


    }
}