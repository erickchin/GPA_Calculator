package com.example.erick.gpacalculator.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.erick.gpacalculator.R;
import com.example.erick.gpacalculator.data.EvaluationDatabaseHelper;

public class AddEvaluationActivity extends AppCompatActivity {

    private EditText mEditName, mEditMark, mEditWeight;
    private EvaluationDatabaseHelper mEvaluationDH;
    private View mProgressView;
    private View mEvaluationFormView;
    private String mCourseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_evaluation);

        // Get object IDs on activity
        mEditName = (EditText)findViewById(R.id.text_name);
        mEditMark = (EditText)findViewById(R.id.text_mark);
        mEditWeight = (EditText)findViewById(R.id.text_weight);
        mEvaluationFormView = findViewById(R.id.evaluation_form);
        mProgressView = findViewById(R.id.evaluation_progress);
        Button buttonSubmit = (Button)findViewById(R.id.button_submit);
        // Check when the user clicks on the button
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvaluation();
            }
        });

        // Get the data sent from previous activity (OpenCourseActivity)
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mCourseCode = bundle.getString("COURSE_CODE");
        }
    }

    // Add a evaluation into evaluation.db with a specific course code
    public void addEvaluation() {
        // Reset errors.
        mEditName.setError(null);
        mEditMark.setError(null);
        mEditWeight.setError(null);

        // Store values at the time of the login attempt.
        String name = mEditName.getText().toString();
        String markString = mEditMark.getText().toString();
        String weightString = mEditWeight.getText().toString();

        boolean canConvert = true;
        boolean cancel = false;
        View focusView = null;
        double mark = 0;
        double weight = 0;

        // Check for if EditText is empty
        if (TextUtils.isEmpty(name)) {
            mEditName.setError(getString(R.string.error_field_required));
            focusView = mEditName;
            cancel = true;
        }

        // Check for if EditText is empty
        if (TextUtils.isEmpty(markString)) {
            mEditMark.setError(getString(R.string.error_field_required));
            focusView = mEditMark;
            cancel = true;
            canConvert = false;
        }

        // Check for if EditText is empty
        if (TextUtils.isEmpty(weightString)) {
            mEditWeight.setError(getString(R.string.error_field_required));
            focusView = mEditWeight;
            cancel = true;
            canConvert = false;
        }

        // Check if you can convert the inputs since users can leave it as null
        if (canConvert) {
            // Convert to Double
            mark = Double.parseDouble(markString);
            weight = Double.parseDouble(weightString);

            // Check if the mark is not valid
            if (!correctMark(mark)) {
                mEditMark.setError(getString(R.string.error_mark));
                focusView = mEditMark;
                cancel = true;
            }

            // CHeck if weight is not valid
            if (!correctWeight(weight)) {
                mEditWeight.setError(getString(R.string.error_weight));
                focusView = mEditWeight;
                cancel = true;
            }
        }

        // If cancel is true, it will create focus to the latest error
        if (cancel) {
            focusView.requestFocus();
        }
        // If not, it will add the evaluation
        else {
            mEvaluationDH = new EvaluationDatabaseHelper(AddEvaluationActivity.this);
            mEvaluationDH.insertEvaluation(name, mCourseCode, mark, weight);
            showProgress(true);
            // Pop-up message
            Toast.makeText(AddEvaluationActivity.this, "Successfully added an evaluation!", Toast.LENGTH_LONG).show();
            // End the activity
            finish();
        }
    }

    // Check if mark is valid
    public boolean correctMark(double mark) {
        // If mark is not between 0-100, it will return false
        if (mark > 100 || mark < 0) {
            return false;
        }
        // Is between 0-100, return true
        else
        {
            return true;
        }
    }

    // Checks if the total weight is less than 100 and if the weight is valid
    public boolean correctWeight(double weight) {
        // Check if weight is 1-100
        if (weight > 100 || weight <= 0) {
            return false;
        }
        else {
            // Set totalWeight to the inputted weight
            double totalWeight = weight;
            // Get the database
            EvaluationDatabaseHelper databaseHelper = new EvaluationDatabaseHelper(this);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            // Selects the weight column from evaluation table where the code is equal to the course code
            Cursor cursor = db.rawQuery("SELECT " + databaseHelper.COL_5 + " FROM " + databaseHelper.TABLE_NAME + " WHERE " + databaseHelper.COL_3 + "= '" + mCourseCode + "'", null);

            if (cursor != null) {
                // Iterate through all the values taken
                if (cursor.moveToFirst()) {
                    do {
                        // Convert value to double and add to totalWeight
                        double w = Double.parseDouble(cursor.getString(cursor.getColumnIndex(databaseHelper.COL_5)));
                        totalWeight += w;
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            // If totalWeight is less than 100, it would satisfy the condition that user can input this evaluation
            if (totalWeight <= 100) {
                return true;
            }
            else {
                return false;
            }
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

            mEvaluationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mEvaluationFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mEvaluationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mEvaluationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
