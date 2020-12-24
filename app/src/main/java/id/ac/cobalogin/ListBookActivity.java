package id.ac.cobalogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import id.ac.cobalogin.Adapter.ListBookAdapter;
import id.ac.cobalogin.Models.Book;

import android.content.SharedPreferences;
import android.os.Bundle;

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

public class ListBookActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    public ArrayList<Book> arrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListBookAdapter listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_book);

        sharedPreferences = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);

        init();
    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerView_listBook);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        swipeRefreshLayout = findViewById(R.id.swipeList_Book);

        getAllBooks();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllBooks();
            }
        });
    }

    private void getAllBooks() {
        arrayList = new ArrayList<>();
        swipeRefreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constant.GET_ALL_BOOKS, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONArray array = new JSONArray(object.getString("book"));
                    for (int i = 0; i < array.length(); i++){
                        JSONObject bookObject = array.getJSONObject(i);

                        Book book = new Book();
                        book.setId(bookObject.getInt("id"));
                        book.setTitle(bookObject.getString("title"));
                        book.setAuthor(bookObject.getString("author"));

                        arrayList.add(book);
                    }
                    listAdapter = new ListBookAdapter(getBaseContext(), arrayList);
                    recyclerView.setAdapter(listAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            swipeRefreshLayout.setRefreshing(false);
        }, error -> {
            error.printStackTrace();
            swipeRefreshLayout.setRefreshing(false);
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getBaseContext());
        queue.add(request);
    }
}