package com.hw4.juri.hw4;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {
    private Official official;

    private TextView locationText;
    private TextView officeText;
    private TextView nameText;
    private TextView partyText;
    private TextView websiteText;
    private TextView emailText;
    private TextView phoneText;
    private TextView addressText;
    private ImageView photoView;
    private ImageView gpView;
    private ImageView fbView;
    private ImageView twView;
    private ImageView ytbView;
    private ConstraintLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        locationText = findViewById(R.id.curLocation);
        officeText = findViewById(R.id.office);
        nameText = findViewById(R.id.name);
        partyText = findViewById(R.id.party);
        websiteText = findViewById(R.id.website);
        emailText = findViewById(R.id.email);
        phoneText = findViewById(R.id.phone);
        addressText = findViewById(R.id.address);
        photoView = findViewById(R.id.photo);
        gpView = findViewById(R.id.google);
        fbView = findViewById(R.id.facebook);
        twView = findViewById(R.id.twitter);
        ytbView = findViewById(R.id.youtube);
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

        // Set text
        locationText.setText(location);
        officeText.setText(official.getOffice());
        nameText.setText(official.getName());
        partyText.setText("("+official.getParty()+")");
        websiteText.setText(official.getWebsite());
        emailText.setText(official.getEmail());
        phoneText.setText(official.getPhone());
        addressText.setText(official.getAddress());

        Linkify.addLinks(websiteText, Linkify.WEB_URLS);
        Linkify.addLinks(phoneText, Linkify.PHONE_NUMBERS);
        Linkify.addLinks(addressText, Linkify.MAP_ADDRESSES);
        Linkify.addLinks(emailText, Linkify.EMAIL_ADDRESSES);

        // Load image
        loadImage();

        // Set social media
        if(official.getGpID().equals("")){
            gpView.setVisibility(View.INVISIBLE);
        }
        if(official.getFbID().equals("")){
            fbView.setVisibility(View.INVISIBLE);
        }
        if(official.getTwID().equals("")){
            twView.setVisibility(View.INVISIBLE);
        }
        if(official.getYtbID().equals("")){
            ytbView.setVisibility(View.INVISIBLE);
        }
    }

    public void loadImage(){
        if (!official.getPhoto().equals("")) {
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
        } else {
            Picasso.get().load(R.drawable.missingimage)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(photoView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.other_menu,menu);
        return true;
    }

    public void imageClick(View v){
        if(!official.getPhoto().equals("")){
            Intent intent = new Intent(OfficialActivity.this, PhotoDetailActivity.class);
            intent.putExtra("OFFICIAL",official);
            intent.putExtra("LOCATION",locationText.getText().toString());
            startActivity(intent);
        }
    }

    public void googlePlusClicked(View v) {
        String name = official.getGpID();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + name)));
        }
    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + official.getFbID();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + official.getFbID();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = official.getTwID();
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void youTubeClicked(View v) {
        String name = official.getYtbID();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }
}
