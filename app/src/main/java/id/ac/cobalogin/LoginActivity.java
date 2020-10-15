package id.ac.cobalogin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity{
    private EditText mViewUser, mViewPass;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*inisialisasi variabel user dan pass dari layout login*/
        mViewUser = findViewById(R.id.et_userSignIn);
        mViewPass = findViewById(R.id.et_passSignIn);

        /*menjalankan method razia jika button SignIn di keyboard disentuh*/
        mViewPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL){
                    razia();
                    return true;
                }
                return false;
            }
        });

        /*menjalankan method razia jika button SignIn disentuh*/
        findViewById(R.id.button_SignIn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                razia();
            }
        });

        /*menuju registeractivity jika button SignUp disentuh*/
        findViewById(R.id.button_SignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),RegisterActivity.class));
            }
        });
    }

    /*menuju MainActivity jika data SignIn True*/
    @Override
    protected void onStart(){
        super.onStart();
        if(Preferences.getStatusUser(getBaseContext())){
            startActivity(new Intent(getBaseContext(),MainActivity.class));
            finish();
        }
    }

    /*cek input user dan pass dan memberikan akses ke mainactivity*/
    private void razia(){
        /*reset error dan restore default*/
        mViewUser.setError(null);
        mViewPass.setError(null);
        View fokus = null;
        boolean cancel = false;

        /*ambil value user dan pass dari form dengan variable string baru*/
        String user = mViewUser.getText().toString();
        String pass = mViewPass.getText().toString();

        /*jika form user kosong maka muncul error harus mengisi form atau jika form user tidak memenuhi kriteria maka set variable fokus dan error di Viewnya juga cancel menjadi true*/
        if(TextUtils.isEmpty(user)){
            mViewUser.setError("This Field is Required");
            fokus = mViewUser;
            cancel = true;
        }else if(!cekUser(user)){
            mViewUser.setError("This Name is not registered");
            fokus = mViewUser;
            cancel = true;
        }

        /*set error jika field pass kosong atau set error jika pass salah*/
        if(TextUtils.isEmpty(pass)){
            mViewPass.setError("This Field is Required");
            fokus = mViewPass;
            cancel = true;
        }else if(!cekPass(pass)){
            mViewPass.setError("This Password is Incorrect");
            fokus = mViewPass;
            cancel = true;
        }

        /*Jika cancel true maka variable fokus dapet fokus atau jika cancel false masuk ke MainActivity*/
        if (cancel) fokus.requestFocus();
        else masuk();
    }

    /*cek username apabila cocok dengan username yg terdaftar di preferences*/
    private boolean cekUser(String user){
        return user.equals(Preferences.getRegisteredUser(getBaseContext()));
    }

    /*cek pass apabila cocok dengan pass yg terdaftar di preferences*/
    private boolean cekPass(String pass){
        return pass.equals(Preferences.getRegisteredPass(getBaseContext()));
    }

    /*masuk ke MainActivity dan set status user login di preferences*/
    private void masuk(){
        Preferences.setLoggedIn(getBaseContext(),Preferences.getRegisteredUser(getBaseContext()));
        Preferences.setStatusUser(getBaseContext(),true);
        startActivity(new Intent(getBaseContext(),MainActivity.class));
        finish();
    }
}
