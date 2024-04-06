package com.smartherd.notes.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.smartherd.notes.R;
import com.smartherd.notes.adapters.CarouselAdapter;

public class AccountActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private int[] images = {R.drawable.notification_reminder_image, R.drawable.share_image, R.drawable.task_image};

    Button button_register;
    Button button_signin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new CarouselAdapter(images, viewPager));

        button_register = findViewById(R.id.button_register);
        button_signin = findViewById(R.id.button_signin);

        button_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this, SignInActivity.class));
            }
        });

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}