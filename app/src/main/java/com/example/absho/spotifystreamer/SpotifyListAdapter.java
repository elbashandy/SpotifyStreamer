package com.example.absho.spotifystreamer;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by absho on 7/7/15.
 */
public class SpotifyListAdapter extends ArrayAdapter<String> {

    private final String LOG_TAG = SpotifyListAdapter.class.getSimpleName();

    private final Activity context;
    private final List<String> artistIds;
    private final List<String> artistNames;
    private final List<String> imageUrls;
    public SpotifyListAdapter(Activity context, List<String> artistNames) {
        super(context, R.layout.list_singer_item, artistNames);
        this.context = context;
        this.artistIds = new ArrayList<>();
        this.artistNames = new ArrayList<>();
        this.imageUrls = new ArrayList<>();

    }

    @Override
    public int getCount() {
        return artistNames.size();
    }

    @Override
    public String getItem(int position) {
        return artistIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.v(LOG_TAG, "###################################");
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_singer_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.list_singer_item_textview);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_singer_item_imageView);

        rowView.setTag(artistIds.get(position));

        txtTitle.setText(artistNames.get(position));

        Log.v(LOG_TAG, rowView.getTag().toString() + " ## " + artistNames.get(position) + " # " + imageUrls.get(position));

        if (imageUrls.get(position) != "") {
            Picasso.with(context).load(imageUrls.get(position)).into(imageView);
        }
        return rowView;
    }

    public void add(String artist_id, String artist_name, String image_url) {
        Log.v(LOG_TAG, artist_name + ", " + image_url);
        artistIds.add(artist_id);
        artistNames.add(artist_name);
        imageUrls.add(image_url);
    }

    public void notifyListView() {
        Log.v(LOG_TAG, "DataSetNotified");
        notifyDataSetChanged();
    }

    public void clear() {
        artistNames.clear();
        imageUrls.clear();
        artistIds.clear();
    }
}

