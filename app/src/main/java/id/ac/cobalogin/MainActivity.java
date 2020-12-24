package id.ac.cobalogin;

import androidx.appcompat.app.AppCompatActivity;
import id.ac.cobalogin.Fragments.LoginFragment;
import id.ac.cobalogin.Models.Book;
import id.ac.cobalogin.SQLite.DbHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public ArrayList<Book> arrayList;
    private SharedPreferences userPref;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userPref = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        dbHelper = new DbHelper(this);
        boolean isLoggedIn = userPref.getBoolean("isLoggedIn", false);

        //execute anything in run method after 1,5 sec
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                isFirstTime();

                if(isLoggedIn){
                    startActivity(new Intent(getBaseContext(), DashboardActivity.class));
//                    getSupportFragmentManager().beginTransaction().replace(R.id.frameMainContainer, new DashboardActivity()).commit();
                    finish();
                }else{
                    startActivity(new Intent(getBaseContext(), AuthActivity.class));
                }
            }
        }, 1500);
    }

    private void isFirstTime() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("onBoard", Context.MODE_PRIVATE);
        boolean isFirst = sharedPreferences.getBoolean("isFirst", true);

        if(isFirst){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirst", false);
            editor.apply();
            Toast.makeText(this, "First Time", Toast.LENGTH_SHORT).show();

            getAllBook();
        }else{
            return;
        }
    }

    private void getAllBook() {
        arrayList = new ArrayList<>();
        Log.d("cobafirst_","ini get first1");

        StringRequest request = new StringRequest(Request.Method.GET, Constant.GET_ALL_BOOKS, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONArray array = new JSONArray(object.getString("book"));
                    for(int i=0; i<array.length(); i++){
                        JSONObject bookObject = array.getJSONObject(i);

                        Book book = new Book();
                        book.setId(bookObject.getInt("id"));
                        book.setCover(bookObject.getString("cover"));
                        book.setTitle(bookObject.getString("title"));
                        book.setFile_path(bookObject.getString("file_path"));
                        book.setAuthor(bookObject.getString("author"));
                        book.setDesc(bookObject.getString("description"));
                        book.setUser_id(bookObject.getInt("user_id"));

                        dbHelper.insertData(book);
                        Log.d("cobafirst1_","ini get first2");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("cobafirst2_","ini get first3");
            }
        }, error -> {
            error.printStackTrace();
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getBaseContext());
        queue.add(request);
    }
}

