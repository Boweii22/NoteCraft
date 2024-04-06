package com.smartherd.notes.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.smartherd.notes.Entitites.Label;
import com.smartherd.notes.R;
import com.smartherd.notes.activities.DataHolder;
import com.smartherd.notes.entities.Note;
import com.smartherd.notes.listeners.LabelListener;

public class LabelAdapter extends FirestoreRecyclerAdapter<Label, LabelAdapter.LabelViewHolder> {
    private LabelListener labelListener;
    private Context context;
    private FirestoreRecyclerOptions<Label> options;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public LabelAdapter(@NonNull FirestoreRecyclerOptions<Label> options, LabelListener labelListener, Context context) {
        super(options);
        this.labelListener = labelListener;
        this.context = context;
        this.options = options;
    }

    @Override
    protected void onBindViewHolder(@NonNull LabelViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Label model) {
        holder.setLabel(model);
        String noteId = this.getSnapshots().getSnapshot(position).getId();
        DataHolder.getInstance().setLabelId(noteId);
        DataHolder.getInstance().setLabelName(model.labelName);
        holder.layoutLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                labelListener.onLabelClicked(position);
            }
        });
    }


    @NonNull
    @Override
    public LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_label, parent, false);
        return new LabelViewHolder(view);
    }

    public static class LabelViewHolder extends RecyclerView.ViewHolder {

        TextView textLabel, textDateTime;
        LinearLayout layoutLabel;

        public LabelViewHolder(@NonNull View itemView){
            super(itemView);
            textLabel = itemView.findViewById(R.id.textLabel);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutLabel = itemView.findViewById(R.id.layoutLabel);
        }

        public void setLabel(Label label){
            textLabel.setText(label.getLabelName());
            textDateTime.setText(label.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutLabel.getBackground();
            if(label.getColor() != null){
                gradientDrawable.setColor(Color.parseColor(label.getColor()));
            }else{
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }
        }

    }
}
