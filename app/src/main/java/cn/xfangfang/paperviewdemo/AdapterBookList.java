package cn.xfangfang.paperviewdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


/**
 * Created by FANGs on 2017/1/25.
 */




class AdapterBookList extends RecyclerView.Adapter<AdapterBookList.ViewHolder> {


    private List<Book> mBookList;
    private OnItemClickListener listener;
    private Context context;


    interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }



    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        View bookView;
        TextView bookName, bookAuthor, bookLastReadArticle,bookImg;
        private OnItemClickListener listener;

        ViewHolder(View itemView,OnItemClickListener l) {
            super(itemView);
            listener = l;
            bookView = itemView;
            bookName = (TextView) itemView.findViewById(R.id.book_name);
            bookAuthor = (TextView) itemView.findViewById(R.id.book_author);
            bookLastReadArticle = (TextView) itemView.findViewById(R.id.book_lastread);
            bookImg = (TextView) itemView.findViewById(R.id.book_pic);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if(listener != null){
               listener.onItemLongClick(v,getAdapterPosition());
                return true;
            }
            return false;
        }
    }



    AdapterBookList(List<Book> bookList) {
        mBookList = bookList;
    }


    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        final ViewHolder holder = new ViewHolder(view, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Book book = mBookList.get(position);
        holder.bookName.setText(book.getName());
        holder.bookLastReadArticle.setText(book.getLastChapterName());
        holder.bookAuthor.setText(book.getAuthor());
        holder.bookImg.setText(String.valueOf(book.getName().charAt(0)));
    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }
}
