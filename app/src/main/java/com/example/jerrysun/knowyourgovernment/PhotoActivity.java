package com.example.jerrysun.knowyourgovernment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

/**
 * Created by jerrysun on 4/29/17.
 */

public class PhotoActivity extends AppCompatActivity {

    private Official official;
    private String locationStr;

    private TextView location;
    private TextView office;
    private TextView name;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        location = (TextView) findViewById(R.id.location);
        office = (TextView) findViewById(R.id.office);
        name = (TextView) findViewById(R.id.name);
        photo = (ImageView) findViewById(R.id.photo);

        Intent intent = getIntent();
        if (intent.hasExtra("OFFICIAL_LOCATION")) {
            locationStr = intent.getStringExtra("OFFICIAL_LOCATION");
            location.setText(locationStr);
        } else {
            location.setText("");
        }
        if (intent.hasExtra("OFFICIAL_DETAIL")) {
            official = intent.getParcelableExtra("OFFICIAL_DETAIL");
            if (official != null) {
                setPhotoDataToView(official);
            } else {
                Toast.makeText(this, "No official data provided.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Fail to receive official data.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        };
        return super.onOptionsItemSelected(item);
    }

    public void setPhotoDataToView(Official official) {

        if (official.getOffice().isEmpty()) {
            office.setText("No Data Provided");
        } else {
            office.setText(official.getOffice());
        }

        if (official.getName().isEmpty()) {
            name.setText("No Data Provided");
        } else {
            name.setText(official.getName());
        }

        if (official.getParty().isEmpty()) {
            findViewById(R.id.photo_layout).setBackgroundColor(Color.WHITE);
        } else {
            if (official.getParty().equals("Republican")) {
                findViewById(R.id.photo_layout).setBackgroundColor(Color.RED);
            } else if (official.getParty().equals("Democratic")) {
                findViewById(R.id.photo_layout).setBackgroundColor(Color.BLUE);
            } else {
                findViewById(R.id.photo_layout).setBackgroundColor(Color.BLACK);
            }
        }

        photoDownloader(official.getPhotoUrl());
    }

    public void photoDownloader(final String photoUrl) {

        if (photoUrl != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {

                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    final String changedUrl = photoUrl.replace("http:", "https:");

                    picasso.load(changedUrl)
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(photo);

                }
            }).build();

            picasso.load(photoUrl)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(photo);
        }
    }
}
