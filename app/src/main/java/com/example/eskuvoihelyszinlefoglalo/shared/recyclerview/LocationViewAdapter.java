package com.example.eskuvoihelyszinlefoglalo.shared.recyclerview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eskuvoihelyszinlefoglalo.MainActivity;
import com.example.eskuvoihelyszinlefoglalo.R;
import com.example.eskuvoihelyszinlefoglalo.shared.models.Location;
import com.example.eskuvoihelyszinlefoglalo.shared.models.MyImage;
import com.example.eskuvoihelyszinlefoglalo.shared.models.Reservation;
import com.example.eskuvoihelyszinlefoglalo.shared.models.User;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LocationViewAdapter extends RecyclerView.Adapter<LocationViewHolder> {

    private List<Location> locations;

    private Context context;

    private FirebaseUser currentUser;
    private User userInfo;

    private ViewGroup parent;

    public LocationViewAdapter(List<Location> locations, Context context,FirebaseUser currentUser) {
        this.locations = locations;
        this.context = context;
        this.currentUser = currentUser;
    }

    public LocationViewAdapter(List<Location> locations, Context context,FirebaseUser currentUser,User userInfo) {
        this.locations = locations;
        this.context = context;
        this.currentUser = currentUser;
        this.userInfo = userInfo;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        Context context
                = parent.getContext();
        LayoutInflater inflater
                = LayoutInflater.from(context);

        View view
                = inflater
                .inflate(R.layout.location_card,
                        parent, false);

        LocationViewHolder locationViewHolder = new LocationViewHolder(view);

        return locationViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        if(currentUser != null && currentUser.getEmail().equals(locations.get(position).getOwner())){
            holder.reserveButton.setVisibility(View.INVISIBLE);
            holder.reserveButton.setEnabled(false);
            holder.showRequestsButton.setVisibility(View.VISIBLE);
            holder.showRequestsButton.setEnabled(true);
            holder.deleteButton.setEnabled(true);
            holder.deleteButton.setVisibility(View.VISIBLE);
        }
        if(currentUser == null){
            holder.reserveButton.setVisibility(View.INVISIBLE);
            holder.reserveButton.setEnabled(false);
        }else {
            Location location = locations.get(position);
            FirebaseFirestore.getInstance().collection("reservations").whereEqualTo("locationOwner",location.getOwner())
                    .whereEqualTo("locationName",location.getName()).whereEqualTo("email",currentUser.getEmail())
                    .get().addOnCompleteListener(task -> {
                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                            DocumentReference dr = task.getResult().getDocuments().get(0).getReference();
                            holder.reserveButton.setVisibility(View.INVISIBLE);
                            holder.reserveButton.setEnabled(false);
                            holder.deleteReservationButton.setEnabled(true);
                            holder.deleteReservationButton.setVisibility(View.VISIBLE);

                            holder.deleteReservationButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dr.delete();
                                    Intent intent = new Intent(context,MainActivity.class);
                                    context.startActivity(intent);
                                }
                            });
                        }
                    });

        }


        holder.name.setText(locations.get(position).getName());
        holder.address.setText(locations.get(position).getAddress());
        holder.city.setText(locations.get(position).getCity());
        holder.description.setText(locations.get(position).getDescription());
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("email",locations.get(position).getOwner())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                        holder.ownerPhone.setText(ds.getString("phoneNumber"));
                        holder.ownerName.setText(ds.getString("firstName") + " " + ds.getString("lastName"));
                    }
                });

        Iterator<Object> iterator = locations.get(position).getImages().iterator();
        while (iterator.hasNext()){
            Map<String, Object> img = (Map<String, Object>)iterator.next();
            ImageView imgv = new ImageView(context);
            final String pureBase64Encoded = ((String)img.get("data")).substring(((String)img.get("data")).indexOf(",")  + 1);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                byte[] bytes = Base64.getDecoder().decode(pureBase64Encoded);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imgv.setImageBitmap(bitmap);
            }else {
                new AlertDialog.Builder(context).setTitle("Hiba").setMessage("Ebben az android verzióban nem lehetséges a kép megjelenítése!");
            }
            holder.images.addView(imgv);
        }

        holder.images.stopFlipping();

        holder.showRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequests(position);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = locations.get(position);
                FirebaseFirestore.getInstance().collection("locations").whereEqualTo("name",location.getName())
                        .whereEqualTo("owner",location.getOwner()).get().addOnCompleteListener(task -> {
                            if(task.isSuccessful() && !task.getResult().getDocuments().isEmpty()){
                                DocumentReference df = task.getResult().getDocuments().get(0).getReference();
                                df.delete();
                                new AlertDialog.Builder(context).setTitle("Info").setMessage("Sikeres törlés!").show();
                                Intent intent = new Intent(context,MainActivity.class);
                                context.startActivity(intent);
                            }else {
                                new AlertDialog.Builder(context).setTitle("Hiba").setMessage("A törlés nem sikerült!").show();
                            }
                        });
            }
        });

        holder.reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReserveForm(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    private void showRequests(int position) {
        Location location = locations.get(position);

        FirebaseFirestore.getInstance().collection("reservations").whereEqualTo("locationName",location.getName())
                .whereEqualTo("locationOwner",location.getOwner()).get().addOnCompleteListener(task ->{
                    ScrollView scrollView = new ScrollView(context);
                    LinearLayout layout = new LinearLayout(context);
                    layout.setGravity(Gravity.CENTER);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    scrollView.addView(layout);
                    if(task.isSuccessful() && !task.getResult().getDocuments().isEmpty()){
                        for(DocumentSnapshot ds : task.getResult().getDocuments()) {
                            DocumentReference df = ds.getReference();
                            String name = ds.getString("fullName");
                            String email = ds.getString("email");
                            String phoneNumber = ds.getString("phoneNumber");
                            String date = ds.getString("date");
                            long nOfPeople = (Long)ds.get("numberOfPeople");
                            String info =
                                    "Név : " + name + "\n" +
                                            "Email cím : " + email + "\n" +
                                            "Telefonszám : " + phoneNumber + "\n" +
                                            "Dátum : " + date + "\n" +
                                            "Emberek száma : " + nOfPeople;
                            boolean accepted = (boolean)ds.get("accepted");

                            LayoutInflater inflater
                                    = LayoutInflater.from(context);
                            View view = inflater.inflate(R.layout.showrequest_card,parent,false);
                            ((TextView)view.findViewById(R.id.reservationInfo)).setText(info);
                            ((CheckBox)view.findViewById(R.id.reservationAcceptDecline)).setChecked(accepted);

                            ((CheckBox)view.findViewById(R.id.reservationAcceptDecline)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    Map<String,Object> updates = new HashMap<>();
                                    updates.put("accepted",isChecked);
                                    df.update(updates);
                                }
                            });
                            layout.addView(view);
                        }

                        Dialog dialog = new Dialog(context);
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = 1000;
                        dialog.setTitle(location.getName());
                        dialog.setContentView(scrollView);
                        dialog.show();
                        dialog.getWindow().setAttributes(lp);
                    }else {
                        new AlertDialog.Builder(context).setTitle("Hiba").setMessage("Nincnsenek foglalási kérelmeid!").show();
                    }
                });
    }

    private void reserve(User user,Location location,int numberOfPeople,String date) {
        Reservation reservation = new Reservation();
        reservation.setDate(date);
        reservation.setEmail(user.getEmail());
        reservation.setLocationName(location.getName());
        reservation.setLocationOwner(location.getOwner());
        reservation.setNumberOfPeople(numberOfPeople);
        reservation.setAccepted(false);
        reservation.setFullName(user.getFirstName() + " " + user.getLastName());
        reservation.setPhoneNumber(user.getPhoneNumber());

        FirebaseFirestore.getInstance().collection("reservations").add(reservation).addOnCompleteListener(task2 -> {
            if(task2.isSuccessful()){
                new AlertDialog.Builder(context).setTitle("Info").setMessage("Foglalási kérelem leadása sikerült!").show().setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Intent intent = new Intent(context,MainActivity.class);
                        context.startActivity(intent);
                    }
                });
            }else {
                new AlertDialog.Builder(context).setTitle("Hiba").setMessage("Foglalási kérelem leadása nem sikerült!").show();
            }
        });
    }

    private void showReserveForm(int position) {
        if(currentUser == null && userInfo.getEmail() != null){
            new AlertDialog.Builder(context).setTitle("Hiba").setMessage("Fiók létrehozása szükséges!").show();
            return;
        }

        Location location = locations.get(position);
        Dialog dialog = new Dialog(context);

        LayoutInflater inflater
                = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.reserveform,parent,false);


        view.findViewById(R.id.reserveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int numberOfPeople = Integer.parseInt(((EditText)view.findViewById(R.id.numberOfPeopleInput)).getText().toString());
                    DatePicker dateInput = ((DatePicker)view.findViewById(R.id.dateInput));
                    Date dateObj = new Date(dateInput.getYear(),dateInput.getMonth(),dateInput.getDayOfMonth());
                    String date = dateObj.toString();

                    reserve(userInfo,location,numberOfPeople,date);
                }catch (Exception ignore){
                    new AlertDialog.Builder(context).setTitle("Hiba").setMessage("Foglalási kérelem leadása nem sikerült!").show();
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.setContentView(view);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
