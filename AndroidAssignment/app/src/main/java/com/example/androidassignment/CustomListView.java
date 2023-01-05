package com.example.androidassignment;
//Alexander Nocciolo, Tejas Nimkar
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class CustomListView extends ArrayAdapter {
    ArrayList<String> imageURL;
    Activity context;

    public CustomListView(Activity context, ArrayList<String> imageURL) {
        super(context, R.layout.row_item, imageURL);
        this.context = context;
        this.imageURL = imageURL;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent)
    {
        View row = convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView == null)
        {
            row = inflater.inflate(R.layout.row_item, null, true);
        }
        TextView textViewCaption = row.findViewById(R.id.picCaption);
        ImageView image = row.findViewById(R.id.thumbnailPic);
        textViewCaption.setText(imageURL.get(pos));
        File file = new File(imageURL.get(pos));
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        image.setImageBitmap(bitmap);
        return row;
    }
}
