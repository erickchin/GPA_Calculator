package com.example.erick.gpacalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by erick on 2016-01-10.
 */
public class GPACalculator {
    private final SharedPreferences sharedPrefs;

    public GPACalculator(Context context) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String checkMark(double average){
        if (average >= 90) {
            return sharedPrefs.getString("grade_a_plus", "4.33");
        }
        else if (average >= 85 && average <= 89) {
            return sharedPrefs.getString("grade_a", "4.00");
        }
        else if (average >= 80 && average <= 84) {
            return sharedPrefs.getString("grade_a_minus", "3.67");
        }
        else if (average >= 77 && average <= 79) {
            return sharedPrefs.getString("grade_b_plus", "3.33");
        }
        else if (average >= 73 && average <= 76) {
            return sharedPrefs.getString("grade_b", "3.00");
        }
        else if (average >= 70 && average <= 72) {
            return sharedPrefs.getString("grade_b_minus", "2.67");
        }
        else if (average >= 67 && average <= 69) {
            return sharedPrefs.getString("grade_c_plus", "2.33");
        }
        else if (average >= 63 && average <= 66) {
            return sharedPrefs.getString("grade_c", "2");
        }
        else if (average >= 60 && average <= 62) {
            return sharedPrefs.getString("grade_c_minus", "1.67");
        }
        else if (average >= 57 && average <= 59) {
            return sharedPrefs.getString("grade_d_plus", "1.33");
        }
        else if (average >= 53 && average <= 56) {
            return sharedPrefs.getString("grade_d", "1.00");
        }
        else if (average >= 50 && average <= 52) {
            return sharedPrefs.getString("grade_d_minus", ".67");
        }
        else {
            return "0";
        }
    }
}
