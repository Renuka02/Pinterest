package com.example.renukamatkar.pinterest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;


public class ClickedUserDetails extends ActionBarActivity {

    SlidingTabLayout tabs;
    ViewPager pager;
    TextView username;
    SharedPreferences sp;
    SharedPreferences.Editor edt;
    Button follow;
    String dpurl = "";
    ImageView disppic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_user_details);
        tabs = (SlidingTabLayout)findViewById(R.id.tabs);
        disppic = (ImageView)findViewById(R.id.userdp);
        follow = (Button)findViewById(R.id.followuser);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendFollow sf = new SendFollow();
                sf.execute();
            }
        });
        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        tabs.setViewPager(pager);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.RED;
            }
        });
        sp = getSharedPreferences("config",0);
        username = (TextView)findViewById(R.id.username);
        username.setText(sp.getString("clickeduser","null"));
        edt = sp.edit();
        GetFollow gff = new GetFollow();
        gff.execute();
        GetDp gdp = new GetDp();
        gdp.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user__details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            edt.putInt("login",0);
            edt.commit();

            startActivity(new Intent(ClickedUserDetails.this,Home_page.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    class MyPagerAdapter extends FragmentPagerAdapter {


        String tabs[]={"PINS","BOARDS","LIKES","FOLLOWING","FOLLOWERS"};

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch(i){
                case 0:
                    return new MyPins1();
                case 1:
                    return new MyBoards1();
                case 2:
                    return new LikedPins1();
                case 3:
                    return new MyFollowings1();
                case 4:
                    return new MyFollowers1();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }




    class GetFollow extends AsyncTask<String,Integer,String>{


        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/getfollow.php");
        MultipartEntity entity = new MultipartEntity();
        HttpResponse response;
        String result;
        int fol = 0;
        JSONObject jo =null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            entity.addPart("followerid",Integer.toString(sp.getInt("userid", -1)));
            entity.addPart("followingid",sp.getString("clickeduserid","-1"));
            post.setEntity(entity);
            try {
                response = client.execute(post);
                result = EntityUtils.toString(response.getEntity());
                jo = new JSONObject(result);
                fol = jo.getInt("following");
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
            if(fol == 0){
                follow.setText("FOLLOW");
            }else{
                follow.setText("UNFOLLOW");
            }
        }
    }


    class SendFollow extends AsyncTask<String,Integer,String>{


        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/followsomeone.php");
        MultipartEntity entity = new MultipartEntity();
        HttpResponse response;
        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            entity.addPart("followerid",Integer.toString(sp.getInt("userid", -1)));
            entity.addPart("followingid",sp.getString("clickeduserid","-1"));
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
            if(follow.getText().toString().contentEquals("FOLLOW")){
                follow.setText("UNFOLLOW");
            }else{
                follow.setText("FOLLOW");
            }
        }
    }


    class GetDp extends AsyncTask<String,Integer,String>{


        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/getdp.php");
        HttpResponse response;
        MultipartEntity entity = new MultipartEntity();
        String result;
        JSONObject jo,jodata;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            entity.addPart("userid",sp.getString("clickeduserid","-1"));
            post.setEntity(entity);
            try {
                response = client.execute(post);
                result = EntityUtils.toString(response.getEntity());
                jo = new JSONObject(result);
                jodata = jo.getJSONObject("data");
                dpurl = "http://pintrestproj.net46.net/"+jodata.getString("imgurl");
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
            if(jodata!=null)
            Picasso.with(ClickedUserDetails.this).load(dpurl).into(disppic);
            else
            disppic.setImageResource(R.drawable.noimg);
        }
    }


}
