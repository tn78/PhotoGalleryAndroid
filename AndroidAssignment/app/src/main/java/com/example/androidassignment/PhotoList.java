package com.example.androidassignment;
//Alexander Nocciolo, Tejas Nimkar
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;


public class PhotoList extends AppCompatActivity implements Serializable {

    //Album currentAlb = MainActivity.openedAlb;

    public void serialize() throws IOException
    {
//        try{
//            FileOutputStream fos = context.openFileOutput("Albums.dat", MODE_PRIVATE);
//            for(Album a: albList)
//            {
//                fos.writeObject()
//            }
//        }
        //File file = new File("Albums.dat");

        FileOutputStream fos = this.openFileOutput("Albums.dat", MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        //oos.writeObject(this);
        for(Album a: MainActivity.albList)
        {
            oos.writeObject(a);
        }
        oos.writeObject("end");
        oos.close();
        fos.close();
    }

    EditText tfRenameAlb, tfPicURL;
    ListView lvPhotos;
    int albumPos = 0;
    public static Pic openedPic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_list);
        tfRenameAlb = findViewById(R.id.renameAlb);
        tfPicURL = findViewById(R.id.tfPicURL);
        lvPhotos = findViewById(R.id.lvPhotos);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            albumPos = bundle.getInt("AlbPos");
        }
        ArrayList<String> urls = new ArrayList<>();
        for(Pic p: MainActivity.openedAlb.pics)
        {
            urls.add(p.fileName);
        }
        CustomListView customListView = new CustomListView(this, urls);
        lvPhotos.setAdapter(customListView);
        //lvPhotos.setAdapter(
        //        new ArrayAdapter<Pic>(this, android.R.layout.simple_list_item_1, MainActivity.openedAlb.pics));
        lvPhotos.setOnItemClickListener((list, view, pos, id) -> showPic(pos));
    }

    public void addPic(View view) throws IOException {
        String imageURL = tfPicURL.getText().toString();
        for(Album a: MainActivity.albList)
        {
            for(Pic p: a.pics)
            {
                if(p.fileName.equals(imageURL))
                {
                    return;
                }
            }
        }
        if(imageURL.length() > 0 && imageURL != null)
        {
            Pic newPic = new Pic(imageURL, new ArrayList<>());
            File imgFile = new File(imageURL);
            if(imgFile.exists())
            {
                MainActivity.openedAlb.pics.add(newPic);
                tfPicURL.setText("");
                //MainActivity.serialize();
                serialize();
                Intent intent = new Intent(this, PhotoList.class);
                startActivity(intent);
            }
           // Image image = new Image()
        }
    }

    public void renameAlb(View view) throws IOException {
        String newName = tfRenameAlb.getText().toString();
        if(!(newName.length() <= 0) || !(newName == null))
        {
            for(Album a: MainActivity.albList)
            {
                if(a.name.equals(newName))
                {
                    return;
                }
            }
            MainActivity.openedAlb.name = newName;
            serialize();
            tfRenameAlb.setText("");
            //MainActivity.serialize();;
        }
    }
    public void deleteAlb(View view) throws IOException {
        MainActivity.albList.remove(MainActivity.openedAlb);
        serialize();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void backToAlb(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void showPic(int pos)
    {
        openedPic = MainActivity.openedAlb.pics.get(pos);
        Intent intent = new Intent(this, PhotoDisplay.class);
        startActivity(intent);
    }
}