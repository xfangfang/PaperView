package cn.xfangfang.paperviewdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by FANGs on 2017/1/25.
 */




class AdapterChapterList extends RecyclerView.Adapter<AdapterChapterList.ViewHolder> {


    private ArrayList<Chapter> chapters;
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
        View mView;
        TextView item;
        private OnItemClickListener listener;

        ViewHolder(View itemView,OnItemClickListener l) {
            super(itemView);
            listener = l;
            mView = itemView;
            item = (TextView) itemView.findViewById(R.id.tocItem);
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



    AdapterChapterList(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }


    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_toc_item, parent, false);
        final ViewHolder holder = new ViewHolder(view, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Chapter chapter = chapters.get(position);
        holder.item.setText(chapter.getName());
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }
}
