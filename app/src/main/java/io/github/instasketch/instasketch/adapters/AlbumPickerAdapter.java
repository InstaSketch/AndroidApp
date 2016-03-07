package io.github.instasketch.instasketch.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.instasketch.instasketch.R;
import io.github.instasketch.instasketch.activities.AlbumPickerActivity;

/**
 * Created by transfusion on 16-3-6.
 */
public class AlbumPickerAdapter extends ArrayAdapter<AlbumPickerActivity.BucketReference> {

    public AlbumPickerAdapter(Context context, int resource){
        super(context, 0);
    }

    public AlbumPickerAdapter(Context context,List<AlbumPickerActivity.BucketReference> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        AlbumPickerActivity.BucketReference ref = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_album_picker, parent, false);
        }

        TextView bucketName = (TextView) convertView.findViewById(R.id.bucket_name);
        TextView bucketPics = (TextView) convertView.findViewById(R.id.bucket_pics);

        Log.i("bucketed:", ref.bucketName);
        bucketName.setText(ref.bucketName);
        bucketPics.setText(String.valueOf(ref.images));
        return convertView;
    }
}
