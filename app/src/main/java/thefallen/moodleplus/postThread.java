package thefallen.moodleplus;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
    Create the view for the option of creating a new thread with spinners to select course and text area to enter the data
 */

public class postThread extends AppCompatActivity {

    Spinner courseSpinner;
    EditText title;
    EditText desc;
    View.OnClickListener sendAction;
    String[] courseCodes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_thread);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title = (EditText) findViewById(R.id.title);
        desc = (EditText) findViewById(R.id.description);
        courseSpinner = (Spinner) findViewById(R.id.course);
        sendAction = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReq();
            }
        };


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initSpinner();
    }
    public void initSpinner() {
        courseCodes = getIntent().getStringArrayExtra("courses");
        for (int i=0;i<courseCodes.length;i++) courseCodes[i]=courseCodes[i].toUpperCase();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courseCodes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(spinnerAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post_thread, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home)
            finish();
        else if (id == R.id.post)
            postReq();
        return super.onOptionsItemSelected(item);
    }

    // calls the function calling the api if the user clicks on post
    public void postReq()
    {
        String title = this.title.getText().toString(),description = this.desc.getText().toString(),course = courseCodes[(int)courseSpinner.getSelectedItemId()].toLowerCase();
        if(validate(title,description))
            callApi(title,description,course);
    }

    public boolean validate(String title,String desc)
    {
        return !title.equals("")&&!desc.equals("");
    }
    void errorSnack(int strID,boolean retry)
    {
        Snackbar snackbar = Snackbar.make(title,strID,Snackbar.LENGTH_SHORT);
        if(retry) snackbar.setAction("RETRY", sendAction);
        snackbar.show();
    }

    /*
        function : calls the API to post a new thread with appropriate parameters
        input : title : String  , description : String , course : String
        output : void
     */

    public void callApi(final String title,final String description,final String course)
    {
        String url = APIdetails.newThread();
        Log.e("json",url+";;"+title+";;"+description+";;"+course);
        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("json", response);
                        try {
                            if((new JSONObject(response)).getBoolean("success"))
                            {
                                finish();
                            }
                            else errorSnack(R.string.error,false);
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
                    errorSnack(R.string.no_internet,true);
                } else if (volleyError instanceof TimeoutError) {
                    errorSnack(R.string.timeout,true);
                } else if (volleyError instanceof ServerError) {
                    errorSnack(R.string.internal_server,true);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("title", title);
                params.put("description", description);
                params.put("course_code", course);
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
        Volley.newRequestQueue(this).add(stringRequest);

    }

}
