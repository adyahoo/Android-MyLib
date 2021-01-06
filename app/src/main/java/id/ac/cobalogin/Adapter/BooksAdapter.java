package id.ac.cobalogin.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import id.ac.cobalogin.Constant;
import id.ac.cobalogin.DashboardActivity;
import id.ac.cobalogin.EditBookActivity;
import id.ac.cobalogin.Models.Book;
import id.ac.cobalogin.R;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BooksHolder>{

    private Context context;
    private ArrayList<Book> list;
    private ArrayList<Book> listAll;
    private SharedPreferences sharedPreferences;
    private View view;

    public BooksAdapter(Context context, ArrayList<Book> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
    }

    public BooksAdapter(ArrayList<Book> list, Context context) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BooksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_book_main, parent, false);
        return new BooksHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksHolder holder, int position) {
        Book book = list.get(position);
        holder.txtTitle.setText(book.getTitle());
        holder.txtAuthor.setText(book.getAuthor());

        holder.btnDetail.setOnClickListener(v -> {
            Intent i = new Intent(context, EditBookActivity.class);
            i.putExtra("bookId", book.getId());
            i.putExtra("position", position);
            i.putExtra("title", book.getTitle());
            i.putExtra("author", book.getAuthor());
            i.putExtra("desc", book.getDesc());
            i.putExtra("cover", book.getCover());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        });

        holder.btnDelete.setOnClickListener(v -> {
//            AlertDialog alertDialog = new AlertDialog.Builder(view.getRootView().getContext()).create();
//            alertDialog.setTitle("Alert");
//            alertDialog.setMessage("Alert message to be shown");
//            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//
////            if (! ((DashboardActivity)context).isFinishing()) {
////                alertDialog.show();
////            }
//            alertDialog.show();

            deleteBook(book.getId(), position);
            return;
        });
    }

    private void deleteBook(int bookId, int position) {
//        Log.d("masukdelete", "masuk delete"+bookId+" dan "+position);
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
        builder.setTitle("Confirm Delete");
        builder.setMessage("Delete This Book?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringRequest request = new StringRequest(Request.Method.POST, Constant.DELETE_BOOK, response -> {

                    try {
                        JSONObject object = new JSONObject(response);
                        if(object.getBoolean("success")){
//                            Log.d("deletebisa_","bisadelete");
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                            listAll.clear();
                            listAll.addAll(list);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> {
                    error.printStackTrace();
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String token = sharedPreferences.getString("token","");
                        HashMap<String,String> map = new HashMap<>();
                        map.put("Authorization","Bearer "+ token);
                        return map;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String,String> map = new HashMap<>();
                        map.put("id", bookId+"");
                        return map;
                    }
                };

                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        builder.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class BooksHolder extends RecyclerView.ViewHolder{

        private TextView txtTitle, txtAuthor;
        private ImageButton btnDetail, btnDelete;

        public BooksHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtDataTitle);
            txtAuthor = itemView.findViewById(R.id.txtDataAuthor);
            btnDetail = itemView.findViewById(R.id.imgBtnDetail);
            btnDelete = itemView.findViewById(R.id.imgBtnDelete);
        }
    }
}
