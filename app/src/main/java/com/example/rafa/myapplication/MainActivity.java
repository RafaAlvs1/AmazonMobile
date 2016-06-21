package com.example.rafa.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "UserPreferenceDemoActivity";
    public static AmazonClientManager clientManager = null;

    private List<String> labels = new ArrayList<String>();
    private ArrayAdapter<String> mAdapter  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clientManager = new AmazonClientManager(this);

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, labels);

        ListView listView = (ListView) findViewById(R.id.listview_tables);

        listView.setAdapter(mAdapter);

        new GetUserListTask().execute();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> av, View v,
                                    final int pos, long id) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if("Devices".equals(av.getItemAtPosition(pos).toString())){
                                    startActivity(new Intent(MainActivity.this,
                                            ItemsTable.class).putExtra("tablename",av.getItemAtPosition(pos).toString()));
                                } else{
                                    Toast toast = Toast.makeText(MainActivity.this,
                                            "Não há suporte para esta tabela.", Toast.LENGTH_LONG);
                                    toast.show();
                                }

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                // Do nothing
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Listar itens desta tabela?")
                        .setPositiveButton("Sim", dialogClickListener)
                        .setNegativeButton("Não", dialogClickListener).show();

            }
        });

        Button buttonList = (Button) findViewById(R.id.button_list);

        buttonList.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new GetUserListTask().execute();
            }
        });
    }

    private class GetUserListTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... inputs) {
            labels = DynamoDBManager.getTablesList();
            return null;
        }

        protected void onPostExecute(Void result) {
            mAdapter.clear();
            if (labels != null) {
                for (String object : labels) {
                    mAdapter.insert(object, mAdapter.getCount());
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

}
