
package id.ac.cobalogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import id.ac.cobalogin.Models.Book;
import id.ac.cobalogin.SQLite.DbHelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StoreBookActivity extends AppCompatActivity {
    private TextInputLayout layoutTitle, layoutAuthor, layoutDesc;
    private TextInputEditText etTitle, etAuthor, etDesc;
    private Button btnSubmit;
    private TextView txtSelectCover;
    private CircleImageView circleImageView;
    private static final int GALLERY_ADD_COVER = 1;
    private Bitmap bitmap = null;
    private SharedPreferences userPref;
    private ProgressDialog dialog;
    private DbHelper dbBook;
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_book);
        init();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        layoutTitle = findViewById(R.id.txtLayoutTitle);
        layoutAuthor = findViewById(R.id.txtLayoutAuthor);
        layoutDesc = findViewById(R.id.txtLayoutDesc);
        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etDesc = findViewById(R.id.etDesc);
        btnSubmit = findViewById(R.id.btnSubmitStoreBook);
        txtSelectCover = findViewById(R.id.txtSelectCover);
        circleImageView = findViewById(R.id.coverBook);
        dbBook = new DbHelper(this);

        txtSelectCover.setOnClickListener(v-> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, GALLERY_ADD_COVER);
        });
        
        btnSubmit.setOnClickListener(v -> {
            if(validate()){
                FirebaseMessaging.getInstance().subscribeToTopic("storing_book");
                storeBook();
//                storeBookLite();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_ADD_COVER && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            circleImageView.setImageURI(imgUri);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void storeBookLite() {
        String titleLite = etTitle.getText().toString();
        String authorLite = etAuthor.getText().toString();
        String descLite = etDesc.getText().toString();
        String coverLite = bitmapToString(bitmap);
        String pathLite = "";
        int userIdLite = userPref.getInt("id",0);

        boolean masuk = dbBook.insertData(new Book(userIdLite, titleLite, authorLite, coverLite, pathLite, descLite));

        if(masuk){
            Toast.makeText(this, "Storing to SQLite is Completed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Storing to SQLite is Failed", Toast.LENGTH_SHORT).show();
        }
        startActivity(new Intent(StoreBookActivity.this, DashboardActivity.class));
        finish();
    }

    private void storeBook() {
//        dialog.setMessage("Storing");
//        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constant.STORE_BOOK ,response->{
            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONObject book = object.getJSONObject("Book");
                    String isiCover = book.getString("cover");

                    Toast.makeText(this, "Storing Your Book Success", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            dialog.dismiss();
        }, error -> {
            error.printStackTrace();
//            dialog.dismiss();
        }){
            //add token to header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token","");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization","Bearer"+token);
                return map;
            }
            //add params

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("title", etTitle.getText().toString().trim());
                map.put("author", etAuthor.getText().toString().trim());
                map.put("description", etDesc.getText().toString().trim());
                map.put("cover", bitmapToString(bitmap));
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(StoreBookActivity.this);
        queue.add(request);
    }

    private String bitmapToString(Bitmap bitmap) {
        if(bitmap!=null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte [] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array,Base64.DEFAULT);
        }
        return "";
    }
}