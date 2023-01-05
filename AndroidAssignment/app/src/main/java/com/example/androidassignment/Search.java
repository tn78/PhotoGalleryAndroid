package com.example.androidassignment;
//Alexander Nocciolo, Tejas Nimkar
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    EditText tfSearch;
    ListView lvSearch;
    ToggleButton searchType;
    public static ArrayList<Pic> searchResults = new ArrayList<>();
    String tagTypeString = "Person";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        tfSearch = findViewById(R.id.tfSearch);
        lvSearch = findViewById(R.id.searchResults);
        searchType = findViewById(R.id.searchType);
        //searchResults.clear();
        searchType.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The toggle is enabled (Location)
                tagTypeString = "Location";
            } else {
                // The toggle is disabled (Person)
                tagTypeString = "Person";
            }
        });
        ArrayList<String> urls = new ArrayList<>();
        for(Pic p: searchResults)
        {
            urls.add(p.fileName);
        }
        CustomListView customListView = new CustomListView(this, urls);
        lvSearch.setAdapter(customListView);
//        lvSearch.setAdapter(
//                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchResults));
        lvSearch.setOnItemClickListener((list, view, pos, id) -> showPic(list, pos));
    }

    public void showPic(AdapterView<?> list, int pos)
    {
        Object selected = list.getAdapter().getItem(pos);
        //System.out.println(selectedObj);
        //Pic selected = (Pic) selectedObj;
        for(Album a: MainActivity.albList)
        {
            for(Pic p: a.pics)
            {
                if(p.fileName.equals(selected))
                {
                    MainActivity.openedAlb = a;
                    PhotoList.openedPic = p;
                    Intent intent = new Intent(this, PhotoDisplay.class);
                    startActivity(intent);
                    return;
                }
            }
        }
    }

    public void goBack(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void search(View view)
    {
        searchResults.clear();
        String searchValue = tfSearch.getText().toString();
        if(searchValue.length() > 0)
        {
            for(Album a: MainActivity.albList)
            {
                for(Pic p: a.pics)
                {
                    for(String t: p.tags)
                    {
                        String type;
                        String value;
                        if(t.charAt(0) == 'P')
                        {
                            type = "Person";
                            value = t.substring(7);
                        }
                        else
                        {
                            type = "Location";
                            value = t.substring(9);
                        }
                        if(type.equals(tagTypeString))
                        {
                            if(searchValue.length() < value.length())
                            {
                                if(searchValue.equalsIgnoreCase(value.substring(0, searchValue.length())))
                                {
                                    searchResults.add(p);
                                    break;
                                }
                            }
                            else if(value.length() == searchValue.length())
                            {
                                if(value.equalsIgnoreCase(searchValue))
                                {
                                    searchResults.add(p);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            Intent intent = new Intent(this, Search.class);
            startActivity(intent);
        }
    }
}