package com.example.renukamatkar.pinterest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class User_Details extends ActionBarActivity {

    SlidingTabLayout tabs;
    ViewPager pager;
    TextView username;
    SharedPreferences sp;
    SharedPreferences.Editor edt;
    String path;
    Bitmap bm;
    ImageView disppic;
    String dpurl = "";
    int flag =0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__details);
        tabs = (SlidingTabLayout)findViewById(R.id.tabs);
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
        username.setText(sp.getString("username","null"));
        edt = sp.edit();
        disppic = (ImageView)findViewById(R.id.userdp);
        disppic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 1;
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 0);
            }
        });
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

            startActivity(new Intent(User_Details.this,Home_page.class));
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
                   return new MyPins();
                case 1:
                    return new MyBoards();
                case 2:
                    return new LikedPins();
                case 3:
                    return new MyFollowings();
                case 4:
                    return new MyFollowers();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
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
            entity.addPart("userid",Integer.toString(sp.getInt("userid",-1)));
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
                dpurl = "";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!dpurl.contentEquals(""))
            Picasso.with(User_Details.this).load(dpurl).into(disppic);
        }
    }


    class UpdateDp extends AsyncTask<Bitmap,Void,Void>{

        ProgressDialog pdd;
        HttpClient client = new DefaultHttpClient();
        HttpPost post= new HttpPost("http://pintrestproj.net46.net/uploaddp.php");
        MultipartEntity entity = new MultipartEntity();
        HttpResponse response;
        String result;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdd = new ProgressDialog(User_Details.this);
            pdd.setMessage("Updating Pic");
            pdd.show();
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            entity.addPart("userid",Integer.toString(sp.getInt("userid",-1)));
            if(bitmaps[0]!=null){
                Bitmap bitmap = bitmaps[0];
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
                InputStream in = new ByteArrayInputStream(stream.toByteArray());
                entity.addPart("cover",
                        System.currentTimeMillis() +path.charAt(2) +".jpg", in);

            }

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
        protected void onPostExecute(Void oid) {
            super.onPostExecute(oid);
            pdd.dismiss();

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && data!=null &&flag == 1){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            final String picturePath = cursor.getString(columnIndex);
            cursor.close();
            path = picturePath;
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            bm = BitmapFactory.decodeFile(picturePath,options);
            disppic.setImageBitmap(bm);
            flag=0;
            UpdateDp upd = new UpdateDp();
            upd.execute(bm);
        }
    }

}
