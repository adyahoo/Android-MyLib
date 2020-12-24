package id.ac.cobalogin.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import id.ac.cobalogin.Models.Book;
import id.ac.cobalogin.R;

public class ListBookAdapter extends RecyclerView.Adapter<ListBookAdapter.ListBookHolder> {

    private Context context;
    private ArrayList<Book> list;
    private ArrayList<Book> listAll;
    private SharedPreferences sharedPreferences;
    private View view;

    public ListBookAdapter(Context context, ArrayList<Book> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ListBookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_book_main, parent, false);
        return new ListBookHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListBookHolder holder, int position) {
        Book book = list.get(position);
        holder.txtTitle.setText(book.getTitle());
        holder.txtAuthor.setText(book.getAuthor());

        holder.btnDetail.setVisibility(View.GONE);
        holder.btnDelete.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListBookHolder extends RecyclerView.ViewHolder{

        private TextView txtTitle, txtAuthor;
        private ImageButton btnDetail, btnDelete;

        public ListBookHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtDataTitle);
            txtAuthor = itemView.findViewById(R.id.txtDataAuthor);
            btnDetail = itemView.findViewById(R.id.imgBtnDetail);
            btnDelete = itemView.findViewById(R.id.imgBtnDelete);
        }
    }
}
