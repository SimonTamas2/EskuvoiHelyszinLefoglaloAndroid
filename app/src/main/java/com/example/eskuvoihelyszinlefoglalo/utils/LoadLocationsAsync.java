package com.example.eskuvoihelyszinlefoglalo.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eskuvoihelyszinlefoglalo.shared.models.Location;
import com.example.eskuvoihelyszinlefoglalo.shared.recyclerview.LocationViewAdapter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class LoadLocationsAsync extends AsyncTask<LocationViewAdapter,Void,List<Location>> {

    LocationViewAdapter adapter;

    @Override
    protected List<Location> doInBackground(LocationViewAdapter... params) {
        adapter = params[0];
        List<Location> locations = new ArrayList<>();
        Task<QuerySnapshot> task = FirebaseFirestore.getInstance().collection("locations").get();
        while(!task.isComplete()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        QuerySnapshot res = task.getResult();
        for(DocumentSnapshot qs : res.getDocuments()) {
            Location location = new Location();
            location.setOwner(qs.getString("owner"));
            location.setCity(qs.getString("city"));
            location.setDescription(qs.getString("description"));
            location.setAddress(qs.getString("address"));
            location.setName(qs.getString("name"));
            location.setImages(new Vector<>((ArrayList)qs.get("images")));
            locations.add(location);
        }
        return locations;
    }

    @Override
    protected void onPostExecute(List<Location> locations) {
        adapter.setLocations(locations);
        Log.d("loadloc","IGEN");
    }

}
