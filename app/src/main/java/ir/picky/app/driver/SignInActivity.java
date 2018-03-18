package ir.picky.app.driver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ir.picky.app.driver.services.MyService;
import ir.picky.app.driver.util.RequestPackage;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignInActivity extends AppCompatActivity {

    TextView textView;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/yekan.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_sign_in);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(MyService.MY_SERVICE_MESSAGE));

        database = this.openOrCreateDatabase("Picky", MODE_PRIVATE, null);

        textView = (TextView) findViewById(R.id.textView5);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message =
                    intent.getStringExtra(MyService.MY_SERVICE_PAYLOAD);
            Log.i("codejson", message);

            int islogin =  getUserCode(message);
            if (islogin == 1) {
                storeInDatabase();
                Intent intent2 = new Intent(SignInActivity.this, MapsActivity.class);
                startActivity(intent2);
            }
        }
    };

    public int getUserCode(String userCodeJson) {
        JSONObject json = null;
        try {
            json = new JSONObject(userCodeJson);
            Toast.makeText(this, json.getString("registercode") , Toast.LENGTH_SHORT).show();
            return Integer.valueOf( json.getString("registercode"));
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void loginButtonHandler(View view) {
        EditText user = (EditText) findViewById(R.id.editTextUser);
        EditText pass = (EditText) findViewById(R.id.editTextPass);


        if (user.length()==0 || pass.length() == 0 ) {
            textView.setText( "نام کاربری و رمز عبور را وارد نمایید." );
        } else {
            textView.setText( " " );
            String singInUrl = "http://comp.isfahanregister.com/api_driver/login" ;
            RequestPackage requestPackage = new RequestPackage();
            requestPackage.setEndPoint(singInUrl);
            requestPackage.setParam("phone", "09133196928");
            //requestPackage.setParam("creat", "2");
            Intent intent = new Intent(this, MyService.class);
            intent.putExtra(MyService.REQUEST_PACKAGE, requestPackage);
            //startService(intent);
            Intent intent2 = new Intent(SignInActivity.this, MapsActivity.class);
            startActivity(intent2);
        }


    }

    private void storeInDatabase() {
        ContentValues userValue = new ContentValues();
        userValue.put("user", "");
        userValue.put("islogin", 1 );
        database.insert("user", null, userValue);
    }

}
