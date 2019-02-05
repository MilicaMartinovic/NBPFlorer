package com.example.milica.nbp_florer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.milica.nbp_florer.Tabs.FragmentExplore;
import com.example.milica.nbp_florer.Tabs.FragmentFeed;
import com.example.milica.nbp_florer.Tabs.SectionPageAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Session session;
    private MenuItem nav_login;
    private MenuItem nav_logout;
    private SectionPageAdapter mSectionPageAdapter;
    private ViewPager mViewPager;
    private MovableFloatingActionButton fabCam;
    private FileResolver fileResolver;
    private FragmentFeed fragmentFeed;
    private FragmentExplore fragmentExplore;
   // private FragmentRequest fragmentRequest;
    private CheckInternet checkInternet;
    private AppBarLayout appBarLayout;
    private Button login;
    private Button myAccount;
    private Button logout;
    private View prvi;
    private View drugi;
    private View treci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        session = new Session(this);
        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        fabCam = findViewById(R.id.fab_kamerica);
        fabCam.setAlpha(0.85f);
        checkInternet = new CheckInternet();
        fileResolver = new FileResolver();
        appBarLayout = findViewById(R.id.appbar);
        appBarLayout.bringToFront();

        // NAVIGATION MENU BUTTONS


        login = findViewById(R.id.btnMenuLogin);
        prvi = findViewById(R.id.prviseparator);
        if(session.loggedin()) {

            login.setVisibility(View.GONE);
            prvi.setVisibility(View.GONE);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!session.loggedin()) {
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginIntent);
                }
            }
        });

        myAccount = findViewById(R.id.btnMenuMyAccount);
        myAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(session.loggedin()) {

                    Intent myAccountIntent = new Intent(getApplicationContext(), MyAccountActivity.class);
                    startActivity(myAccountIntent);
                }
                else {

                    Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(loginActivity);
                }
            }
        });

        logout = findViewById(R.id.btnMenuLogout);
        treci = findViewById(R.id.treciseparator);
        if(!session.loggedin()) {

            logout.setVisibility(View.GONE);
            treci.setVisibility(View.GONE);
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                session.setLoggedin(false);
                session.setUser("");
                session.setID("");
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

        // ------------------------------------------------------------------------------------------------------

        mViewPager.setOffscreenPageLimit(2);

        fragmentFeed = new FragmentFeed();
        fragmentExplore = new FragmentExplore();

        setupViewPager(mViewPager);

        final TabLayout mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        getSupportActionBar().setElevation(0);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        //AUTO COMPLETE APP BAR

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.custom_app_bar, null);

        actionBar.setCustomView(v);
        //-------------------------------------

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*NavigationView nv = findViewById(R.id.nav_view);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case(R.id.nav_login):
                        if(!session.loggedin()) {
                            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(loginIntent);
                        }
                        break;
                    case(R.id.nav_logout):
                        session.setLoggedin(false);
                        session.setUser("");
                        session.setID("");
                        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainIntent);
                        break;
                    case(R.id.nav_myAccount):
                        Intent myAccountIntent = new Intent(getApplicationContext(), MyAccountActivity.class);
                        startActivity(myAccountIntent);
                }
                return true;
            }
        });*/

        fabCam.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},1);
            }
        });

        //nav_login = mDrawerLayout.findViewById(R.id.nav_view).findViewById(R.id.nav_login);
        //nav_logout = mDrawerLayout.findViewById(R.id.nav_view).findViewById(R.id.nav_logout);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 15);
            }
            return;
        }

        if(getIntent().hasExtra("fragment_position")) {

            mViewPager.setCurrentItem(getIntent().getIntExtra("fragment_position", 0));
        }

        if(!checkInternet.isInternetAvailable(this)) {

            Toast.makeText(this, "Not connected to network", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = fileResolver.createImageFile(this);
                    } catch (IOException ex) {
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "com.example.milica.nbp_florer",
                                photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, 0);
                    }

                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }

            }
        }
        else if(requestCode == 15)
        {
            MapResolver mapResolver = new MapResolver(this);
            mapResolver.initListener();
            mapResolver.sendLocationRequest();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0 && resultCode==RESULT_OK) {
            Bitmap bitmap;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();

            File f = new File(fileResolver.getmCurrentPhotoPath());
            bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, bmOptions);
                fileResolver.setBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            };
            new Encode_image().execute();
        }
    }

    public class Encode_image extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            Intent intent = new Intent(getApplicationContext(), PlantUploadActivity.class);
            intent.putExtra("path", fileResolver.getmCurrentPhotoPath());
            startActivity(intent);
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupViewPager(ViewPager viewPager) {

        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());

        mSectionPageAdapter.addFragment(fragmentFeed, "FEED");
        mSectionPageAdapter.addFragment(fragmentExplore, "EXPLORE");

        viewPager.setAdapter(mSectionPageAdapter);
    }

    @Override
    public void onBackPressed() {

        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {

            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else {

            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        /*else if(session.loggedin()) {

            session.setLoggedin(true);
            this.recreate();
        }
        else if(!session.loggedin()){

            session.setLoggedin(false);
            this.recreate();
        }*/
    }
}
