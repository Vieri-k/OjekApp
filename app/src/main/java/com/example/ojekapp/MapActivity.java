package com.example.ojekapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

//    private static final LatLngBounds LOCKED_MAP_CAMERA_BOUNDS = new LatLngBounds.Builder()
//            .include(new LatLng(40.87096725853152, -74.08277394720501))
//            .include(new LatLng(40.67035340371385,
//                    -73.87063900287112)).build();
    
    private MapboxMap mapboxMap;
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private Point origin;
    private static final String DESTINATION_SYMBOL_LAYER_ID = "destination-symbol-layer-id";
    private static final String DESTINATION_ICON_ID = "destination-icon-id";
    private static final String DESTINATION_SOURCE_ID = "destination-source-id";

    private FloatingActionButton back;
    private Query referenceorder;

//    double LatD, LongD;
    String orderID;
    public TextView Ltn, tx, tx2;
    private LatLng currentPosition = new LatLng(64.900932, -18.167040);
    MyService servisku = null;
    MarkerOptions motorMarkerOptions = null;
    private ValueAnimator animator;
    private GeoJsonSource geoJsonSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        back = findViewById(R.id.fab_location_back);

        orderID = getIntent().getStringExtra("OrderID");

        Ltn = (TextView)findViewById(R.id.locatLat);
        Ltn.setText(orderID);

        servisku = new MyService();

        tx = (TextView)findViewById(R.id.locat);
        tx2 = (TextView)findViewById(R.id.locatlong);



        int count = 100; //Declare as inatance variable

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {

                        referenceorder = FirebaseDatabase.getInstance().getReference().child("order").orderByChild("OrderID").equalTo(orderID);
                        referenceorder.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                    CusOrder cs = snapshot.getValue(CusOrder.class);

                                    if (cs != null){
                                        String latit = cs.getLatitude();
                                        String longi = cs.getLongitude();

                                        tx.setText(latit);
                                        tx2.setText(longi);

                                        double latt = Double.parseDouble(latit);
                                        double lon = Double.parseDouble(longi);
                                        Log.d("LOG_POS",latit);
                                        updateMarkerPosition(new LatLng(latt, lon));
//                                        motorMarker.setPosition(new LatLng(latt, lon));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
//                                    tx.setText("Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude));
                            }
                        }, 1000);
                    }
                });
            }
        }, 0, 1000);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

    }
    private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LatLng animatedPosition = (LatLng) valueAnimator.getAnimatedValue();
                    geoJsonSource.setGeoJson(Point.fromLngLat(animatedPosition.getLongitude(), animatedPosition.getLatitude()));
                }
            };
    private void updateMarkerPosition(LatLng position) {
// This method is where we update the marker position once we have new coordinates. First we
// check if this is the first time we are executing this handler, the best way to do this is
// check if marker is null;
        if (mapboxMap.getStyle() != null) {
            GeoJsonSource spaceStationSource = mapboxMap.getStyle().getSourceAs("source-id");
            if (spaceStationSource != null) {
                spaceStationSource.setGeoJson(FeatureCollection.fromFeature(
                        Feature.fromGeometry(Point.fromLngLat(position.getLongitude(), position.getLatitude()))
                ));
            }
        }

        animator = ObjectAnimator
                .ofObject(latLngEvaluator, currentPosition, position)
                .setDuration(2000);
        animator.addUpdateListener(animatorUpdateListener);
        animator.start();

        currentPosition = position;
//
//        motorMarkerOptions.position(position);
//        Marker marker = motorMarkerOptions.getMarker();
//        this.mapboxMap.updateMarker(marker);

// Lastly, animate the camera to the new position so the user
// wont have to search for the marker and then return.
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLng(position));
    }

    private static final TypeEvaluator<LatLng> latLngEvaluator = new TypeEvaluator<LatLng>() {

        private final LatLng latLng = new LatLng();

        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude()
                    + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude()
                    + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    };

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        geoJsonSource = new GeoJsonSource("source-id",
                Feature.fromGeometry(Point.fromLngLat(currentPosition.getLongitude(),
                        currentPosition.getLatitude())));
        if (animator != null && animator.isStarted()) {
            currentPosition = (LatLng) animator.getAnimatedValue();
            animator.cancel();
        }


//        return true;
//        motorMarkerOptions = new MarkerOptions();
//        motorMarkerOptions.title("Motor "+orderID);
//        IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
//        Icon icon = iconFactory.fromResource(R.drawable.ic_motorcycle);
//        motorMarkerOptions.icon(icon);
//        motorMarkerOptions.position(new LatLng(-7, 109));
////        motorMarker = motorMarkerOptions.getMarker();
//        this.mapboxMap.addMarker(motorMarkerOptions);



//        String Lat = tx.getText().toString();
////        Lat = NumberFormat.getInstance().format(tx);
//        Double LatD = Double.parseDouble(Lat.trim());
//        String Long = tx2.getText().toString();
////        Long = NumberFormat.getInstance().format(tx2);
//        Double LongD = Double.parseDouble(Long.trim());




        mapboxMap.setStyle(new Style.Builder().fromUri(Style.OUTDOORS),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
                        enableLocationComponent(style);
                        initLayers(style);
                        style.addImage(("marker_icon"), BitmapFactory.decodeResource(
                                getResources(), R.drawable.ic_motorcycle));

                        style.addSource(geoJsonSource);

                        style.addLayer(new SymbolLayer("layer-id", "source-id")
                                .withProperties(
                                        PropertyFactory.iconImage("marker_icon"),
                                        PropertyFactory.iconIgnorePlacement(true),
                                        PropertyFactory.iconAllowOverlap(true)
                                ));
                        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                            @Override
                            public boolean onMapClick(@NonNull LatLng point) {
                                return true;
                            }
                        });
                    }
                });
    }

    private void initLayers(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage(DESTINATION_ICON_ID,
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource(DESTINATION_SOURCE_ID);
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer(DESTINATION_SYMBOL_LAYER_ID, DESTINATION_SOURCE_ID);
        destinationSymbolLayer.withProperties(
                iconImage(DESTINATION_ICON_ID),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }


    @SuppressLint("MissingPermission")
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build()
            );

            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);

            locationComponent.setRenderMode(RenderMode.COMPASS);
            this.origin = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                    locationComponent.getLastKnownLocation().getLatitude());
        }else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Grant Location Permission", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        }else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
//        adapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
//        adapter.stopListening();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}