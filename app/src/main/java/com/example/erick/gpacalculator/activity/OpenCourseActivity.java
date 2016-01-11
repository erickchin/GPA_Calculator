package com.example.erick.gpacalculator.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.erick.gpacalculator.GPACalculator;
import com.example.erick.gpacalculator.R;
import com.example.erick.gpacalculator.activity.AddEvaluationActivity;
import com.example.erick.gpacalculator.data.CourseDatabaseHelper;
import com.example.erick.gpacalculator.data.EvaluationDatabaseHelper;

import java.util.ArrayList;

public class OpenCourseActivity extends AppCompatActivity {

    private ArrayAdapter<String> mEvaluationAdapter;
    private String mCourseCode;
    private String mCourseName;
    private CourseDatabaseHelper mCourseDH;
    EvaluationDatabaseHelper mEvaluationDH;

    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList<String> codeList = new ArrayList<String>();
    ArrayList<String> markList = new ArrayList<String>();
    ArrayList<String> weightList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCourseDH = new CourseDatabaseHelper(this);
        mEvaluationDH = new EvaluationDatabaseHelper(this);
        // Receive passed on value
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mCourseCode = bundle.getString("COURSE_CODE");
            mCourseName = bundle.getString("COURSE_NAME");
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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                mEvaluationDH.deleteData(nameList.get(position), codeList.get(position), markList.get(position), weightList.get(position));
                Toast.makeText(getApplicationContext(), nameList.get(position) + " evaluation deleted. ", Toast.LENGTH_LONG).show();
                getEvaluations();
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getEvaluations();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getEvaluations();
    }

    private void getEvaluations() {
        mEvaluationAdapter.clear();
        nameList.clear();
        codeList.clear();
        markList.clear();
        weightList.clear();
        SQLiteDatabase db = mEvaluationDH.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + mEvaluationDH.COL_2 + ", " + mEvaluationDH.COL_3  + ", "  + mEvaluationDH.COL_4  + ", " + mEvaluationDH.COL_5 + " FROM " + mEvaluationDH.TABLE_NAME + " WHERE " + mEvaluationDH.COL_3 + "= '" + mCourseCode + "'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String evaluationName = cursor.getString(cursor.getColumnIndex(mEvaluationDH.COL_2));
                    String evaluationCode = cursor.getString(cursor.getColumnIndex(mEvaluationDH.COL_3));
                    String evaluationMark = cursor.getString(cursor.getColumnIndex(mEvaluationDH.COL_4));
                    String evaluationWeight = cursor.getString(cursor.getColumnIndex(mEvaluationDH.COL_5));

                    nameList.add(evaluationName);
                    codeList.add(evaluationCode);
                    markList.add(evaluationMark);
                    weightList.add(evaluationWeight);

                    mEvaluationAdapter.add(evaluationName + "\r\nMark: " + evaluationMark + "% | Weight: " + evaluationWeight + "/100");
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        calculateAverage();
    }

    private void calculateAverage() {
        if (markList.size() == 0) {
            setTitle("[" + mCourseCode + "] " + mCourseName);
        }
        else {
            double totalWeight = 0;
            double courseAverage = 0;
            double evaluationGrade = 0;
            double evaluationWeight = 0;
            SQLiteDatabase db = mCourseDH.getReadableDatabase();

            for (int i = 0; i < markList.size(); i++) {
                evaluationGrade = Double.parseDouble(markList.get(i));
                evaluationWeight = Double.parseDouble(weightList.get(i));
                courseAverage += evaluationGrade * evaluationWeight;
                totalWeight += Double.parseDouble(weightList.get(i));
            }
            courseAverage = (double) Math.round(courseAverage / totalWeight * 100) / 100;
            GPACalculator gpa = new GPACalculator(getApplicationContext());
            String gpaGrade = gpa.checkMark(new Float(Math.round(courseAverage)));
            db.execSQL("UPDATE " + mCourseDH.TABLE_NAME + " SET " + mCourseDH.COL_4 + " = '" + courseAverage + "' WHERE " + mCourseDH.COL_3 + " = '" + mCourseCode + "';");
            setTitle("[" + mCourseCode + "] AVG: " + courseAverage + " / GPA: " + gpaGrade );
        }
    }
}
