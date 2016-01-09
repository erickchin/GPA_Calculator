package com.example.erick.gpacalculator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddCourseActivity extends AppCompatActivity {

    private EditText mEditName, mEditCode;
    private View mProgressView, mCourseFormView;
    private CourseDatabaseHelper mCourseDH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        // Get the object IDs on the activity
        mEditName = (EditText)findViewById(R.id.text_name);
        mEditCode = (EditText)findViewById(R.id.text_code);
        mCourseFormView = findViewById(R.id.course_form);
        mProgressView = findViewById(R.id.course_progress);
        Button buttonSubmit = (Button)findViewById(R.id.button_submit);

        // Create a listener for the button to recieve user input
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourse();
            }
        });
    }

    // Add courses into the courses.db with the user's input
    public void addCourse() {
        // Reset errors.
        mEditName.setError(null);
        mEditCode.setError(null);

        boolean cancel = false;
        View focusView = null;
        // Store values at the time of the login attempt.
        String name = mEditName.getText().toString();
        String code = mEditCode.getText().toString();

        // Check if EditText is empty
        if (TextUtils.isEmpty(name)) {
            mEditName.setError(getString(R.string.error_field_required));
            focusView = mEditName;
            cancel = true;
        }

        // Check if EditText is empty
        if (TextUtils.isEmpty(code)) {
            mEditCode.setError(getString(R.string.error_field_required));
            focusView = mEditCode;
            cancel = true;
        }

        // If cancel is true, it will create focus to the latest error
        if (cancel) {
            focusView.requestFocus();
        // Adds the course into the database
        } else {
            // Access the database
            mCourseDH = new CourseDatabaseHelper(AddCourseActivity.this);
            mCourseDH.insertCourse(name, code);
            showProgress(true);
            // Pop-up message
            Toast.makeText(AddCourseActivity.this, "Successfully added a course!", Toast.LENGTH_LONG).show();
            // End the activity
            finish();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mCourseFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mCourseFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCourseFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mCourseFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
