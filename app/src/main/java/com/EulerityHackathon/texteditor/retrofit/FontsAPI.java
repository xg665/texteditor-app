package com.EulerityHackathon.texteditor.retrofit;
import com.EulerityHackathon.texteditor.retrofit.Response.Fonts;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FontsAPI {

    @GET("fonts/all")
    Observable<List<Fonts>> getResponse();          //create api interface for retrofit




}
