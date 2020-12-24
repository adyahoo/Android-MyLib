package id.ac.cobalogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import id.ac.cobalogin.Adapter.BooksAdapter;
import id.ac.cobalogin.Models.Book;
import id.ac.cobalogin.Models.User;
import id.ac.cobalogin.SQLite.DbHelper;

public class DashboardActivity extends AppCompatActivity {
    private Button btnLogout, btnStore, btnSearch;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;
    private CircleImageView imgProfile;
    private View view;
    private RecyclerView recyclerView;
    public ArrayList<Book> arrayList;
    public static ArrayList<Book> bukuBakcup = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private BooksAdapter booksAdapter, booksBackupAdapter;
    private DbHelper dbBook = new DbHelper(this);

    public DashboardActivity(){}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name","");
        String lastName = sharedPreferences.getString("lastname","");
        String photo = sharedPreferences.getString("photo","");
        TextView user = findViewById(R.id.loggedUser);
        user.setText(name+" "+lastName);

        imgProfile = findViewById(R.id.imgUserDashboard);
        if(photo.equals("null")){
            Picasso.get().load(Constant.URL+"storage/"+"user.jpg").into(imgProfile);
        }else{
            Picasso.get().load(Constant.URL+"storage/profiles/"+photo).into(imgProfile);
        }

        dialog = new ProgressDialog(getBaseContext());
        dialog.setCancelable(false);
        btnLogout = findViewById(R.id.btnLogOut);
        btnLogout.setOnClickListener(v->{
            logout();
        });

        imgProfile.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, EditUserInfoActivity.class));
        });

        btnStore = findViewById(R.id.btnStoreBook);
        btnStore.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, StoreBookActivity.class));
        });

        btnSearch = findViewById(R.id.btnSearchBook);
        btnSearch.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, ListBookActivity.class));
        });

        init();
    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerView_book);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        swipeRefreshLayout = findViewById(R.id.swipeMain_Book);

        getBooks();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBooks();
            }
        });
    }

    private void getBooks() {
        arrayList = new ArrayList<>();
        swipeRefreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constant.GET_BOOKS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("book"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject bookObject = array.getJSONObject(i);

                        Book book = new Book();
                        book.setId(bookObject.getInt("id"));
                        book.setAuthor(bookObject.getString("author"));
                        book.setTitle(bookObject.getString("title"));
                        book.setCover(bookObject.getString("cover"));
//                        book.setFile_path(bookObject.getString("file_path"));
                        book.setDesc(bookObject.getString("description"));
                        book.setUser_id(bookObject.getInt("user_id"));

                        arrayList.add(book);
                    }
                    booksAdapter = new BooksAdapter(getBaseContext(), arrayList);
                    recyclerView.setAdapter(booksAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            swipeRefreshLayout.setRefreshing(false);

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NetworkError || error instanceof AuthFailureError || error instanceof NoConnectionError || error instanceof TimeoutError){
                    swipeRefreshLayout.setRefreshing(false);
                    int userIdLite = sharedPreferences.getInt("id",0);
                    bukuBakcup = (ArrayList<Book>) dbBook.findUserBook(userIdLite);
                    booksBackupAdapter = new BooksAdapter(bukuBakcup, getApplicationContext());
                    recyclerView.setAdapter(booksBackupAdapter);
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token","");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getBaseContext());
        queue.add(request);
    }

    private void logout(){
//        dialog.setMessage("Logging Out");
//        dialog.show();
//        dialog.dismiss();
        StringRequest request = new StringRequest(Request.Method.GET, Constant.LOGOUT, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"Log Out Success",Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(getBaseContext(), AuthActivity.class));
                    startActivity(new Intent(DashboardActivity.this, AuthActivity.class));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
//            dialog.dismiss();
        }, error -> {
            //error if fail to connect
            error.printStackTrace();
//            dialog.dismiss();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token","");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization","Bearer"+token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getBaseContext());
        queue.add(request);

    }
}
