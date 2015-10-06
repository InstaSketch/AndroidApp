package io.github.instasketch.instasketch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.instasketch.instasketch.R;
import io.github.instasketch.instasketch.ServerAPI.SearchResult;

/**
 * Created by transfusion on 15-10-6.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {

    private List<SearchResult> searchResultList;
    private Context mContext;

    public RecyclerViewAdapter(Context context, List<SearchResult> list){
        this.searchResultList = list;
        this.mContext = context;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textView = (TextView) view.findViewById(R.id.similarity);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.material_cardview_result, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        SearchResult searchResult = searchResultList.get(position);
//        System.out.println(searchResult.getThumbnailImageUrl());
//        load image into imageview
        Picasso.with(mContext).load(searchResult.getThumbnailImageUrl()).error(R.drawable.drawer_background).into(holder.imageView);
        holder.textView.setText(String.valueOf(searchResult.getSimilarityIndex()));

    }

    @Override
    public int getItemCount() {
        return (searchResultList == null ? 0 : searchResultList.size());
    }

}
