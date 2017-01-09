package com.example.renukamatkar.pinterest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainHomePage extends ActionBarActivity {

    RecyclerView rv;
    String title[]={"Web Design","Android Studio"};
    String icon[] = {"R.drawable.wiki","R.drawable.wiki"};
    HomeListAdapter adapter;
    List<HomeListInfo> infopass;
    String pinids[];
    SharedPreferences sp;
    SharedPreferences.Editor edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_page);
        rv = (RecyclerView)findViewById(R.id.mainhomelist);
        sp = getSharedPreferences("config",0);
        infopass = new ArrayList<>();
        GetAllPins gap = new GetAllPins();
        gap.execute();
        edt = sp.edit();


    }

    private List<HomeListInfo> senddata() {
        for(int i =0;i<title.length;i++){
           HomeListInfo inf = new HomeListInfo();
            inf.icon = icon[i];
            inf.title = title[i];
            infopass.add(inf);
        }
        return infopass;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.user) {
            startActivity(new Intent(MainHomePage.this,User_Details.class));
            return true;
        }

        if (id == R.id.search) {
            startActivity(new Intent(MainHomePage.this,Search.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class HomeListInfo{
        String title;
        String icon;
    }

    class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.HomeListHolder>{

        List<HomeListInfo> infoList = Collections.emptyList();
        Context cc;
        LayoutInflater inflater;

        public HomeListAdapter(Context c, List<HomeListInfo> data){
            this.cc = c;
            infoList = data;
            inflater = LayoutInflater.from(c);
        }

        @Override
        public HomeListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.customhomeitem,parent,false);
            HomeListHolder holder = new HomeListHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(HomeListHolder holder, int position) {
            HomeListInfo info = infoList.get(position);
            holder.tv.setText(info.title);
            String url = info.icon;
            url = url.replace(" ","%20");
            if(url.contentEquals("http://pintrestproj.net46.net/")){
                url = "https://mb3is.megx.net/net.megx.esa/img/no_photo.png";
            }
           Picasso.with(MainHomePage.this).load(url).into(holder.iv);
        }

        @Override
        public int getItemCount() {
            return infoList.size();
        }

        class HomeListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tv;
            ImageView iv;
            public HomeListHolder(View itemView) {
                super(itemView);
                iv = (ImageView)itemView.findViewById(R.id.homepinico);
                tv=(TextView)itemView.findViewById(R.id.homepintext);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                String pos = pinids[getPosition()];
                edt.putString("pinclicked",pos);
                edt.commit();
                startActivity(new Intent(MainHomePage.this, Pin.class));
            }
        }

    }


    class GetAllPins extends AsyncTask<String,Integer,String>{

        ProgressDialog pd;


        HttpClient client = new DefaultHttpClient();
        HttpPost get = new HttpPost("http://pintrestproj.net46.net/getallpins.php");
        HttpResponse response;
        String result;
        MultipartEntity entity = new MultipartEntity();

        JSONArray ja = null;
        JSONObject jo;
        int count=0;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainHomePage.this);
            pd.setMessage("Getting Pins");
            pd.show();
            pd.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                entity.addPart("userid",Integer.toString(sp.getInt("userid",-1)));
                get.setEntity(entity);
            response = client.execute(get);

                result = EntityUtils.toString(response.getEntity());

                ja= new JSONArray(result);
                for (int i = 0;i<ja.length();i++)
                count++;

                title = new String[count];
                icon = new String[count];
                pinids = new String[count];
                for(int i = 0 ; i<ja.length();i++){
                    jo = ja.getJSONObject(i);
                    title[i] = jo.getString("title");
                    icon[i] = "http://pintrestproj.net46.net/"+jo.getString("imgurl");
                    pinids[i]= jo.getString("id");
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
            pd.dismiss();
            if (count>0){
                adapter = new HomeListAdapter(MainHomePage.this, senddata());
                rv.setAdapter(adapter);
                rv.setLayoutManager(new GridLayoutManager(MainHomePage.this,2));
            }else{
                Toast.makeText(MainHomePage.this,"Error getting pins.",Toast.LENGTH_SHORT).show();
                rv.setVisibility(View.GONE);
            }
        }
    }


}
