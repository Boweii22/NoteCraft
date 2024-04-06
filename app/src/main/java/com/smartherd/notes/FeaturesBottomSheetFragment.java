package com.smartherd.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FeaturesBottomSheetFragment extends BottomSheetDialogFragment {

    public FeaturesBottomSheetFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_features, container, false);



        // Handle menu item clicks
        view.findViewById(R.id.whiteboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Whiteboard Clicked", Toast.LENGTH_SHORT).show();
                dismiss(); // Dismiss the bottom sheet after handling the click
            }
        });

        view.findViewById(R.id.image_to_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Image To Text Clicked", Toast.LENGTH_SHORT).show();
                dismiss(); // Dismiss the bottom sheet after handling the click
            }
        });
        view.findViewById(R.id.import_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Import Clicked", Toast.LENGTH_SHORT).show();
                dismiss(); // Dismiss the bottom sheet after handling the click
            }
        });
        view.findViewById(R.id.speech_to_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Speech To Text Clicked", Toast.LENGTH_SHORT).show();
                dismiss(); // Dismiss the bottom sheet after handling the click
            }
        });
        view.findViewById(R.id.export_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Add more menu items as needed

        return view;
    }
}
