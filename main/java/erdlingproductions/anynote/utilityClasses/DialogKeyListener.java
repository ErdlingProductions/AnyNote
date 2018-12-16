package erdlingproductions.anynote.utilityClasses;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;

public class DialogKeyListener implements android.content.DialogInterface.OnKeyListener {

    // itemHasChanged holds the information, if the corresponding dialog has been altered
    private boolean itemHasChanged;
    // context holds the context of the dialog, usually the activity context
    private Context context;


    // when a change or touch in the dialog is registered, itemHasChanged is set to true
    public void setItemHasChangedTrue() {
        itemHasChanged = true;
    }

    // reset to false
    public void setItemHasChangedFalse() {
        itemHasChanged = false;
    }

    // the context is required for the alert dialog builder
    public void setContext(Context context) {
        this.context = context;
    }

    // get the status of textHasChanged
    public boolean getItemHasChanged(){
        return itemHasChanged;
    }





    // Declaration of the onKey Listener
    // if the back button is pressed, the method triggers
    @Override
    public boolean onKey(final DialogInterface dialogDetail, int keyCode, KeyEvent event) {
        // DialogInterface dialogDetail is declared final, otherwise it is not possible to call dialogDetail in
        // the AlertDialog OnClickListener

        // onKey is called twice, when pressing down and when releasing
        // the following statement filters the downpress away
        if (event.getAction() != KeyEvent.ACTION_DOWN) return true; // todo: what return value?


        // if the back button is pressed, stard the
        if (keyCode == KeyEvent.KEYCODE_BACK) {

           checkStatus(dialogDetail);
        }
        // todo: what return value?
        return true;
    }




    public void checkStatus(final DialogInterface dialogInterface) {
        // if nothing in the dialog has been altered, then the dialog can be dismissed
        if (itemHasChanged == false) {
            Log.v("DialogKeyListenerClass", "itemHasChanged = false");
            dialogInterface.dismiss();
        }
        // if the dialog has been altered, a alert dialog will pop up and will ask, if the
        // user really wants to discard the changes
        else {
            // create the OnClickListener for the AlertDialog, that will be attached afterwards in the AlertDialog Builder
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Info: dialog is referring to the AlertDialog

                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            // Do nothing
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            // Discard the Dialog dialogDetail
                            dialogInterface.dismiss();

                            // reset itemHasChanged
                            itemHasChanged = false;
                            break;
                    }
                }
            };

            // Initialize the AlertDialog Builder with the context provided by the method "setContext"
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            // The AlertDialog can not be cancelled by clicking outside the AlertDialog or by pressing back
            builder.setCancelable(false);
            // Set the message and the previosly defined click listeners of the AlertDialog
            builder.setMessage("Discard changes without saving?").setPositiveButton("Keep editing", dialogClickListener).
                    setNegativeButton("Discard", dialogClickListener).show();
        }
    }



}