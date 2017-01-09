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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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


public class Search extends ActionBarActivity {

    AutoCompleteTextView etsearch;
    ArrayList<String> searchlist;
    String searchresult[];
    ArrayAdapter<String> searchadapter;
    Button srclick;
    RecyclerView rv;
    SharedPreferences sp;
    SharedPreferences.Editor edt;
    String pinids[],title[],icon[];
    HomeListAdapter adapter;
    List<HomeListInfo> infopass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        sp = getSharedPreferences("config",0);
        edt = sp.edit();
        rv = (RecyclerView)findViewById(R.id.searchlist);
        rv.setVisibility(View.GONE);
        srclick = (Button)findViewById(R.id.btsearchpins);
        srclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllocPins app = new AllocPins();
                app.execute();
            }
        });
        infopass = new ArrayList<>();
        etsearch=(AutoCompleteTextView)findViewById(R.id.etsearchpins);
        etsearch.setThreshold(3);
        etsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
              if(i==2){
                  SearchPin spp= new SearchPin();
                  spp.execute();
              }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(Search.this,SearchUser.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class SearchPin extends AsyncTask<String,Integer,String>{

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/search.php");
        MultipartEntity entity = new MultipartEntity();
        HttpResponse response;
        String result;

        JSONArray ja = null;
        JSONObject jo;

        int count=0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            entity.addPart("title",etsearch.getText().toString());
            post.setEntity(entity);

            try {
                response = client.execute(post);
                result = EntityUtils.toString(response.getEntity());
                ja = new JSONArray(result);
                for(int i =0 ; i < ja.length();i++){
                    count++;
                }

                searchresult = new String[count];


                for(int i =0;i<ja.length();i++){
                    jo = ja.getJSONObject(i);
                    searchresult[i] = jo.getString("title");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
                searchresult = new String[0];
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(searchresult.length>0){
                searchlist = new ArrayList<>();
                for(int i = 0;i<searchresult.length;i++){
                    searchlist.add(searchresult[i]);
                }
                searchadapter = new ArrayAdapter<String>(Search.this,android.R.layout.simple_list_item_1,searchlist);
                etsearch.setAdapter(searchadapter);
                searchadapter.notifyDataSetChanged();
            }else{
                searchresult= new String[0];
                searchlist = new ArrayList<>();
                searchadapter = new ArrayAdapter<String>(Search.this,android.R.layout.simple_list_item_1,searchresult);
               searchadapter.notifyDataSetChanged();
            }
        }
    }

class AllocPins extends AsyncTask<String,Integer,String>{

    HttpClient client = new DefaultHttpClient();
    HttpPost post = new HttpPost("http://pintrestproj.net46.net/search.php");
    MultipartEntity entity = new MultipartEntity();
    HttpResponse response;
    String result;

    JSONArray ja = null;
    JSONObject jo;

    int count=0;

    ProgressDialog pdd;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pdd = new ProgressDialog(Search.this);
        pdd.setMessage("Searching");
        pdd.show();
        infopass.clear();
    }

    @Override
    protected String doInBackground(String... strings) {
        entity.addPart("title",etsearch.getText().toString());
        post.setEntity(entity);

        try {
            response = client.execute(post);
            result = EntityUtils.toString(response.getEntity());
            ja = new JSONArray(result);
            for(int i =0 ; i < ja.length();i++){
                count++;
            }


            pinids = new String[count];
            title = new String[count];
            icon = new String[count];


            for(int i =0;i<ja.length();i++){
                jo = ja.getJSONObject(i);
                title[i] = jo.getString("title");
                icon[i] = "http://pintrestproj.net46.net/"+jo.getString("imgurl");
                pinids[i]= jo.getString("id");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            searchresult = new String[0];
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        pdd.dismiss();
        if (count>0){
            rv.setVisibility(View.VISIBLE);
            adapter = new HomeListAdapter(Search.this, senddata());
            rv.setAdapter(adapter);
            rv.setLayoutManager(new GridLayoutManager(Search.this,2));
        }else{
            Toast.makeText(Search.this, "Error getting pins.", Toast.LENGTH_SHORT).show();
        }
    }
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
            Picasso.with(Search.this).load(url).into(holder.iv);
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
                startActivity(new Intent(Search.this, Pin.class));
            }
        }

    }
}
