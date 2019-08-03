package com.example.chrisdarnell.oauthandroid;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by chrisdarnell on 6/2/17.
 */

public class MakePost extends MainActivity implements Button.OnClickListener {

        private AuthorizationService mAuthorizationService;
        private AuthState mAuthState;
        private OkHttpClient mOkHttpClient;


        @Override
        public void onClick(View v) {
            mAuthState.performActionWithFreshTokens(mAuthorizationService, new AuthState.AuthStateAction() {
                @Override
                public void execute(@Nullable final String accessToken, @Nullable String idToken, @Nullable AuthorizationException e) {
                    if (e == null) {
                        mOkHttpClient = new OkHttpClient.Builder().build();
                        MediaType textPlainMT = MediaType.parse("text/plain; charset=utf-8");
                        String postMsg = mpostText.getText().toString();
                        HttpUrl reqUrl = HttpUrl.parse("https://www.googleapis.com/plusDomains/v1/people/me/activities/user");
                        reqUrl = reqUrl.newBuilder().addQueryParameter("key", "AIzaSyBXF_4nM6viuiZ3A7RDcvRMSvfQ1EAQszI").build();
                        Request request = new Request.Builder()
                                .url(reqUrl)
                                .addHeader("Authorization", "Bearer " + accessToken)
                                .post(RequestBody.create(textPlainMT, postMsg)).build();

                        mOkHttpClient.newCall(request).enqueue(new Callback() {

                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                OkHttpClient client = new OkHttpClient.Builder().build();
                                HttpUrl reqUrl = HttpUrl.parse("https://www.googleapis.com/plusDomains/v1/people/me/activities/user");
                                reqUrl = reqUrl.newBuilder().addQueryParameter("key", "AIzaSyBXF_4nM6viuiZ3A7RDcvRMSvfQ1EAQszI").build();
                                MediaType textPlainMT = MediaType.parse("text/plain; charset=utf-8");
                                String postMsg = mpostText.getText().toString();
                                Request request = new Request.Builder()
                                        .url(reqUrl)
                                        .addHeader("Authorization", "Bearer " + accessToken)
                                        .post(RequestBody.create(textPlainMT, postMsg))
                                        .build();
                            }
                        });
                    }
                }
            });
        }
    }

