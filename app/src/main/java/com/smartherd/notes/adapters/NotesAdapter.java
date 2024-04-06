package com.smartherd.notes.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.smartherd.notes.R;
import com.smartherd.notes.activities.CreateNoteActivity;
import com.smartherd.notes.activities.DataHolder;
import com.smartherd.notes.entities.Note;
import com.smartherd.notes.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends FirestoreRecyclerAdapter<Note, NotesAdapter.NoteViewHolder> {

    private NotesListener notesListener;
    private Context context;
    private List<Note> originalNotesList; // Store the original list of notes
    private List<Note> filteredNotesList; // Store the filtered list of notes

    Note note = new Note();


    public NotesAdapter(@NonNull FirestoreRecyclerOptions<Note> options, NotesListener notesListener, Context context) {
        super(options);
        this.notesListener = notesListener;
        this.context = context;
        originalNotesList = new ArrayList<>(options.getSnapshots()); // Initialize original list
        filteredNotesList = new ArrayList<>(originalNotesList); // Initialize filtered list
    }

    public List<Note> getOriginalNotesList(){return this.originalNotesList;}
    public List<Note> getFilteredNotesList(){return this.filteredNotesList;}


    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note model) {
        Log.d("TAG","The model " + model);
        holder.setNote(model);
//        holder.layoutNote.setOnClickListener(v -> {
//            if (notesListener != null) {
//                DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
//                Note note = snapshot.toObject(Note.class);
//                if (note != null) {
//                    notesListener.onNoteClicked(note, position);
//                }
//            }
//        });
        holder.itemView.setOnClickListener((v -> {
            Intent intent = new Intent(context, CreateNoteActivity.class);
            intent.putExtra("title",model.getTitle());
            intent.putExtra("subTitle",model.getSubtitle());
            intent.putExtra("content",model.getNoteText());
            intent.putExtra("url",model.getWebLink());
            intent.putExtra("color",model.getColor());
            intent.putExtra("imagePath",model.getImagePath());
            intent.putExtra("search",model.getTitle().toLowerCase());
            intent.putExtra("reminder",model.getReminder());
            intent.putExtra("document_path",model.getDocument_path());
            intent.putExtra("audio_path",model.getAudioPath());
            intent.putExtra("document_text",model.getDocument_text());
//            intent.putExtra("Reminder",model.getReminder());
            String noteId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("noteId",noteId);
            context.startActivity(intent);
        }));
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        boolean isBlackTheme = DataHolder.getInstance().isBlackTheme();
        // Inflate different layouts based on the selected theme
        View view = isBlackTheme
                ? LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_note, parent, false)
                : LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_note_light, parent, false);

        return new NoteViewHolder(view);
    }
//    // Method to update dataset with filtered list
//    public void filterNotes(String query) {
//        filteredNotesList.clear();
//        if (query.isEmpty()) {
//            filteredNotesList.addAll(originalNotesList); // If query is empty, show original list
//        } else {
//            for (Note note : originalNotesList) {
//                // Filter notes based on title or subtitle containing the query
//                if (note.getTitle().toLowerCase().contains(query.toLowerCase()) ||
//                        note.getSubtitle().toLowerCase().contains(query.toLowerCase())) {
//                    filteredNotesList.add(note);
//                }
//            }
//        }
//        notifyDataSetChanged(); // Notify adapter about the change
//    }
// Override getFilter method to provide custom filtering logic
public Filter getFilter() {
    return new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<Note> filteredNotes = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredNotes.addAll(originalNotesList); // If no search query, show all notes
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Note note : originalNotesList) {
                    // Filter notes based on search query
                    if (note.getTitle().toLowerCase().contains(filterPattern) || note.getSubtitle().toLowerCase().contains(filterPattern) || note.getNoteText().toLowerCase().contains(filterPattern)) {
                        filteredNotes.add(note);
                    }
                }
            }

            filterResults.values = filteredNotes;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredNotesList.clear();
            filteredNotesList.addAll((List<Note>) results.values);
            notifyDataSetChanged(); // Notify adapter about data changes
        }
    };
}

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle, textSubtitle, textDateTime, textReminderTime;
        LinearLayout layoutNote, reminderBackground;
        RoundedImageView imageNote;
        long futureTimeInMillis = DataHolder.getInstance().getData();
        long currentTimeInMillis = System.currentTimeMillis();

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);
            textReminderTime = itemView.findViewById(R.id.textReminderTime);
            reminderBackground = itemView.findViewById(R.id.reminder_background);

        }

       public void setNote(Note note) {
            textTitle.setText(note.getTitle());
            textSubtitle.setText(note.getSubtitle());
            textDateTime.setText(note.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if (note.getColor() != null) {
//                gradientDrawable.setColor(Color.parseColor(note.getColor()));
                gradientDrawable.setStroke(3, Color.parseColor(note.getColor()));
            } else {
//                gradientDrawable.setColor(Color.parseColor("#333333"));
                gradientDrawable.setStroke(3, Color.parseColor("#333333"));
            }

            if (note.getImagePath() != null && !note.getImagePath().trim().isEmpty()) {
                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                imageNote.setVisibility(View.VISIBLE);
            } else {
                imageNote.setVisibility(View.GONE);
            }
            if(note.getReminder() != null && !note.getReminder().trim().isEmpty()){
                textReminderTime.setVisibility(View.VISIBLE);
                textReminderTime.setText(note.getReminder());
                textReminderTime.setTextColor(Color.parseColor("#A4A4A4"));

                if(futureTimeInMillis <= currentTimeInMillis){
                    SpannableString spannableString = new SpannableString(textReminderTime.getText());
                    spannableString.setSpan(new StrikethroughSpan(),0,textReminderTime.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textReminderTime.setText(spannableString);
                }

            }
        }
    }


}
