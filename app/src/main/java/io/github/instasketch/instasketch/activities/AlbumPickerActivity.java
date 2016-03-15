package io.github.instasketch.instasketch.activities;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.ArrayList;

import io.github.instasketch.instasketch.R;
import io.github.instasketch.instasketch.adapters.AlbumPickerAdapter;
import io.github.instasketch.instasketch.receivers.ImageDatabaseResultReceiver;
import io.github.instasketch.instasketch.services.ImageDatabaseIntentService;

public class AlbumPickerActivity extends AppCompatActivity {
    public static int POPULATE_FINISHED = 0;
    public static String ALBUMS_LIST = "albumsList";


    protected Toolbar mToolbar;

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.album_picker_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.albumScreenFrame, new AlbumPickerFragment()).commit();

    }

    public static class AlbumPickerFragment extends android.support.v4.app.ListFragment {

        private AlbumPickerAdapter adapter;
        private ImageDatabaseResultReceiver dbStatusReceiver;

        private void setupServiceReceiver(){

            this.dbStatusReceiver = new ImageDatabaseResultReceiver(new Handler());

            this.dbStatusReceiver.setReceiver(new ImageDatabaseResultReceiver.Receiver(){
                @Override
                public void onReceiveResult(int resultCode, Bundle resultData){
                if (resultCode == AlbumPickerActivity.POPULATE_FINISHED){
                    adapter.addAll((ArrayList<AlbumPickerActivity.BucketReference>)resultData.get(AlbumPickerActivity.ALBUMS_LIST));
                }

                }
            });
        }
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            /*List<BucketReference> bucketsArr = new ArrayList<BucketReference>();
            bucketsArr.add(new BucketReference("album1", 100));
            bucketsArr.add(new BucketReference("album2", 200));
            adapter = new AlbumPickerAdapter(getActivity(), bucketsArr);*/

            adapter = new AlbumPickerAdapter(getActivity(), 0);
            setupServiceReceiver();
            Intent testIntent = new Intent(AlbumPickerFragment.this.getActivity(), ImageDatabaseIntentService.class);

            testIntent.putExtra(ImageDatabaseIntentService.RECEIVER_KEY, dbStatusReceiver);
            testIntent.putExtra(ImageDatabaseIntentService.REQUEST, ImageDatabaseIntentService.REQ_IMAGE_BUCKETS);
            AlbumPickerFragment.this.getActivity().startService(testIntent);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState){
            super.onActivityCreated(savedInstanceState);

            setListAdapter(adapter);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.listview_album_picker, container, false);

           /* TextView tv = (TextView) view.findViewById(R.id.text);
            tv.setText("Fragment");*/
            return view;
        }
    }

    public static class BucketReference implements Parcelable {
        public String bucketName;
        public int images;

        public BucketReference(String name, int imgs){
            bucketName = name;
            images = imgs;
        }

        public static final Creator<BucketReference> CREATOR = new Creator<BucketReference>() {
            @Override
            public BucketReference createFromParcel(Parcel in) {
                return new BucketReference(in);
            }

            @Override
            public BucketReference[] newArray(int size) {
                return new BucketReference[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(images);
            dest.writeString(bucketName);
        }

        private BucketReference(Parcel in){
            images = in.readInt();
            bucketName = in.readString();
        }
    }

}
