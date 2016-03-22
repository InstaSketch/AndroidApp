package io.github.instasketch.instasketch.database;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by transfusion on 15-10-6.
 */
public class SearchResult implements Parcelable {
    private int imageID;
    private String thumbnailImageUrl;
    private String imageUrl;
    private float similarityIndex;

    public SearchResult(){

    }
    protected SearchResult(Parcel in) {
        imageID = in.readInt();
        thumbnailImageUrl = in.readString();
        imageUrl = in.readString();
        similarityIndex = in.readFloat();
    }

    public static final Creator<SearchResult> CREATOR = new Creator<SearchResult>() {
        @Override
        public SearchResult createFromParcel(Parcel in) {
            return new SearchResult(in);
        }

        @Override
        public SearchResult[] newArray(int size) {
            return new SearchResult[size];
        }
    };

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getSimilarityIndex() {
        return similarityIndex;
    }

    public void setSimilarityIndex(float similarityIndex) {
        this.similarityIndex = similarityIndex;
    }

    public int getImageID() { return imageID; }

    public void setImageID(int imageID){
        this.imageID = imageID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imageID);
        dest.writeString(thumbnailImageUrl);
        dest.writeString(imageUrl);
        dest.writeFloat(similarityIndex);
    }
}