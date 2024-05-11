package com.example.eskuvoihelyszinlefoglalo.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.eskuvoihelyszinlefoglalo.MainActivity;
import com.example.eskuvoihelyszinlefoglalo.R;
import com.example.eskuvoihelyszinlefoglalo.activity.InfoActivity;
import com.example.eskuvoihelyszinlefoglalo.activity.ProfileActivity;
import com.example.eskuvoihelyszinlefoglalo.activity.auth.LoginActivity;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class NavUtils {

    public static void setupBottomNav(Context context,NavigationBarView nav,MENU currentMenu) {

        nav.getMenu().getItem(currentMenu.value).setChecked(true);

        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("navigation_log",item.getTitle().toString());
                int nav_info = R.id.nav_info;
                int nav_home = R.id.nav_home;
                int nav_profile = R.id.nav_profile;

                Intent intent = null;

                if(nav_info == item.getItemId()){
                    intent = new Intent(context, InfoActivity.class);
                }
                if(nav_home == item.getItemId()){
                    intent = new Intent(context, MainActivity.class);
                }
                if(nav_profile == item.getItemId()){
                    if(FirebaseAuth.getInstance().getCurrentUser() == null){
                        intent = new Intent(context, LoginActivity.class);
                    }else {
                        intent = new Intent(context, ProfileActivity.class);
                    }
                }

                if(intent == null){
                    return false;
                }else {
                  context.startActivity(intent);
                }

                return true;
            }
        });
    }
}
