package com.hw2.juri.hw2;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AsyncLoad extends AsyncTask<Long, Integer, List<Note>> {
    private MainActivity mainActivity;

    public AsyncLoad(MainActivity m){
        mainActivity = m;
    }

    @Override
    protected List<Note> doInBackground(Long... params) {
        List<Note> list = new ArrayList<Note>();
        try {
            InputStream is = mainActivity.getApplicationContext().openFileInput(mainActivity.getString(R.string.note_file));
            JsonReader reader = new JsonReader(new InputStreamReader(is, mainActivity.getString(R.string.encoding)));

            reader.beginArray();
            while (reader.hasNext()) {
                list.add(loadNote(reader));
            }
            reader.endArray();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private Note loadNote(JsonReader reader){
        Note note = new Note();
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("title")) {
                    note.setTitle(reader.nextString());
                }else if(name.equals("text")) {
                    note.setText(reader.nextString());
                }else if(name.equals("date")){
                    note.setLastSave(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return note;
    }

    @Override
    protected void onPostExecute(List<Note> notes) {
        super.onPostExecute(notes);
        mainActivity.loadTask(notes);
    }
}
