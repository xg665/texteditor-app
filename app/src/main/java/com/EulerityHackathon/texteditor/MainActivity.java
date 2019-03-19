package com.EulerityHackathon.texteditor;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.os.Environment;
import android.provider.FontRequest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.EulerityHackathon.texteditor.retrofit.FontsAPI;
import com.EulerityHackathon.texteditor.retrofit.Response.Fonts;
import com.EulerityHackathon.texteditor.retrofit.RetrofitClient;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    String baseURL="https://eulerity-hackathon.appspot.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=(RecyclerView)findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));                   //Get recycler view
        showWelcom();
        RetrofitClient.newInstance().create(FontsAPI.class).getResponse()              //Initiate retrofit to for GET request
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setData,this::handleError);                            //If successful, then go to setData,where
                                                                                        //recyclerAdapter is set

    }
    private void setData(List<Fonts> fontsList) throws InterruptedException {

            List<String> urlList= new ArrayList<>();    //list of download links
            HashMap<Integer, String[]> fontsNames=new HashMap<>();
            int index=0;
            for(int i=0;i<fontsList.size();i++){
                urlList.add(new String(baseURL+fontsList.get(i).url)); //create a download link

                String [] temp=new String[2];
                temp[0]=fontsList.get(i).family;                           //Family names that associated
                temp[1]=fontsList.get(i).url.substring(7);              //File names that adapter needs

                if(okToAdd(fontsNames, temp[0])) {
                    fontsNames.put(index++,temp);                       //Only need to add one of family member for display
                }

            }

            for(int i=0;i<urlList.size();i++){
                new DownloadFileFromURL().execute(urlList.get(i),fontsList.get(i).url.substring(6)); //Download each font file according to its name
            }
            Thread.sleep(2000);                                         //Waiting for download to finish before setting adapter
            recyclerView.setAdapter(new FontsRecyclerAdapter(fontsList,fontsNames,this));
    }
    private void showWelcom(){                                              //Show user welcome dialog while waiting download

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Welcome To TextEditor");
            alert.setMessage("Choose Your Font Here");

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    return;

                }
            });
            alert.show();


    }
    private boolean okToAdd(HashMap<Integer,String []> map,String str){             //Check if map already contains one of the
                                                                                    //family members
        for(Map.Entry m: map.entrySet()){
            String [] temp=(String[])m.getValue();
            if(temp[0].equals(str)){
                return false;
            }
        }
        return true;

    }



    private void handleError(Throwable T){                                          //Handle error when failed to get json
        Toast.makeText(this, "Error in getting API", Toast.LENGTH_LONG).show();
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {           //Download font files in background threads
                                                                                    //via extending asynctask
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {                        //The first paremeter is url, the second is

            int count;                                                              //name of the file, which will be assigned
            try {
                String root = Environment.getExternalStorageDirectory().toString();

                URL url = new URL(strings[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                String filename=strings[1];
                OutputStream output = new FileOutputStream(root + filename);                    //Download to externalStorage
                byte data[] = new byte[1024];                                                   //and append filename

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);

                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }


        @Override
        protected void onPostExecute(String file_url) {                                 //After Downloading...

            System.out.print("Downloaded");
        }
    }
}
