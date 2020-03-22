package com.family.familyprotector;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Browser;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // currently not possible
        //String[] proj = new String[] { Browser., Browser.BookmarkColumns.URL };
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
