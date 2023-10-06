package com.example.EC2.service;

import org.springframework.stereotype.Service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



@Service
public class EC2Service {
    public String getVantagePricing() throws Exception {
        System.out.println("api가 호출되긴 함");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.vantage.sh/v1/products?provider_id=aws&service_id=aws-ec2")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("authorization", "Bearer vntg_tkn_014d4aa24bbd5029e43f62a7bb6cca4ce7562770")
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
