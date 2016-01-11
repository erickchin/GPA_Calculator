package com.example.erick.gpacalculator.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.erick.gpacalculator.GPACalculator;
import com.example.erick.gpacalculator.activity.AddCourseActivity;
import com.example.erick.gpacalculator.activity.OpenCourseActivity;
import com.example.erick.gpacalculator.R;
import com.example.erick.gpacalculator.data.CourseDatabaseHelper;
import com.example.erick.gpacalculator.data.EvaluationDatabaseHelper;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CoursesFragment extends Fragment {

    private ArrayAdapter<String> mCourseAdapter;
    private CourseDatabaseHelper mCourseDH;
    private EvaluationDatabaseHelper mEvaluationDH;
    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList<String> codeList = new ArrayList<String>();

    public CoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        mCourseDH = new CourseDatabaseHelper(getContext());
        mEvaluationDH = new EvaluationDatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCourseAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_course, // The name of the layout ID.
                        R.id.list_item_course_textview, // The ID of the textview to populate.
                        new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_course, container, false);


        // Setup FloatingActionButton
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddCourseActivity.class);
                startActivity(intent);
            }
        });

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_courses);
        listView.setAdapter(mCourseAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String course = mCourseAdapter.getItem(position);
                // Gets only the course code
                Intent intent = new Intent(getActivity(), OpenCourseActivity.class)
                        .putExtra("COURSE_CODE", codeList.get(position));
                intent.putExtra("COURSE_NAME", nameList.get(position));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                mCourseDH.deleteData(nameList.get(position), codeList.get(position));
                mEvaluationDH.deleteAllCourseCode(codeList.get(position));
                Toast.makeText(getContext(), nameList.get(position) + " evaluation deleted. ", Toast.LENGTH_LONG).show();
                getCourses();
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getCourses();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getCourses();
    }

    private void getCourses() {
        mCourseAdapter.clear();
        codeList.clear();
        nameList.clear();
        CourseDatabaseHelper databaseHelper = new CourseDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        double average = 0;
        int gradedCourses = 0;
        GPACalculator gpa = new GPACalculator(getContext());
        Cursor cursor = db.rawQuery("SELECT " + databaseHelper.COL_2 + ", " + databaseHelper.COL_3 + ", " + databaseHelper.COL_4 + " FROM " + databaseHelper.TABLE_NAME, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String courseName = cursor.getString(cursor.getColumnIndex(databaseHelper.COL_2));
                    String courseCode = cursor.getString(cursor.getColumnIndex(databaseHelper.COL_3));
                    String courseAverage = cursor.getString(cursor.getColumnIndex(databaseHelper.COL_4));
                    boolean hasEvaluation = hasEvaluations(courseCode);
                    if (courseAverage == null || !hasEvaluation) {
                        mCourseAdapter.add("[" + courseCode + "] " + courseName);
                    }
                    else {
                        String grade = gpa.checkMark(new Float(Math.round(Double.parseDouble(courseAverage))));
                        gradedCourses++;
                        average += Double.parseDouble(courseAverage);
                        mCourseAdapter.add("[" + courseCode + "] " + courseName + "\r\nAverage: " + courseAverage + " / GPA: " + grade);
                    }
                    nameList.add(courseName);
                    codeList.add(courseCode);


                } while (cursor.moveToNext());
            }
        }
        TextView textView = (TextView) getActivity().findViewById(R.id.textview_summary);

        if (gradedCourses > 0) {
            average = (double) Math.round(average / gradedCourses * 100) / 100;
            String grade = gpa.checkMark(new Float(Math.round(average)));
            textView.setText("\r\nTotal AVG: " + average + " / GPA: " + grade);
        }
        else
        {
            textView.setText("\r\nAdd courses and evaluations");
        }
        cursor.close();
    }

    public boolean hasEvaluations(String code) {
        EvaluationDatabaseHelper databaseHelper = new EvaluationDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + databaseHelper.TABLE_NAME + " WHERE " + databaseHelper.COL_3 + " = '" + code + "'", null);
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }
}
