package thefallen.moodleplus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText userID,password;
    Button login;
    Context mContext;
    SharedPreferences sharedPreferences;
    Intent intent;
    RequestQueue queue;
    View.OnClickListener loginAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        queue = Volley.newRequestQueue(mContext);
        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        userID= (EditText) findViewById(R.id.userid);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);

        loginAction = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                    login();
            }
        };
        login.setOnClickListener(loginAction);
    }
    // To Display errors
    // (id of the error string:int,to retry or not to retry:boolean) ==> params
    void errorSnack(int strID,boolean retry)
    {
        Snackbar snackbar = Snackbar.make(login,strID,Snackbar.LENGTH_SHORT);
        if(retry) snackbar.setAction("RETRY", loginAction);
                snackbar.show();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        String userID = sharedPreferences.getString("userID", "");
        String password = sharedPreferences.getString("password", "");
        if(!userID.equals("")&&!password.equals("")) callApi(userID,password);
        this.userID.setText(userID);
        this.password.setText(password);
        // CookieStore is just an interface, you can implement it and do things like
        // Optionally, you can just use the default CookieManager
        CookieManager manager = new java.net.CookieManager();
        CookieHandler.setDefault(manager);
    }
    boolean validate()
    {
        return !userID.getText().toString().equals("")&&!password.getText().toString().equals("");
    }
    void login()
    {
        //save user details if remember me checked
        if(((CheckBox)findViewById(R.id.save_details)).isChecked()){
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString("userID", userID.getText().toString());
        sharedPreferencesEditor.putString("password", password.getText().toString());
        sharedPreferencesEditor.apply();
        }
        
        // call the api with user details    
        callApi(userID.getText().toString(), password.getText().toString());
    
    }

    // post request to log in to the system
    // (username:String,password:String) -> void
    // onSuccess gets further details from the API and starts the next activity
    public void callApi(final String name,final String pass)
    {
        String url = APIdetails.login();

        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("json",response);
                        try {
                            if((new JSONObject(response)).getBoolean("success"))
                            {intent = new Intent(mContext, NavDrawerActivity.class);
                            intent.putExtra("json_user",response);
                            getDetails(APIdetails.coursesList());}
                            else errorSnack(R.string.wrong_credentials,false);
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
                    params.put("userid", name);
                    params.put("password", pass);
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
    public void getDetails(String url)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    if(response.equals("{}\n")) errorSnack(R.string.error,true);
                    else {
                        intent.putExtra("json_courses", response);
                        Log.e("json", "("+response+")");
                        startActivity(intent);
                    }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(volleyError instanceof NoConnectionError) {
                    errorSnack(R.string.no_internet,true);
                } else if (volleyError instanceof TimeoutError) {
                    errorSnack(R.string.timeout,true);
                } else if (volleyError instanceof ServerError) {
                    errorSnack(R.string.internal_server,true);
                }
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
