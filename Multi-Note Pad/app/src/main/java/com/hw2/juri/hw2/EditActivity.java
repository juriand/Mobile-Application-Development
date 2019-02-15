package com.hw2.juri.hw2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity implements TextWatcher{
    private static final String TAG = "EditActivity";
    private Note note;
    private EditText inputTitle;
    private EditText inputText;
    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        inputTitle = findViewById(R.id.inputTitle);
        inputText = findViewById(R.id.inputText);
        inputText.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        note = new Note();
        if (intent.hasExtra("TITLE")) {
            note.setPos(intent.getIntExtra("POS",0));
            inputTitle.setText(intent.getStringExtra("TITLE"));
            note.setLastSave(intent.getStringExtra("DATE"));
            inputText.setText(intent.getStringExtra("TEXT"));
        }

        inputTitle.addTextChangedListener(this);
        inputText.addTextChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if(isChanged && inputTitle.getText().toString().trim().length() != 0){
                    Intent data = new Intent();
                    data.putExtra("POS",note.getPos());
                    data.putExtra("RESULT_TITLE", inputTitle.getText().toString());
                    data.putExtra("RESULT_TEXT", inputText.getText().toString());
                    data.putExtra("RESULT_DATE",note.updateDate());
                    setResult(RESULT_OK, data);
                    finish();
                }else if(!isChanged){
                    if(inputTitle.getText().toString().trim().length() == 0) {
                        Toast.makeText(this, getString(R.string.no_title_save), Toast.LENGTH_SHORT).show();
                    }
                    setResult(RESULT_CANCELED);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(isChanged){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if(inputTitle.getText().toString().trim().length() == 0){
                        Toast.makeText(EditActivity.this, getString(R.string.no_title_save), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_CANCELED);
                        finish();
                    }else{
                        Intent data = new Intent();
                        data.putExtra("POS",note.getPos());
                        data.putExtra("RESULT_TITLE", inputTitle.getText().toString());
                        data.putExtra("RESULT_TEXT", inputText.getText().toString());
                        data.putExtra("RESULT_DATE",note.updateDate());
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            });
            builder.setTitle("Your note is not saved!\n Save note " +"'"+ inputTitle.getText().toString()+"'?");
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.setCancelable(false);
        }else{
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        isChanged = true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
