package com.example.renukamatkar.pinterest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.LinearLayout;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by AVIN PC on 17-10-2015.
 */
public class MyFollowers1 extends Fragment {

    Button crboard,bboard,crboard1;
    EditText etbname;
    Dialog d;
    SharedPreferences sp;
    RecyclerView rv;
    String title[],date[];
    LinearLayout boardsll;
    List<Boardinfo> biib;
    BoardAdapter adapter;
    TextView cboard;
    String userids[];
    SharedPreferences.Editor edt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.myboards,container,false);
        sp = getActivity().getSharedPreferences("config",0);
        edt = sp.edit();
        rv = (RecyclerView)v.findViewById(R.id.boardlist);
        boardsll = (LinearLayout)v.findViewById(R.id.llboard);
        boardsll.setVisibility(View.GONE);
        GetBoards gb = new GetBoards();
        gb.execute();
        cboard= (TextView)v.findViewById(R.id.countboards);
        biib = new ArrayList<>();

        d = new Dialog(getActivity());
        d.setContentView(R.layout.dialogview);
        crboard = (Button)v.findViewById(R.id.createboard);
        crboard1 = (Button)v.findViewById(R.id.createboard1);
        crboard.setVisibility(View.GONE);
        crboard1.setVisibility(View.GONE);
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
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/getfollowers.php");
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
                entity.addPart("userid",Integer.toString(sp.getInt("",-1)));
                entity.addPart("bname",etbname.getText().toString());
                entity.addPart("btime",cc.get(Calendar.DAY_OF_MONTH)+"/"+cc.get(Calendar.MONTH)+"/"+cc.get(Calendar.YEAR));
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
                tvdate.setVisibility(View.GONE);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        edt.putString("clickeduser",title[getPosition()]);
                        edt.putString("clickeduserid",userids[getPosition()]);
                        edt.commit();
                        if(!sp.getString("clickeduserid","-1").contentEquals(Integer.toString(sp.getInt("userid",-1))))
                        startActivity(new Intent(getActivity(),ClickedUserDetails.class));
                        else{
                            startActivity(new Intent(getActivity(),User_Details.class));
                        }
                    }
                });
            }
        }

    }


    class GetBoards extends AsyncTask<String,Integer,String>{

        ProgressDialog pd;
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/getfollowers.php");
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
                entity.addPart("followingid",sp.getString("clickeduserid","-1"));
                post.setEntity(entity);
                response = client.execute(post);
                result = EntityUtils.toString(response.getEntity());

                ja = new JSONArray(result);
                for(int i =0;i<ja.length();i++)
                    count++;

                title = new String[count];
                date = new String[count];
                userids = new String[count];

                for(int i = 0;i<ja.length();i++)
                {
                    jo  = ja.getJSONObject(i);
                    title[i] = jo.getString("name");
                    //date[i] = "Created on: "+jo.getString("time");
                    userids[i] = jo.getString("id");
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

                crboard1.setVisibility(View.GONE);
                adapter = new BoardAdapter(getActivity() , senddata());
                rv.setAdapter(adapter);
                rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                cboard.setText(count+" Follower(s) found");
            }else{
                cboard.setText("No Follower(s) found");
                rv.setVisibility(View.GONE);
            }
        }
    }

}
