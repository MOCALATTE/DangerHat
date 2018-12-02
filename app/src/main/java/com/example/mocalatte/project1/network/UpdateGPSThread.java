package com.example.mocalatte.project1.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.mocalatte.project1.ui.GlobalApplication;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class UpdateGPSThread extends AsyncTask<Void, Void, UpdateGPSThread.Repo> {

    Context context;
    long id;
    double lat;
    double lng;

    //ProgressDialog pDialog;

    JSONObject jsonObject;

    public UpdateGPSThread(Context context, long id, double lat, double lng){
        this.context = context;
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    protected void onPreExecute() {

        /*pDialog = new ProgressDialog(context);
        pDialog.setMessage("작업중..");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();*/

        jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("lat", lat);
            jsonObject.put("lng", lng);
        } catch (JSONException e) {
            e.printStackTrace();
            // if fail to generating jsonObj... abort..!
            jsonObject = null;
        }

        super.onPreExecute();
    }

    @Override
    protected Repo doInBackground(Void... params) {
        if(jsonObject != null) {
            try {

                // http 로그 보기 위함..
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                httpClient.addInterceptor(logging);
                // build
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(GlobalApplication.appurl)
                        .client(httpClient.build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiInterface service = retrofit.create(ApiInterface.class);
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                Call<Repo> c = service.repo(body);
                //repo = c.execute().body();

                // response.. for GET
                //Call<Repo> c = service.repo(GlobalApplication222222.OpenApiServiceKey, "ETC", "AppTest", "json");

                Repo repo = c.execute().body();
                Log.e("doinBackground", "SUCCESS");
                return repo;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.e("doinBackground", "FAIL !!!!!");
        return null;
    }

    @Override
    protected void onPostExecute(Repo rpo) {
        //pDialog.dismiss();
        super.onPostExecute(rpo);
        if(rpo!= null){

            if(rpo.getMsg().equals("success")){
                Toast.makeText(context, "Success to join service.", Toast.LENGTH_LONG).show();
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
            else if(rpo.getMsg().equals("fail")){
                Toast.makeText(context, "Already joined user", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context, "Fail to join service..", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class Repo{

        @SerializedName("msg")
        String msg;
        @SerializedName("reason")
        String reason;

        public String getMsg(){
            return msg;
        }
        public String getReason(){
            return reason;
        }

    }

    public interface ApiInterface {
        @POST("/dangerhat/updateGPSData.php")
        Call<Repo> repo(@Body RequestBody jsonRequest);
    }
}
