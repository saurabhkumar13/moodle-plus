package thefallen.moodleplus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import thefallen.moodleplus.ThreadHelper.courseComp;
import thefallen.moodleplus.ThreadHelper.createdAtComp;
import thefallen.moodleplus.ThreadHelper.thread;
import thefallen.moodleplus.ThreadHelper.updatedAtComp;
import thefallen.moodleplus.identicons.SymmetricIdenticon;

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RequestQueue queue;
    ArrayList<thefallen.moodleplus.ThreadHelper.thread> threads;
    ArrayList<thefallen.moodleplus.thread_view> thread_views;
    int currentThread;
    ArrayList<notification> notifications;
    ArrayList<assignmentListItem> assignments;
    ArrayList<grade> grades;
    Context mContext;
    RecyclerView rv;
    SharedPreferences sharedPreferences;
    NavigationView navigationView;
    ImageView logout;
    SubMenu topChannelMenu;
    state State=state.THREADS;
    FloatingActionButton fab;
    String course;
    String[] code;
    int[] order = new int[]{-1,-1,1};
    public enum state
    {
        THREADS,NOTIFICATIONS,COURSE,GRADES,COMMENTS;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;
        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        queue = Volley.newRequestQueue(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_create_white_48dp));
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
    public static final int PICKFILE_RESULT_CODE = 1;
    public void setItemClickListeners()
    {
        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(State==state.THREADS){
                            Log.e("thread","yup");
                            int thread_id = threads.get(position).getThread_id();
                            getThreadView(thread_id,position);
                        }
                        // TODO ADD TRANSITION CODES FOR THREADS AND ASSIGNMENTS HERE ~ SAURABH
                        // TODO USE THESE IN ASSIGNMENT ACTIVITY ~ WHENEVER YOU GUYS MAKE IT
                        // TODO CODE TO PICK AND UPLOAD A FILE
//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                        intent.setType("file/*");
//                        startActivityForResult(intent,PICKFILE_RESULT_CODE);
                       // TODO CODE TO DOWNLOAD JUST BELOW 3 LINES...
//                        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                        DownloadManager.Request request = new DownloadManager.Request(
//                                Uri.parse("http://192.168.43.254:8000/courses/download/submissions.file_.8fb9ad3499891dba.612e747874.txt"));
//                        dm.enqueue(request);

                    }
                }
                )
        );

        topChannelMenu.add("Notifications").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, APIdetails.notifications(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                notifications = new ArrayList<>();
                                try {
                                    JSONArray notifications_json_array = ((new JSONObject(response)).getJSONArray("notifications"));
                                    for (int i = 0; i < notifications_json_array.length(); i++) {
                                        notifications.add(new notification(notifications_json_array.getJSONObject(i)));
                                    }
                                    initializeAdapter(new RVAdapterNoti(notifications));
                                    State = state.NOTIFICATIONS;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        finish();
                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
                return false;
            }
        });
        topChannelMenu.add("Grades").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, APIdetails.grades(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                ArrayList gradesList = new ArrayList<>();
                                try {
                                    JSONArray grades_json_array = ((new JSONObject(response)).getJSONArray("grades"));
                                    JSONArray courses_json_array = ((new JSONObject(response)).getJSONArray("courses"));
                                    for (int i = 0; i < grades_json_array.length(); i++) {
                                        gradesList.add(new grade(grades_json_array.getJSONObject(i),courses_json_array.getJSONObject(i)));
                                    }
                                    initializeAdapter(new RVAdapterGradesAll(toArrayList(gradesList)));
                                    State = state.NOTIFICATIONS;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        finish();
                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (State == state.COMMENTS) {
                    EditText edit = (EditText) findViewById(R.id.comment);
                    Log.e("nyest", edit.getText().toString() + currentThread + "");
                    PostComment(currentThread + "", edit.getText().toString());
                }
                if (State == state.COURSE) {
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, APIdetails.courseGrade(course),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    grades = new ArrayList<>();
                                    try {
                                        JSONArray grades_json_array = ((new JSONObject(response)).getJSONArray("grades"));
                                        for (int i = 0; i < grades_json_array.length(); i++) {
                                            grades.add(new grade(grades_json_array.getJSONObject(i)));
                                        }
                                        initializeAdapter(new RVAdapterGrades(grades));
                                        State = state.GRADES;

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            finish();
                        }
                    });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                } else if ((State == state.THREADS) && code != null) {
                    Intent intent = new Intent(mContext, postThread.class);
                    intent.putExtra("courses", code);
                    startActivity(intent);
                }
            }
        });

    }

    void errorSnack(int strID,boolean retry, final String thread_id, final String description)
    {
        Snackbar snackbar = Snackbar.make(fab, strID, Snackbar.LENGTH_SHORT);
        if(retry) snackbar.setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostComment(thread_id,description);
            }
        });
        snackbar.show();
    }
    public void PostComment(final String thread_id,final String description)
    {
        String url = APIdetails.comment();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("commentresponse", response);
                        try {
                            if((new JSONObject(response)).getBoolean("success"))
                            {
                                // to add
                            }
                            else errorSnack(R.string.error,false,thread_id,description);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // Error handling
                volleyError.printStackTrace();

                if(volleyError instanceof NoConnectionError) {
                    errorSnack(R.string.no_internet,true,thread_id,description);
                } else if (volleyError instanceof TimeoutError) {
                    errorSnack(R.string.timeout,true,thread_id,description);
                } else if (volleyError instanceof ServerError) {
                    errorSnack(R.string.internal_server,true,thread_id,description);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("thread_id",thread_id);
                params.put("description", description);
                return params;

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        // Add the request to the queue
        queue.add(stringRequest);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode==RESULT_OK){
                    String file = data.getData().getPath(),file2 = getRealPathFromUri(mContext,data.getData());
                    if(!file2.equals("")) file = file2;
                    MultipartRequest multipartRequest = new MultipartRequest(APIdetails.assignmentSubmission(1), file, "azazel",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    Log.e("file","success");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    Log.e("file",volleyError.getLocalizedMessage());
                                }
                            }
                    );
                    queue.add(multipartRequest);
                }
                break;

        }
    }
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (NullPointerException e)
        {
            return "";
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public ArrayList<ArrayList<grade>> toArrayList(ArrayList<grade> grades)
    {
        ArrayList<Integer> done_courses = new ArrayList<>();
        ArrayList<ArrayList<grade>> res = new ArrayList<>();
        for(int i=0;i<grades.size();i++)
        {
            if(!done_courses.contains(grades.get(i).course_id)) {
                res.add(new ArrayList<grade>());
                done_courses.add(grades.get(i).course_id);
            }
            res.get(done_courses.indexOf(grades.get(i).course_id)).add(grades.get(i));
        }
        return res;
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
        Menu m = navigationView.getMenu();
        topChannelMenu = m.addSubMenu("Courses");
        MenuItem mi = m.getItem(m.size()-1);
        mi.setTitle(mi.getTitle());
        logout = new ImageView(mContext);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayHelper.dpToPx(54,mContext));
        flp.gravity= Gravity.BOTTOM;
        logout.setBackgroundResource(R.drawable.rectangle);
        logout.setLayoutParams(flp);
        logout.setScaleType(ImageView.ScaleType.CENTER);
        logout.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_settings_new_white_18dp));
        navigationView.addView(logout);

        final JSONObject courses;
        try {
            courses = new JSONObject(getIntent().getStringExtra("json_courses"));
            JSONArray coursesList = courses.getJSONArray("courses");
            code = new String[coursesList.length()];
            for(int i=0;i<coursesList.length();i++) {
                code[i] = coursesList.getJSONObject(i).getString("code");
                topChannelMenu.add(0,i,0,coursesList.getJSONObject(i).getString("code").toUpperCase() + ": " + coursesList.getJSONObject(i).getString("name")).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        course=code[item.getItemId()];
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, APIdetails.assignments(code[item.getItemId()]),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        assignments = new ArrayList<>();
                                        try {
                                            JSONArray assignments_json_array = ((new JSONObject(response)).getJSONArray("assignments"));
                                            for (int i = 0; i < assignments_json_array.length(); i++) {
                                                assignments.add(new assignmentListItem(assignments_json_array.getJSONObject(i)));
                                            }
                                            Collections.sort(assignments);
                                            initializeAdapter(new RVAdapterAssignments(assignments));
                                            State = state.COURSE;
                                            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_school_white_48dp));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                finish();
                            }
                        });
                        // Add the request to the RequestQueue.
                        queue.add(stringRequest);
                        return false;
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Call all APIs for all courses threads, then sort them and show in recyclerView
    public void getThreads(final String[] code,final int in)
    {
        String url = APIdetails.coursesThreads(code[in]);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray course_threads = (new JSONObject(response)).getJSONArray("course_threads");
                            for (int i = 0; i < course_threads.length(); i++)
                                threads.add(new thefallen.moodleplus.ThreadHelper.thread(course_threads.getJSONObject(i), code[in]));
                            } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(in<code.length-1) getThreads(code, in + 1);
                        else    {
                            Collections.sort(threads, new updatedAtComp(-1));
                            initializeAdapter(new RVAdapterThreads(threads));
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

    public void getThreadView(final int id, final int position)
    {
        String url = APIdetails.thread(id);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        currentThread =id;
                        try {
                            JSONArray comments = (new JSONObject(response)).getJSONArray("comments");
                            JSONArray comment_users = (new JSONObject(response)).getJSONArray("comment_users");
                            thread_views = new ArrayList<>();
                            thread_views.add(new thread_view(threads.get(position).getUser_id(),threads.get(position).getDescription(),threads.get(position).getCreatedAt(),threads.get(position).getTitle()));
                            for (int i = 0; i < comment_users.length(); i++)
                                thread_views.add(new thread_view(comment_users.getJSONObject(i).getInt("id"),comments.getJSONObject(i).getString("description"),Timestamp.valueOf(comments.getJSONObject(i).getString("created_at")),comment_users.getJSONObject(i).getString("first_name")+" " +comment_users.getJSONObject(i).getString("last_name")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(thread_views!=null){
                            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_white_36dp));
                            initializeAdapter(new RVAdapterThreadShow(thread_views));
                            State=state.COMMENTS;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext,"thread load error",Toast.LENGTH_LONG).show();
                Log.e("threadloaderror","threadloaderror");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    // Initialize cards to display
    private void initializeAdapter(RecyclerView.Adapter adapter){
        invalidateOptionsMenu();
        rv.setAdapter(adapter);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        threads = new ArrayList<>();
        thread_views = new ArrayList<>();
        getThreads(code, 0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(State!=state.THREADS)
        {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_create_white_48dp));
            State=state.THREADS;
            initializeAdapter(new RVAdapterThreads(threads));
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(State == state.THREADS) getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return State == state.THREADS &&  super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.created) {
            Collections.sort(threads, new createdAtComp(order[0]));
            initializeAdapter(new RVAdapterThreads(threads));
            order[0]*=-1;
            return true;
        }
        else if (id == R.id.updated) {
            Collections.sort(threads, new updatedAtComp(order[1]));
            initializeAdapter(new RVAdapterThreads(threads));
            order[1]*=-1;
            return true;
        }
        else if (id == R.id.course) {
            Collections.sort(threads, new courseComp(order[2]));
            initializeAdapter(new RVAdapterThreads(threads));
            order[2]*=-1;
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
