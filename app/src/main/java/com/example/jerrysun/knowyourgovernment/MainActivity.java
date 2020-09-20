package com.example.jerrysun.knowyourgovernment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private Locator locator;

    private List<Official> officialList = new ArrayList<>();
    private String location;
    private TextView locationTextView;
    private RecyclerView recyclerView;
    private OfficialAdapter officialAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationTextView = (TextView) findViewById(R.id.location);

        //Check network connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            //Create locator
            locator = new Locator(this);
        } else {
            String message = "Data cannot be accessed/loaded without an internet connection.";
            locationTextView.setText("No Data For Location");
            noNetworkConnectionAlertDialog(message).show();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        officialAdapter = new OfficialAdapter(officialList, this);
        recyclerView.setAdapter(officialAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public Dialog noNetworkConnectionAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage(message);
        return builder.create();
    }

    //Get the postalcode from the local address to download civic data
    public void doLocationWork(double latitude, double longitude) {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String postalCode = addresses.get(0).getPostalCode();
            new CivicInfoDownloader(this).execute(postalCode);

        } catch (IOException e) {
            Toast.makeText(this, "The address cannot be acquired from the provided latitude and longitude.", Toast.LENGTH_LONG).show();
        }
    }

    public void noLocationAvailable() {
        Toast.makeText(this, "No location providers were available", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 5) {

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        locator.setUpLocationManager();
                        locator.determineLocation();
                    } else {
                        Toast.makeText(this, "Location permission was denied - cannot determine address", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setOfficialList(ArrayList<Object> resultObjList) {

        if (resultObjList == null) {
            location = "No Data For Location";
            officialList.clear();
        } else {
            location = (String) resultObjList.get(0);
            locationTextView.setText(location);
            officialList.clear();
            officialList.addAll((List<Official>) resultObjList.get(1));
        }

        officialAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildAdapterPosition(v);
        Official official = officialList.get(position);

        Intent intent_official = new Intent(MainActivity.this, OfficialActivity.class);
        intent_official.putExtra("OFFICIAL_LOCATION", location);
        intent_official.putExtra("OFFICIAL_DETAIL", official);
        startActivity(intent_official);
    }

    @Override
    public boolean onLongClick(View v) {
        //Call the onClick(View v) method
        onClick(v);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.infoItem:

                //Open the About Activity, which indicates the app's information
                Intent intent_info = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent_info);
                return true;

            case R.id.searchItem:
                //Open an alert dialog for address input
                enterAddressDialog().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Dialog enterAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
        et.setGravity(Gravity.CENTER_HORIZONTAL);

        builder.setMessage("Enter a City, State or a Zip Code:");
        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (et.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Input cannot be null.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String address = et.getText().toString();
                new CivicInfoDownloader(MainActivity.this).execute(address);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the search
                Toast.makeText(MainActivity.this, "Search civic info is cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }

    @Override
    protected void onDestroy() {
        locator.shutdown();
        super.onDestroy();
    }
}
