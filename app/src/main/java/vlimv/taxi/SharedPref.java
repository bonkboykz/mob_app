package vlimv.taxi;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by HP on 22-Mar-18.
 */

public class SharedPref {
    private static SharedPreferences sharedPreferences;
    private static String tagIsReg = "IS_REGISTERED";
    private static String tagNumber = "NUMBER";
    private static String tagId = "ID";
    private static String tagUserType = "USER_TYPE";
    private static String tagToken ="TOKEN";
    private static String tagUserSurname = "USER_SURNAME";
    private static String tagUserName = "USER_NAME";
    public static void saveNumber(Context context, String number) {
        sharedPreferences = context.getSharedPreferences(tagNumber, 0);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(tagNumber, number);
        ed.apply();
    }
    public static String loadNumber(Context context) {
        sharedPreferences = context.getSharedPreferences(tagNumber, Context.MODE_PRIVATE);
        String number = sharedPreferences.getString(tagNumber, "");
        return number;
    }

    public static void saveIsReg(Context context, boolean isReg) {
        sharedPreferences = context.getSharedPreferences(tagIsReg, 0);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putBoolean(tagIsReg, isReg);
        ed.apply();
    }
    public static boolean loadIsRegistered(Context context) {
        sharedPreferences = context.getSharedPreferences(tagIsReg, Context.MODE_PRIVATE);
        boolean isReg = sharedPreferences.getBoolean(tagIsReg, false);
        return isReg;
    }

    public static void saveUserId(Context context, String userId) {
        sharedPreferences = context.getSharedPreferences(tagId, 0);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(tagId, userId);
        ed.apply();
    }
    public static String loadUserId(Context context) {
        sharedPreferences = context.getSharedPreferences(tagId, Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString(tagId, "");
        return userId;
    }

    public static void saveUserType(Context context, String userType) {
        sharedPreferences = context.getSharedPreferences(tagUserType, 0);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(tagUserType, userType);
        ed.apply();
    }
    public static String loadUserType(Context context) {
        sharedPreferences = context.getSharedPreferences(tagUserType, Context.MODE_PRIVATE);
        String userType = sharedPreferences.getString(tagUserType, "");
        return userType;
    }

    public static void saveToken(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(tagToken, 0);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(tagToken, token);
        ed.apply();
    }
    public static String loadToken(Context context) {
        sharedPreferences = context.getSharedPreferences(tagToken, Context.MODE_PRIVATE);
        String userType = sharedPreferences.getString(tagToken, "");
        return userType;
    }

    public static void saveUserSurname(Context context, String userSurname) {
        sharedPreferences = context.getSharedPreferences(tagUserSurname, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(tagUserSurname, userSurname);
        ed.apply();
    }
    public static String loadUserSurname(Context context) {
        sharedPreferences = context.getSharedPreferences(tagUserSurname, Context.MODE_PRIVATE);
        String userSurname = sharedPreferences.getString(tagUserSurname, "");
        return userSurname;
    }
    public static void saveUserName(Context context, String userName) {
        sharedPreferences = context.getSharedPreferences(tagUserName, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(tagUserName, userName);
        ed.apply();
    }

    public static String loadUserName(Context context) {
        sharedPreferences = context.getSharedPreferences(tagUserName, Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString(tagUserName, "");
        return userName;
    }
}
