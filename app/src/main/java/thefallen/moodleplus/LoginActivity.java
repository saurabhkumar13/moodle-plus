package thefallen.moodleplus;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText userID,password;
    Button login;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        userID= (EditText) findViewById(R.id.userid);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            if(validate())
                login();
            }
        });
    }
    boolean validate()
    {
        return true;
    }
    void login()
    {
        callApi(userID.getText().toString(),password.getText().toString());
    }
    public void callApi(final String name,final String pass)
    {
        String url = "http://10.0.0.5:8080/default/login.json";

        // Request a string response
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(mContext, NavDrawerActivity.class);//LoginActivity.class,MainActivity.class);
                        intent.putExtra("json",response);
                        startActivity(intent);
                        // Result handling
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // Error handling
                volleyError.printStackTrace();

                if(volleyError instanceof NoConnectionError) {
//                    errorSnack(R.string.error_noInternet);
                } else if (volleyError instanceof TimeoutError) {
//                    errorSnack(R.string.error_timeOut);
                } else if (volleyError instanceof ServerError) {
//                    errorSnack(R.string.error_serverError);
                }
            }
        }){
            @Override
            protected Map<String,String> getParams(){
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
            Volley.newRequestQueue(this).add(stringRequest);

    }
}
