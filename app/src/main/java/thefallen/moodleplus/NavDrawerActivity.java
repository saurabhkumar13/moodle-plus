package thefallen.moodleplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import thefallen.moodleplus.identicons.AsymmetricIdenticon;
import thefallen.moodleplus.identicons.SymmetricIdenticon;

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    RequestQueue queue;
    ArrayList<thread> threads;
    Context mContext;
    private RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        threads = new ArrayList<>();
        mContext = this;
        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO onClick open thread
                        }
                    }
                )
        );

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_nav_drawer);
        Menu m = navigationView.getMenu();
        SubMenu topChannelMenu = m.addSubMenu("Courses");
        MenuItem mi = m.getItem(m.size()-1);
        mi.setTitle(mi.getTitle());
        TextView text1 = (TextView) header.findViewById(R.id.userName);
        TextView text2 = (TextView) header.findViewById(R.id.userEmail);
        SymmetricIdenticon symmetricIdenticon = (SymmetricIdenticon) header.findViewById(R.id.identicon);
        JSONObject user_details,courses;
        String[]  code=null;
        try {
            user_details = new JSONObject(getIntent().getStringExtra("json_user"));
            courses = new JSONObject(getIntent().getStringExtra("json_courses"));
            JSONArray coursesList = courses.getJSONArray("courses");
            code = new String[coursesList.length()];
            for(int i=0;i<coursesList.length();i++) {
                code[i] = coursesList.getJSONObject(i).getString("code");
                topChannelMenu.add(coursesList.getJSONObject(i).getString("code").toUpperCase() + ": " + coursesList.getJSONObject(i).getString("name"));
                // TODO add menu actions
            }
            user_details = user_details.getJSONObject("user");
            text1.setText(user_details.getString("first_name"));
            text2.setText(user_details.getString("email"));
            symmetricIdenticon.show(user_details.getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        queue = Volley.newRequestQueue(this);
        getThreads(code, 0);
    }
    public void getThreads(final String[] code,final int in)
    {
        String url = APIdetails.coursesThreads()+"/"+code[in]+"/threads";
        Log.e("json",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("json", response);
                        try {
                            JSONArray course_threads = (new JSONObject(response)).getJSONArray("course_threads");
                            for(int i=0;i<course_threads.length();i++) threads.add(new thread(course_threads.getJSONObject(i)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(in<code.length-1) getThreads(code,in+1);
                        else    {
                            Collections.sort(threads);
                            initializeAdapter();
                            //TODO display threads with card adapter or whateva
                            Log.e("LIST", threads.toString());}

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(in<code.length-1) getThreads(code,in+1);
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(threads);
        rv.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
