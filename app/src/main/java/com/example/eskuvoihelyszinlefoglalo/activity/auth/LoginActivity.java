package com.example.eskuvoihelyszinlefoglalo.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eskuvoihelyszinlefoglalo.R;
import com.example.eskuvoihelyszinlefoglalo.activity.ProfileActivity;
import com.example.eskuvoihelyszinlefoglalo.utils.MENU;
import com.example.eskuvoihelyszinlefoglalo.utils.NavUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText emailInput;
    TextInputEditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        NavUtils.setupBottomNav(this,findViewById(R.id.nav), MENU.LOGIN);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
    }

    public void gotoReg(View view) {
        Intent intent = new Intent(this, RegistActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        try {
            String email = Objects.requireNonNull(emailInput.getText()).toString();
            String password = Objects.requireNonNull(passwordInput.getText()).toString();
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                }else {
                    new AlertDialog.Builder(this).setTitle("Hiba").setMessage("Hibás felhasználónév vagy jelszó!").show();
                }
            });
        }catch (Exception ignore){}
    }
}
