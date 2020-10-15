package id.ac.cobalogin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity{
    private EditText mViewUser, mViewPass, mViewConfPass;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /*inisialisasi variable dgn form user,pass,confpass dari layout activity_register*/
        mViewUser = findViewById(R.id.et_userRegis);
        mViewPass = findViewById(R.id.et_passRegis);
        mViewConfPass = findViewById(R.id.et_confPassRegis);

        /*run method razia jika button signup di keyboard disentuh*/
        mViewConfPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL){
                    razia();
                    return true;
                }
                return false;
            }
        });

        /*run method razia jika button signup disentuh*/
        findViewById(R.id.button_SignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                razia();
            }
        });
    }

    /*cek input user dan berikan akses ke MainActivity*/
    private void razia(){
        /*reset error dan set fokus jadi default*/
        mViewUser.setError(null);
        mViewPass.setError(null);
        mViewConfPass.setError(null);
        View fokus = null;
        boolean cancel = false;

        /*ambil value dari form dan dimasukkan ke variable string*/
        String user = mViewUser.getText().toString();
        String pass = mViewPass.getText().toString();
        String confPass = mViewConfPass.getText().toString();

        /*apabila field username empty maka tampilkan error harus diisi atau jika username sudah ada munculkan error*/
        if(TextUtils.isEmpty(user)){
            mViewUser.setError("This Field is Required");
            fokus = mViewUser;
            cancel = true;
        }else if(cekUser(user)){
            mViewUser.setError("This Username is Already Registered");
            fokus = mViewUser;
            cancel = true;
        }

        /*sama seperti di atas tapi cek password*/
        if(TextUtils.isEmpty(pass)){
            mViewPass.setError("This Field is Required");
            fokus = mViewPass;
            cancel = true;
        }else if(!cekPass(pass,confPass)){
            mViewConfPass.setError("This Confirm Password is Incorrect");
            fokus = mViewConfPass;
            cancel = true;
        }

        /*jika cancel=true, variable fokus=fokus. jika cancel=false kembali ke LoginActivity dan set user dan pass menjadi terdaftar*/
        if(cancel){
            fokus.requestFocus();
        }else{
            Preferences.setRegisteredUser(getBaseContext(),user);
            Preferences.setRegisteredPass(getBaseContext(),pass);
            finish();
        }
    }

    /*cek apakah pass dan confpass sama*/
    private boolean cekPass(String pass, String confPass){
        return pass.equals(confPass);
    }

    /*cek username sama dengan data pada preferences*/
    private boolean cekUser(String user){
        return user.equals(Preferences.getRegisteredUser(getBaseContext()));
    }
}
