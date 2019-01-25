package jcpsnowproductions.mykotlinapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecylerViewAdapter";

    private ArrayList<vehicle> mVehicles = new ArrayList<>();
    //private ArrayList<String> mVehicleNames = new ArrayList<>();
    private  ArrayList<String> mImages = new ArrayList<>();
    //private ArrayList<Integer> mVehicleIDs = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<vehicle> mVehicles, ArrayList<String> mImages, Context mContext) {
        //this.mVehicleNames = mVehicleNames;
        this.mImages = mImages;
        //this.mVehicleIDs = mVehicleIDs;
        this.mContext = mContext;
        this.mVehicles = mVehicles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(i))
                .into(viewHolder.image);

        viewHolder.vehicleName.setText(mVehicles.get(i).getVName());

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + mVehicles.get(i).getVName());
                Intent intent = new Intent(mContext, ViewVehicle.class);
                //Bundle b = new Bundle();
                //b.putInt("key", mVehicles.get(i));
                intent.putExtra("vehicle", mVehicles.get(i));
                mContext.startActivity(intent);
                //Toast.makeText(mContext, mVehicleNames.get(i) + mVehicleIDs.get(i), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVehicles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView vehicleName;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            vehicleName = itemView.findViewById(R.id.vehicle_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
