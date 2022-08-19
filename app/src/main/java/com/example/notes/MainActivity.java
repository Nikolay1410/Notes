package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.example.notes.adapters.NotesAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<Note> notes = new ArrayList<>();
    public NotesAdapter adapter;
    public NotesDBHelper dbHelper;
    private SQLiteDatabase database;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchPriority;
    private String sortData = "ASC";
    private String sortBy = "data "+sortData;

    private TextView textViewNoteData;
    private TextView textViewPriority;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        switchPriority = findViewById(R.id.switchPriority);
        textViewNoteData = findViewById(R.id.textViewNoteData);
        textViewPriority = findViewById(R.id.textViewPriority);
        Intent intentDell = getIntent();
        dbHelper = new NotesDBHelper(this);
        database = dbHelper.getWritableDatabase();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        sortBy = preferences.getString("sort", null);
        getData(sortBy);
        adapter = new NotesAdapter(notes);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotes.setAdapter(adapter);

        if (intentDell.hasExtra("dell")){
            int positionDell = intentDell.getIntExtra("dell", 9999);
            if (sortBy.contains("data DESC")){
                remove(positionDell+1);
            }else {
                remove(positionDell);
            }
            adapter.notifyDataSetChanged();
        }

        if (sortBy.contains("data ASC") || sortBy.contains("data DESC")){
            switchPriority.setChecked(false);
            textViewNoteData.setTextColor(getResources().getColor(R.color.my_color));
            textViewPriority.setTextColor(getResources().getColor(R.color.black));
        }else {
            switchPriority.setChecked(true);
            textViewNoteData.setTextColor(getResources().getColor(R.color.black));
            textViewPriority.setTextColor(getResources().getColor(R.color.my_color));
        }

        adapter.setOnNoteClickListener(new NotesAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(int position) {
                Note note = notes.get(position);
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("title", note.getTitle());
                intent.putExtra("description", note.getDescription());
                intent.putExtra("priority", note.getPriority());
                intent.putExtra("position", position);
                startActivity(intent);
            }
            @Override
            public void onLongClick(int position) {
                remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewNotes);
        switchPriority.setOnCheckedChangeListener((compoundButton, b) -> setMethodOfSort(b));
    }

    private void remove(int position){
        int id = notes.get(position).getId();
        String where = NotesContract.NotesEntry._ID+" =?";
        String[] whereArgs = new String[]{Integer.toString(id)};
        database.delete(NotesContract.NotesEntry.TABLE_NAME, where, whereArgs);
        getData(sortBy);
    }

    public void onClickAddNote(View view) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    private void getData(String methodOfSort){
        notes.clear();
        Cursor cursor = database.query(NotesContract.NotesEntry.TABLE_NAME, null, null, null, null, null, methodOfSort);
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(NotesContract.NotesEntry._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_DESCRIPTION));
            String data = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_DATA));
            int priority = cursor.getInt(cursor.getColumnIndexOrThrow(NotesContract.NotesEntry.COLUMN_PRIORITY));
            Note note = new Note(id, title, description, data, priority);
            notes.add(note);
        }
        cursor.close();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onClickNoteData(View view) {
        getData("data DESC");
        switchPriority.setChecked(false);
        textViewNoteData.setTextColor(getResources().getColor(R.color.my_color));
        textViewPriority.setTextColor(getResources().getColor(R.color.black));
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onClickPriority(View view) {
        getData("priority");
        switchPriority.setChecked(true);
        textViewNoteData.setTextColor(getResources().getColor(R.color.black));
        textViewPriority.setTextColor(getResources().getColor(R.color.my_color));
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMethodOfSort(boolean isTopRated){
        String methodOfSort;
        if (isTopRated){
            sortBy = "priority";
            methodOfSort = NotesContract.NotesEntry.COLUMN_PRIORITY;
            textViewNoteData.setTextColor(getResources().getColor(R.color.black));
            textViewPriority.setTextColor(getResources().getColor(R.color.my_color));
        }else {
            sortBy = "data "+sortData;
            methodOfSort = sortBy;
            textViewNoteData.setTextColor(getResources().getColor(R.color.my_color));
            textViewPriority.setTextColor(getResources().getColor(R.color.black));
        }
        getData(methodOfSort);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.edit().putString("sort", methodOfSort).apply();
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onClickUp(View view) {
        if (sortBy.contains("data ASC") || sortBy.contains("data DESC")) {
            sortData = "ASC";
            sortBy = "data ASC";
            String sort = "data ASC";
            getData(sort);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            preferences.edit().putString("sort", sort).apply();
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onClickDown(View view) {
        if (sortBy.contains("data ASC") || sortBy.contains("data DESC")) {
            sortData = "DESC";
            sortBy = "data DESC";
            String sort = "data DESC";
            getData(sort);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            preferences.edit().putString("sort", sort).apply();
            adapter.notifyDataSetChanged();
        }
    }
}