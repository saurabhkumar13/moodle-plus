package thefallen.moodleplus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text = (TextView) findViewById(R.id.text);
        JSONObject user_details;
        try {
            user_details = new JSONObject(getIntent().getStringExtra("json"));
            user_details = user_details.getJSONObject("user");
            text.setText("YO!   "+user_details.getString("first_name")+"  "+user_details.getString("last_name")+" \n"+"PS your password "+user_details.getString("password"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
