package com.rentride.rentaride;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    TextInputLayout phoneEdt,passEdt;
    CheckBox rememberMe;
    TextView LoginSignUp,ForgotPass;
    Button login;
    String parentDBName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        LoginSignUp = findViewById(R.id.LoginSignUp);
        login = findViewById(R.id.login);
        phoneEdt = findViewById(R.id.phoneLogin);
        passEdt = findViewById(R.id.password);
        rememberMe = findViewById(R.id.RememberMe);
        ForgotPass = findViewById(R.id.forgotPassword);
        Paper.init(this);


        LoginSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
        //In case a user clicked RememberMe checkbox
        String phoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String passwordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (phoneKey != " " && passwordKey != " "){
            if (!TextUtils.isEmpty(phoneKey) && !TextUtils.isEmpty(passwordKey)){
                AllowAccess(phoneKey,passwordKey);//if the user clicked the checkbox
            }

        }
        login.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                login.setText("Admin Login");
                LoginAdmin();
                return true;
            }
        });

    }

    private void AllowAccess(String phone, String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(phone).exists()){
                    Users userData = snapshot.child("Users").child(phone).getValue(Users.class);

                    if (userData.getPhone().equals(phone)){
                        if (userData.getPassword().equals(password)){
                            Toast.makeText(Login.this, "Already logged In", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Login.this,Home.class);
                            Prevalent.CurrentOnlineUser = userData;
                            startActivity(intent);

                        }
                        else {
                            Toast.makeText(Login.this, "Incorrect Password.Try Again", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
                else {
                    Toast.makeText(Login.this, "Account with phone number "+ phone + "Unavailable.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private Boolean validatePhone(){
        String sim = phoneEdt.getEditText().getText().toString();
        if (sim.isEmpty()){
            phoneEdt.setError("Enter phone number");
            return false;
        }else {
            phoneEdt.setError(null);
            phoneEdt.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validatePassword(){
        String pass = passEdt.getEditText().getText().toString();
        if (pass.isEmpty()){
            passEdt.setError("Enter password");
            return false;
        }

        else {
            passEdt.setError(null);
            passEdt.setErrorEnabled(false);
            return true;
        }
    }

    private void LoginUser() {
        String phone = phoneEdt.getEditText().getText().toString();
        String password = passEdt.getEditText().getText().toString();

        if (!validatePhone() | !validatePassword()){
            return;

        }else {
            VerifyData(phone,password);
        }
    }
    private void LoginAdmin(){
        String phone = phoneEdt.getEditText().getText().toString();
        String password = passEdt.getEditText().getText().toString();

        if (!validatePhone() | !validatePassword()){
            return;

        }else {
            VerifyAdminData(phone,password);
        }

    }

    private void VerifyAdminData(String phone, String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Admins").child(phone).exists()){
                    Users userData = snapshot.child("Admins").child(phone).getValue(Users.class);

                    if (userData.getPhone().equals(phone)){
                        if (userData.getPassword().equals(password)){
                            Toast.makeText(Login.this, "Admin Login successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this,AdminPanel.class);
//                            Prevalent.CurrentOnlineUser = userData;
                            startActivity(intent);

                        }
                        else {
//                            Toast.makeText(Login.this, "Incorrect Password.Try Again", Toast.LENGTH_SHORT).show();
                            passEdt.setError("Incorrect password.");
                        }

                    }

                }
                else {
//                    Toast.makeText(Login.this, "Account with phone number "+ phone + "Unavailable.", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(Login.this, "Create New Account.", Toast.LENGTH_SHORT).show();
                    passEdt.setError("This user does not exist.");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void VerifyData(String phone, String password) {
        if (rememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDBName).child(phone).exists()){
                    Users userData = snapshot.child(parentDBName).child(phone).getValue(Users.class);

                    if (userData.getPhone().equals(phone)){
                        if (userData.getPassword().equals(password)){
                            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this,Home.class);
                            Prevalent.CurrentOnlineUser = userData;
                            startActivity(intent);

                        }
                        else {
                            passEdt.setError("Incorrect password.");
                        }

                    }

                }
                else {
                    passEdt.setError("This user does not exist.");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}