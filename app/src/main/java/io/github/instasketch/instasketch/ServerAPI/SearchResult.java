package io.github.instasketch.instasketch.ServerAPI;

/**
 * Created by transfusion on 15-10-6.
 */
public class SearchResult {
    private String thumbnailImageUrl;
    private String imageUrl;
    private int similarityIndex;

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

    public int getSimilarityIndex() {
        return similarityIndex;
    }

    public void setSimilarityIndex(int similarityIndex) {
        this.similarityIndex = similarityIndex;
    }
}