package ir.picky.app.driver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ir.picky.app.driver.util.NetworkHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/yekan.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        if (!NetworkHelper.hasNetworkAccess(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("دسترسی به اینترنت مقدور نیست");
            builder.setTitle("پیکی");
            builder.setIcon(R.drawable.exclamation);
            builder.setNegativeButton("بستن", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int id) {
                    finish();
                }
            });
            builder.show();

        } else {

            try {

                database = this.openOrCreateDatabase("Picky", MODE_PRIVATE, null);

                database.execSQL("CREATE TABLE IF NOT EXISTS user ( user VARCHAR , pass VARCHAR , islogin int(1),aut_key VARCHAR)");

                //database.execSQL("INSERT INTO user (number) VALUES ('09133196928')");

                Cursor c = database.rawQuery("SELECT * FROM user", null);

                int userIndex = c.getColumnIndex("user");
                int isloginIndex = c.getColumnIndex("islogin");

                c.moveToFirst();

                if (c.getCount() == 0) {
                    // TODO
                    Intent intent = new Intent(this, SignInActivity.class);
                    startActivity(intent);
                } else if (c.getInt(isloginIndex) == 0) {

                    Intent intent = new Intent(this, SignInActivity.class);
                    startActivity(intent);
                } else {
//                    Intent intent = new Intent(this, MapsActivity.class);
//                    startActivity(intent);
                }

            } catch (Exception e) {

                e.printStackTrace();

            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
