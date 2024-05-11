package com.example.eskuvoihelyszinlefoglalo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.example.eskuvoihelyszinlefoglalo.shared.models.Location;
import com.example.eskuvoihelyszinlefoglalo.shared.models.User;
import com.example.eskuvoihelyszinlefoglalo.shared.recyclerview.LocationViewAdapter;
import com.example.eskuvoihelyszinlefoglalo.utils.LoadLocationsAsync;
import com.example.eskuvoihelyszinlefoglalo.utils.MENU;
import com.example.eskuvoihelyszinlefoglalo.utils.NavUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity{

    RecyclerView locationList;

    private FirebaseUser user;

    private User userInfo;

    LoadLocationsAsync loadLocationsAsync;

    List<Location> locations;

    LocationViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            user = FirebaseAuth.getInstance().getCurrentUser();

            userInfo = new User();

            FirebaseFirestore.getInstance().collection("users").whereEqualTo("email",user.getEmail()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                    userInfo.setEmail(ds.getString("email"));
                    userInfo.setFirstName(ds.getString("firstName"));
                    userInfo.setLastName(ds.getString("lastName"));
                    userInfo.setPhoneNumber(ds.getString("phoneNumber"));
                }
            });
        }


        NavUtils.setupBottomNav(this,findViewById(R.id.nav),MENU.HOME);

        ImageView downArrow = (ImageView)findViewById(R.id.DownArrow);
        Animation animation = new TranslateAnimation(0,0,0,50);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(500);
        downArrow.startAnimation(animation);

        Animation animation2 = new AlphaAnimation(0,1);
        animation2.setStartOffset(800);
        animation2.setInterpolator(new AccelerateInterpolator());
        animation2.setDuration(1000);
        animation2.setFillAfter(true);
        findViewById(R.id.textView5).startAnimation(animation2);

        locationList = findViewById(R.id.locationList);

        locations = new ArrayList<>();
        adapter = new LocationViewAdapter(locations,this,user,userInfo);
        locationList.setAdapter(adapter);
        locationList.setLayoutManager(
                new LinearLayoutManager(this));


       loadLocationsAsync = new LoadLocationsAsync();
       loadLocationsAsync.execute(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!loadLocationsAsync.getStatus().equals(AsyncTask.Status.RUNNING)) {
            loadLocationsAsync = new LoadLocationsAsync();
            loadLocationsAsync.execute(adapter);
        }

    }
}