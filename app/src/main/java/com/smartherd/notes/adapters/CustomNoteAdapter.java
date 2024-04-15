package com.smartherd.notes.adapters;

import android.content.Context;
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
import com.smartherd.notes.entities.Note;
import com.smartherd.notes.entities.NoteTag;

import java.util.List;

public class CustomNoteAdapter extends RecyclerView.Adapter<CustomNoteAdapter.ViewHolder> {
    private Context mContext;
    private List<NoteTag> mNotes;

    public CustomNoteAdapter(Context context, List<NoteTag> notes) {
        mContext = context;
        mNotes = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_container_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoteTag note = mNotes.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.contentTextView.setText(note.getContent());

        Note note1 = new Note();
        GradientDrawable gradientDrawable = (GradientDrawable) holder.layoutNote.getBackground();
        if (DataHolder.getInstance().getLabelColor() != null) {
//                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            gradientDrawable.setStroke(3, Color.parseColor(DataHolder.getInstance().getLabelColor()));
        } else {
//                gradientDrawable.setColor(Color.parseColor("#333333"));
            gradientDrawable.setStroke(3, Color.parseColor("#333333"));
        }

    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        LinearLayout layoutNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textTitle);
            contentTextView = itemView.findViewById(R.id.textSubtitle);
            layoutNote = itemView.findViewById(R.id.layoutNote);
        }
    }
}
