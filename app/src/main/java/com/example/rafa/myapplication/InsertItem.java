package com.example.rafa.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class InsertItem extends AppCompatActivity {

    DynamoDBManager.UserPreference userPreference = new DynamoDBManager.UserPreference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_item);

        Button buttonAddItem = (Button) findViewById(R.id.button_insert);

        buttonAddItem.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Insert();
            }
        });
    }

    private void Insert(){
        EditText editText = (EditText) findViewById(R.id.input_nome);
        String nome = editText.getText().toString();
        if(nome.equals("")){
            Toast toast = Toast.makeText(InsertItem.this,
                    "Campo vazio!.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        editText = (EditText) findViewById(R.id.input_user);
        String user = editText.getText().toString();
        if(user.equals("")){
            Toast toast = Toast.makeText(InsertItem.this,
                    "Campo vazio!.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        editText = (EditText) findViewById(R.id.input_situation);
        String situation = editText.getText().toString();
        if(situation.equals("")){
            Toast toast = Toast.makeText(InsertItem.this,
                    "Campo vazio!.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        editText = (EditText) findViewById(R.id.input_version);
        String version = editText.getText().toString();
        if(version.equals("")){
            Toast toast = Toast.makeText(InsertItem.this,
                    "Campo vazio!.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Calendar calendar = Calendar.getInstance();

        String idString = new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime()).toString();

        userPreference.setIdDevice(Long.parseLong(idString));
        userPreference.setNameDevice(nome);
        userPreference.setUserName(user);
        userPreference.setSituation(situation);
        userPreference.setVersionHardware(version);

        new InsertItemTask().execute();

        this.finish();


    }

    private class InsertItemTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... types) {

            System.out.println("Inserir");

            DynamoDBManager.insertUsers(userPreference);

            return null;
        }

        protected void onPostExecute(Void result) {
            String item = userPreference.getNameDevice() + " - " + userPreference.getUserName();
            ItemsTable.updateIsertItem(item);
        }
    }
}
