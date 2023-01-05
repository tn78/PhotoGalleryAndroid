package com.example.androidassignment;
//Alexander Nocciolo, Tejas Nimkar
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PhotoDisplay extends AppCompatActivity implements Serializable {

    ImageView ivCurrent;
    ToggleButton tagType;
    EditText tagValue, moveTo;
    TextView tags;
    String tagTypeString = "Person";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_display);
        ivCurrent = findViewById(R.id.displayedImage);
        tagType = findViewById(R.id.toggleTagType);
        tagValue = findViewById(R.id.tagValue);
        moveTo = findViewById(R.id.tfMoveTo);
        tags = findViewById(R.id.tagList);

        String tagList = PhotoList.openedPic.tagsToString();
        File imgFile = new File(PhotoList.openedPic.fileName);
        if(imgFile.exists())
        {
            //System.out.println("Image exists");
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ivCurrent.setImageBitmap(bitmap);
        }
        if(tagList != null)
        {
            tags.setText(tagList);
        }
        tagType.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The toggle is enabled (Location)
                tagTypeString = "Location";
            } else {
                // The toggle is disabled (Person)
                tagTypeString = "Person";
            }
        });
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tag_types, android.R.layout.simple_spinner_item);

        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        //ivCurrent.setImageBitmap(BitmapFactory.decodeFile(PhotoList.openedPic.fileName));
    }

    public void addTag(View view) throws IOException {
        if(tagValue.getText().toString().length() > 0)
        {
            String tag = tagTypeString + "=" + tagValue.getText().toString();
            if(!PhotoList.openedPic.tags.contains(tag))
            {
                PhotoList.openedPic.tags.add(tag);
                serialize();
                //MainActivity.serialize();
                String tagList = PhotoList.openedPic.tagsToString();
                tags.setText(tagList);
                tagValue.setText("");
            }
        }
    }

    public void deleteTag(View view) throws IOException {
        if(tagValue.getText().toString().length() > 0)
        {
            String tag = tagTypeString + "=" + tagValue.getText().toString();
            if(PhotoList.openedPic.tags.contains(tag))
            {
                PhotoList.openedPic.tags.remove(tag);
                serialize();
                //MainActivity.serialize();
                String tagList = PhotoList.openedPic.tagsToString();
                tags.setText(tagList);
                tagValue.setText("");
            }
        }
    }

    public void backToCurrentAlb(View view)
    {
        Intent intent = new Intent(this, PhotoList.class);
        startActivity(intent);
    }

    public void nextPic(View view)
    {
        int length = MainActivity.openedAlb.pics.size();
        int index = MainActivity.openedAlb.pics.indexOf(PhotoList.openedPic);
        if(index >= length - 1)
        {
            PhotoList.openedPic = MainActivity.openedAlb.pics.get(0);
            Intent intent = new Intent(this, PhotoDisplay.class);
            startActivity(intent);
        }
        else
        {
            PhotoList.openedPic = MainActivity.openedAlb.pics.get(index + 1);
            Intent intent = new Intent(this, PhotoDisplay.class);
            startActivity(intent);
        }
    }

    public void prevPic(View view)
    {
        int length = MainActivity.openedAlb.pics.size();
        int index = MainActivity.openedAlb.pics.indexOf(PhotoList.openedPic);
        if(index <= 0)
        {
            PhotoList.openedPic = MainActivity.openedAlb.pics.get(length - 1);
            Intent intent = new Intent(this, PhotoDisplay.class);
            startActivity(intent);
        }
        else
        {
            PhotoList.openedPic = MainActivity.openedAlb.pics.get(index - 1);
            Intent intent = new Intent(this, PhotoDisplay.class);
            startActivity(intent);
        }
    }

    public void deletePic(View view) throws IOException {
        MainActivity.openedAlb.pics.remove(PhotoList.openedPic);
        //MainActivity.serialize();
        serialize();
        Intent intent = new Intent(this, PhotoList.class);
        startActivity(intent);
    }

    public void moveTo(View view) throws IOException {
        String newAlb = moveTo.getText().toString();
        if(newAlb != null && newAlb.length() > 0 && !newAlb.equals(MainActivity.openedAlb.name))
        {
            for(Album a: MainActivity.albList)
            {
                if(a.name.equals(newAlb))
                {
                    a.pics.add(PhotoList.openedPic);
                    MainActivity.openedAlb.pics.remove(PhotoList.openedPic);
                    //MainActivity.serialize();
                    serialize();

                    Intent intent = new Intent(this, PhotoList.class);
                    startActivity(intent);
                    return;
                }
            }
        }
    }
}