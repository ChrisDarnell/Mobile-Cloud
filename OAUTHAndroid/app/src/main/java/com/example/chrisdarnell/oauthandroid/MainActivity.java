package com.example.chrisdarnell.oauthandroid;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.squareup.picasso.Picasso;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenResponse;

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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.chrisdarnell.oauthandroid.MainApplication.LOG_TAG;

public class MainActivity extends AppCompatActivity {

    private static final String SHARED_PREFERENCES_NAME = "AuthStatePreference";
    private static final String AUTH_STATE = "AUTH_STATE";
    private static final String USED_INTENT = "USED_INTENT";

    MainApplication mMainApplication;

    // state
    AuthState mAuthState;

    // views
    AppCompatButton mAuthorize;
    AppCompatButton mMakeApiCall;
    AppCompatButton mMakePost;
    AppCompatButton mSignOut;
    AppCompatButton mgetPosts;
    AppCompatTextView mFullName;
    ImageView mProfileView;
    EditText mpostText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);
        mMainApplication = (MainApplication) getApplication();
        mAuthorize = (AppCompatButton) findViewById(R.id.authorize);
        mMakeApiCall = (AppCompatButton) findViewById(R.id.makeApiCall);
        mSignOut = (AppCompatButton) findViewById(R.id.signOut);
        mgetPosts = (AppCompatButton) findViewById(R.id.getPosts);
        mMakePost = (AppCompatButton) findViewById(R.id.makePost);
        mFullName = (AppCompatTextView) findViewById(R.id.fullName);
        mProfileView = (ImageView) findViewById(R.id.profileImage);


        enablePostAuthorizationFlows();


        // wire click listeners
        mAuthorize.setOnClickListener(new AuthorizeListener());
    }

        public void helpB(View v) {
            Button clickedButton = (Button) v;
            switch (clickedButton.getId()) {
            case R.id.authorize:
                Intent intent = new Intent(this, AuthorizeListener.class);
                this.startActivity(intent);
                break;
            case R.id.makeApiCall:
                Intent intent1 = new Intent(this, MakeApiCallListener.class);
                this.startActivity(intent1);
                break;
            case R.id.makePost:
                Intent intent2 = new Intent(this, MakePost.class);
                this.startActivity(intent2);
                break;
            case R.id.getPosts:
                Intent intent3 = new Intent(this, GetPost.class);
                this.startActivity(intent3);
                break;
            default:
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        checkIntent(intent);
    }

    private void checkIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case "com.example.chrisdarnell.oauthandroid.HANDLE_AUTHORIZATION_RESPONSE":
                    if (!intent.hasExtra(USED_INTENT)) {
                        handleAuthorizationResponse(intent);
                        intent.putExtra(USED_INTENT, true);
                    }
                    break;
//                case "com.example.chrisdarnell.oauthandroid.MAKEPOST":
//                    this.startActivity();
//                    break;
//                case "com.example.chrisdarnell.oauthandroid.GETPOST":
//                    this.startActivity();
//                    break;

                default:
                    // do nothing
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIntent(getIntent());
    }

    private void enablePostAuthorizationFlows() {
        mAuthState = restoreAuthState();
        if (mAuthState != null && mAuthState.isAuthorized()) {
            if (mMakeApiCall.getVisibility() == View.GONE) {
                mMakeApiCall.setVisibility(View.VISIBLE);
                mMakeApiCall.setOnClickListener(new MakeApiCallListener(this, mAuthState, new AuthorizationService(this)));
            }
            if (mSignOut.getVisibility() == View.GONE) {
                mSignOut.setVisibility(View.VISIBLE);
                mSignOut.setOnClickListener(new SignOutListener(this));
            }
        } else {
            mMakeApiCall.setVisibility(View.GONE);
            mSignOut.setVisibility(View.GONE);
        }
    }

    /**
     * Exchanges the code, for the {@link TokenResponse}.
     *
     * @param intent represents the {@link Intent} from the Custom Tabs or the System Browser.
     */
    private void handleAuthorizationResponse(@NonNull Intent intent) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);
        final AuthState authState = new AuthState(response, error);

        if (response != null) {
            Log.i(LOG_TAG, String.format("Handled Authorization Response %s ", authState.toJsonString()));
            AuthorizationService service = new AuthorizationService(this);
            service.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
                @Override
                public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                    if (exception != null) {
                        Log.w(LOG_TAG, "Token Exchange failed", exception);
                    } else {
                        if (tokenResponse != null) {
                            authState.update(tokenResponse, exception);
                            persistAuthState(authState);
                            Log.i(LOG_TAG, String.format("Token Response [ Access Token: %s, ID Token: %s ]", tokenResponse.accessToken, tokenResponse.idToken));
                        }
                    }
                }
            });
        }
    }

    private void persistAuthState(@NonNull AuthState authState) {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                .putString(AUTH_STATE, authState.toJsonString())
                .commit();
        enablePostAuthorizationFlows();
    }

    private void clearAuthState() {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(AUTH_STATE)
                .apply();
    }

    @Nullable
    private AuthState restoreAuthState() {
        String jsonString = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(AUTH_STATE, null);
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                return AuthState.fromJson(jsonString);
            } catch (JSONException jsonException) {
                // should never happen
            }
        }
        return null;
    }

    /**
     * Kicks off the authorization flow.
     */
    public static class AuthorizeListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
                    Uri.parse("https://accounts.google.com/o/oauth2/v2/auth") /* auth endpoint */,
                    Uri.parse("https://www.googleapis.com/oauth2/v4/token") /* token endpoint */
            );

            String clientId = "288428941902-r2vtrl2esouu5i4dt6c02hhermf9anmd.apps.googleusercontent.com";
            Uri redirectUri = Uri.parse("com.example.chrisdarnell.oauthandroid:/oauth2callback");
            AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                    serviceConfiguration,
                    clientId,
                    AuthorizationRequest.RESPONSE_TYPE_CODE,
                    redirectUri
            );
            builder.setScopes("profile");
            AuthorizationRequest request = builder.build();

            AuthorizationService authorizationService = new AuthorizationService(view.getContext());

            String action = "com.example.chrisdarnell.oauthandroid.HANDLE_AUTHORIZATION_RESPONSE";
            Intent postAuthorizationIntent = new Intent(action);
            PendingIntent pendingIntent = PendingIntent.getActivity(view.getContext(), request.hashCode(), postAuthorizationIntent, 0);
            authorizationService.performAuthorizationRequest(request, pendingIntent);
        }
    }

    public static class SignOutListener implements Button.OnClickListener {

        private final MainActivity mMainActivity;

        public SignOutListener(@NonNull MainActivity mainActivity) {
            mMainActivity = mainActivity;
        }

        @Override
        public void onClick(View view) {
            mMainActivity.mAuthState = null;
            mMainActivity.clearAuthState();
            mMainActivity.enablePostAuthorizationFlows();
        }
    }


// API CALL STUFF


    public static class MakeApiCallListener implements Button.OnClickListener {

        private final MainActivity mMainActivity;
        private AuthState mAuthState;
        private AuthorizationService mAuthorizationService;

        public MakeApiCallListener(@NonNull MainActivity mainActivity, @NonNull AuthState authState, @NonNull AuthorizationService authorizationService) {
            mMainActivity = mainActivity;
            mAuthState = authState;
            mAuthorizationService = authorizationService;
        }

        @Override
        public void onClick(View view) {
            mAuthState.performActionWithFreshTokens(mAuthorizationService, new AuthState.AuthStateAction() {


                @Override
                public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException exception) {
                    new AsyncTask<String, Void, JSONObject>() {
                        @Override
                        protected JSONObject doInBackground(String... tokens) {
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url("https://www.googleapis.com/oauth2/v3/userinfo")
                                    .addHeader("Authorization", String.format("Bearer %s", tokens[0]))
                                    .build();

                            try {
                                Response response = client.newCall(request).execute();
                                String jsonBody = response.body().string();
                                Log.i(LOG_TAG, String.format("User Info Response %s", jsonBody));
                                return new JSONObject(jsonBody);
                            } catch (Exception exception) {
                                Log.w(LOG_TAG, exception);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(JSONObject userInfo) {
                            if (userInfo != null) {
                                String fullName = userInfo.optString("name", null);
                                String imageUrl = userInfo.optString("picture", null);
                                if (!TextUtils.isEmpty(imageUrl)) {
                                    Picasso.with(mMainActivity)
                                            .load(imageUrl)
                                            .into(mMainActivity.mProfileView);
                                }
                                if (!TextUtils.isEmpty(fullName)) {
                                    mMainActivity.mFullName.setText(fullName);

                                }

                                String message;
                                if (userInfo.has("error")) {
                                    message = String.format("%s [%s]", mMainActivity.getString(R.string.request_failed), userInfo.optString("error_description", "No description"));
                                } else {
                                    message = mMainActivity.getString(R.string.request_complete);
                                }
                            }
                        }
                    }.execute(accessToken);
                }
            });


        }
    }




//
//    public class GetPostListener implements Button.OnClickListener {
//        private AuthorizationService mAuthorizationService;
//        private AuthState mAuthState;
//        private OkHttpClient mOkHttpClient;
//        private final MainActivity mMainActivity;
//
//        public GetPostListener(@NonNull MainActivity mainActivity, @NonNull AuthState authState, @NonNull AuthorizationService authorizationService) {
//            mMainActivity = mainActivity;
//            mAuthState = authState;
//            mAuthorizationService = authorizationService;
//        }

//        @Override
//        public void onClick(View v) {
//
//            mAuthState.performActionWithFreshTokens(mAuthorizationService, new AuthState.AuthStateAction() {
//
//
//                @Override
//                public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException e) {
//                    if (e == null) {
//                        mOkHttpClient = new OkHttpClient();
//                        HttpUrl reqUrl = HttpUrl.parse("https://www.googleapis.com/plusDomains/v1/people/me/activities/user");
//                        reqUrl = reqUrl.newBuilder().addQueryParameter("key", "AIzaSyBXF_4nM6viuiZ3A7RDcvRMSvfQ1EAQszI").build();
//                        Request request = new Request.Builder()
//                                .url(reqUrl)
//                                .addHeader("Authorization", "Bearer " + accessToken)
//                                .build();
//                        mOkHttpClient.newCall(request).enqueue(new Callback() {
//
//
//                            @Override
//                            public void onFailure(Call call, IOException e) {
//                                e.printStackTrace();
//                            }
//
//                            @Override
//                            public void onResponse(Call call, Response response) throws IOException {
//                                String r = response.body().string();
//                                try {
//                                    JSONObject j = new JSONObject(r);
//                                    JSONArray items = j.getJSONArray("items");
//                                    List<Map<String, String>> posts = new ArrayList<Map<String, String>>();
//                                    for (int i = 0; i < 3; i++) {
//                                        HashMap<String, String> m = new HashMap<String, String>();
//                                        m.put("published", items.getJSONObject(i).getString("published"));
//                                        m.put("title", items.getJSONObject(i).getString("title"));
//                                        posts.add(m);
//                                    }
//                                    final SimpleAdapter postAdapter = new SimpleAdapter(
//                                            MainActivity.this,
//                                            posts,
//                                            R.layout.google_plus_item,
//                                            new String[]{"published", "title"},
//                                            new int[]{R.id.google_plus_item_date_text, R.id.google_plus_item_text});
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            ((ListView) findViewById(R.id.google_post_list)).setAdapter(postAdapter);
//                                        }
//                                    });
//                                } catch (JSONException e1) {
//                                    e1.printStackTrace();
//                                }
//
//                            }
//                        });
//                    }
//                }
//            });
//        }
//    }



//    public class MakePostListener implements Button.OnClickListener {
//        private AuthorizationService mAuthorizationService;
//        private AuthState mAuthState;
//        private OkHttpClient mOkHttpClient;
//        private final MainActivity mMainActivity;
//
//
//        public MakePostListener(@NonNull MainActivity mainActivity, @NonNull AuthState authState, @NonNull AuthorizationService authorizationService) {
//            mMainActivity = mainActivity;
//            mAuthState = authState;
//            mAuthorizationService = authorizationService;
//        }
//
//
//        @Override
//        public void onClick(View v) {
//            mAuthState.performActionWithFreshTokens(mAuthorizationService, new AuthState.AuthStateAction() {
//                @Override
//                public void execute(@Nullable final String accessToken, @Nullable String idToken, @Nullable AuthorizationException e) {
//                    if (e == null) {
//                        mOkHttpClient = new OkHttpClient.Builder().build();
//                        MediaType textPlainMT = MediaType.parse("text/plain; charset=utf-8");
//                        String postMsg = mpostText.getText().toString();
//                        HttpUrl reqUrl = HttpUrl.parse("https://www.googleapis.com/plusDomains/v1/people/me/activities/user");
//                        reqUrl = reqUrl.newBuilder().addQueryParameter("key", "AIzaSyBXF_4nM6viuiZ3A7RDcvRMSvfQ1EAQszI").build();
//                        Request request = new Request.Builder()
//                                .url(reqUrl)
//                                .addHeader("Authorization", "Bearer " + accessToken)
//                                .post(RequestBody.create(textPlainMT, postMsg)).build();
//
//                        mOkHttpClient.newCall(request).enqueue(new Callback() {
//
//                            @Override
//                            public void onFailure(Call call, IOException e) {
//                                e.printStackTrace();
//                            }
//
//                            @Override
//                            public void onResponse(Call call, Response response) throws IOException {
//
//                                OkHttpClient client = new OkHttpClient.Builder().build();
//                                HttpUrl reqUrl = HttpUrl.parse("https://www.googleapis.com/plusDomains/v1/people/me/activities/user");
//                                reqUrl = reqUrl.newBuilder().addQueryParameter("key", "AIzaSyBXF_4nM6viuiZ3A7RDcvRMSvfQ1EAQszI").build();
//                                MediaType textPlainMT = MediaType.parse("text/plain; charset=utf-8");
//                                String postMsg = mpostText.getText().toString();
//                                Request request = new Request.Builder()
//                                        .url(reqUrl)
//                                        .addHeader("Authorization", "Bearer " + accessToken)
//                                        .post(RequestBody.create(textPlainMT, postMsg))
//                                        .build();
//                            }
//                        });
//                    }
//                }
//            });
//        }
//    }









}

//    public static class APIActivity implements Button.OnClickListener {
//
//
//        private AuthState mAuthState;
//        private AuthorizationService mAuthorizationService;
//
//
//        EditText mpostText;
//        AppCompatButton mgetPosts;
//        AppCompatButton mMakePost;
//    }
//
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            SharedPreferences authPreference = getSharedPreferences("auth", MODE_PRIVATE);
//            setContentView(R.layout.activity_main);
//            mAuthorizationService = new AuthorizationService(this);
//
//            mgetPosts = (AppCompatButton) findViewById(R.id.getPosts);
//            mpostText = (EditText) findViewById(R.id.google_plus_item_text);
//            mMakePost = (AppCompatButton) findViewById(R.id.makePost);
//
//
//            mgetPosts.setOnClickListener(new View.OnClickListener() {
//                                             @Override
//                                             public void onClick(View v) {
//                                                 mAuthState.performActionWithFreshTokens(mAuthorizationService, new AuthState.AuthStateAction() {
//
//
//                                                     @Override
//                                                     public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException exception) {
//                                                         new AsyncTask<String, Void, JSONObject>() {
//                                                             @Override
//                                                             protected JSONObject doInBackground(String... tokens) {
//                                                         @Override
//                                                         public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException e) {
//                                                             if (e == null) {
//                                                                 mOkHttpClient = new OkHttpClient();
//                                                                 HttpUrl reqUrl = HttpUrl.parse("https://www.googleapis.com/plusDomains/v1/people/me/activities/user");
//                                                                 reqUrl = reqUrl.newBuilder().addQueryParameter("key", "AIzaSyBXF_4nM6viuiZ3A7RDcvRMSvfQ1EAQszI").build();
//                                                                 Request request = new Request.Builder()
//                                                                         .url(reqUrl)
//                                                                         .addHeader("Authorization", "Bearer " + accessToken)
//                                                                         .build();
//                                                                 mOkHttpClient.newCall(request).enqueue(new Callback() {
//                                                                     @Override
//                                                                     public void onFailure(Call call, IOException e) {
//                                                                         e.printStackTrace();
//                                                                     }
//
//                                                                     @Override
//                                                                     public void onResponse(Call call, Response response) throws IOException {
//                                                                         String r = response.body().string();
//                                                                         try {
//                                                                             JSONObject j = new JSONObject(r);
//                                                                             JSONArray items = j.getJSONArray("items");
//                                                                             List<Map<String, String>> posts = new ArrayList<Map<String, String>>();
//                                                                             for (int i = 0; i < 3; i++) {
//                                                                                 HashMap<String, String> m = new HashMap<String, String>();
//                                                                                 m.put("published", items.getJSONObject(i).getString("published"));
//                                                                                 m.put("title", items.getJSONObject(i).getString("title"));
//                                                                                 posts.add(m);
//                                                                             }
//                                                                             final SimpleAdapter postAdapter = new SimpleAdapter(
//                                                                                     APIActivity.this,
//                                                                                     posts,
//                                                                                     R.layout.google_plus_item,
//                                                                                     new String[]{"published", "title"},
//                                                                                     new int[]{R.id.google_plus_item_date_text, R.id.google_plus_item_text});
//                                                                             runOnUiThread(new Runnable() {
//                                                                                 @Override
//                                                                                 public void run() {
//                                                                                     ((ListView) findViewById(R.id.google_post_list)).setAdapter(postAdapter);
//                                                                                 }
//                                                                             });
//                                                                         } catch (JSONException e1) {
//                                                                             e1.printStackTrace();
//                                                                         }
//
//                                                                     }
//
//                                                                 });
//                                                             }
//                                                         }
//                                                     });
//
//
//                                                 }
//                                             }
//                })
//        ;}
//    }
//}
//    public class APIActivity extends MainActivity {
//
//        private AuthorizationService mAuthorizationService;
//        private AuthState mAuthState;
//        private OkHttpClient mOkHttpClient;
//        {
//            SharedPreferences authPreference = getSharedPreferences("auth", MODE_PRIVATE);
//            mAuthorizationService = new AuthorizationService(this);
//            findViewById(R.id.makeApiCall).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    try {
//                        mAuthState.performActionWithFreshTokens(mAuthorizationService, new AuthState.AuthStateAction() {
//
//
//
//                            @Override
//                            public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException e) {
//                                if (e == null) {
//                                    mOkHttpClient = new OkHttpClient();
//                                    HttpUrl reqUrl = HttpUrl.parse("https://www.googleapis.com/plusDomains/v1/people/me/activities/user");
//                                    reqUrl = reqUrl.newBuilder().addQueryParameter("key", "AIzaSyBXF_4nM6viuiZ3A7RDcvRMSvfQ1EAQszI").build();
//                                    Request request = new Request.Builder()
//                                            .url(reqUrl)
//                                            .addHeader("Authorization", "Bearer " + accessToken)
//                                            .build();
//                                    mOkHttpClient.newCall(request).enqueue(new Callback() {
//
//
//
//                                        @Override
//                                        public void onFailure(Call call, IOException e) {
//                                            e.printStackTrace();
//                                        }
//
//                                        @Override
//                                        public void onResponse(Call call, Response response) throws IOException {
//                                            String r = response.body().string();
//                                            try {
//                                                JSONObject j = new JSONObject(r);
//                                                JSONArray items = j.getJSONArray("items");
//                                                List<Map<String, String>> posts = new ArrayList<Map<String, String>>();
//                                                for (int i = 0; i < 3; i++) {
//                                                    HashMap<String, String> m = new HashMap<String, String>();
//                                                    m.put("published", items.getJSONObject(i).getString("published"));
//                                                    m.put("title", items.getJSONObject(i).getString("title"));
//                                                    posts.add(m);
//                                                }
//                                                final SimpleAdapter postAdapter = new SimpleAdapter(
//                                                        APIActivity.this,
//                                                        posts,
//                                                        R.layout.google_plus_item,
//                                                        new String[]{"published", "title"},
//                                                        new int[]{R.id.google_plus_item_date_text, R.id.google_plus_item_text});
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        ((ListView) findViewById(R.id.google_post_list)).setAdapter(postAdapter);
//                                                    }
//                                                });
//                                            } catch (JSONException e1) {
//                                                e1.printStackTrace();
//                                            }
//
//                                        }
//                                    });
//                                }
//                            }
//                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//            });
//        }
//    }
//}
