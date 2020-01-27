package com.walkerstechbase.openpal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class CounselViewHolder extends RecyclerView.ViewHolder{
    TextView name, date, time, number, message, accept;
    LinearLayout lottieAnimationView;
    LottieAnimationView lottieObj;

    public CounselViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        date = itemView.findViewById(R.id.date);
        time = itemView.findViewById(R.id.time);
        number = itemView.findViewById(R.id.number);
        message = itemView.findViewById(R.id.message);
        lottieAnimationView = itemView.findViewById(R.id.lottie_layer_name);
        lottieObj = itemView.findViewById(R.id.animation_view);
        accept = itemView.findViewById(R.id.accept);
    }
}
