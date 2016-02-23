package thefallen.moodleplus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.TextView;

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

    //Defines all variables required for view creation and switching

    RequestQueue queue;
    ArrayList<thefallen.moodleplus.ThreadHelper.thread> threads;
    ArrayList<thefallen.moodleplus.thread_view> thread_views;
    ArrayList<thefallen.moodleplus.assignment_view> assignment_views;
    int currentThreadid;
    int currentThreadpos;
    int currentAssgnId;
    int currentAssgnpos;
    course currCourse;
    assignment_header ah;
    ArrayList<notification> notifications;
    ArrayList<assignmentListItem> assignments;
    ArrayList<grade> grades;
    Context mContext;
    RecyclerView rv;
    SharedPreferences sharedPreferences;
    NavigationView navigationView;
    SubMenu topChannelMenu;
    state State=state.THREADS;
    FloatingActionButton fab;
    String course;
    String[] code;
    EditText comment;
    DrawerLayout drawer;
    DrawerArrowDrawable dArrow;
    Toolbar toolbar;
    public static boolean adapterInit = false;
    int[] order = new int[]{-1,-1,1};
    AlertDialog.Builder alertDialogBuilder;
    AppBarLayout mToolbarContainer;

    //Datatype to let you know the current state of the application

    public enum state
    {
        THREADS,NOTIFICATIONS,COURSE,GRADES,COMMENTS,GRADESALL,SUBMISSIONS
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the display
        setContentView(R.layout.activity_nav_drawer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mToolbarContainer = ((AppBarLayout) findViewById(R.id.toolbarContainer));

        mContext = this;

        // Check for saved Login Credentials and apply if available

        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        queue = Volley.newRequestQueue(this);

        comment = (EditText)findViewById(R.id.comment);
        alertDialogBuilder = new AlertDialog.Builder(mContext,R.style.AppthemeDialog);

        //Setup the FAB to be +

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_create_white_48dp));

        //Setup the Recycler View

        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        dArrow = new DrawerArrowDrawable(mContext);
        toolbar.setNavigationIcon(dArrow);
        dArrow.setColor(Color.parseColor("#ffffff"));
        getSupportActionBar().setTitle("Moodle Plus");
        toolbar.setSubtitle("Threads");
        dArrow.setSpinEnabled(true);

        //Initiate the Navigation View

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initNavDrawerHeader();
        initNavDrawerMenu();
        setItemClickListeners();
    }

    // Set onClick listeners for all the menu options

    public static final int PICKFILE_RESULT_CODE = 1;

    public void setItemClickListeners()
    {
        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, final int position, boolean left) {
                        Log.e("state",getState()+"");
                        if (getState() == state.THREADS) {
                            int thread_id = threads.get(position).getThread_id();
                            getThreadView(thread_id, position);
                        }

                        if (getState() == state.COURSE) {
                            if (position == 0) return;
                            int assgn_id = assignments.get(position - 1).getassgnId();
                            getAssgnView(assgn_id, position);
                        }
                        if (getState() == state.NOTIFICATIONS) {
                            getThreadView(notifications.get(position).thread_id, getPosition(threads, notifications.get(position).thread_id));
                        }
                        if (getState() == state.SUBMISSIONS) {
                            if (position > assignment_views.size() || position == 0) return;
                            RVAdapterAssgnShow.ElementHolder eh = (RVAdapterAssgnShow.ElementHolder) rv.findViewHolderForAdapterPosition(position);
                            final String submission_link = assignment_views.get(position - 1).getLink();
                            if (left) {
                                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                DownloadManager.Request request = new DownloadManager.Request(
                                        Uri.parse(APIdetails.subDLlink(submission_link)));
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setTitle("Moodle Plus");
                                request.setDescription(eh.name.getText());
                                dm.enqueue(request);
                                eh.download.animate()
                                        .scaleX(1.1f)
                                        .scaleY(1.1f)
                                        .setInterpolator(new CycleInterpolator(1))
                                        .setDuration(200)
                                        .start();
                            } else {
                                alertDialogBuilder.setTitle("Delete Submission?")
                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                StringRequest stringRequest = new StringRequest(Request.Method.GET, APIdetails.subDelete(submission_link),
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {
                                                                try {
                                                                    assignment_views.remove(position - 1);
                                                                } catch (IndexOutOfBoundsException e) {
                                                                    Log.e("index", e.getLocalizedMessage());
                                                                }
                                                                initializeAdapter(new RVAdapterAssgnShow(ah, assignment_views));
                                                            }
                                                        }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError volleyError) {
                                                    }
                                                });
                                                // Add the request to the RequestQueue.
                                                queue.add(stringRequest);
                                            }
                                        })
                                        .setCancelable(true)
                                        .setNegativeButton("NO", null)
                                        .show();


                            }
                        }
                    }

                }
                )
        );

        //Adds the see notifications button in the Navdrawer menu

        navigationView.getMenu().add(1, 1, 1, "Notifications").setIcon(R.drawable.ic_notifications_active_blue_grey_500_24dp).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
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
                                    changeState(state.NOTIFICATIONS);
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

        //Adds the view all grades button in the NavDrawer menu

        navigationView.getMenu().add(1, 1, 1, "Grades").setIcon(R.drawable.ic_school_blue_grey_500_24dp).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
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
                                        gradesList.add(new grade(grades_json_array.getJSONObject(i), courses_json_array.getJSONObject(i)));
                                    }
                                    initializeAdapter(new RVAdapterGradesAll(toArrayList(gradesList)));
                                    changeState(state.GRADESALL);
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

        // Adds the Logout button in the NavDrawer menu

        navigationView.getMenu().add(1, 1, 1, "Logout").setIcon(R.drawable.ic_exit_to_app_blue_grey_500_24dp).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putString("userID", "");
                sharedPreferencesEditor.putString("password", "");
                sharedPreferencesEditor.apply();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, APIdetails.logout(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Intent intent = new Intent(mContext, LoginActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
                return false;
            }
        });

        // Performs actions on clicking the FAB depending on the State
        /*
            COMMENTS : Post a new comment
            COURSE : See grades for the course
            THREAD : Post new thread
            SUBMISSIONS : Add new Submission
         */

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getState() == state.COMMENTS) {
                    PostComment(currentThreadid + "", comment.getText().toString());
                }
                if (getState() == state.COURSE) {
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
                                        initializeAdapter(new RVAdapterGrades(grades,currCourse));
                                        changeState(state.GRADES);

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
                } else if ((getState() == state.THREADS) && code != null) {
                    Intent intent = new Intent(mContext, postThread.class);
                    intent.putExtra("courses", code);
                    startActivity(intent);
                } else if (getState() == state.SUBMISSIONS) {
                    if (TimeHelper.timeFromNow(assignments.get(currentAssgnpos-1).deadline, -1," left").equals("")) {
                        Snackbar.make(fab, "DEADLINE PASSED", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("file/*");
                    startActivityForResult(intent, PICKFILE_RESULT_CODE);
                }
            }
        });

        //Hide the title bar on scrolling down

        rv.addOnScrollListener(new HidingScrollListener(getStatusBarHeight() + DisplayHelper.getToolbarHeight(mContext)) {
            @Override
            public void onMoved(int distance) {

                if (!adapterInit) mToolbarContainer.setTranslationY(-distance);
                else mToolbarContainer.setTranslationY(0);
            }
        });

    }

    /*
        function : Return position of an element in an array
        input : ArrayList : of the objects obj : the object to be located
        output : int position of obj
     */

    public int getPosition(ArrayList<thread> list,int obj){
        for(int i=0;i<list.size();i++) if(list.get(i).getThread_id()==obj) return i;
        return -1;
    }

    /*
        function : Get height of the status bar
        input : void
        output : returns the height of status bar in the current view
    */

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    state getState()
    {
        return State;
    }

    /*
        function : transitions from the current state to the next state
        input : STATE : the state to go to
        output : void
     */

    void changeState(state STATE) {
        if(STATE == state.COMMENTS && State !=state.COMMENTS) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_white_36dp));
            comment.setVisibility(View.VISIBLE);
            comment.setX(DisplayHelper.dpToPx(20,mContext)+DisplayHelper.getWidth(mContext));
            comment.animate()
                    .translationXBy(-DisplayHelper.getWidth(mContext))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                        }
                    })
                    .setInterpolator(new AccelerateDecelerateInterpolator());
        }
        else if(STATE != state.COMMENTS ) {
            comment.animate()
                    .translationXBy(DisplayHelper.getWidth(mContext))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            comment.setVisibility(View.GONE);
                        }
                    })
                    .setInterpolator(new AccelerateDecelerateInterpolator());
        }
        if(STATE == state.COURSE) {
            toolbar.setSubtitle("Course");
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_school_white_48dp));
        } else if(STATE == state.THREADS) {
            toolbar.setSubtitle("Threads");
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_create_white_48dp));
        } else if(STATE == state.SUBMISSIONS) {
            toolbar.setSubtitle("Assignment");
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_48dp));
        }
        else if(STATE == state.NOTIFICATIONS) {
            toolbar.setSubtitle("Notifications");
        }
        else if(STATE == state.GRADES||STATE == state.GRADESALL) {
            toolbar.setSubtitle("Grades");
        }
        else if(STATE == state.COMMENTS) {
            toolbar.setSubtitle("Chat");
        }

        if(noFabStates(State)&&!noFabStates(STATE))
            fab.animate()
                .scaleX(1)
                .scaleY(1)
                .setStartDelay(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationCancel(animation);
                        }
                    })
                .setInterpolator(new OvershootInterpolator(3));
        else if(!noFabStates(State)&&noFabStates(STATE)&&STATE!=State)
            fab.animate()
                .scaleX(0)
                .scaleY(0)
                .setStartDelay(300)
                .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationCancel(animation);
            }
        })
                .setInterpolator(new AnticipateInterpolator(3));
        else if(!noFabStates(STATE))
            fab.animate()
                    .scaleX(1.3f)
                    .scaleY(1.3f)
                    .setDuration(600)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationCancel(animation);
                            fab.setScaleX(1.0f);
                            fab.setScaleY(1.0f);
                        }
                    })
                    .setInterpolator(new CycleInterpolator(2));
        if (State == state.THREADS && STATE!=state.THREADS)
        {
            android.animation.ObjectAnimator.ofFloat(dArrow, "progress", 1).setDuration(600).start();
        }
        else if (State != state.THREADS && STATE==state.THREADS)
        {
            android.animation.ObjectAnimator.ofFloat(dArrow, "progress", 0).setDuration(600).start();
        }
        State = STATE;
    }

    // returns if the state has a fab or not

    boolean noFabStates(state STATE)
    {
        return STATE == state.GRADES ||STATE == state.GRADESALL ||STATE == state.NOTIFICATIONS;
    }

    /*
        function : Displays error messages using a Snackbar
        input : int : String ID retry : bool whether to perform the function again or not.
        output : void
    */

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

    /*
        function : post comment on a thread API call
        input : String : thread id, String description : The comment
        output : void
     */

    public void PostComment(final String thread_id,final String description)
    {
        String url = APIdetails.comment();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if((new JSONObject(response)).getBoolean("success"))
                            {
                                comment.setText("");
                                getThreadView(currentThreadid, currentThreadpos);
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

    /*
        function : performs actions after the file for submission has been sent
        input : request code : int result code : int data :Intent
        output : void
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode==RESULT_OK){
                    String file = data.getData().getPath(),file2 = getRealPathFromUri(mContext,data.getData());
                    if(!file2.equals("")) file = file2;
                    MultipartRequest multipartRequest = new MultipartRequest(APIdetails.assignmentSubmission(currentAssgnId), file, "enter file name here",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    getAssgnView(currentAssgnId,currentAssgnpos);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                }
                            }
                    );
                    queue.add(multipartRequest);
                }
                break;

        }
    }

    /*
        function : Translates Uri to usable path
        input : Context, Uri
        output : String
     */

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

    /*
        function : make the input array split and then combine to form array of arrays
        input : ArrayList
        output : ArrayList<ArrayList>
     */

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

        final JSONObject courses;
        try {
            courses = new JSONObject(getIntent().getStringExtra("json_courses"));
            JSONArray coursesList = courses.getJSONArray("courses");
            code = new String[coursesList.length()];
            for(int i=0;i<coursesList.length();i++) {
                code[i] = coursesList.getJSONObject(i).getString("code");
                topChannelMenu.add(0,i,1,coursesList.getJSONObject(i).getString("code").toUpperCase() + ": " + coursesList.getJSONObject(i).getString("name")).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
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
                                            currCourse = new course(new JSONObject(response));
                                            initializeAdapter(new RVAdapterAssignments(assignments,currCourse));
                                            changeState(state.COURSE);
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

    //Set views for the threads that are displayed

    public void getThreadView(final int id, final int position)
    {
        if(position>=threads.size()) return;
        String url = APIdetails.thread(id);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        currentThreadid = id;
                        currentThreadpos = position;
                        try {
                            JSONArray comments = (new JSONObject(response)).getJSONArray("comments");
                            JSONArray comment_users = (new JSONObject(response)).getJSONArray("comment_users");
                            thread_views = new ArrayList<>();
                            thread_views.add(new thread_view(threads.get(position).getUser_id(), threads.get(position).getDescription(), threads.get(position).getCreatedAt(), threads.get(position).getTitle()));
                            for (int i = 0; i < comment_users.length(); i++)
                                thread_views.add(new thread_view(comment_users.getJSONObject(i).getInt("id"), comments.getJSONObject(i).getString("description"), Timestamp.valueOf(comments.getJSONObject(i).getString("created_at")), comment_users.getJSONObject(i).getString("first_name") + " " + comment_users.getJSONObject(i).getString("last_name")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (thread_views != null) {
                            initializeAdapter(new RVAdapterThreadShow(thread_views));
                            changeState(state.COMMENTS);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    //Setup the view for assignment details to be viewed

    public void getAssgnView(final int id, final int position)
    {
        String url = APIdetails.assignment(id);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        currentAssgnId = id;
                        currentAssgnpos = position;
                        try {
                            JSONObject jsobj = new JSONObject(response);
                            ah = new assignment_header(jsobj.getJSONObject("assignment"),jsobj.getJSONObject("registered"),jsobj.getJSONObject("course"));
                            JSONArray submissions = jsobj.getJSONArray("submissions");
                            assignment_views = new ArrayList<>();
                            for (int i = 0; i < submissions.length(); i++) {
                                JSONObject obji = submissions.getJSONObject(i);
                                assignment_views.add(new assignment_view(obji.getInt("id"), obji.getString("name"), Timestamp.valueOf(obji.getString("created_at")), obji.getString("file_")));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (assignment_views!= null) {
                            initializeAdapter(new RVAdapterAssgnShow(ah,assignment_views));
                            changeState(state.SUBMISSIONS);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    // Initialize cards to display

    private void initializeAdapter(RecyclerView.Adapter adapter){
        invalidateOptionsMenu();
        adapterInit = true;
        rv.setAdapter(adapter);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(getState()==state.SUBMISSIONS)
        {
            return;
        }
        threads = new ArrayList<>();
        getThreads(code, 0);
        drawer.openDrawer(GravityCompat.START);
        if(getState()!=state.THREADS) changeState(state.THREADS);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(getState()==state.SUBMISSIONS)
        {
            changeState(state.COURSE);
            initializeAdapter(new RVAdapterAssignments(assignments,currCourse));
        }
        else if(getState()==state.GRADES)
        {
            changeState(state.COURSE);
            initializeAdapter(new RVAdapterAssignments(assignments, currCourse));
        }
        else if(getState()!=state.THREADS) {
            changeState(state.THREADS);
            initializeAdapter(new RVAdapterThreads(threads));
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(getState()== state.THREADS) getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return getState() == state.THREADS &&  super.onPrepareOptionsMenu(menu);
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
        else if (id == android.R.id.home)
        {
            if(State != state.THREADS)
            onBackPressed();
            else drawer.openDrawer(GravityCompat.START);
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
