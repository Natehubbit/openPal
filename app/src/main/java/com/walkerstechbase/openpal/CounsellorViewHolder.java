package com.walkerstechbase.openpal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class CounsellorViewHolder extends RecyclerView.ViewHolder{
    TextView counsellorName;

    public CounsellorViewHolder(@NonNull View itemView) {
        super(itemView);

        counsellorName = itemView.findViewById(R.id.counsellor_name);
    }
}
