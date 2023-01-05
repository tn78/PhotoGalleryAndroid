package com.example.androidassignment;
//Alexander Nocciolo, Tejas Nimkar
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
//import android.widget.Toolbar;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

class Album implements Serializable{
    String name;
    ArrayList<Pic> pics;
    Album(String name, ArrayList<Pic> pics)
    {
        this.name = name;
        this.pics = pics;
    }
    public String toString()
    {
        return this.name;
    }
}

class Pic implements Serializable{
    String fileName;
    //Image image;
    ArrayList<String> tags;
    Pic(String fileName, ArrayList<String> tags)
    {
        this.fileName = fileName;
        //this.image = image;
        this.tags = tags;
    }

    public String toString()
    {
        return this.fileName;
    }

    public String tagsToString()
    {
        String string = null;
        for(String s: tags)
        {
            if(string == null)
            {
                string = s;
            }
            else
            {
                string = string + ", " + s;
            }
        }
        return string;
    }
}

public class MainActivity extends AppCompatActivity implements Serializable {

    ListView lvAlbums;
    public static ArrayList<Album> albList = new ArrayList<>();
    public static Album openedAlb;
    Button btnAddAlbum;
    EditText albText;
    //Button btnSave;
    //Button back;
//    private static final int REQUEST_EXTERNAL_STORAGE = 1;
//    private static String[] PERMISSIONS_STORAGE = {
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//    };

//    public static void verifyStoragePermissions(Activity activity) {
//        // Check if we have write permission
//        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//            );
//        }
//    }

    void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

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
        for(Album a: albList)
        {
            oos.writeObject(a);
        }
        oos.writeObject("end");
        oos.close();
        fos.close();
    }

    public void deserial() throws IOException, ClassNotFoundException
    {
        FileInputStream fis = openFileInput("Albums.dat");
        ObjectInputStream ois = new ObjectInputStream(fis);
        //SimpleClass simpleClass = ois.readObject()
        Object ob = ois.readObject();
        if(ob instanceof String)
        {
            ois.close();
            return;
        }
        while(!(ob instanceof String))
        {
            albList.add((Album) ob);
            ob = ois.readObject();
        }
        ois.close();
        fis.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list);
        askPermissions();
        if(albList.isEmpty())
        {
            try {
                deserial();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        //System.out.println("Set content view");
        //Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //setSupportActionBar(toolbar);

        //insert loading file here
        //albList = new ArrayList<Album>();
        //Activity activity = MainActivity;
        //verifyStoragePermissions(activity);
        btnAddAlbum = findViewById(R.id.btnAddAlbum);
        albText = findViewById(R.id.albText);
        btnAddAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String albName = albText.getText().toString();
                if(albName == null || albName.length() <= 0)
                {
                    return;
                }
                for(Album a: albList)
                {
                    if(a.name.equals(albName))
                    {
                        return;
                    }
                }
                ArrayList<Pic> pics = new ArrayList<>();
                Album alb = new Album(albName, pics);
                albList.add(alb);
                albText.setText("");
                try {
                    serialize();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        lvAlbums = findViewById(R.id.album_list);
        lvAlbums.setAdapter(
                new ArrayAdapter<Album>(this, android.R.layout.simple_list_item_1, albList));
        lvAlbums.setOnItemClickListener((list, view, pos, id) -> showAlbum(pos));

        //registerActivities();
    }

    public void showAlbum(int pos)
    {
        //Bundle bundle = new Bundle();
        Album album = albList.get(pos);
        openedAlb = album;
        //bundle.putInt(AddEditAlbum.ALBUM_INDEX, pos);
        //bundle.putString(AddEditAlbum.ALBUM_NAME, album.name);

        Intent intent = new Intent(this, PhotoList.class);
        intent.putExtra("AlbPos", pos);
        startActivity(intent);
    }

    public void goToSearch(View view)
    {
        Search.searchResults.clear();
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }
    /*
    ActivityResultLauncher<Intent> startForResultAdd;
    ActivityResultLauncher<Intent> startForResultEdit;

    public void registerActivities()
    {
        startForResultEdit =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if(result.getResultCode() == Activity.RESULT_OK){
                                applyEdit(result, "edit");
                            }
                        });
        startForResultAdd =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if(result.getResultCode() == Activity.RESULT_OK){
                                applyEdit(result, "add");
                            }
                        });
    }

    public void applyEdit(ActivityResult result, String addEdit)
    {
        Intent intent = result.getData();
        Bundle bundle = intent.getExtras();
        if(bundle == null)
        {
            return;
        }

        String name = bundle.getString(AddEditAlbum.ALBUM_NAME);
    }

    public void showAlbum(int pos)
    {
        Bundle bundle = new Bundle();
        Album album = albList.get(pos);
        bundle.putInt(AddEditAlbum.ALBUM_INDEX, pos);
        bundle.putString(AddEditAlbum.ALBUM_NAME, album.name);

        Intent intent = new Intent(this, AddEditAlbum.class);
        intent.putExtras(bundle);
        startForResultEdit.launch(intent);


    }

    public void addMovie()
    {
        Intent intent = new Intent(this, AddEditAlbum.class);
        startForResultAdd.launch(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_add:
                addMovie();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    */

    /*
    private EditText tfCurrentAlb;
    private ListView lvAlbums;
    Button btnCreateAlb;
    ArrayList<Album> albList = new ArrayList<Album>();
    ArrayAdapter<Album> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreateAlb = findViewById(R.id.btnCreateAlb);
        lvAlbums = findViewById(R.id.lvAlbums);
        tfCurrentAlb = findViewById(R.id.tfCurrentAlb);
        adapter = new ArrayAdapter<Album>(this, android.R.layout.simple_list_item_1, albList);
        lvAlbums.setAdapter(adapter);
        btnCreateAlb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String albName = tfCurrentAlb.getText().toString();
                ArrayList<Pic> pics = new ArrayList<Pic>();
                Album alb = new Album(albName, pics);
                albList.add(alb);
                adapter.notifyDataSetChanged();
            }
        });
    }

//    void createAlb(View view)
//    {
//        String albName = tfCurrentAlb.getText().toString();
//        ArrayList<Pic> pics = new ArrayList<Pic>();
//        Album alb = new Album(albName, pics);
//        lvAlbums.
//    }


     */

}