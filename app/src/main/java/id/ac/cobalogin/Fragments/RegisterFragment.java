package id.ac.cobalogin.Fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import id.ac.cobalogin.Constant;
import id.ac.cobalogin.R;

public class RegisterFragment extends Fragment {
    private View view;
    private TextInputLayout layoutConfPass, layoutName, layoutEmail, layoutPass;
    private TextInputEditText etConfPass, etName, etEmail, etPass;
    private Button btnRegis;
    private TextView toLogin;
    private ProgressDialog dialog;

    public RegisterFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_register,container,false);
        init();
        return view;
    }

    private void init() {
        layoutEmail = view.findViewById(R.id.txtLayoutEmailRegister);
        layoutPass = view.findViewById(R.id.txtLayoutPassRegister);
        layoutConfPass = view.findViewById(R.id.txtLayoutConfPassRegister);
        layoutName = view.findViewById(R.id.txtLayoutNameRegister);
        etEmail = view.findViewById(R.id.etEmailRegister);
        etPass = view.findViewById(R.id.etPassRegister);
        etConfPass = view.findViewById(R.id.etConfPassRegister);
        etName = view.findViewById(R.id.etNameRegister);
        btnRegis = view.findViewById(R.id.btnRegister);
        toLogin = view.findViewById(R.id.toLogin);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        toLogin.setOnClickListener(v->{
            //change fragment
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer,new LoginFragment()).commit();
        });

        btnRegis.setOnClickListener(v->{
            //validate field first
            if(validate()){
                register();
            }
        });

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!etName.getText().toString().isEmpty()){
                    layoutName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!etEmail.getText().toString().isEmpty()){
                    layoutEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etPass.getText().toString().length()>5){
                    layoutPass.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etConfPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etConfPass.getText().toString().equals(etPass.getText().toString())){
                    layoutPass.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void register() {
        dialog.setMessage("Registering");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.REGISTER, response->{
            //get response if connection success
            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONObject user = object.getJSONObject("user");
                    //make sharedpreferences for user
                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user",getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("token",object.getString("token"));
                    editor.putString("name",user.getString("name"));
                    editor.apply();
                    //if successs
                    Toast.makeText(getContext(),"Register Success",Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer,new LoginFragment()).commit();
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
            dialog.dismiss();
        }, error -> {
            //error if fail to connect
            error.printStackTrace();
            dialog.dismiss();
        }){
            //add parameter

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("name",etName.getText().toString().trim());
                map.put("email",etEmail.getText().toString().trim());
                map.put("password",etPass.getText().toString());
                return map;
            }
        };
        //add this request to requestqueue
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private boolean validate() {
        if(etName.getText().toString().isEmpty()){
            layoutName.setErrorEnabled(true);
            layoutName.setError("This Field is Required");
            return false;
        }
        if(etEmail.getText().toString().isEmpty()){
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("This Field is Required");
            return false;
        }
        if(etPass.getText().toString().length()<6){
            layoutPass.setErrorEnabled(true);
            layoutPass.setError("Password Must Be more than 6 Characters");
            return false;
        }if(!etConfPass.getText().toString().equals(etPass.getText().toString())){
            layoutConfPass.setErrorEnabled(true);
            layoutConfPass.setError("Password does not Match");
            return false;
        }
        return true;
    }

}
