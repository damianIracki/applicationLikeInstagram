package com.p.applicationlikeinstagram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.p.applicationlikeinstagram.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder>{

    private Context context;
    private List<String> mTags;
    private List<String> mTagsCount;

    public TagAdapter(Context context, List<String> mTags, List<String> mTasCount) {
        this.context = context;
        this.mTags = mTags;
        this.mTagsCount = mTasCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tag_item, parent, false);
        return new TagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.txtTag.setText("# " + mTags.get(position));
        holder.txtNoOfPosts.setText(mTagsCount.get(position) + " posts");

    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtTag, txtNoOfPosts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTag = itemView.findViewById(R.id.hash_tag);
            txtNoOfPosts = itemView.findViewById(R.id.no_of_posts);

        }
    }

    public void filter(List<String> filterTags, List<String> filterCountOfTags){
        this.mTags = filterTags;
        this.mTagsCount = filterCountOfTags;

        notifyDataSetChanged();
    }
}
