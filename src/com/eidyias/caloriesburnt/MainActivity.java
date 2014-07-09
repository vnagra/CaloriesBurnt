package com.eidyias.caloriesburnt;

import java.text.DecimalFormat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity implements OnClickListener {
	
	private GoogleMap googleMap;
	private LatLng myPosition;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private double latitude;
	private double longitude;
	private double			weight;
	private double			distance;
	private double			calorieBurnt;
	private Location lastLocation = null;
	private Location currentLocation=null;
	private Typeface font;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//always call the super method first
		super.onCreate(savedInstanceState);
		
		//removing the title
		//Needs to be done before setting the content
		//Take away the title bar
		//Flag for no title is FEATURE_NO_TITLE which turns of the title bar on the top of the screen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       
        //set the content of the view
		setContentView(R.layout.activity_main);
		
        //set the font for the application (thank you KGill7 :) for teaching me this)
        setFont(font);
        //Grab the start button
        Button startButton = (Button) findViewById(R.id.startButton);
        //make a listener for this button
        startButton.setOnClickListener(this);
        //set the font of the satrt button
        startButton.setTypeface(font);
        //Grab the textviews for caloriesburnt and distancetravelled
        TextView caloriesBurnt = (TextView) findViewById(R.id.caloriesBurnt);
        TextView distanceTravelled = (TextView) findViewById(R.id.distanceTravelled);
        //set the font of the Text views
        caloriesBurnt.setTypeface(font);
        
        distanceTravelled.setTypeface(font);
        
        //set Calories Burnt to 0.
        setCalorieBurnt(0.0);
        //set Distance to 0.
        setDistance(0.0);
        
        
        //Initialize the location change listener
       //##################
        setLocationListener(new MyLocationListener ());
        // Getting LocationManager object from System Service LOCATION_SERVICE
        // LOCATION_SERVICE is a service that is  used to retrive location updates
        // getSystemService returns a handle to the system-level services by name. 
        setLocationManager((LocationManager) getSystemService(LOCATION_SERVICE));

        /*Find and initialize map view*/
        initMapview();
        /*Find current Position on the map*/
        initMyLocation();

        
        
	}
	/*Find and initialize map view*/
	private void initMapview() {
		// Getting reference to the SupportMapFragment of activity_main.xml
	    SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

	    // Getting GoogleMap object from the fragment
	    googleMap = fm.getMap();
	    setGoogleMap(googleMap);
	    
	    // Changing the map type to hybrid view
	    //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	    // Changing the map type to normal view
	    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		
	}
	/*Find current Position on the map*/
	private void initMyLocation() {
		// Enabling MyLocation Layer of Google Map
	    googleMap.setMyLocationEnabled(true);
	    // Creating a criteria object to retrieve provider
	    Criteria criteria = new Criteria();

	    // Getting the name of the best provider
	    String provider = locationManager.getBestProvider(criteria, true);

	    
	    // Getting Current Location
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0.5F, locationListener);
	    setCurrentLocation(locationManager.getLastKnownLocation(provider));
	    //Check to see if it has accepted the location properly
	    if(currentLocation!=null){
		    // Getting latitude of the current location
		    setLatitude(currentLocation.getLatitude());
		
		    // Getting longitude of the current location
		    setLongitude(currentLocation.getLongitude());
		
		    // Creating a LatLng object for the current location
		     setMyPosition(new LatLng(latitude, longitude));
		    
		     //Moving the camera to my current location
		     // Construct a CameraPosition focusing on your location and animate the camera to that position.
		     CameraPosition cameraPosition = new CameraPosition.Builder()
		         .target(myPosition)      // Sets the center of the map to current location 
		         .zoom(18)                   // Sets the zoom
		         .bearing(90)                // Sets the orientation of the camera to east
		         .tilt(30)                   // Sets the tilt of the camera to 30 degrees
		         .build();                   // Creates a CameraPosition from the builder
		     googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));   
		    //adds a custom marker to the current location
		    //.addMarker(new MarkerOptions()
		    //.position(myPosition)			//Sets the marker at current location 
		    //.title("You are here")				//Sets the title
		    //.snippet("Calories Burnt: 00") //Sets the snippet
		    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.launch_icon)));
		    								// Sets the launcher_icon as the custom marker 
	    }
	    else
	     {
	    	//Toast a message if cuurrentLocation is null
	    	 Toast.makeText(getApplicationContext(), "Current GPS locatin cannot be calculated. Please Check your location settings.", Toast.LENGTH_SHORT).show(); 
	     }
	}
	@Override
	protected void onResume() {
		//call the super
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public double getCalorieBurnt() {
		return calorieBurnt;
	}

	public void setCalorieBurnt(double calorieBurnt) {
		this.calorieBurnt = calorieBurnt;
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getWeight() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Enter Weight");
		alert.setMessage("Weight in lbs.");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		   
		   try{
			        float value = Float.valueOf(input.getText().toString().trim()).floatValue();
			        setWeight(value);
			  } catch(NumberFormatException e){
					Toast.makeText(getApplicationContext(), "Entry in invalid format. Try again", Toast.LENGTH_SHORT).show();
					return;
			  }
		 
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    Toast.makeText(getApplicationContext(), "Please, eneter weight to measure your calories", Toast.LENGTH_LONG).show();
		  }
		});

		alert.show();
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public LocationManager getLocationManager() {
		return locationManager;
	}

	public void setLocationManager(LocationManager locationManager) {
		this.locationManager = locationManager;
	}

	public LocationListener getLocationListener() {
		return locationListener;
	}

	public void setLocationListener(LocationListener locationListener) {
		this.locationListener = locationListener;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public LatLng getMyPosition() {
		return myPosition;
	}

	public void setMyPosition(LatLng myPosition) {
		this.myPosition = myPosition;
	}

	public GoogleMap getGoogleMap() {
		return googleMap;
	}

	public void setGoogleMap(GoogleMap googleMap) {
		this.googleMap = googleMap;
	}

	public Typeface getFont() {
		return font;
	}

	public void setFont(Typeface font) {
		font = Typeface.createFromAsset(getAssets(), "themefont.ttf");
		this.font = font;
	}

	@Override
	public void onClick(View v) {
		// response to any view that is clickable 
		// find the view that is clicked from its id and match it with id in the layout
		if(v.getId() == R.id.startButton)
		{
			// add a pop up to get the user weight to calculate the calories
			getWeight();
		
		}
		
	}
	public  Location getLastLocation() {
		return lastLocation;
	}
	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	/*Round to two decimal places*/
	private double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.#");
		return Double.valueOf(twoDForm.format(d));
	
	}
	
	private class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			setLastLocation(getCurrentLocation());
		    //Gps has an ambiguity of (am-bi-gyoo-i-tee) of 30 feet. Hence hold the location change for  about 10 meters for now :)
	    	double difference= 0;
	    	
			while(difference < 15){
				//Toast.makeText(getApplicationContext(), "Move around and press start to enter weight to calculate the calories burnt", Toast.LENGTH_SHORT).show();
				difference = difference + getLastLocation().distanceTo(location);
	    	}
			setCurrentLocation(location);
			googleMap.addPolyline(new PolylineOptions()
		     .add(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
		     .width(5)
		     .color(Color.RED));
			 distance = distance + lastLocation.distanceTo(currentLocation);
			 calorieBurnt = getCalorieBurnt() + (distance * weight * 0.4535 * 1.036 )/1000;
			 setDistance(roundTwoDecimals(distance));
			 setCalorieBurnt(roundTwoDecimals(calorieBurnt));
			 TextView distanceDisplay = (TextView) findViewById(R.id.distanceTravelled);
			 TextView calorieBurntDisplay = (TextView) findViewById(R.id.caloriesBurnt);
			 distanceDisplay.setText(String.valueOf(getDistance()) + " m" );
			 calorieBurntDisplay.setText(String.valueOf(getCalorieBurnt()) + " cal");
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
