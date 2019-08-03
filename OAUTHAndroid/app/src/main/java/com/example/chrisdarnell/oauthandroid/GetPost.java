package com.example.chrisdarnell.oauthandroid;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetPost extends MainActivity implements Button.OnClickListener {



        private AuthorizationService mAuthorizationService;
        private AuthState mAuthState;
        private OkHttpClient mOkHttpClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getpost);
    }

        @Override
        public void onClick(View v) {


           mAuthState.performActionWithFreshTokens(mAuthorizationService, new AuthState.AuthStateAction() {


                @Override
                public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException e) {
                    if (e == null) {
                        mOkHttpClient = new OkHttpClient();
                        HttpUrl reqUrl = HttpUrl.parse("https://www.googleapis.com/plusDomains/v1/people/me/activities/user");
                        reqUrl = reqUrl.newBuilder().addQueryParameter("key", "AIzaSyBXF_4nM6viuiZ3A7RDcvRMSvfQ1EAQszI").build();
                        Request request = new Request.Builder()
                                .url(reqUrl)
                                .addHeader("Authorization", "Bearer " + accessToken)
                                .build();
                        mOkHttpClient.newCall(request).enqueue(new Callback() {


                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String r = response.body().string();
                                try {
                                    JSONObject j = new JSONObject(r);
                                    JSONArray items = j.getJSONArray("items");
                                    List<Map<String, String>> posts = new ArrayList<Map<String, String>>();
                                    for (int i = 0; i < 3; i++) {
                                        HashMap<String, String> m = new HashMap<String, String>();
                                        m.put("published", items.getJSONObject(i).getString("published"));
                                        m.put("title", items.getJSONObject(i).getString("title"));
                                        posts.add(m);
                                    }
                                    final SimpleAdapter postAdapter = new SimpleAdapter(
                                            com.example.chrisdarnell.oauthandroid.GetPost.this,
                                            posts,
                                            R.layout.activity_getpost,
                                            new String[]{"published", "title"},
                                            new int[]{R.id.google_plus_item_date_text, R.id.google_plus_item_text});
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((ListView) findViewById(R.id.google_post_list)).setAdapter(postAdapter);
                                        }
                                    });
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                            }
                        });
                    }
                }
            });
        }
    }

