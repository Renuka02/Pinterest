package com.example.renukamatkar.pinterest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Login_page extends AppCompatActivity {

    EditText email,pass;
    Button btnlogin;
    SharedPreferences sp;//koi b s/w chalu krte h to prefeences hoti h data stored rhta h data ko persist krta h
    SharedPreferences.Editor edt;// edt store krta h
    String name;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        email = (EditText)findViewById(R.id.EmailSigninText);
        pass = (EditText)findViewById(R.id.PasswordSigninText);
        btnlogin = (Button)findViewById(R.id.LoginSIginButton);
        sp = getSharedPreferences("config",0);//preference ko banan padta h agr exist krti h to create nai krta ya phr create kr k deta h
        edt = sp.edit();//                      private public shared shared me apps acces kr sakte h // edit krne ka kaam edt ka
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TryLogin tl = new TryLogin();
                tl.execute();
            }
        });
    }




        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_page, menu);
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


    class TryLogin extends AsyncTask<String,Integer,String>{

        ProgressDialog pd;


        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://pintrestproj.net46.net/login.php");
        MultipartEntity entity = new MultipartEntity();
        HttpResponse response;
        String result;
        JSONObject jo;
        JSONObject ja = null;
        int success = 0;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           pd = new ProgressDialog(Login_page.this);
            pd.setMessage("Logging In");
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
            entity.addPart("email",email.getText().toString());
            entity.addPart("pass",pass.getText().toString());
            post.setEntity(entity);
            response = client.execute(post);

                result = EntityUtils.toString(response.getEntity());

                jo = new JSONObject(result);
                success = jo.getInt("success");
                if(success == 1){//app me ki na koi login h
                    ja = jo.getJSONObject("data");
                    name = ja.getString("name");
                    id = ja.getInt("id");
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
            if(success == 1)
            {
                Toast.makeText(Login_page.this,"Login successfull name: "+name+" id: "+id,Toast.LENGTH_LONG).show();
                edt.putInt("userid",id);//php ne la k diya login ho gya
                edt.putString("username",name);
                edt.putInt("login",1);
                edt.commit();//baar baar login na krna pade
                startActivity(new Intent(Login_page.this,MainHomePage.class));
                finish();
            }else{
                Toast.makeText(Login_page.this,"INVALID CREDENTIALS!!",Toast.LENGTH_LONG).show();
            }
        }
    }
}
