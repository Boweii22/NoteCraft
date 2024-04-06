package com.smartherd.notes.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.smartherd.notes.R;
import com.smartherd.notes.SharedPreferencesHelper;
import com.smartherd.notes.entities.Feedbacks;

public class HelpFeedbackActivity extends AppCompatActivity {

    private int rating = 0;
    private ImageView textBack;
    private ImageView[] stars;

    Button saveFeedbackToFirebase;
    EditText feedbackText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isBlackTheme = SharedPreferencesHelper.loadThemeChoice(this);
        if (isBlackTheme) {
            setTheme(R.style.Theme_App_Black);
        } else {
            setTheme(R.style.Theme_App_White);
        }
        setContentView(R.layout.activity_help_feedback);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        saveFeedbackToFirebase = findViewById(R.id.saveFeedbackToFirebase);
        feedbackText = findViewById(R.id.feedbackText);

        textBack = findViewById(R.id.back);
        textBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        stars = new ImageView[5];
        stars[0] = findViewById(R.id.star1);
        stars[1] = findViewById(R.id.star2);
        stars[2] = findViewById(R.id.star3);
        stars[3] = findViewById(R.id.star4);
        stars[4] = findViewById(R.id.star5);

        final LinearLayout ratingLayout = findViewById(R.id.ratingLayout);

        // Set onClickListener to each star ImageView
        for (int i = 0; i < 5; i++) {
            final int index = i;
            stars[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRating(index + 1, stars);
                }
            });
        }

        saveFeedbackToFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedbackString = feedbackText.getText().toString();
                int starRating = DataHolder.getInstance().getRating();
                if (feedbackString.isEmpty()) {
                    Toast.makeText(HelpFeedbackActivity.this, "Please enter feedback", Toast.LENGTH_SHORT).show();
                    return;
                }
                Feedbacks feedbacks = new Feedbacks();
                feedbacks.setFeedbacks(feedbackString);
                feedbacks.setStars(starRating);
                saveFeedbackToFirebase(feedbacks);
            }
        });

    }

    private void setRating(int rate, ImageView[] stars) {
        rating = rate;
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.star_filled);
            } else {
                stars[i].setImageResource(R.drawable.star_empty);
            }
        }
        DataHolder.getInstance().setRating(rating);
    }

    private void saveFeedbackToFirebase(Feedbacks feedbacks) {
        DocumentReference documentReference = Utility.getCollectionReferenceForFeedbacks().document();
        documentReference.set(feedbacks).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    feedbackText.setText("");
                    rating = 0;
                    for (int i = 0; i < 5; i++) {
                        stars[i].setImageResource(R.drawable.star_empty);
                    }
                    Toast.makeText(HelpFeedbackActivity.this, "Feedback saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HelpFeedbackActivity.this, "Feedback not saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
