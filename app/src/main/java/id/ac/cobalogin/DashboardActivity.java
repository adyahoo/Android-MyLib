package id.ac.cobalogin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {
    private Button btnLogout;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        TextView user = findViewById(R.id.loggedUser);
//        user.setText();

        dialog = new ProgressDialog(getBaseContext());
        dialog.setCancelable(false);
        btnLogout = findViewById(R.id.button_LogOut);
        btnLogout.setOnClickListener(v->{
            logout();
        });
    }

    private void logout(){
        dialog.setMessage("Logging Out");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.LOGOUT, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){

                }
            }catch (){

            }
        });
    }
}
