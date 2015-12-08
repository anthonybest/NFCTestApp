package com.micronet_inc.abest.nfctestapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        LocationManager locationManager;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationManager.addNmeaListener(new GpsStatus.NmeaListener() {
            @Override
            public void onNmeaReceived(long timestamp, String nmea) {
                TextView textView;
                if(nmea.startsWith("$GPGGA,,"))
                    textView = (TextView) findViewById(R.id.textViewGps1);
                else if(nmea.startsWith("$GPGGA"))
                    textView = (TextView) findViewById(R.id.textViewGps2);
                else if(nmea.startsWith("$GPRMC"))
                    textView = (TextView) findViewById(R.id.textViewGps3);
                else if(nmea.startsWith("$GPVTG"))
                    textView = (TextView) findViewById(R.id.textViewGps4);
                else if(nmea.startsWith("$GPGSA"))
                    textView = (TextView) findViewById(R.id.textViewGps5);
                else if(nmea.startsWith("$GPGSV"))
                    textView = (TextView) findViewById(R.id.textViewGps6);
                else if(nmea.startsWith("$GLGSV"))
                    textView = (TextView) findViewById(R.id.textViewGps7);
                else
                    textView = (TextView) findViewById(R.id.textViewGps1);

                textView.setText(nmea);
            }
        });
        //GpsStatus gpsStatus = locationManager.getGpsStatus();
        //Iterable<GpsSatellite> sats = gpsStatus.getSatellites();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, (float) 0.001, this);



        nfcAdapter = NfcAdapter.getDefaultAdapter(this);


        nfcPendingIntent = PendingIntent.getActivity(this, 0, new
                Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ntech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

        intentFiltersArray = new IntentFilter[]{ntech};
        techListArray = new String[][] {
                        new String[]{
                                NfcV.class.getName()
                        }
        };

        // forgrround dispatch can only be enabled on resume
        //nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists);

        handleIntent(getIntent());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

    public void onPause()
    {
        super.onPause();

        nfcAdapter.disableForegroundDispatch(this);
    }

    public void onResume()
    {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(
                this,
                nfcPendingIntent,
                intentFiltersArray,
                techListArray);

        String action = getIntent().getAction();
        if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
        {
            handleIntent(getIntent());
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private void handleIntent(Intent intent)
    {
        String action = intent.getAction();
        if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
        {
            String type = intent.getType();
           // if(MIME_TEXT_PLAIN.equals(type))
            {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String s =  bytesToHex(tag.getId());
                TextView textView = (TextView)findViewById(R.id.textView1);
                textView.setText(s);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
