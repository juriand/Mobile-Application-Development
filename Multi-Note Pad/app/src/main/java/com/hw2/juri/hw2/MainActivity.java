package com.hw2.juri.hw2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{
    private static final int CHANGE_NOTE = 1;
    private static final int NEW_NOTE = 2;
    private RecyclerView recyclerView;
    private List<Note> noteList = new ArrayList<Note>();
    private NoteAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AsyncLoad(this).execute();
        recyclerView = findViewById(R.id.recycler);
        mAdapter = new NoteAdapter(noteList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.edit:
                Intent intent1 = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(intent1,NEW_NOTE);
                return true;
            case R.id.info:
                Intent intent2 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        saveData();
        super.onPause();
    }

    public void loadTask(List<Note> nl){
        noteList.addAll(nl);
    }

    private void saveData(){
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.note_file), Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginArray();
            for (Note note:noteList) {
                saveNote(writer, note);
            }
            writer.endArray();
            writer.close();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void saveNote(JsonWriter writer, Note n){
        try {
            writer.beginObject();
            writer.name("title").value(n.getTitle());
            writer.name("text").value(n.getText());
            writer.name("date").value(n.getLastSave());
            writer.endObject();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Note n = noteList.get(pos);
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("POS",pos);
        intent.putExtra("TITLE",n.getTitle());
        intent.putExtra("DATE",n.getLastSave());
        intent.putExtra("TEXT",n.getText());
        startActivityForResult(intent,CHANGE_NOTE);
    }

    @Override
    public boolean onLongClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                noteList.remove(pos);
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setTitle("Delete Note " +"'"+ noteList.get(pos).getTitle()+"'?");
        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHANGE_NOTE) {
            if (resultCode == RESULT_OK) {
                int pos = data.getIntExtra("POS",0);
                Note n = noteList.get(pos);
                n.setTitle(data.getStringExtra("RESULT_TITLE"));
                n.setLastSave(data.getStringExtra("RESULT_DATE"));
                n.setText(data.getStringExtra("RESULT_TEXT"));
                Collections.swap(noteList, 0, pos);
                mAdapter.notifyDataSetChanged();
            }
        } else if(requestCode == NEW_NOTE){
            if (resultCode == RESULT_OK) {
                String title = data.getStringExtra("RESULT_TITLE");
                String text = data.getStringExtra("RESULT_TEXT");
                Note n = new Note(title,text);
                noteList.add(0,n);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}