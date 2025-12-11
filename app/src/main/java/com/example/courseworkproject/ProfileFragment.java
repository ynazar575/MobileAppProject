package com.example.courseworkproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TextView txtEmail, txtName;
    private EditText inputName, inputEmail, inputPassword;
    private Button btnUpdateName, btnUpdateEmail, btnUpdatePassword, btnSignOut;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        txtEmail = view.findViewById(R.id.txtEmail);
        txtName = view.findViewById(R.id.txtName);
        inputName = view.findViewById(R.id.inputName);
        inputEmail = view.findViewById(R.id.inputEmail);
        inputPassword = view.findViewById(R.id.inputPassword);

        btnUpdateName = view.findViewById(R.id.btnUpdateName);
        btnUpdateEmail = view.findViewById(R.id.btnUpdateEmail);
        btnUpdatePassword = view.findViewById(R.id.btnUpdatePassword);
        btnSignOut = view.findViewById(R.id.btnSignOut);

        loadUserDetails();
        setupListeners();

        return view;
    }

    private void loadUserDetails() {
        if (user == null) return;

        txtEmail.setText(user.getEmail());

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        if (name != null && !name.isEmpty()) {
                            txtName.setText(name);  // Show the name
                        } else {
                            txtName.setText("No name set");  // Default if no name
                        }
                    } else {
                        txtName.setText("No name set");
                    }
                })
                .addOnFailureListener(e -> {
                    txtName.setText("Error loading name");
                });
    }

    private void setupListeners() {

        btnUpdateName.setOnClickListener(v -> {
            if (getContext() == null || user == null) return;
            String name = inputName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Enter a name", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("users")
                    .document(user.getUid())
                    .update("name", name)
                    .addOnSuccessListener(a -> {
                        txtName.setText(name);
                        inputName.setText("");
                        Toast.makeText(getContext(), "Name updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update name", Toast.LENGTH_SHORT).show());
        });

        btnUpdateEmail.setOnClickListener(v -> {
            if (getContext() == null || user == null) return;
            String email = inputEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(getContext(), "Enter an email", Toast.LENGTH_SHORT).show();
                return;
            }

            user.updateEmail(email)
                    .addOnSuccessListener(a -> {
                        txtEmail.setText(email);
                        inputEmail.setText("");
                        Toast.makeText(getContext(), "Email updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update email", Toast.LENGTH_SHORT).show());
        });

        btnUpdatePassword.setOnClickListener(v -> {
            if (getContext() == null || user == null) return;
            String pass = inputPassword.getText().toString().trim();
            if (pass.isEmpty()) {
                Toast.makeText(getContext(), "Enter a password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.length() < 6) {
                Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            user.updatePassword(pass)
                    .addOnSuccessListener(a -> {
                        inputPassword.setText("");
                        Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show());
        });

        btnSignOut.setOnClickListener(v -> {
            if (getContext() == null || getActivity() == null) return;
            auth.signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        });
    }
}