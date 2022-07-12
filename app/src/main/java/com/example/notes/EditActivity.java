package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {
    private TextView textTitleEdit;
    private TextView textDescriptionEdit;
    private RadioButton button3;
    private RadioButton button2;
    private RadioButton button1;
    private int position;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        textTitleEdit = findViewById(R.id.textTitleEdit);
        textDescriptionEdit = findViewById(R.id.textDescriptionEdit);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        NotesDBHelper dbHelper = new NotesDBHelper(this);
        database = dbHelper.getWritableDatabase();
        Intent intent = getIntent();
        if (intent.hasExtra("title") && intent.hasExtra("description") && intent.hasExtra("priority") && intent.hasExtra("position")){
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            int priority = intent.getIntExtra("priority", 1);
            position = intent.getIntExtra("position", 1);
            textTitleEdit.setText(title);
            textDescriptionEdit.setText(description);
        switch (priority){
            case 1: button1.setChecked(true);
            break;
            case 2: button2.setChecked(true);
            break;
            case 3: button3.setChecked(true);
            }
        }
    }

    public void onClickEdit(View view) {
        int  positionButton = 999;

        if (button1.isChecked()){
            positionButton = 1;
        }
        if(button2.isChecked()){
            positionButton = 2;
        }
        if (button3.isChecked()){
            positionButton = 3;
        }

        if(!textTitleEdit.getText().toString().isEmpty() && !textDescriptionEdit.getText().toString().isEmpty()) {
            Date dateNow = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy hh:mm");
            String data = formatForDateNow.format(dateNow);

            ContentValues contentValues = new ContentValues();
            contentValues.put(NotesContract.NotesEntry.COLUMN_TITLE, textTitleEdit.getText().toString());
            contentValues.put(NotesContract.NotesEntry.COLUMN_DESCRIPTION, textDescriptionEdit.getText().toString());
            contentValues.put(NotesContract.NotesEntry.COLUMN_DATA, data);
            contentValues.put(NotesContract.NotesEntry.COLUMN_PRIORITY, positionButton);
            database.insert(NotesContract.NotesEntry.TABLE_NAME, null, contentValues);
            Intent intentDell = new Intent(this, MainActivity.class);
            intentDell.putExtra("dell", position);
            startActivity(intentDell);
        }else {
            Toast.makeText(this, "Все поля должны быть заполнены!", Toast.LENGTH_SHORT).show();
        }


    }
}