package com.example.renukamatkar.pinterest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by AVIN PC on 17-10-2015.
 */
public class LikedPins extends Fragment {

    RecyclerView rv;
    String text[] = {"HII","HELO"};
    String icons[] = {"R.drawable.map","R.drawable.map"};
    String pinids[];
    Spinner selectboard;
    String boards[] = {"first","second"};
    ArrayAdapter<String> adapter;
    SharedPreferences sp;
    String bids[];
    TextView countpins;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.likedpins,container,false);
        sp = getActivity().getSharedPreferences("config",0);
        countpins = (TextView)v.findViewById(R.id.countlikedpins);
        rv = (RecyclerView)v.findViewById(R.id.likedpinslist);
        GetUserPins gup = new GetUserPins();
        gup.execute();
        return v;
    }

    private List<PinsInfo> senddata() {
        List<PinsInfo> pi = new ArrayList<>();

        for(int i = 0 ;i<text.length;i++){
            PinsInfo p = new PinsInfo();
            p.icon=icons[i];
            p.pintext = text[i];
            pi.add(p);
        }
        return pi;
    }



    class GetUserPins extends AsyncTask<String,Integer,String>{


        ProgressDialog pdd;

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/getlikedpins.php");
        HttpResponse response;
        MultipartEntity entity = new MultipartEntity();
        String result;

        JSONArray ja = null;
        JSONObject jo;
        int count =0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdd = new ProgressDialog(getActivity());
            pdd.setMessage("Getting Pins");
            pdd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            entity.addPart("userid",Integer.toString(sp.getInt("userid",-1)));
            post.setEntity(entity);

            try {
                response = client.execute(post);
                result = EntityUtils.toString(response.getEntity());
                ja = new JSONArray(result);
                for(int i = 0;i<ja.length();i++){
                    count++;
                }

                text = new String[count];
                icons = new String[count];
                pinids = new String[count];

                for(int i=0;i<ja.length();i++){
                    jo = ja.getJSONObject(i);
                    text[i] = jo.getString("title");
                    if(jo.getString("imgurl").contentEquals(""))
                        icons[i]="https://mb3is.megx.net/net.megx.esa/img/no_photo.png";
                    else
                        icons[i] = "http://pintrestproj.net46.net/"+jo.getString("imgurl");
                    pinids[i] = jo.getString("id");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pdd.dismiss();
            if(count>0){

                rv.setAdapter( new PinsAdapter(getActivity() , senddata() ,pinids));
                rv.setLayoutManager(new GridLayoutManager(getActivity(),3));
                countpins.setText(text.length+" Pins(s) found");
            }else{
                rv.setVisibility(View.GONE);
            }

        }
    }

}
