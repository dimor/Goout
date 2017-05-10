package com.dimorm.apps.goout.controller;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Dima on 4/21/2017.
 */

public class OkHttpNetCall {

    OkHttpClient client = new OkHttpClient();

   public String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}