package io.github.instasketch.instasketch.ServerAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by transfusion on 2015/10/6.
 */
public class SearchResultsModel {

    public String queryEnc;
    public String queryExt;
    public int listNum;
    public int displayNum;
    public String bdFmtDispNum;
    public String bdSearchTime;
    public int isNeedAsyncRequest;
    public String bdIsClustered;
    public List<ImageModel> data = new ArrayList<ImageModel>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
