package com.example.renukamatkar.pinterest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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


public class Pin extends ActionBarActivity {


    SharedPreferences sp;
    SharedPreferences.Editor edt;
    String pinid = "-1";
    String title,desc,imgurl,time,username,userid;
    TextView tvtitle,tvdesc,tvuser,tvcount;
    ImageView iv;
    ImageView likeiv;
    int gllike = 0;
    Button pinit,pinonmyboard;
    Dialog d;
    Spinner boardsspinner;
    ArrayAdapter adapter;
    String boards[],bids[];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        sp = getSharedPreferences("config",0);
        edt = sp.edit();
        pinid = sp.getString("pinclicked", "-1");
        tvcount = (TextView)findViewById(R.id.countlikes);
        tvtitle = (TextView)findViewById(R.id.pinacttitle);
        tvdesc = (TextView)findViewById(R.id.pinactdesc);
        iv = (ImageView)findViewById(R.id.pinactiv);
        tvuser = (TextView)findViewById(R.id.pinactuser);
        likeiv = (ImageView)findViewById(R.id.like);
        GetLike gll = new GetLike();
        gll.execute();
        CountLikes cll = new CountLikes();
        cll.execute();
        d = new Dialog(Pin.this);
        d.setContentView(R.layout.pinitdialog);
        d.setTitle("SELECT BOARD");
        boardsspinner = (Spinner)d.findViewById(R.id.pinspinner);
        pinonmyboard = (Button)d.findViewById(R.id.otherpinit);
        pinonmyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PinonMyboard pob = new PinonMyboard();
                pob.execute();
            }
        });
        GetBoards gbb = new GetBoards();
        gbb.execute();
        pinit = (Button)findViewById(R.id.pinonmyboard);
        pinit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            d.show();
            }
        });
        likeiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // like.setImageResource(R.drawable.hrtfill);
                SendLike sll = new SendLike();
                sll.execute();
            }
        });
        GetAPin gp = new GetAPin();
        gp.execute();
        tvuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt.putString("clickeduser",username);
                edt.putString("clickeduserid",userid);
                edt.commit();
                if(Integer.parseInt(userid)!= sp.getInt("userid",-1))
                startActivity(new Intent(Pin.this,ClickedUserDetails.class));
                else
                startActivity(new Intent(Pin.this,User_Details.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pin, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class GetAPin extends AsyncTask<String,Integer,String>{

        ProgressDialog pdd;

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/getapin.php");
        HttpResponse response;
        String result;
        MultipartEntity entity = new MultipartEntity();

        JSONArray ja = null;
        JSONObject jo;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdd = new ProgressDialog(Pin.this);
            pdd.setMessage("Loading");
            pdd.setCanceledOnTouchOutside(false);
            pdd.setCancelable(false);
            pdd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            entity.addPart("pinid",pinid);
            post.setEntity(entity);
            try {
                response = client.execute(post);
                result = EntityUtils.toString(response.getEntity());

                ja = new JSONArray(result);
                jo = ja.getJSONObject(0);
                title = jo.getString("title");
                desc = jo.getString("description");
                username = jo.getString("name");
                userid = jo.getString("user_id");
                imgurl = "http://pintrestproj.net46.net/"+jo.getString("imgurl");
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
            tvtitle.setText(title);
            tvdesc.setText(desc);
            tvuser.setText("By : "+username);
            if(!imgurl.contentEquals("http://pintrestproj.net46.net/"))
            Picasso.with(Pin.this).load(imgurl).into(iv);
            else{
                Picasso.with(Pin.this).load("https://mb3is.megx.net/net.megx.esa/img/no_photo.png").into(iv);
            }
        }
    }


    class SendLike extends AsyncTask<String,Integer,String>{


        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/sendlike.php");
        HttpResponse response;
        String result;
        MultipartEntity entity = new MultipartEntity();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            entity.addPart("pinid",pinid);
            entity.addPart("userid",Integer.toString(sp.getInt("userid",-1)));
            try {
                post.setEntity(entity);
                response = client.execute(post);
                result = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CountLikes clikes = new CountLikes();
            clikes.execute();
            if(gllike == 1){
                likeiv.setImageResource(R.drawable.hrt);
                gllike =0;
            }else{
                likeiv.setImageResource(R.drawable.hrtfill);
                gllike = 1;
            }
        }
    }



    class GetLike extends AsyncTask<String,Integer,String>{

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/getlike.php");
        HttpResponse response;
        MultipartEntity entity= new MultipartEntity();
        String result;

        JSONObject jo;
        int like=0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            entity.addPart("pinid",pinid);
            entity.addPart("userid",Integer.toString(sp.getInt("userid",-1)));
            try {
                post.setEntity(entity);
                response = client.execute(post);
                result = EntityUtils.toString(response.getEntity());
                jo = new JSONObject(result);
                like = jo.getInt("like");
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
            gllike = like;
            if(like==1){
                likeiv.setImageResource(R.drawable.hrtfill);
            }else{
                likeiv.setImageResource(R.drawable.hrt);
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

                boards=new String[count];
                bids = new String[count];

                for(int i = 0;i<ja.length();i++)
                {
                    jo  = ja.getJSONObject(i);
                    boards[i] = jo.getString("name");
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
            //pd.dismiss();
            if(count>0){
                adapter = new ArrayAdapter(Pin.this,android.R.layout.simple_list_item_1,boards);
                boardsspinner.setAdapter(adapter);
            }
            else{
                d.setTitle("No Boards");
                boardsspinner.setVisibility(View.GONE);
            }
        }
    }




   class PinonMyboard extends AsyncTask<String,Integer,String>{


       ProgressDialog pdd;

       HttpClient client = new DefaultHttpClient();
       HttpPost post = new HttpPost("http://pintrestproj.net46.net/otherpin.php");
       HttpResponse response;
       String result;
       MultipartEntity entity = new MultipartEntity();


       @Override
       protected void onPreExecute() {
           super.onPreExecute();
        pdd = new ProgressDialog(Pin.this);
           pdd.setMessage("Pinning It");
           pdd.show();
           pdd.setCanceledOnTouchOutside(false);
       }

       @Override
       protected String doInBackground(String... strings) {
           entity.addPart("userid",Integer.toString(sp.getInt("userid",-1)));
           entity.addPart("pinid",pinid);
           entity.addPart("bid",bids[boardsspinner.getSelectedItemPosition()]);
           post.setEntity(entity);

           try {
               response = client.execute(post);
               result = EntityUtils.toString(response.getEntity());
           } catch (IOException e) {
               e.printStackTrace();
           }
           return null;
       }

       @Override
       protected void onPostExecute(String s) {
           super.onPostExecute(s);
           pdd.dismiss();
           Toast.makeText(Pin.this,"Pinned",Toast.LENGTH_SHORT).show();
           d.dismiss();
       }
   }


    class CountLikes extends AsyncTask<String,Integer,String>{


        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/countlikes.php");
        HttpResponse response;
        MultipartEntity etity = new MultipartEntity();
        String result;
        JSONObject jo = null;
        String likecount=null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            etity.addPart("pinid",pinid);
            post.setEntity(etity);

            try {
                response = client.execute(post);
                result = EntityUtils.toString(response.getEntity());
                jo = new JSONObject(result);
                likecount = jo.getString("count");
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
            if(likecount!= null){
                tvcount.setText(likecount+" Like(s)");
            }
        }
    }
}
