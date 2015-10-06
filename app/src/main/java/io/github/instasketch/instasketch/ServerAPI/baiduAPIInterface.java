package io.github.instasketch.instasketch.ServerAPI;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by transfusion on 2015/10/6.
 */
public interface baiduAPIInterface {

    @GET("/i?tn=baiduimagejson&ct=201326592&cl=2&lm=-1&st=-1&fm=result&fr=&sf=1&fmq=1349413075627_R&pv=&ic=0&nc=1&z=&se=1&showtab=0&fb=0&width=&height=&face=0&istype=2&word=test&rn=5&pn=5")
    Call<SearchResultsModel> getResults();
}
