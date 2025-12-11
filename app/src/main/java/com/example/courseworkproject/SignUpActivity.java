package com.example.courseworkproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(user).matches()){
                    signupEmail.setError("please enter valid email");
                    return;
                }
                if(pass.isEmpty()){
                    signupPaswword.setError("password cannot be empty");
                    return;
                }
                if(pass.length() < 6){
                    signupPaswword.setError("password must be at least 6 characters");
                    return;
                }
                
                // All validations passed
                {
                    auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                // Create a new user map
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("email", user);
                                userMap.put("name", ""); // Default empty name

                                // Save to Firestore
                                String userId = auth.getCurrentUser().getUid();
                                FirebaseFirestore.getInstance().collection("users").document(userId)
                                        .set(userMap)
                                        .addOnSuccessListener(v -> {
                                            Toast.makeText(SignUpActivity.this,"SignUp Successful",Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(SignUpActivity.this, "Account created but failed to save profile. Please try logging in.", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                            finish();
                                        });

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
