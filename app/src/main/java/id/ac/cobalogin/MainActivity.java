package id.ac.cobalogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        boolean isLoggedIn = userPref.getBoolean("isLoggedIn", false);

        //execute anything in run method after 1,5 sec
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isLoggedIn){
                    startActivity(new Intent(getBaseContext(), DashboardActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(getBaseContext(), AuthActivity.class));
                }
            }
        }, 1500);


//        setContentView(R.layout.activity_main);

        /*deklarasi dan inisalisasi variable user dgn label user dari layout activity_main*/
//        TextView user = findViewById(R.id.loggedUserMain);
//        TextView allUsers = findViewById(R.id.allUsers);

        /*set label user dgn username yg sedang login*/
//        user.setText(Preferences.getLoggedIn(getBaseContext()));
//        allUsers.setText(Preferences.getRegisteredUser(getBaseContext()));

        /*menuju form login jika diklik*/
//        findViewById(R.id.button_toLogin).setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                startActivity(new Intent(getBaseContext(),AuthActivity.class));
//            }
//        });

        /*set status dan user yg login menjadi default atau kosong di preferences lalu redirect ke login activity*/
//        findViewById(R.id.button_LogOutMain).setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                /*hapus status login dan redirect ke form login*/
//                Preferences.clearLoggedUser(getBaseContext());
//                startActivity(new Intent(getBaseContext(),LoginActivity.class));
//                finish();
//            }
//        });
    }
}

