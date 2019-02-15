package com.hw4.juri.hw4;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity {
    private Official official;

    private TextView locationText;
    private TextView officeText;
    private TextView nameText;
    private ImageView photoView;
    private ConstraintLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        locationText = findViewById(R.id.curLocation);
        officeText = findViewById(R.id.office);
        nameText = findViewById(R.id.name);
        photoView = findViewById(R.id.photo);
        background = findViewById(R.id.background);

        Intent intent = getIntent();
        official = (Official) intent.getSerializableExtra("OFFICIAL");
        String location = intent.getStringExtra("LOCATION");

        // Set background color
        if(official.getParty().equals("Republican")){
            background.setBackgroundColor(Color.RED);
        }else if(official.getParty().equals("Democratic")){
            background.setBackgroundColor(Color.BLUE);
        }else{
            background.setBackgroundColor(Color.BLACK);
        }

        locationText.setText(location);
        officeText.setText(official.getOffice());
        nameText.setText(official.getName());

        loadImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.other_menu,menu);
        return true;
    }

    public void loadImage(){
        Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                final String changedUrl = official.getPhoto().replace("http:", "https:");
                picasso.load(changedUrl)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(photoView);
            }
        }).build();
        picasso.load(official.getPhoto())
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(photoView);
    }
}
