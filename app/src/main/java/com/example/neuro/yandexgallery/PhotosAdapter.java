package com.example.neuro.yandexgallery;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    public static final String URL_KEY = "URLS";

    private Context context;
    private LayoutInflater inflater;
    private List<Photo> data;
    private TextView total;

    PhotosAdapter(Context context, TextView total) {
        this.context = context;
        this.total = total;
        inflater = LayoutInflater.from(context);
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.photo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Photo item = data.get(position);

        if (item.getUrls().size() > 0) {
            Glide
                    .with(context)
                    .load(item.getLowQuality())
                    .into(holder.image);
        } else {
            Glide
                    .with(context)
                    .load(R.drawable.not_loaded)
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowPhoto.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(URL_KEY, item);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setTotal(int total){
        this.total.setText("Total: " + total);
    }

    public void setData(List<Photo> data) {
        this.data = data.subList(1, data.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.photo);
        }
    }
}
