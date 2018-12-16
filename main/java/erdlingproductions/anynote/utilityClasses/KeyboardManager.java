package erdlingproductions.anynote.utilityClasses;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardManager {

    // declare an InputMethodManager and an Activity that can be used throughout the class
    private InputMethodManager inputMethodManager;
    private Activity activity;



    // initialize the class with an Activity
    public KeyboardManager(Activity activity) {
        inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        this.activity = activity;
    }



    // if an object of the class is used in a different activity and the object remains the same, the activity can be changed
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

        //todo activity is not needed anymore

    public void hideKeyboard() {
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    public void showKeyboard() {
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
