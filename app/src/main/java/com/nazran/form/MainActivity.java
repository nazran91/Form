package com.nazran.form;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextInputEditText emailET, passET;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailET = findViewById(R.id.emailET);
        passET = findViewById(R.id.passwordET);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signUpBT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidity();
            }
        });
    }

    private void checkValidity() {
        String email = emailET.getText().toString();
        String pass = passET.getText().toString();

        View focusView = null;
        boolean cancel = false;

        if (TextUtils.isEmpty(email)) {
            focusView = emailET;
            cancel = true;
            emailET.setError("Please put your email");
        } else if (TextUtils.isEmpty(pass)) {
            focusView = passET;
            cancel = true;
            passET.setError("Please put your password");
        } else if (!isEmailValid(email)) {
            focusView = emailET;
            cancel = true;
            emailET.setError("Please put a valid email");
        } else if (!isPassValid(pass)) {
            focusView = passET;
            cancel = true;
            passET.setError("Password should be at least 4 characters");
        }

        if (cancel)
            focusView.requestFocus();
        else
            signUpAttempt(email, pass);
    }

    private boolean isPassValid(String pass) {
        return pass.length() >= 6;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private void signUpAttempt(String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "Sign Up Success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.e(TAG, user.getEmail());
                            Log.e(TAG, user.getUid());
                        } else {
                            Log.e(TAG, "Sign Up Failed " + task.getException());
                        }
                    }
                }).addOnFailureListener(MainActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Sign Up Failed " + e);
            }
        });
    }
}
