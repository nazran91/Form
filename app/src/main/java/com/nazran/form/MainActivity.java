package com.nazran.form;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextInputEditText emailET, passET;
    private FirebaseAuth mAuth;
    @BindView(value = R.id.nameET)
    TextInputEditText nameET;
    @BindView(value = R.id.phoneET)
    TextInputEditText phoneET;
    @BindView(value = R.id.genderRadioGroup)
    RadioGroup genderRadioGroup;
    String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
        String name = nameET.getText().toString();
        String phone = phoneET.getText().toString();

        View focusView = null;
        boolean cancel = false;

        if (TextUtils.isEmpty(name)) {
            focusView = nameET;
            cancel = true;
            passET.setError("Please put your name");
        } else if (TextUtils.isEmpty(phone)) {
            focusView = phoneET;
            cancel = true;
            passET.setError("Please put your phone");
        } else if (TextUtils.isEmpty(email)) {
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

        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                gender = radioButton.getText().toString();
            }
        });

        if (cancel)
            focusView.requestFocus();
        else {
            signUpAttempt(name, phone, email, pass);
        }
    }

    private boolean isPassValid(String pass) {
        return pass.length() >= 6;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private void signUpAttempt(final String name, final String phone, final String email, final String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "Sign Up Success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.e(TAG, user.getEmail());
                            Log.e(TAG, user.getUid());
                            saveDataToDatabase(name, phone, email, user.getUid());
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

    private void saveDataToDatabase(String name, String phone, String email, String uid) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("User");

        Map<String, String> user = new HashMap<>();
        user.put("name", name);
        user.put("phone", phone);
        user.put("gender", gender);
        user.put("email", email);

        myRef.push().setValue(user);
    }
}
