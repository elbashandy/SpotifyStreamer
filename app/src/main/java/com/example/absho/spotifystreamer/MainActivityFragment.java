package com.example.absho.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    private final static String EXTRA_ARTIST_ID = "com.example.absho.spotifystreamer.ARTIST_ID";

    private SpotifyListAdapter spotifyAdapter;
    private Toast toast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] data = {};
        String[] images = {};

        spotifyAdapter = new SpotifyListAdapter(getActivity(), new ArrayList<String>());
        ListView listView = (ListView) rootView.findViewById(R.id.listview_spotify);
        listView.setAdapter(spotifyAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (view.getTag() != null) {
                    String artistId = spotifyAdapter.getItem(position);
                    Intent intent = new Intent(getActivity(), TopTracksActivity.class);

                    intent.putExtra(EXTRA_ARTIST_ID, artistId);
                    startActivity(intent);
                }
                else {
                    String msg = "No Tracks are available";

                    if (toast != null) {
                        toast.cancel();
                    }

                    toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        MainActivity activity = (MainActivity) getActivity();
        String artistName = activity.getQuery();

        SpotifyFeedBack spotifyFeedBack = new SpotifyFeedBack();
        spotifyFeedBack.execute(artistName);

        return rootView;
    }


    public class SpotifyFeedBack extends AsyncTask<String, Void, Boolean> {

        private final String LOG_TAG = SpotifyFeedBack.class.getSimpleName();
        private Exception exception;

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                SpotifyApi spotifyApi = new SpotifyApi();
                SpotifyService spotify = spotifyApi.getService();
                ArtistsPager artistlist = spotify.searchArtists(params[0]);

                Log.v(LOG_TAG, Integer.toString(artistlist.artists.total));

                spotifyAdapter.clear();
                String[][] artistArray = new String[artistlist.artists.total][2];

                for (int i = 0; i < artistlist.artists.total; i++) {
                    Artist item = artistlist.artists.items.get(i);
                    artistArray[i][0] = item.name;
                    Log.v(LOG_TAG + " Images:", Integer.toString(item.images.size()));
                    if (item.images.size() != 0){
                        artistArray[i][1] = item.images.get(item.images.size() - 1).url;
                    }else {
                        artistArray[i][1] = "";
                    }

                    //Look for top tracks
                    Map<String, Object> hm = new HashMap<String, Object>();
                    hm.put("country", "US");
                    Tracks tracks = spotify.getArtistTopTrack(item.id, hm);

                    if (tracks.tracks.size() != 0) {
                        spotifyAdapter.add(item.id, artistArray[i][0], artistArray[i][1], true);
                    }
                    else {
                        spotifyAdapter.add(item.id, artistArray[i][0], artistArray[i][1], false);
                    }
                    Log.v(LOG_TAG, item.name);
                }

                return true;
            }
            catch (Exception e) {
                this.exception = e;

                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result)
                spotifyAdapter.notifyListView();
        }
    }
}
