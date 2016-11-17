package com.example.vinayak.firestorage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vinayak.firestorage.R;
import com.example.vinayak.firestorage.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Vinayak on 11/16/2016.
 */
public class ContactsAdapter extends ArrayAdapter<User> {

    List<User> mData;
    Context mContext;
    int mResource;

    public ContactsAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mData = objects;
        this.mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,parent,false);
        }

        User user = mData.get(position);
        TextView textViewContact = (TextView) convertView.findViewById(R.id.textViewContact);
        ImageView imageViewProfile = (ImageView) convertView.findViewById(R.id.ivProfThumb);

        textViewContact.setText(user.getFullName().toUpperCase());
        Picasso.with(mContext).load(user.getImageUrl()).into(imageViewProfile);
        return convertView;
    }
}
