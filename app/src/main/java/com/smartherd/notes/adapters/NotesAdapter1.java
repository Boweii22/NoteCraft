package com.smartherd.notes.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.smartherd.notes.R;
import com.smartherd.notes.SharedPreferencesHelper;
import com.smartherd.notes.entities.Note;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class NotesAdapter1 extends FirestoreRecyclerAdapter<Note, NotesAdapter1.NoteViewHolder> {
    private Timer timer;
   Context context;
    public NotesAdapter1(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {
            holder.titleTextView.setText(note.getTitle());
            holder.subTitleTextView.setText(note.getSubtitle());
            holder.dateTimeTextView.setText(note.getDateTime());

        //Apply font style
        holder.applySelectedFont();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_note, parent, false);
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView, subTitleTextView, dateTimeTextView;
        LinearLayout layoutNote;
        RoundedImageView imageNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textTitle);
            subTitleTextView = itemView.findViewById(R.id.textSubtitle);
            dateTimeTextView = itemView.findViewById(R.id.textDateTime);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);
        }

        private void applySelectedFont() {
            applyFontStyleToView(titleTextView, SharedPreferencesHelper.getFontStyle(itemView.getContext()));
            applyFontStyleToView(subTitleTextView, SharedPreferencesHelper.getFontStyle(itemView.getContext()));
            applyFontStyleToView(dateTimeTextView, SharedPreferencesHelper.getFontStyle(itemView.getContext()));
        }
        private void applyFontStyleToView(TextView textView, String fontStyle) {
            switch (fontStyle) {
                case "sans":
                    textView.setTypeface(Typeface.SANS_SERIF);
                    break;
                case "serif":
                    textView.setTypeface(Typeface.SERIF);
                    break;
                case "monospace":
                    textView.setTypeface(Typeface.MONOSPACE);
                    break;
                default:
                    textView.setTypeface(Typeface.DEFAULT);
                    break;
            }
        }
    }
    public void searchNotes(final String searchKeyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyword.trim().isEmpty()){

                }else {

                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }
    public void cancelTimer(){
        if(timer != null){
            timer.cancel();
        }
    }
}
