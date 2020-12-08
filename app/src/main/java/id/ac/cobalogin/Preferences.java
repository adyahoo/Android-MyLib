package id.ac.cobalogin;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    /*melakukan deklarasi key-data berupa string, untuk wadah menyimpan data, sehingga setiap data memiliki key berbeda*/
    static final String KEY_USER_TEREGIS ="user", KEY_PASS_TEREGIS ="pass";
    static final String KEY_USER_SEDANG_LOGIN ="user_sedang_login";
    static final String KEY_STATUS_SEDANG_LOGIN ="status_login";

    /*deklarasi shared preference berdasarkan parameter context*/
    private static SharedPreferences getSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /*deklarasi edit preference, dengan key KEY_USER_TEREGIS dan parameter username*/
    public static void setRegisteredUser(Context context, String username){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_USER_TEREGIS,username);
        editor.apply();
    }

    /*return value dari key KEY_USER_TEREGIS berupa string*/
    public static String getRegisteredUser(Context context){
        return getSharedPreferences(context).getString(KEY_USER_TEREGIS,"");
    }

    /*deklarasi edit preference, dengan key KEY_PASS_TEREGIS dan parameter password*/
    public static void setRegisteredPass(Context context, String password){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_PASS_TEREGIS,password);
        editor.apply();
    }

    /*return value dari key KEY_PASS_TEREGIS berupa string*/
    public static String getRegisteredPass(Context context){
        return getSharedPreferences(context).getString(KEY_PASS_TEREGIS,"");
    }

    /*deklarasi edit preferences, dengan key KEY_USER_SEDANG_LOGIN dan parameter username*/
    public static void setLoggedIn(Context context, String username){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_USER_SEDANG_LOGIN,username);
        editor.apply();
    }

    /*return value dari key KEY_USER_SEDANG_LOGIN berupa string*/
    public static String getLoggedIn(Context context){
        return getSharedPreferences(context).getString(KEY_USER_SEDANG_LOGIN,"");
    }

    /*deklarasi edit preference, dengan key KEY_STATUS_SEDANG_LOGIN dan parameter status*/
    public static void setStatusUser(Context context, boolean status){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(KEY_STATUS_SEDANG_LOGIN,status);
        editor.apply();
    }

    /*return value dari key KEY_STATUS_SEDANG_LOGIN berupa string*/
    public static boolean getStatusUser(Context context){
        return getSharedPreferences(context).getBoolean(KEY_STATUS_SEDANG_LOGIN,false);
    }

    /*deklarasi edit preferences dan menghapus data untuk mengembalikan data user dan status login menjadi default*/
    public static void clearLoggedUser(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(KEY_USER_SEDANG_LOGIN);
        editor.remove(KEY_STATUS_SEDANG_LOGIN);
        editor.apply();
    }
}
