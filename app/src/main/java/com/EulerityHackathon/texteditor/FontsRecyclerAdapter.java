package com.EulerityHackathon.texteditor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.EulerityHackathon.texteditor.retrofit.Response.Fonts;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class FontsRecyclerAdapter extends RecyclerView.Adapter<FontsRecyclerAdapter.MyViewHolder> {

    HashMap<Integer,String[]> familyNames;
    Context context;
    List<Fonts> fontsList;


    public FontsRecyclerAdapter(List<Fonts> fontsList,HashMap<Integer,String[]> familyNames, Context context){

        this.familyNames=familyNames;                                   //Initialize adapter with three parameters
        this.context=context;
        this.fontsList=fontsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.row_fonts, viewGroup, false);                         //Inflate cardview

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        String [] temp=familyNames.get(i);
        String name=temp[0];
        String fileName=temp[1];                                            //Set one of the typeface of each family
        myViewHolder.familyname.setText(name);                              //for displaying purpose
        myViewHolder.familyname.setTextColor(context.getResources().getColorStateList(R.color.textColor));
        CardView cardView=myViewHolder.cardView;
        cardView.setCardBackgroundColor(context.getResources().getColorStateList(R.color.cardBackground));
        TextView tv=myViewHolder.sampletext;
        tv.setTextColor(context.getResources().getColorStateList(R.color.textColor));
        Typeface tf= Typeface.createFromFile(new File(Environment.getExternalStorageDirectory().toString() + "/" + fileName));
        tv.setTypeface(tf);                                                    //Get the font file by its filename

        ImageView editBtn= myViewHolder.edit;

        editBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                showPopupMenu(v, name);                                     //Show list of style options i.e bold,italic,etc.


            }
        });

    }
    private void showPopupMenu(View view, String name) {                            //Show menu when editing button is clicked
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.bothmenu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(name));
        popup.show();
    }

    public int getItemCount() {                                             //Total of cards is size of the hashmap
        return familyNames.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {                  //Customed viewholder that has necessar fields

        TextView familyname, sampletext;
        CardView cardView;
        ImageView edit;
        public MyViewHolder(View v) {
            super(v);
            familyname=(TextView) v.findViewById(R.id.familyname);
            sampletext=v.findViewById(R.id.sample);
            cardView=v.findViewById(R.id.card);
            edit=v.findViewById(R.id.chooseEdit);
        }

    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private String nameToCheck;
        public MyMenuItemClickListener(String name){
            nameToCheck=name;
        }
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                    case R.id.bold:                                     //Bold style
                        String info[]=new String[4];  //0:filename 1: familyname 2:bold 3: italic
                        if(!hasOption(4, nameToCheck,info)){                        //Check if this option is availble for this font
                            Toast.makeText(context, "Option Not Available", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent= new Intent(context,EditingPanel.class);
                            intent.putExtra("FILE_NAME",info[0]);
                            intent.putExtra("FAMILY_NAME",info[1]);
                            intent.putExtra("BOLD",info[2]);
                            intent.putExtra("ITALIC",info[3]);
                            context.startActivity(intent);                  //Triggering editing panel activity and passing all the information
                        }                                                   //to access this particular style of font in next activity
                        return true;
                    case R.id.neither:                                      //Regular style
                        String info1[]=new String[4];
                        if(!hasOption(3, nameToCheck,info1)){
                            Toast.makeText(context, "Option Not Available", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent= new Intent(context,EditingPanel.class);
                            intent.putExtra("FILE_NAME",info1[0]);
                            intent.putExtra("FAMILY_NAME",info1[1]);
                            intent.putExtra("BOLD",info1[2]);
                            intent.putExtra("ITALIC",info1[3]);
                            context.startActivity(intent);
                        }
                        return true;
                    case R.id.ita:                                                  //italic style
                        String info2[]=new String[4];
                        if(!hasOption(2, nameToCheck,info2)){
                            Toast.makeText(context, "Option Not Available", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent= new Intent(context,EditingPanel.class);
                            intent.putExtra("FILE_NAME",info2[0]);
                            intent.putExtra("FAMILY_NAME",info2[1]);
                            intent.putExtra("BOLD",info2[2]);
                            intent.putExtra("ITALIC",info2[3]);
                            context.startActivity(intent);
                        }
                        return true;
                    case R.id.both:                                                 //It has both italic and bold
                        String info3[]=new String[4];
                        if(!hasOption(1, nameToCheck,info3)){
                            Toast.makeText(context, "Option Not Available", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intent= new Intent(context,EditingPanel.class);
                            intent.putExtra("FILE_NAME",info3[0]);
                            intent.putExtra("FAMILY_NAME",info3[1]);
                            intent.putExtra("BOLD",info3[2]);
                            intent.putExtra("ITALIC",info3[3]);
                            context.startActivity(intent);
                        }
                        return true;
            }
            return false;
        }
    }

    private boolean hasOption(int flag, String checkingName,String [] info){            //Logic to see if currently selected option is availble
                                                                                        //0:filename 1: familyname 2:bold 3: italic
        if(flag==4){

            for(Fonts font: fontsList){

                if(font.family.equals(checkingName)){

                    if(font.bold.equals("true")&&font.italic.equals("false")){
                        info[0]=font.url.substring(7);
                        info[1]=font.family;
                        info[2]="true";
                        info[3]="false";
                        return true;

                    }
                }

            }
            return false;

        }
        else if(flag==3){
            for(Fonts font: fontsList){

                if(font.family.equals(checkingName)){

                    if(font.bold.equals("false")&&font.italic.equals("false")){
                        info[0]=font.url.substring(7);
                        info[1]=font.family;
                        info[2]="false";
                        info[3]="false";
                        return true;
                    }
                }

            }
            return false;

        }
        else if(flag==2){
            for(Fonts font: fontsList){

                if(font.family.equals(checkingName)){

                    if(font.bold.equals("false")&&font.italic.equals("true")){
                        info[0]=font.url.substring(7);
                        info[1]=font.family;
                        info[2]="false";
                        info[3]="true";
                        return true;
                    }
                }

            }
            return false;

        }
        else if(flag==1){
            for(Fonts font: fontsList){

                if(font.family.equals(checkingName)){

                    if(font.bold.equals("true")&&font.italic.equals("true")){
                        info[0]=font.url.substring(7);
                        info[1]=font.family;
                        info[2]="true";
                        info[3]="true";
                        return true;
                    }
                }

            }
            return false;

        }
        return false;
    }
}
