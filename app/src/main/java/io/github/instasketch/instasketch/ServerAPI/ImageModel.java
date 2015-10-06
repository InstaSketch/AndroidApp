package io.github.instasketch.instasketch.ServerAPI;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by transfusion on 2015/10/6.
 */
public class ImageModel {
    public String thumbURL;
    public String middleURL;
    public String largeTnImageUrl;
    public int hasLarge;
    public String hoverURL;
    public int pageNum;
    public String objURL;
    public String fromURL;
    public String fromURLHost;
    public String currentIndex;
    public int width;
    public int height;
    public String type;
    public String filesize;
    public String bdSrcType;
    public String di;
    public String is;
    public Object simidInfo;
    public Object faceInfo;
    public Object xiangshiInfo;
    public String adPicId;
    public int bdSetImgNum;
    public String bdImgnewsDate;
    public String fromPageTitle;
    public String fromPageTitleEnc;
    public String bdSourceName;
    public String bdFromPageTitlePrefix;
    public String token;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
