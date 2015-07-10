package com.example.absho.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


public class TopTracksActivity extends ActionBarActivity {

    private final static String EXTRA_ARTIST_ID = "com.example.absho.spotifystreamer.ARTIST_ID";
    private final static String EXTRA_TRACK_ID = "com.example.absho.spotifystreamer.TRACK_ID";
    private final String LOG_TAG_PARENT = TopTracksActivity.class.getSimpleName();

    private SpotifyListAdapter spotifyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        spotifyAdapter = new SpotifyListAdapter(this, new ArrayList<String>());
        ListView listView = (ListView) findViewById(R.id.listview_top_tracks_spotify);
        listView.setAdapter(spotifyAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String trackId = spotifyAdapter.getItem(position);
                Intent intent = new Intent(view.getContext(), TrackActivity.class);

                intent.putExtra(EXTRA_TRACK_ID, trackId);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        String artistId = intent.getStringExtra(EXTRA_ARTIST_ID);

        Log.v(LOG_TAG_PARENT, "Artist Id: " + artistId);
        SpotifyTracksFeedBack spotifyTracksFeedBack = new SpotifyTracksFeedBack();
        spotifyTracksFeedBack.execute(artistId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SpotifyTracksFeedBack extends AsyncTask<String, Void, Boolean> {

        private final String LOG_TAG = SpotifyTracksFeedBack.class.getSimpleName();
        private Exception exception;

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                SpotifyApi spotifyApi = new SpotifyApi();
                SpotifyService spotify = spotifyApi.getService();

                //Look for top tracks
                Map<String, Object> hm = new HashMap<String, Object>();
                hm.put("country", "US");
                Tracks tracks = spotify.getArtistTopTrack(params[0], hm);
                String[][] tracksArray = new String[tracks.tracks.size()][2];

                spotifyAdapter.clear();

                Log.v(LOG_TAG + " track", "********************************");
                for (int i = 0; i < tracks.tracks.size() && i < 10; i++) {
                    Log.v(LOG_TAG + " track", tracks.tracks.get(i).name);
                    Track item = tracks.tracks.get(i);

                    tracksArray[i][0] = item.name;
                    Log.v(LOG_TAG + " Images:", Integer.toString(item.album.images.size()));
                    if (item.album.images.size() != 0){
                        tracksArray[i][1] = item.album.images.get(item.album.images.size() - 1).url;
                    }else {
                        tracksArray[i][1] = "";
                    }

                    spotifyAdapter.add(item.id, tracksArray[i][0], tracksArray[i][1]);
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
