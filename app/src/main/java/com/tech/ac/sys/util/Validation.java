package com.tech.ac.sys.util;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import com.tech.ac.sys.R;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Anil chourasiya on 2/27/2016.
 */
public class Validation {

    private final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";
    private final String FULLNAME_PATTERN = "^[\\p{L} .'-]+$";
    private final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";

    private Pattern pattern;
    private Matcher matcher;
    private Context context;

    public Validation(Context context){
        this.context = context;
    }

    private String getString(TextView textView){
        String getValue = textView.getText().toString().trim();
        return getValue;
    }

    // "use isEmpty instead of the isNullValue"
    public boolean isNullValue(TextView textView){
        if(getString(textView).isEmpty()){
            return false;
        }
        return true;
    }

    public boolean isEmpty(TextView textView){
        if(getString(textView).isEmpty()){
            textView.setError(("field_cant_be_empty"));
            textView.requestFocus();
            return true;
        }
        return false;
    }

    public boolean isUserNameValid(TextView textView){
        pattern = Pattern.compile(USERNAME_PATTERN);
        matcher = pattern.matcher(getString(textView));
        boolean bool = matcher.matches();
        if(!bool){
            textView.setError("enter_valid_userName");
            textView.requestFocus();
        }
        return bool;
    }

    public boolean isFullNameValid(TextView textView){
        Pattern pattern = Pattern.compile(FULLNAME_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(getString(textView));
        return matcher.find();
    }

    public boolean isPasswordValid(EditText editText) {
        String getValue = editText.getText().toString().trim();
        return getValue.length() > 3;
    }

    public boolean isEmailValid(TextView textView) {
        boolean bool = android.util.Patterns.EMAIL_ADDRESS.matcher(getString(textView)).matches();
        if(!bool){
            //textView.setError(context.getString(R.string.enter_valid_email));
        }
        return bool;
    }

}
