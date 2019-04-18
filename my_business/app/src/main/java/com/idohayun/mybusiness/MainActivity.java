package com.idohayun.mybusiness;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static FragmentManager fragmentManager;
    private static TextView pageTitle;
    private ToolsView toolsView = new ToolsView();
    private FrameLayout frameLayout;
    private OrderView orderView;
    private ManagerPanel manageCalendar;
    private GalleryView galleryView = new GalleryView();
    private HomePage homePage;
    private NavigationView navigationView;
    private int counter = 0;
    private baseUSER user = new baseUSER();
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: called no " + counter++);
        setContentView(R.layout.activity_main);
        view = View.inflate(this, R.layout.activity_main, null);

        Log.d(TAG, "onCreate: " + user.getId());
        navigationView  = findViewById(R.id.nav_view);
        fragmentManager = getSupportFragmentManager();

        baseUSER.setNavigationView(navigationView);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        pageTitle = findViewById(R.id.main_page_title);
        pageTitle.setVisibility(View.INVISIBLE);
        ImageView menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(Gravity.START);
                }
            }
        });

        if(savedInstanceState == null) {
            HomePage homePage = new HomePage();
            pageTitle.setVisibility(View.INVISIBLE);
            fragmentManager.beginTransaction().add(R.id.fragment,homePage).commit();
        }

        frameLayout = findViewById(R.id.fragment);
        orderView = new OrderView();
        homePage = new HomePage();
        manageCalendar = new ManagerPanel();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragmentManager.getBackStackEntryCount() > 0) {
                int index = fragmentManager.getBackStackEntryCount() - 2;
                Log.d(TAG, "onBackPressed: " + index);
                if(index >= 0) {
                    FragmentManager.BackStackEntry backEntry = fragmentManager.getBackStackEntryAt(index);
                    String tag = backEntry.getName();
                    Log.d(TAG, "onBackPressed: " + tag);
                } else {
                    pageTitle.setVisibility(View.INVISIBLE);
                }
                fragmentManager.popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public static void changeTitlePage(String i_title)
    {
        pageTitle.setVisibility(View.VISIBLE);
        pageTitle.setText(i_title);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment foundFragment;

        switch (id) {
            case R.id.nav_homepage:
                fragmentManager.beginTransaction().replace(R.id.fragment, homePage, "HomePage").addToBackStack("HomePage").commit();
                frameLayout.removeAllViews();
                break;
            case R.id.nav_gallery:
                fragmentManager.beginTransaction().replace(R.id.fragment, galleryView, "GalleryView").addToBackStack("GalleryView").commit();
                frameLayout.removeAllViews();
                break;
            case R.id.nav_order:
                foundFragment = fragmentManager.findFragmentByTag("OrderView");
                if (foundFragment != null) {
                    fragmentManager.popBackStackImmediate("OrderView", 0);
                    Log.d(TAG, "onNavigationItemSelected: from stack");
                } else {
                    fragmentManager.beginTransaction().replace(R.id.fragment, orderView, "OrderView").addToBackStack("OrderView").commit();
                    frameLayout.removeAllViews();
                }
                break;
            case R.id.nav_manage:
                fragmentManager.beginTransaction().replace(R.id.fragment, toolsView, "ToolsView").addToBackStack("ToolsView").commit();
                frameLayout.removeAllViews();
                break;
            case R.id.nav_manage_calendar:
                fragmentManager.beginTransaction().replace(R.id.fragment, manageCalendar, "ManagerPanel").addToBackStack("ManagerPanel").commit();
                frameLayout.removeAllViews();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
