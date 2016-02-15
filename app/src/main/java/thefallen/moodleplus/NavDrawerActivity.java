package thefallen.moodleplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import thefallen.moodleplus.ThreadHelper.updatedAtComp;
import thefallen.moodleplus.identicons.SymmetricIdenticon;

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RequestQueue queue;
    ArrayList<thefallen.moodleplus.ThreadHelper.thread> threads;
    Context mContext;
    RecyclerView rv;
    SharedPreferences sharedPreferences;
    NavigationView navigationView;
    ImageView logout;
    SubMenu topChannelMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        threads = new ArrayList<>();
        mContext = this;
        sharedPreferences = getSharedPreferences("userData",MODE_PRIVATE);
        queue = Volley.newRequestQueue(this);

        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initNavDrawerHeader();
        initNavDrawerMenu();
        setItemClickListeners();
    }
    // Set onClick listeners for Notifications, Grades, Logout menu items and cards in Recycler View
    public void setItemClickListeners()
    {
        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO onClick open thread
                    }
                }
                )
        );

        topChannelMenu.add("Notifications").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //TODO onClick listeners
                return false;
            }
        });
        topChannelMenu.add("Grades").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //TODO onClick listeners
                return false;
            }
        });
        topChannelMenu.add("");

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                    sharedPreferencesEditor.putString("userID", "");
                    sharedPreferencesEditor.putString("password", "");
                    sharedPreferencesEditor.apply();
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, APIdetails.logout(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.e("json", response);
                                    finish();
                                }

                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                                finish();
                        }
                    });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                }
            }
        });

    }


    // Extract user name, email and id from json in bundle and set the header accordingly
    public void initNavDrawerHeader()
    {
        View header = navigationView.inflateHeaderView(R.layout.nav_header_nav_drawer);
        TextView text1 = (TextView) header.findViewById(R.id.userName);
        TextView text2 = (TextView) header.findViewById(R.id.userEmail);
        SymmetricIdenticon symmetricIdenticon = (SymmetricIdenticon) header.findViewById(R.id.identicon);
        JSONObject user_details;
        try {
            user_details = new JSONObject(getIntent().getStringExtra("json_user"));
            user_details = user_details.getJSONObject("user");
            text1.setText(user_details.getString("first_name"));
            text2.setText(user_details.getString("email"));
            symmetricIdenticon.show(user_details.getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Extract courses list from the json in bundle and set their onClick listeners
    @SuppressWarnings("deprecation")
    public void initNavDrawerMenu()
    {
        String[] code=null;
        Menu m = navigationView.getMenu();
        topChannelMenu = m.addSubMenu("Courses");
        MenuItem mi = m.getItem(m.size()-1);
        mi.setTitle(mi.getTitle());
        logout = new ImageView(mContext);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayHelper.dpToPx(64,mContext));
        flp.gravity= Gravity.BOTTOM;
        logout.setBackgroundResource(R.drawable.rectangle);
        logout.setLayoutParams(flp);
        logout.setScaleType(ImageView.ScaleType.CENTER);
        logout.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_settings_new_red_900_48dp));
        navigationView.addView(logout);

        JSONObject courses;
        try {
            courses = new JSONObject(getIntent().getStringExtra("json_courses"));
            JSONArray coursesList = courses.getJSONArray("courses");
            code = new String[coursesList.length()];
            for(int i=0;i<coursesList.length();i++) {
                code[i] = coursesList.getJSONObject(i).getString("code");
                topChannelMenu.add(coursesList.getJSONObject(i).getString("code").toUpperCase() + ": " + coursesList.getJSONObject(i).getString("name")).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //TODO onClick listeners
                        return false;
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getThreads(code, 0);
    }

    // Call all APIs for all courses threads, then sort them and show in recyclerView
    public void getThreads(final String[] code,final int in)
    {
        String url = APIdetails.coursesThreads(code[in]);
        Log.e("json",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("json", response);
                        try {
                            JSONArray course_threads = (new JSONObject(response)).getJSONArray("course_threads");
                            for(int i=0;i<course_threads.length();i++) threads.add(new thefallen.moodleplus.ThreadHelper.thread(course_threads.getJSONObject(i),code[in]));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(in<code.length-1) getThreads(code,in+1);
                        else    {
                            initializeAdapter();
                        }

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

    // Initialize cards to display
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
            Collections.sort(threads,new updatedAtComp(-1));
            initializeAdapter();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
