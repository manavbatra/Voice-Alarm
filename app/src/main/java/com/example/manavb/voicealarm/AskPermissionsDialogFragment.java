package com.example.manavb.voicealarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;

public class AskPermissionsDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.overlay_permission_dialog_msg)
                .setTitle(R.string.overlay_permission_dialog_title)
                .setCancelable(true)
                .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent action = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        startActivity(action);
                    }
                });


        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Snackbar.make(MainActivity.mLayout, getString(R.string.permissions_dialog_cancelled_msg),
                Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
