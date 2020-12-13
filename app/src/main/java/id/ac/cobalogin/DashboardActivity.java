package id.ac.cobalogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity {
    private Button btnLogout, btnStore, btnSearch;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;
    private CircleImageView imgProfile;



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
