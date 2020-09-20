package com.example.jerrysun.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import com.squareup.picasso.Picasso;

/**
 * Created by jerrysun on 4/30/17.
 */

public class OfficialActivity extends AppCompatActivity {

    private Official official;
    private String locationStr;

    private TextView location;
    private TextView office;
    private TextView name;
    private TextView party;
    private ImageView photoButton;
    private TextView address;
    private TextView phone;
    private TextView email;
    private TextView website;
    private ImageView googleplus;
    private ImageView facebook;
    private ImageView twitter;
    private ImageView youtube;
    private String googlePlusId;
    private String facebookId;
    private String twitterId;
    private String youtubeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        location = (TextView) findViewById(R.id.location);
        office = (TextView) findViewById(R.id.office);
        name = (TextView) findViewById(R.id.name);
        party = (TextView) findViewById(R.id.party);
        photoButton = (ImageView) findViewById(R.id.photoButton);
        address = (TextView) findViewById(R.id.address);
        phone = (TextView) findViewById(R.id.phone);
        email = (TextView) findViewById(R.id.email);
        website = (TextView) findViewById(R.id.website);
        googleplus = (ImageView) findViewById(R.id.googleplus);
        facebook = (ImageView) findViewById(R.id.facebook);
        twitter = (ImageView) findViewById(R.id.twitter);
        youtube = (ImageView) findViewById(R.id.youtube);

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
                setOfficialDataToView(official);
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

    public void setOfficialDataToView (Official offcial) {

        office.setText(official.getOffice());
        name.setText(official.getName());

        if (official.getParty().equals("Republican")) {
            party.setText("(" + official.getParty() + ")");
            findViewById(R.id.info_layout).setBackgroundColor(Color.RED);
        } else if (official.getParty().equals("Democratic")) {
            party.setText("(" + official.getParty() + ")");
            findViewById(R.id.info_layout).setBackgroundColor(Color.BLUE);
        } else {
            findViewById(R.id.info_layout).setBackgroundColor(Color.BLACK);
        }

        if (photoDownloader(official.getPhotoUrl())) {
            photoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photoButtonClicked(photoButton);
                }
            });
        }

        //set views
        address.setText(official.getAddress());
        if (!official.getAddress().equals("No Data Provided")) {
            Linkify.addLinks(address, Linkify.MAP_ADDRESSES);
        }

        phone.setText(official.getPhone());
        if (!official.getPhone().equals("No Data Provided")) {
            Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
        }

        email.setText(official.getEmail());
        if (!official.getEmail().equals("No Data Provided")) {
            Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
        }

        website.setText(official.getUrl());
        if (!official.getUrl().equals("No Data Provided")) {
            Linkify.addLinks(website, Linkify.WEB_URLS);
        }

        if (official.getChannels() != null) {
            Map<String, String> channels = official.getChannels();
            if (channels.containsKey("GooglePlus")) {
                googlePlusId = channels.get("GooglePlus");
                googleplus.setVisibility(View.VISIBLE);
            } else {
                googleplus.setVisibility(View.INVISIBLE);
            }

            if (channels.containsKey("Facebook")) {
                facebookId = channels.get("Facebook");
                facebook.setVisibility(View.VISIBLE);
            } else {
                facebook.setVisibility(View.INVISIBLE);
            }

            if (channels.containsKey("Twitter")) {
                twitterId = channels.get("Twitter");
                twitter.setVisibility(View.VISIBLE);
            } else {
                twitter.setVisibility(View.INVISIBLE);
            }

            if (channels.containsKey("YouTube")) {
                youtubeId = channels.get("YouTube");
                youtube.setVisibility(View.VISIBLE);

            } else {
                youtube.setVisibility(View.INVISIBLE);
            }
        }
    }

    //download photo
    public boolean photoDownloader(final String photoUrl) {

        if (photoUrl != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {

                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    final String changedUrl = photoUrl.replace("http:", "https:");

                    picasso.load(changedUrl)
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(photoButton);

                }
            }).build();

            picasso.load(photoUrl)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(photoButton);

            return true;

        }
        return false;
    }

    public void photoButtonClicked(View v) {
        Intent intent_official = new Intent(this, PhotoActivity.class);
        intent_official.putExtra("OFFICIAL_LOCATION", locationStr);
        intent_official.putExtra("OFFICIAL_DETAIL", official);
        startActivity(intent_official);
    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + facebookId;

        Intent intent = null;
        String urlToUse;
        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);

            int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + facebookId;
            }
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
        }

        startActivity(intent);

    }

    public void twitterClicked(View v) {

        String twitterAppUrl = "twitter://user?screen_name=" + twitterId;
        String twitterWebUrl = "https://twitter.com/" + twitterId;

        Intent intent = null;
        try {
            //Get the twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
        } catch (Exception e) {
            //No twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
        }
        startActivity(intent);
    }

    public void googleplusClicked(View v) {
        String googleplusWebUrl = "https://plus.google.com/" + googlePlusId;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", googlePlusId);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(googleplusWebUrl)));
        }
    }

    public void youtubeClicked(View v) {
        String youtubeWebUrl = "https://www.youtube.com/" + youtubeId;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse(youtubeWebUrl));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeWebUrl)));
        }
    }


}
