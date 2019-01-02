package tech.rtsproduction.tuktuk.View;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import tech.rtsproduction.tuktuk.R;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button mNavigationMenu , mConfirmBtn ;
    private DrawerLayout mDrawerLayout;
    private GoogleMap mMap;
    private NavigationView mNavigationView;
    private PlaceAutocompleteFragment locationFromFragment, locationToFragment;
    private Place fromLoc, toLoc;
    private LinearLayout mFromPlaces, mToPlaces;

    private static final int DEFAULT_ZOOM = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationMenu = findViewById(R.id.btn_navigation_menu_main);
        mNavigationView = findViewById(R.id.nav_view_main);
        mDrawerLayout = findViewById(R.id.drawer_layout_main);
        //Changing Color Of Confirm Button
        mConfirmBtn = findViewById(R.id.btn_confirm_main);
        mConfirmBtn.getBackground().setColorFilter(getResources().getColor(android.R.color.holo_green_dark),PorterDuff.Mode.SRC_ATOP);
        mFromPlaces = findViewById(R.id.ll_places_from_main);
        mToPlaces = findViewById(R.id.ll_places_to_main);

        locationFromFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.frag_place_from_main);
        locationToFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.frag_place_to_main);
        setupPlacesFragment();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //MARK: ONCLICK LISTENER
        mNavigationMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_trips:{
                        startActivity(new Intent(MainActivity.this,HistoryActivity.class));
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }
                return false;
            }
        });


        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromLoc != null) {
                    mConfirmBtn.getBackground().setColorFilter(getResources().getColor(android.R.color.holo_orange_dark),PorterDuff.Mode.SRC_ATOP);
                    mConfirmBtn.setText("Looks Good!");
                    mConfirmBtn.setEnabled(false);
                    swapVisibility(mToPlaces, mFromPlaces);
                }
                if (toLoc != null) {
                    startActivity(new Intent(MainActivity.this, ConfirmationActivity.class).putExtra("points", new double[]{
                            fromLoc.getLatLng().latitude, fromLoc.getLatLng().longitude,
                            toLoc.getLatLng().latitude, toLoc.getLatLng().longitude
                    }));
                }
            }
        });
    }

    private void setupPlacesFragment() {
        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setCountry("IN")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .build();
        locationFromFragment.setFilter(filter);
        locationFromFragment.setHint("From");
        locationToFragment.setFilter(filter);
        locationToFragment.setHint("To");

        locationFromFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                fromLoc = place;
                updateMap(place);
                mConfirmBtn.setEnabled(true);
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        locationToFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                toLoc = place;
                updateMap(place);
                mConfirmBtn.setEnabled(true);
            }

            public void onError(Status status) {
            }
        });
    }

    private void swapVisibility(LinearLayout l1, LinearLayout l2) {
        l1.setVisibility(View.VISIBLE);
        l2.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void updateMap(Place p) {
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(p.getLatLng(), DEFAULT_ZOOM));
        mMap.addMarker(new MarkerOptions().position(p.getLatLng()).title(p.getName().toString()));
    }

}