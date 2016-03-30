package io.github.instasketch.instasketch.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.instasketch.instasketch.R;
import io.github.instasketch.instasketch.database.SearchResult;
import io.github.instasketch.instasketch.fragments.SearchResultFragment;

/**
 * Created by transfusion on 15-10-6.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static int SORT_BY_COLOR = 1;
//    public static int SORT_BY_
    private List<SearchResult> searchResultList;
    private Context mContext;
    private SearchResultFragment mFragment;
    private OnItemClickListener mItemClickListener;

    public RecyclerViewAdapter(Context context, List<SearchResult> list){
        this.searchResultList = list;
        this.mContext = context;
    }

    public RecyclerViewAdapter(Context context, List<SearchResult> list, SearchResultFragment fragment){
        this.searchResultList = list;
        this.mContext = context;
        this.mFragment = fragment;
    }



    public class CustomViewHolderList extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        protected ImageView imageView;
        protected TextView textView;
        protected TextView similarityTextView;

        public CustomViewHolderList(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.cardview_list_thumbnail);
            this.similarityTextView = (TextView) view.findViewById(R.id.cardview_list_similarityText);
            this.textView = (TextView) view.findViewById(R.id.cardview_list_similarity);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i("clicked", "in adapter");
            mItemClickListener.onItemClick(v, getPosition());
        }

        @Override
        public boolean onLongClick(View v){
            Log.i("longclicked", "in adapter");
            mItemClickListener.onItemLongClick(v, getPosition());
            return true;
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
        public void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.material_cardview_result, null);
        CustomViewHolderList viewHolder = new CustomViewHolderList(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SearchResult searchResult = searchResultList.get(position);
        Uri imgUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(searchResult.getImageID()) );
        ((CustomViewHolderList) holder).imageView.setImageBitmap(MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), ContentUris.parseId(imgUri), MediaStore.Video.Thumbnails.MINI_KIND, null));
        ((CustomViewHolderList) holder).textView.setText(String.valueOf(searchResult.getSimilarityIndex()));
        if (!mFragment.isList){
            ((CustomViewHolderList) holder).similarityTextView.setVisibility(View.GONE);
            ((CustomViewHolderList) holder).textView.setVisibility(View.GONE);
            ((CustomViewHolderList) holder).imageView.setAdjustViewBounds(true);
        }
    }

    public SearchResult getSearchResult(int position){
        return searchResultList.get(position);
    }

    /*public void onBindViewHolder(CustomViewHolderList holder, int position) {
        SearchResult searchResult = searchResultList.get(position);
//        System.out.println(searchResult.getThumbnailImageUrl());
//        load image into imageview
//        Picasso.with(mContext).load(searchResult.getThumbnailImageUrl()).error(R.drawable.drawer_background).into(holder.imageView);
//        Uri imgUri = Uri.parse(searchResult.getImageUrl());

        Uri imgUri = Uri.withAppendedPath( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(searchResult.getImageID()) );

        holder.imageView.setImageBitmap(MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), ContentUris.parseId(imgUri), MediaStore.Video.Thumbnails.MINI_KIND, null));
//        Picasso.with(mContext).l
        holder.textView.setText(String.valueOf(searchResult.getSimilarityIndex()));
    }*/

    @Override
    public int getItemCount() {
        return (searchResultList == null ? 0 : searchResultList.size());
    }

    public void swap(List<SearchResult> searchResults, int sortMethod){
        searchResultList.clear();
        Collections.sort(searchResults, new Comparator<SearchResult>(){
            @Override
            public int compare(SearchResult lhs, SearchResult rhs) {
                return Float.compare(lhs.getSimilarityIndex(),rhs.getSimilarityIndex());
            }

        });
        searchResultList.addAll(searchResults);
        notifyDataSetChanged();
    }


}
