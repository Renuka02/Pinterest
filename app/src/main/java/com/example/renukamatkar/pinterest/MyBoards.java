package com.example.renukamatkar.pinterest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class MyBoards extends Fragment {

    Button crboard,bboard,crboard1;
    EditText etbname;
    Dialog d;
    SharedPreferences sp;
    SharedPreferences.Editor edt;
    RecyclerView rv;
    String title[],date[];
    LinearLayout boardsll;
    List<Boardinfo> biib;
    BoardAdapter adapter;
    TextView cboard;
    EditText tilldate;
    CheckBox cbtilldate,cblocboard;
    DatePickerDialog dpd;
    Calendar usercal;
    Dialog d1;
    RatingBar rb1;
    Button gorating,viewboardpins;
    String bids[];
    LocationManager lm;
    Location l;
    Float lat,lon;
    Criteria cc = new Criteria();
    String cityname="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.myboards,container,false);
        sp = getActivity().getSharedPreferences("config",0);
        edt = sp.edit();
        lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        rv = (RecyclerView)v.findViewById(R.id.boardlist);
        boardsll = (LinearLayout)v.findViewById(R.id.llboard);
        GetBoards gb = new GetBoards();
        gb.execute();
        cboard= (TextView)v.findViewById(R.id.countboards);
        biib = new ArrayList<>();
        d = new Dialog(getActivity());
        d.setContentView(R.layout.dialogview);
        d.setTitle("New Board");
        cblocboard = (CheckBox)d.findViewById(R.id.cblocboard);
        d1 = new Dialog(getActivity());
        d1.setContentView(R.layout.showrating);
        d1.setTitle("Board Rating");
        viewboardpins = (Button)d1.findViewById(R.id.viewboardpins);
        viewboardpins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),DisplayBoardPins.class));
            }
        });
        rb1 = (RatingBar)d1.findViewById(R.id.ratbar);
        rb1.setActivated(false);
        gorating = (Button)d1.findViewById(R.id.gorating);
        gorating.setVisibility(View.GONE);
        usercal = Calendar.getInstance();
        tilldate = (EditText)d.findViewById(R.id.boardtilldate);
        cbtilldate = (CheckBox)d.findViewById(R.id.cbtilldate);
        cbtilldate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    tilldate.setVisibility(View.GONE);
                }else
                {
                    tilldate.setVisibility(View.VISIBLE);
                }
            }
        });
        tilldate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    dpd = new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                            tilldate.setText(i+"-"+(i2+1)+"-"+i3);
                        }
                    },usercal.get(Calendar.YEAR),usercal.get(Calendar.MONTH),usercal.get(Calendar.DAY_OF_MONTH));
                    // dpd.getDatePicker().setMinDate(System.currentTimeMillis());
                    dpd.show();
                }
            }
        });
        crboard = (Button)v.findViewById(R.id.createboard);
        crboard1 = (Button)v.findViewById(R.id.createboard1);
        crboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.show();
            }
        });
        crboard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.show();
            }
        });
        bboard = (Button)d.findViewById(R.id.bcreateboard);
        etbname=(EditText)d.findViewById(R.id.etbname);
        bboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateBoard cb = new CreateBoard();
                cb.execute();
            }
        });
        cblocboard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 50, new LocationListener() {

                        @Override
                        public void onLocationChanged(Location location) {
                            l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            lat = (float) l.getLatitude();
                            lon = (float) l.getLongitude();
                            //cityname.setText(" "+lat+","+lon);
                            //GetCityName cn = new GetCityName();
                            //cn.execute();
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {

                        }

                        @Override
                        public void onProviderDisabled(String s) {

                        }
                    });
                    l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    lat = (float) l.getLatitude();
                    lon = (float) l.getLongitude();
                    GetCityName gcn = new GetCityName();
                    gcn.execute();
                }else{
                    cityname = "";
                }
            }
        });
        return v;
    }

    private List<Boardinfo> senddata() {
        biib = new ArrayList<>();
        for(int i = 0 ; i<title.length;i++)
        {
            Boardinfo bi = new Boardinfo();
            bi.title = title[i];
            bi.date = date[i];
            biib.add(bi);
        }
        return biib;
    }


    class CreateBoard extends AsyncTask<String,Integer,String>{


        ProgressDialog pd;


        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/createboard.php");
        MultipartEntity entity = new MultipartEntity();
        HttpResponse response;
        String result;
        int success=0;

        JSONObject jo;
        Calendar cc = Calendar.getInstance();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Creating Board");
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                entity.addPart("userid",Integer.toString(sp.getInt("userid",-1)));
                entity.addPart("bname",etbname.getText().toString());
                entity.addPart("btime",cc.get(Calendar.DAY_OF_MONTH)+"/"+cc.get(Calendar.MONTH)+"/"+cc.get(Calendar.YEAR));
                entity.addPart("tilldate",tilldate.getText().toString());
                entity.addPart("city",cityname);
                post.setEntity(entity);
                response = client.execute(post);
                result = EntityUtils.toString(response.getEntity());
                jo = new JSONObject(result);
                success = jo.getInt("success");
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

            if(success == 1){
                boardsll.setVisibility(View.GONE);
                Toast.makeText(getActivity(),"New Board Created!!",Toast.LENGTH_SHORT).show();
                Boardinfo brdinf = new Boardinfo();
                brdinf.title = etbname.getText().toString();
                brdinf.date = cc.get(Calendar.DAY_OF_MONTH)+"/"+cc.get(Calendar.MONTH)+"/"+cc.get(Calendar.YEAR);
                biib.add(brdinf);
                startActivity(new Intent(getActivity(),User_Details.class));
                getActivity().finish();
                //adapter.notifyDataSetChanged();

            }else{
                Toast.makeText(getActivity(),"Error Occurred!!",Toast.LENGTH_SHORT).show();
            }
            d.dismiss();
        }
    }



    class Boardinfo{
        String title,date;
    }


    class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardHolder>{


        Context cc;
        List<Boardinfo> bi = Collections.emptyList();
        LayoutInflater inflater;

        public BoardAdapter(Context c , List<Boardinfo> data){
            cc = c;
            bi = data;
            inflater=LayoutInflater.from(c);
        }

        @Override
        public BoardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.customboard,parent,false);
            BoardHolder holder = new BoardHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(BoardHolder holder, int position) {
            Boardinfo info = bi.get(position);
            holder.tvname.setText(info.title);
            holder.tvdate.setText(info.date);
        }

        @Override
        public int getItemCount() {
            return bi.size();
        }

        class BoardHolder extends RecyclerView.ViewHolder{

            TextView tvname, tvdate;

            public BoardHolder(View itemView) {
                super(itemView);
                tvname=(TextView)itemView.findViewById(R.id.tvboardname);
                tvdate = (TextView)itemView.findViewById(R.id.tvboarddate);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        edt.putString("boardclicked",bids[getPosition()]);
                        edt.commit();
                        GetRating grr = new GetRating();
                        grr.execute();
                        d1.show();
                    }
                });
            }
        }

    }


    class GetBoards extends AsyncTask<String,Integer,String>{

        ProgressDialog pd;
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/getboards.php");
        MultipartEntity entity = new MultipartEntity();

        HttpResponse response ;
        String result;

        JSONArray ja;
        JSONObject jo;
        int count =0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Displaying Boards");
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                entity.addPart("userid",Integer.toString(sp.getInt("userid",-1)));
                post.setEntity(entity);
                response = client.execute(post);
                result = EntityUtils.toString(response.getEntity());

                ja = new JSONArray(result);
                for(int i =0;i<ja.length();i++)
                    count++;

                title = new String[count];
                date = new String[count];
                bids = new String[count];

                for(int i = 0;i<ja.length();i++)
                {
                    jo  = ja.getJSONObject(i);
                    title[i] = jo.getString("name");
                    if(jo.getString("location").contentEquals(""))
                        date[i] = "Created on: "+jo.getString("time");
                    else
                        date[i] = "Created on: "+jo.getString("time")+" @ "+jo.getString("location");
                    bids[i] = jo.getString("id");

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
            if(count>0){
                boardsll.setVisibility(View.GONE);
                crboard1.setVisibility(View.VISIBLE);
                adapter = new BoardAdapter(getActivity() , senddata());
                rv.setAdapter(adapter);
                rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                cboard.setText(count+" Board(s) found");
            }else{
                rv.setVisibility(View.GONE);
            }
        }
    }


    class GetRating extends AsyncTask<String,Integer,String>{

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/getrating.php");
        MultipartEntity entity = new MultipartEntity();
        HttpResponse response;
        String result;

        JSONObject jo,jo1;
        float rating=0;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rb1.setRating(0);
        }

        @Override
        protected String doInBackground(String... strings) {
            entity.addPart("bid",sp.getString("boardclicked","-1"));
            post.setEntity(entity);

            try {
                response = client.execute(post);
                result = EntityUtils.toString(response.getEntity());
                jo = new JSONObject(result);
                jo1 = jo.getJSONObject("data");
                rating = Float.parseFloat(jo1.getString("rating"));
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
            rb1.setActivated(false);
            rb1.setClickable(false);
            if(jo1!=null){
                rb1.setRating(rating);
            }else{
                rb1.setRating(0);
            }
        }
    }



    class GetCityName extends AsyncTask<String,Integer,String>{

        ProgressDialog pdd;

        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet("http://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lon+"&sensor=true");
        HttpResponse response;
        String result,city,type;
        JSONArray ja,ja1;
        JSONObject jo,jo1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdd = new ProgressDialog(getActivity());
            pdd.setMessage("Getting your location");
            pdd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = client.execute(get);
                result = EntityUtils.toString(response.getEntity());
                jo = new JSONObject(result);
                ja = jo.getJSONArray("results");
                jo = ja.getJSONObject(0);
             /*   ja = jo.getJSONArray("address_components");
                for(int i = 0;i<ja.length();i++){
                    jo = ja.getJSONObject(i);
                    ja1 = jo.getJSONArray("types");
                    for(int j=0; j<ja1.length();j++){
                        type = ja1.getString(j);
                        if(type.contentEquals("locality")){
                            city = jo.getString("long_name");
                            break;
                        }
                    }
                 }
                    */
                city = jo.getString("formatted_address");
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
            cityname = city;
            pdd.dismiss();
            Toast.makeText(getActivity(),"Your location : "+city,Toast.LENGTH_SHORT).show();
        }
    }
}