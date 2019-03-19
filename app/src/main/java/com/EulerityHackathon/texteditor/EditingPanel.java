package com.EulerityHackathon.texteditor;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class EditingPanel extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panelediting);

        ImageView img= findViewById(R.id.backbtn);
        img.setOnClickListener(new View.OnClickListener() {                         //Back button. If pressed, then go back to previous page
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        String filename=getIntent().getStringExtra("FILE_NAME");                        //locate the font file that the users selected
        String root = Environment.getExternalStorageDirectory().toString();
        Typeface tf= Typeface.createFromFile(new File(root+"/"+filename));                 //Set the type face to EditText
        EditText et=findViewById(R.id.edittextsmall);
        EditText et1=findViewById(R.id.edittextmedium);
        EditText et2=findViewById(R.id.edittextlarge);
        et.setTypeface(tf);                                                             //Create three different size of Edit text for
        et1.setTypeface(tf);                                                                //user experience
        et2.setTypeface(tf);
        et.getText();
        SyncTextWatcher syncTextWatcher=new SyncTextWatcher();
        syncTextWatcher.addEditText(et,et1,et2);

        String familyname=getIntent().getStringExtra("FAMILY_NAME");                        //assembling different parts for posting request
        String bold=getIntent().getStringExtra("BOLD");
        String italic=getIntent().getStringExtra("ITALIC");
        String result = et.getText().toString();
        String url="/fonts/"+filename;
        String [] body= new String[6];
        body[1]=familyname;body[2]=bold;body[3]=italic;body[4]=result;body[5]=url;
        ImageView savebut=findViewById(R.id.savebtn);
        savebut.setOnClickListener(new View.OnClickListener(){                              //If saved button is clicked, then let user enter
            @Override                                                                       //its email address as its unique id
            public void onClick(View view){

                showAlert(body);

            }
        });

    }
    class SyncTextWatcher implements TextWatcher {                                          //TextWatch class for synchonizing three EditText views
        private List<EditText> editTexts = new ArrayList<>();
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
        @Override
        public void afterTextChanged(Editable s) {

            for (int i = 0; i < editTexts.size(); i++) {
                EditText editText = editTexts.get(i);
                if(editText.getText()==s)continue;
                editText.removeTextChangedListener(this);
                editText.setText(s.toString());
                editText.addTextChangedListener(this);
            }

        }
        public void addEditText(EditText... editTexts) {
            for (int i = 0; i < editTexts.length; i++){
                this.editTexts.add(editTexts[i]);
                editTexts[i].addTextChangedListener(this);
            }
        }
        public void removeEditText(EditText editText) {
            boolean b = editTexts.remove(editText);
            if (b) editText.removeTextChangedListener(this);
        }
    }

    private void showAlert(String [] body){                                         //Alert function that prompt user to enter
        AlertDialog.Builder alert = new AlertDialog.Builder(this);                  //email address

        alert.setTitle("Save?");
        alert.setMessage("Type Your Email Below");


        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                body[0]=value;                                                      //Store id to String array and passed it to
                new doPostAsync().execute(body);                                       //Asynctask to POST JSON
                return;
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {           //If clicked cancel, nothing happens,
            public void onClick(DialogInterface dialog, int whichButton) {                  //User can keep editing
                return;
            }
        });

        alert.show();

    }

    private void handlePost(String [] body){                                        //Method that takes all information in String array


        try {
            JSONObject jsonbody= new JSONObject();
            jsonbody.put("appid",body[0]);
            jsonbody.put("fontFamilyName",body[1]);
            jsonbody.put("bold",body[2]);
            jsonbody.put("italic",body[3]);
            jsonbody.put("textTyped",body[4]);
            jsonbody.put("url",body[5]);
            URL url = new URL("https://eulerity-hackathon.appspot.com/makeText");               //Build the json body
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type", "application/json");                    //Set appropriate property to connection
            httpConn.setDoOutput(true);
            OutputStream os = httpConn.getOutputStream();
            os.write(jsonbody.toString().getBytes());
            os.flush();
            os.close();
            int responseCode = httpConn.getResponseCode();
            Log.d("response code:","POST Response Code :  " + responseCode);

            if (responseCode == 200) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in .readLine()) != null) {

                    response.append(inputLine);

                }
                in.close();
                Log.d("response:",response.toString());
            } else {
                Log.d("response","POST NOT WORKED");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private class doPostAsync extends AsyncTask<String[],String,String>{                    //Asynctask to do post request in background thread



        @Override
        protected String doInBackground(String[]... strings) {
            handlePost(strings[0]);
            return null;
        }
        @Override
        protected void onPostExecute(String file_url) {                                     //After completing post request, update UI thread

            onBackPressed();                                                                //In this case, perform onBackPressed
        }
    }
}
