package com.example.eskuvoihelyszinlefoglalo.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.Validators;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eskuvoihelyszinlefoglalo.R;
import com.example.eskuvoihelyszinlefoglalo.shared.models.User;
import com.example.eskuvoihelyszinlefoglalo.utils.MENU;
import com.example.eskuvoihelyszinlefoglalo.utils.NavUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class RegistActivity extends AppCompatActivity {

    TextInputEditText emailInput;
    TextInputEditText firstNameInput;
    TextInputEditText lastNameInput;
    TextInputEditText passwordInput;
    TextInputEditText passwordAgainInput;

    TextInputEditText phoneNumberInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        NavUtils.setupBottomNav(this,findViewById(R.id.nav), MENU.LOGIN);

        emailInput = findViewById(R.id.emailInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        passwordInput = findViewById(R.id.passwordInput);
        passwordAgainInput = findViewById(R.id.passwordAgainInput);
        phoneNumberInput = findViewById(R.id.phoneNumberInput);
    }

    public void regist(View view) {
        try {
            String email = Objects.requireNonNull(emailInput.getText()).toString();
            String password = Objects.requireNonNull(passwordInput.getText()).toString();
            String passwordAgain = Objects.requireNonNull(passwordAgainInput.getText()).toString();
            String firstName = Objects.requireNonNull(firstNameInput.getText()).toString();
            String lastName = Objects.requireNonNull(lastNameInput.getText()).toString();
            String phoneNumber = Objects.requireNonNull(phoneNumberInput.getText()).toString();

            if(!passwordAgain.equals(password)) {
                new AlertDialog.Builder(this).setTitle("Hiba").setMessage("A jelszavak nem eggyeznek meg!").show();
                return;
            }

            if(email.isEmpty() || passwordAgain.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()
            || phoneNumber.isEmpty()) {
                new AlertDialog.Builder(this).setTitle("Hiba").setMessage("Eggyik mező sem lehet üres!").show();
                return;
            }

            if(password.length() < 5) {
                new AlertDialog.Builder(this).setTitle("Hiba").setMessage("A jelszo nem lehet kisebb mint 5 karakter!").show();
                return;
            }

            if(!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                new AlertDialog.Builder(this).setTitle("Hiba").setMessage("Az email nem megfelelő formátumú!").show();
                return;
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    User user = new User(email,firstName,lastName,phoneNumber);
                    FirebaseFirestore.getInstance().collection("users").add(user);
                    Intent intent = new Intent(this,LoginActivity.class);
                    startActivity(intent);
                }else {
                    new AlertDialog.Builder(this).setTitle("Hiba").setMessage("Ez az email már foglalt!").show();
                }
            });
        }catch(Exception err){
            new AlertDialog.Builder(this).setTitle("Hiba").setMessage("Megadott adatok nem megfelelőek!").show();
        }
    }

    public void gotoLogin(View view) {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
}
