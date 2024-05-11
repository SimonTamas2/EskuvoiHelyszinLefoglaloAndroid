package com.example.eskuvoihelyszinlefoglalo.shared.recyclerview;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eskuvoihelyszinlefoglalo.R;

public class LocationViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    TextView city;
    TextView address;
    TextView description;

    TextView ownerName;
    TextView ownerPhone;

    ViewFlipper images;

    Button showRequestsButton;

    Button reserveButton;

    Button deleteButton;

    Button deleteReservationButton;

    public LocationViewHolder(@NonNull View itemView) {
        super(itemView);

        name = (TextView)itemView.findViewById(R.id.name);
        city = (TextView)itemView.findViewById(R.id.city);
        address = (TextView)itemView.findViewById(R.id.address);
        description = (TextView)itemView.findViewById(R.id.description);
        ownerName = (TextView)itemView.findViewById(R.id.ownerName);
        ownerPhone = (TextView)itemView.findViewById(R.id.ownerPhone);
        images = (ViewFlipper)itemView.findViewById(R.id.images);
        showRequestsButton = (Button)itemView.findViewById(R.id.showRequestsButton);
        reserveButton = (Button)itemView.findViewById(R.id.reserveButton);
        deleteButton = (Button)itemView.findViewById(R.id.deleteButton);
        deleteReservationButton  = itemView.findViewById(R.id.deleteReservationButton);
    }


}
