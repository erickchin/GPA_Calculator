package com.example.erick.gpacalculator;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class CourseActivity extends AppCompatActivity {

    private ArrayAdapter<String> mEvaluationAdapter;
    private String mCourseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Receive passed on value
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mCourseCode = bundle.getString("COURSE_CODE");
        }

        // Setup the fab to receive user input
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddEvaluationActivity.class)
                        .putExtra("COURSE_CODE", mCourseCode);;
                startActivity(intent);
            }
        });

        // Set-up the list view
        mEvaluationAdapter =
                new ArrayAdapter<String>(
                        this,
                        R.layout.list_item_evaluation,
                        R.id.list_item_evaluation_textview,
                        new ArrayList<String>());

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) this.findViewById(R.id.listview_evaluations);
        listView.setAdapter(mEvaluationAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //TODO: When clicking a evaluation, allow editing
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getEvaluations();
    }

    private void getEvaluations() {
        mEvaluationAdapter.clear();
        EvaluationDatabaseHelper databaseHelper = new EvaluationDatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + databaseHelper.COL_2 + ", " + databaseHelper.COL_4 + " FROM " + databaseHelper.TABLE_NAME + " WHERE " + databaseHelper.COL_3 + "= '" + mCourseCode + "'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String evaluationName = cursor.getString(cursor.getColumnIndex(databaseHelper.COL_2));
                    String evaluationMark = cursor.getString(cursor.getColumnIndex(databaseHelper.COL_4));
                    mEvaluationAdapter.add(evaluationName + " | " + evaluationMark);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
    }

    // TODO: Get course average
    private void calculateAverage() {

    }
}
