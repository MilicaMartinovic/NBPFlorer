package com.example.milica.nbp_florer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Milica on 08-Feb-19.
 */

public class CustomReplyDialog extends AppCompatDialogFragment {

    CustomReplyDialogListener listener;
    private EditText editText;


    public static CustomReplyDialog newInstance(String msg) {
        CustomReplyDialog customReplyDialog = new CustomReplyDialog();
        Bundle bundle = new Bundle();
        bundle.putString("comm_id", msg);
        customReplyDialog.setArguments(bundle);

        return customReplyDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_reply_dialog, null);

        final String comm_id = getArguments().getString("comm_id");

        builder.setView(view)
                .setTitle("Type your comment reply")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String comment = editText.getText().toString();
                        listener.applyResult(comment,comm_id);
                        dialog.dismiss();
                    }
                });
            editText = view.findViewById(R.id.etReplyComment);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (CustomReplyDialogListener) context;
        }
        catch (Exception e) {
            Toast.makeText(getContext(), "asd", Toast.LENGTH_SHORT).show();
        }
    }

    public interface  CustomReplyDialogListener {
        void applyResult(String comment, String comm_id);
    }

}
