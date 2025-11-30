package com.example.courseworkproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText LoginEmail, loginPassword;
    private TextView signupRedirectText;
    private Button LoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        auth = FirebaseAuth.getInstance();
        LoginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        LoginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.SignUpRedirectText);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                String email = LoginEmail.getText().toString();
                String pass = loginPassword.getText().toString();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(LoginActivity.this, "login succesful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        finish();
                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this,"login failed",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else {
                        loginPassword.setError("password cannot be empty");
                    }

                }else if (email.isEmpty()){
                    LoginEmail.setError("email acnnot be empty");
                }else {
                    LoginEmail.setError("please enter valid email");
                }
            }
        });


                signupRedirectText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                    }
                });
    }

}