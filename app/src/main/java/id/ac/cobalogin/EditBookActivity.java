package id.ac.cobalogin;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import id.ac.cobalogin.Models.Book;
import id.ac.cobalogin.SQLite.DbHelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditBookActivity extends AppCompatActivity {
    private int position = 0, bookid = 0;
    private TextInputLayout layoutTitle, layoutAuthor, layoutDesc;
    private TextInputEditText etTitle, etAuthor, etDesc;
    private Button btnSubmit;
    private TextView txtCover;
    private CircleImageView imgCover;
    private Bitmap bitmap = null;
    private static final int GALLERY_EDIT_COVER = 5;
    private ProgressDialog dialog;
    private DbHelper dbBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);
        init();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        layoutTitle = findViewById(R.id.txtLayoutEditTitle);
        layoutAuthor = findViewById(R.id.txtLayoutEditAuthor);
        layoutDesc = findViewById(R.id.txtLayoutEditDesc);
        etTitle = findViewById(R.id.etEditTitle);
        etAuthor = findViewById(R.id.etEditAuthor);
        etDesc = findViewById(R.id.etEditDesc);
        btnSubmit = findViewById(R.id.btnSubmitEditBook);
        txtCover = findViewById(R.id.txtEditCover);
        imgCover = findViewById(R.id.coverEditBook);

        dbBook = new DbHelper(this);

        position = getIntent().getIntExtra("position",0);
        bookid = getIntent().getIntExtra("bookId",0);
        etTitle.setText(getIntent().getStringExtra("title"));
        etAuthor.setText(getIntent().getStringExtra("author"));
        etDesc.setText(getIntent().getStringExtra("desc"));

        txtCover.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_EDIT_COVER);
        });

        btnSubmit.setOnClickListener(v -> {
            if(validate()){
                editBook();
//                editBookLite();
            }
        });
    }

    private boolean validate() {
        if(etTitle.getText().toString().isEmpty()){
            layoutTitle.setErrorEnabled(true);
            layoutTitle.setError("Book's Title is Required");
            return false;
        }
        if(etAuthor.getText().toString().isEmpty()){
            layoutAuthor.setErrorEnabled(true);
            layoutAuthor.setError("Author Name is Required");
            return false;
        }
        if(etDesc.getText().toString().isEmpty()){
            layoutDesc.setErrorEnabled(true);
            layoutDesc.setError("Description is Required");
            return false;
        }
        return true;
    }

    private void editBookLite() {
        String titleLite = etTitle.getText().toString();
        String authorLite = etAuthor.getText().toString();
        String descLite = etDesc.getText().toString();
        String coverLite = "";
        String pathLite = "";

        Book book = new Book();
        book.setId(bookid);
        book.setTitle(titleLite);
        book.setAuthor(authorLite);
        book.setDesc(descLite);
        book.setCover(coverLite);
        book.setFile_path(pathLite);

        boolean masuk = dbBook.updateData(book);

        if(masuk){
            Toast.makeText(this,"Editing Book from SQLite Success", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Editing Book from SQLite Failed", Toast.LENGTH_SHORT).show();
        }
        startActivity(new Intent(EditBookActivity.this, DashboardActivity.class));
        finish();

    }

    private void editBook() {
        dialog.setMessage("Saving..");
        dialog.show();
//        Log.d("coba_editasasd", "editUserInfo: gagal");

        StringRequest request = new StringRequest(Request.Method.POST, Constant.EDIT_BOOK, response -> {

            try {
                JSONObject object = new JSONObject(response);
//                Log.d("coba_editasasd", "editUserInfo: sukses");
                if(object.getBoolean("success")){
//                    Log.d("coba_editasasd", "editUserInfo: suksesedit");
                    Toast.makeText(this, "Editing yoqur Book Success",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditBookActivity.this, DashboardActivity.class));
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        },error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
                String token = sharedPreferences.getString("token","");
                HashMap<String,String> header = new HashMap<>();
                header.put("Authorization","Bearer"+token);
                return header;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap <String,String> map = new HashMap<>();
                map.put("id", bookid+"");
                map.put("title", etTitle.getText().toString().trim());
                map.put("author", etAuthor.getText().toString().trim());
                map.put("description", etDesc.getText().toString().trim());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditBookActivity.this);
        queue.add(request);
    }


}