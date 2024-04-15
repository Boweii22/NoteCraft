package com.smartherd.notes.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartherd.notes.R;
import com.smartherd.notes.activities.DataHolder;
import com.smartherd.notes.entities.TagWithDateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private List<TagWithDateTime> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private LongClickListener mLongClickListener; // Declaration of long click listener
    private String tagToDelete; // New field to store the tag marked for deletion

    private Context mContext;

    public CustomAdapter(Context context, List<TagWithDateTime> data) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_custom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TagWithDateTime item = mData.get(position);
        holder.textView.setText(item.getTag());
        holder.dateTime.setText(item.getDateTime());

        GradientDrawable gradientDrawable = (GradientDrawable) holder.layoutLabel.getBackground();

        if (item.getColor() != null) {
//                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            gradientDrawable.setStroke(3, Color.parseColor(item.getColor()));
            DataHolder.getInstance().setLabelColor(item.getColor());
        } else {
//                gradientDrawable.setColor(Color.parseColor("#333333"));
            gradientDrawable.setStroke(3, Color.parseColor("#333333"));
        }

        holder.layoutLabel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mLongClickListener != null) {
                    mLongClickListener.onItemLongClick(item.getTag());
                    return true;
                }
                return false;
            }
        });
    }
    public void setLongClickListener(LongClickListener longClickListener) {
        this.mLongClickListener = longClickListener;
    }

    public String getTagToDelete() {
        return tagToDelete;
    }

    public void setTagToDelete(String tagToDelete) {
        this.tagToDelete = tagToDelete;
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0; // or any other default value or behavior you prefer
        }
    }

    public void setData(List<TagWithDateTime> filteredNotes) {
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        TextView dateTime;
        LinearLayout layoutLabel;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            dateTime = itemView.findViewById(R.id.dateTime);
            itemView.setOnClickListener(this);
            layoutLabel = itemView.findViewById(R.id.labelLayout);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    String getItem(int id) {
        return mData.get(id).getTag();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public interface LongClickListener {
        void onItemLongClick(String tag);
    }

//    // Method to save tagsList to SharedPreferences
//    public void saveTagsListToSharedPreferences() {
//        SharedPreferences sharedPref = mContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        Set<String> tagSet = new HashSet<>(mData);
//        editor.putStringSet("tags_list", tagSet);
//        editor.apply();
//    }
//
//    // Method to retrieve tagsList from SharedPreferences
//    public void retrieveTagsListFromSharedPreferences() {
//        SharedPreferences sharedPref = mContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        Set<String> tagSet = sharedPref.getStringSet("tags_list", new HashSet<>());
//        mData = new ArrayList<>(tagSet);
//    }
}
