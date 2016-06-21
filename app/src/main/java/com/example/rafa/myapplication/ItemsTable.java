package com.example.rafa.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.rafa.myapplication.DynamoDBManager.UserPreference;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.util.ArrayList;


public class ItemsTable extends AppCompatActivity {

    private ArrayList<UserPreference> items = null;
    private static ArrayAdapter<String> mAdapter;
    private static ArrayList<String> labels = new ArrayList<String>();;
    private int currentPosition = 0;
    private CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_table);

        Bundle b = getIntent().getExtras();
        String table = b.getString("tablename");
        setTitle(table);

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, labels);

        ListView listView = (ListView) findViewById(R.id.listview_item_table);

        listView.setAdapter(mAdapter);

        new GetDevicesListTask().execute();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v,
                                           int pos, long id) {
                return onLongListItemClick(v, pos, id);
            }
        });


        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.item_content);

        final BottomBar bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.noNavBarGoodness();
        bottomBar.setItemsFromMenu(R.menu.three_buttons_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(int menuItemId) {
                switch (menuItemId) {
                    case R.id.item_add:
                        new GetDevicesListTask().execute();
                        Snackbar.make(coordinatorLayout, "Lista atualizada", Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.item_list:
                        startActivity(new Intent(ItemsTable.this, InsertItem.class));
                        break;
                    case R.id.info_list:
                        Snackbar.make(coordinatorLayout, "Location Item Selected", Snackbar.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(int menuItemId) {
                switch (menuItemId) {
                    case R.id.item_add:
                        new GetDevicesListTask().execute();
                        Snackbar.make(coordinatorLayout, "Lista atualizada", Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.item_list:
                        startActivity(new Intent(ItemsTable.this, InsertItem.class));
                        //Snackbar.make(coordinatorLayout, "Item ", Snackbar.LENGTH_LONG).show();
                        break;
                    case R.id.info_list:
                        Snackbar.make(coordinatorLayout, "Location Item Selected", Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });



        // Set the color for the active tab. Ignored on mobile when there are more than three tabs.
        bottomBar.setActiveTabColor("#C2185B");

    }

    public static void updateIsertItem(String item){
        labels.add(item);

        mAdapter.clear();
        if (labels != null){
            for (String object : labels) {
                mAdapter.insert(object, mAdapter.getCount());
            }
        }
        mAdapter.notifyDataSetChanged();
        //CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.item_content);
        //Snackbar.make(coordinatorLayout, "Item Adicionado ao banco de dados!", Snackbar.LENGTH_LONG).show();
    }

    protected boolean onLongListItemClick(View v, int position, long id) {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        new DeleteItemTask().execute();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete this user?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

        currentPosition = position;
        return true;
    }


    private class GetDevicesListTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... inputs) {

            labels = new ArrayList<String>();
            items = DynamoDBManager.getItemsList();

            for (DynamoDBManager.UserPreference up : items) {
                labels.add(up.getNameDevice() + " - " + up.getUserName());
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            mAdapter.clear();
            if (labels != null){
                for (String object : labels) {
                    mAdapter.insert(object, mAdapter.getCount());
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private class DeleteItemTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... types) {

            System.out.println(items.get(currentPosition).getNameDevice());
            DynamoDBManager.deleteUser(items.get(currentPosition));

            items.remove(currentPosition);
            labels.remove(currentPosition);

            return null;
        }

        protected void onPostExecute(Void result) {
            mAdapter.clear();
            if (labels != null){
                for (String object : labels) {
                    mAdapter.insert(object, mAdapter.getCount());
                }
            }
            mAdapter.notifyDataSetChanged();

        }
    }
}
