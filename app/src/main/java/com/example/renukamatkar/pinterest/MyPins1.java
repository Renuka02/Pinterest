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
public class MyPins1 extends Fragment {

    RecyclerView rv;
    String text[] = {"HII","HELO"};
    String icons[] = {"R.drawable.map","R.drawable.map"};
    String pinids[];
    LinearLayout lldisp;
    Button createpin,pinit;
    Dialog d;
    Spinner selectboard;
    String boards[] = {"first","second"};
    ArrayAdapter<String> adapter;
    SharedPreferences sp;
    ImageView selimg;
    String path;
    Bitmap bm;
    String bids[];
    EditText pintitle,pindesc;
    int selectedboard = 0;
    TextView countpins;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.mypins,container,false);
        sp = getActivity().getSharedPreferences("config",0);
        countpins = (TextView)v.findViewById(R.id.countpins);

        d = new Dialog(getActivity());
        d.setContentView(R.layout.pindialog);
        d.setTitle("New Pin");
        pintitle = (EditText)d.findViewById(R.id.pintitle);
        pindesc = (EditText)d.findViewById(R.id.pindescription);
        pinit = (Button)d.findViewById(R.id.pinit);
        pinit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubmitPin sbmpin = new SubmitPin();
                sbmpin.execute(bm);
            }
        });
        selectboard = (Spinner)d.findViewById(R.id.selectboard);

        selimg = (ImageView)d.findViewById(R.id.selimg);
        selimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                selimg.setImageResource(R.drawable.noimg);
                startActivityForResult(i, 0);
            }
        });
        createpin = (Button)v.findViewById(R.id.createpin);
        createpin.setVisibility(View.GONE);
        pinit.setVisibility(View.GONE);
        createpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.show();
            }
        });
        GetBoards gb = new GetBoards();
        gb.execute();
        rv = (RecyclerView)v.findViewById(R.id.allpins);
        lldisp = (LinearLayout)v.findViewById(R.id.pinsll);
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==getActivity().RESULT_OK && data!=null){
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            final String picturePath = cursor.getString(columnIndex);
            cursor.close();
            path = picturePath;
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            bm = BitmapFactory.decodeFile(picturePath,options);
            selimg.setImageBitmap(bm);
        }
    }


    class GetBoards extends AsyncTask<String,Integer,String> {

        // ProgressDialog pd;
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
            //   pd = new ProgressDialog(getActivity());
            //   pd.setMessage("Displaying Boards");
            //  pd.show();
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

                boards = new String[count];
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
            // pd.dismiss();
            if(count>0){
                //   boardsll.setVisibility(View.GONE);
                // crboard1.setVisibility(View.VISIBLE);
                //adapter = new BoardAdapter(getActivity() , senddata());
                // rv.setAdapter(adapter);
                //rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                //cboard.setText(count+" Board(s) found");
                adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,boards);
                selectboard.setAdapter(adapter);

            }
        }
    }


    class SubmitPin extends AsyncTask<Bitmap,Void,Void>{


        ProgressDialog pd;


        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/createpin.php");
        MultipartEntity entity = new MultipartEntity();
        HttpResponse response;
        String result;

        int success = 0;
        JSONObject jo = null;

        Calendar cal = Calendar.getInstance();



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Pinning It");
            pd.show();
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            entity.addPart("title",pintitle.getText().toString());
            entity.addPart("desc",pindesc.getText().toString());
            entity.addPart("userid",Integer.toString(sp.getInt("clickeduserid", -1)));
            selectedboard = selectboard.getSelectedItemPosition();
            entity.addPart("bid",bids[selectedboard]);
            entity.addPart("time",cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR));

            if(bitmaps[0]!=null){
                Bitmap bitmap = bitmaps[0];
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
                InputStream in = new ByteArrayInputStream(stream.toByteArray());
                entity.addPart("cover",
                        System.currentTimeMillis() +path.charAt(2) +".jpg", in);
            }

            post.setEntity(entity);
            try {
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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            d.dismiss();
            pintitle.setText("");
            pindesc.setText("");
            selimg.setImageResource(R.drawable.noimg);

            if(success==1){
                Toast.makeText(getActivity(),"Pin Created Successfully",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(),"Error creating pin!!",Toast.LENGTH_SHORT).show();
            }
        }
    }


    class GetUserPins extends AsyncTask<String,Integer,String>{


        ProgressDialog pdd;

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/getuserpins.php");
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
            entity.addPart("pinid",sp.getString("clickeduserid","-1"));
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
                lldisp.setVisibility(View.GONE);
                rv.setAdapter( new PinsAdapter(getActivity() , senddata() ,pinids));
                rv.setLayoutManager(new GridLayoutManager(getActivity(),3));
                countpins.setText(text.length+" Pins(s) found");
            }else{
                rv.setVisibility(View.GONE);
            }

        }
    }

}
