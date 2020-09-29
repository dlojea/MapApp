package com.example.mapapp;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class MarkerActivity extends Activity {

    private EditText name;
    private EditText description;

    private Button save;
    private Button cancel;

    private LatLng latLng;

    private SQLiteDatabase db;

    protected void onCreate (Bundle SavedInstance) {
        super.onCreate(SavedInstance);
        setContentView(R.layout.activity_marker);

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);

        latLng = (LatLng) this.getIntent().getExtras().get("latLng");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    private void save() {
        String nameToString = name.getText().toString();
        String descriptionToString = description.getText().toString();
        if (TextUtils.isEmpty(nameToString.trim()) || TextUtils.isEmpty(descriptionToString.trim())) {
            Toast.makeText(this, "Rellena los campos", Toast.LENGTH_LONG).show();
        } else {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("name", nameToString);
            resultIntent.putExtra("description", descriptionToString);
            resultIntent.putExtra("latLng", latLng);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
