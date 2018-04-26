package com.example.android.popularmovies.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;

import com.example.android.popularmovies.R;

/**
 * Created by seridan on 27/03/2018.
 * This dialog fragment class handles the alert dialog that we will show
 * if there is no internet connection. To finish the activity it is necessary to get a context
 * from the activity that is being called. Fragment#getActivity() will return the instance of
 * the Activity (which is a Context) that the Fragment is attached to. Use it after the Fragment's
 * onAttach() is called.
 * https://stackoverflow.com/questions/15464263/passing-context-as-argument-of-dialogfragment
 * https://developer.android.com/reference/android/app/Fragment.html
 */

public class NoConnectionDialogFragment extends DialogFragment{

    Context mContext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder
                .setMessage(R.string.error_connection_message_main_activity)
                .setTitle(R.string.error_connection_tittle)
                .setPositiveButton(R.string.retry_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();//avoid the window leaks on finish the activity.

            }
        });
        return builder.create();
    }

    /**
     * Override onAttach method to get the context from the activity that is being called.
     * @param context the context that has been obtained from the activity, and then
     *                it has been used to finish the activity.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
