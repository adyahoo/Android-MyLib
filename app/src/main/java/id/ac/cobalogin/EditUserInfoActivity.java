package id.ac.cobalogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditUserInfoActivity extends AppCompatActivity {
    private TextInputLayout layoutName, layoutLastName;
    private TextInputEditText etName, etLastName;
    private Button btnSubmitEdit;
    private TextView txtSelectPhoto;
    private CircleImageView imgEditProfile;
    private Bitmap bitmap = null;
    private static final int GALLERY_EDIT_PROFILE = 5;
    private SharedPreferences userPref;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        init();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        userPref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        layoutName = findViewById(R.id.txtLayoutNameEditUserInfo);
        layoutLastName = findViewById(R.id.txtLayoutLastNameEditUserInfo);
        etName = findViewById(R.id.etNameEditUserInfo);
        etLastName = findViewById(R.id.etLastNameEditUserInfo);
        btnSubmitEdit = findViewById(R.id.btnSubmitEditUserInfo);
        txtSelectPhoto = findViewById(R.id.txtEditSelectPhoto);
        imgEditProfile = findViewById(R.id.imgEditUserInfo);

        userPref = getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
        String photo = userPref.getString("photo","");
        if(photo.equals("null")){
            Picasso.get().load(Constant.URL+"storage/"+"user.jpg").into(imgEditProfile);
        }else{
            Picasso.get().load(Constant.URL+"storage/profiles/"+photo).into(imgEditProfile);
        }

        etName.setText(userPref.getString("name",""));
        etLastName.setText(userPref.getString("lastname",""));

        txtSelectPhoto.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, GALLERY_EDIT_PROFILE);
        });

        btnSubmitEdit.setOnClickListener(v -> {
            if(validate()){
                editUserInfo();
            }
        });
    }

    private boolean validate() {
        if(etName.getText().toString().isEmpty()){
            layoutName.setErrorEnabled(true);
            layoutName.setError("Name is Required");
            return false;
        }
        if(etLastName.getText().toString().isEmpty()){
            layoutLastName.setErrorEnabled(true);
            layoutLastName.setError("Last Name is Required");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_EDIT_PROFILE && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            imgEditProfile.setImageURI(imgUri);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void editUserInfo() {
        dialog.setMessage("Updating");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Constant.SAVE_USER_INFO, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("name", etName.getText().toString().trim());
                    editor.putString("lastname", etLastName.getText().toString().trim());
                    String isiPhoto = object.getString("photo");
                    if (isiPhoto.equals("")){
                        editor.putString("photo", userPref.getString("photo",""));
                    }else{
                        editor.putString("photo", object.getString("photo"));
                    }
                    editor.apply();
                    Toast.makeText(this,"Update Success",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditUserInfoActivity.this, DashboardActivity.class));
                    finish();
                }
//                else{
//                    String errorData = null;
//                    try {
//                        errorData = object.getString("message");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Toast.makeText(this, errorData,Toast.LENGTH_SHORT).show();
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = userPref.getString("token","");
                HashMap <String, String> map = new HashMap<>();
                map.put("Authorization","Bearer"+token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap <String, String> map = new HashMap<>();
                map.put("name", etName.getText().toString().trim());
                map.put("lastname", etLastName.getText().toString().trim());
                map.put("photo", bitmapToString(bitmap));
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditUserInfoActivity.this);
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