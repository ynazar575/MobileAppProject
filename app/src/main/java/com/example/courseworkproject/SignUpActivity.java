package com.example.courseworkproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText signupEmail,signupPaswword;
    private Button signupButton;
    private TextView LoginRedirectText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPaswword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        LoginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = signupEmail.getText().toString().trim();
                String pass = signupPaswword.getText().toString().trim();

                if(user.isEmpty()){
                    signupEmail.setError("email cannot be empty");
                }
                if(pass.isEmpty()){
                    signupPaswword.setError("password cannot be empty");
                }
                else if(pass.length() < 6){
                    signupPaswword.setError("password must be at least 6 characters");
                }
                else{
                    auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this,"SignUp Succesfil",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));

                            }
                            else{
                                Toast.makeText(SignUpActivity.this, "SignUp failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        LoginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
            }
        });
    }
}
