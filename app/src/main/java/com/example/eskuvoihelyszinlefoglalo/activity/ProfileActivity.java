package com.example.eskuvoihelyszinlefoglalo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eskuvoihelyszinlefoglalo.MainActivity;
import com.example.eskuvoihelyszinlefoglalo.R;
import com.example.eskuvoihelyszinlefoglalo.activity.auth.LoginActivity;
import com.example.eskuvoihelyszinlefoglalo.shared.models.Location;
import com.example.eskuvoihelyszinlefoglalo.shared.models.MyImage;
import com.example.eskuvoihelyszinlefoglalo.shared.recyclerview.LocationViewAdapter;
import com.example.eskuvoihelyszinlefoglalo.utils.FileUtils;
import com.example.eskuvoihelyszinlefoglalo.utils.MENU;
import com.example.eskuvoihelyszinlefoglalo.utils.NavUtils;
import com.google.android.gms.common.util.JsonUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class ProfileActivity extends AppCompatActivity {

    TextView nameText;
    TextView emailText;
    TextView phoneNumberText;


    TextInputEditText nameInput;
    TextInputEditText cityInput;
    TextInputEditText addressInput;
    TextInputEditText descriptionInput;

    List<MyImage> uploadedImgs;

    FirebaseUser user;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    ViewSwitcher flipper;

    RecyclerView selfLocationsView;

    List<Location> selfLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        flipper = findViewById(R.id.flipper);

        selfLocationsView = findViewById(R.id.selfLocationsView);

        ((TabLayout)findViewById(R.id.tabLayout)).addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0 && flipper.getCurrentView().getId() != R.id.locationMakerView){
                    flipper.showNext();
                    return;
                }
                if(tab.getPosition() == 1 && flipper.getCurrentView().getId() != R.id.selfLocationsView){
                    if(selfLocations.isEmpty()){
                        new AlertDialog.Builder(tab.parent.getContext()).setTitle("Hiba").setMessage("Még nem töltöttél fel helyszínt vagy a helyszíneid" +
                                "még nem töltődtek be!").show();
                    }else {
                        flipper.showNext();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0 && flipper.getCurrentView().getId() != R.id.locationMakerView){
                    flipper.showNext();
                    return;
                }
                if(tab.getPosition() == 1 && flipper.getCurrentView().getId() != R.id.selfLocationsView){
                    if(selfLocations.isEmpty()){
                        new AlertDialog.Builder(tab.parent.getContext()).setTitle("Hiba").setMessage("Még nem töltöttél fel helyszínt vagy a helyszíneid" +
                                "még nem töltődtek be!").show();
                    }else {
                        flipper.showNext();
                    }
                }
            }
        });

        uploadedImgs = new ArrayList<>();

        NavUtils.setupBottomNav(this,findViewById(R.id.nav), MENU.PROFILE);

        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        phoneNumberText = findViewById(R.id.phoneNumberText);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }


        FirebaseFirestore.getInstance().collection("users").where(
                Filter.equalTo("email",user.getEmail())).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                emailText.setText(user.getEmail());
                nameText.setText(ds.getString("firstName") + " " + ds.getString("lastName"));
                phoneNumberText.setText(ds.getString("phoneNumber"));
            }else {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        });

        nameInput = findViewById(R.id.nameInput);
        cityInput = findViewById(R.id.cityInput);
        addressInput = findViewById(R.id.addressInput);
        descriptionInput = findViewById(R.id.descriptionInput);


        pickMedia = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(5), uri -> {
                    if (!uri.isEmpty()) {
                        for(Uri picked : uri){
                            try(InputStream stream = getContentResolver().openInputStream(picked)) {
                                byte[] bytes = FileUtils.getBytes(stream);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    String data = "data:image/jpeg;base64,"+Base64.getEncoder().encodeToString(bytes);
                                    String type = getContentResolver().getType(picked);
                                    int size = bytes.length;
                                    String name = picked.getLastPathSegment();
                                    MyImage img = new MyImage();
                                    img.setData(data);
                                    img.setName(name);
                                    img.setSize(size);
                                    img.setType(type);
                                    uploadedImgs.add(img);
                                }else {
                                    new AlertDialog.Builder(this).setTitle("Hiba").setMessage("Ebben az android verzióban nem lehetséges a képfeltöltés!");
                                    return;
                                }
                                new AlertDialog.Builder(this).setTitle("Üzenet").setMessage(uri.size() + " kép feltöltve!").show();
                            }catch (Exception ignore){
                                new AlertDialog.Builder(this).setTitle("Hiba").setMessage("A fájl nem megfelelő formátumú vagy túl nagy!").show();
                            }
                        }
                    }
                });

        FirebaseFirestore.getInstance().collection("locations").whereEqualTo("owner",user.getEmail()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(DocumentSnapshot qs : task.getResult().getDocuments()) {
                    Location location = new Location();
                    location.setOwner(user.getEmail());
                    location.setCity(qs.getString("city"));
                    location.setDescription(qs.getString("description"));
                    location.setAddress(qs.getString("address"));
                    location.setName(qs.getString("name"));
                    location.setImages(new Vector<>((ArrayList)qs.get("images")));
                    selfLocations.add(location);
                }
            }
        });


        LocationViewAdapter adapter = new LocationViewAdapter(selfLocations,this,user);
        selfLocationsView.setAdapter(adapter);
        selfLocationsView.setLayoutManager(
                new LinearLayoutManager(this));
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void upload(View view) {
        try {
            String name = Objects.requireNonNull(nameInput.getText()).toString();
            String city = Objects.requireNonNull(cityInput.getText()).toString();
            String address = Objects.requireNonNull(addressInput.getText()).toString();
            String description = Objects.requireNonNull(descriptionInput.getText()).toString();
            String owner = user.getEmail();

            if(name.isEmpty() || city.isEmpty() || address.isEmpty() || description.isEmpty()){
                new AlertDialog.Builder(this).setTitle("Hiba").setMessage("Nem lehet eggyik mező sem üres!").show();
                return;
            }

            if(uploadedImgs.isEmpty()) {
                new AlertDialog.Builder(this).setTitle("Hiba").setMessage("Legalább 1 képet fel kell tölteni!").show();
                return;
            }

            FirebaseFirestore.getInstance().collection("locations").whereEqualTo("owner",user.getEmail())
                    .whereEqualTo("name",name).get().addOnCompleteListener(res -> {
                        if(res.getResult().isEmpty()){
                            Location location = new Location();
                            location.setAddress(address);
                            location.setName(name);
                            location.setDescription(description);
                            location.setCity(city);
                            location.setImages(new Vector<>(uploadedImgs));
                            location.setOwner(owner);

                            FirebaseFirestore.getInstance().collection("locations").add(location).addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    new AlertDialog.Builder(this).setTitle("Információ").setMessage("Sikeres feltöltés!").show();
                                }else {
                                    task.getException().printStackTrace();
                                    new AlertDialog.Builder(this).setTitle("Hiba").setMessage("Feltöltés nem sikerült!").show();
                                }
                            });
                        }else {
                            new AlertDialog.Builder(this).setTitle("Hiba").setMessage("Nem lehet ugyanazzal a névvel 2 helyszíned!").show();
                        }
                    });
        }catch (Exception ignore){};
    }

    public void imageUpload(View view) {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

}
