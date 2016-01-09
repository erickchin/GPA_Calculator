package com.example.erick.gpacalculator;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CoursesFragment extends Fragment {

    private ArrayAdapter<String> CourseAdapter;

    public CoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        CourseAdapter =
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
        listView.setAdapter(CourseAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String course = CourseAdapter.getItem(position);
                // Gets only the course code
                String courseCode = course.substring(course.indexOf("[") + 1, course.indexOf("]"));
                Intent intent = new Intent(getActivity(), CourseActivity.class)
                        .putExtra("COURSE_CODE", courseCode);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getCourses();
    }

    private void getCourses() {
        CourseAdapter.clear();
        CourseDatabaseHelper databaseHelper = new CourseDatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + databaseHelper.COL_2 + ", " + databaseHelper.COL_3 + " FROM " + databaseHelper.TABLE_NAME, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String courseName = cursor.getString(cursor.getColumnIndex(databaseHelper.COL_2));
                    String courseCode = cursor.getString(cursor.getColumnIndex(databaseHelper.COL_3));
                    CourseAdapter.add("[" + courseCode + "] " + courseName);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
    }

    // TODO: Get total average of courses
    private void calculateTotalAverage()
    {

    }
}
