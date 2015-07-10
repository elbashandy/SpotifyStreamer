package com.example.absho.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


public class TrackActivity extends ActionBarActivity {

    private final static String EXTRA_TRACK_ID = "com.example.absho.spotifystreamer.TRACK_ID";
    private final String LOG_TAG_PARENT = TrackActivity.class.getSimpleName();

    private TextView albumTextView = null;
    private TextView trackTextView = null;
    private ImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_song_item);

        albumTextView = (TextView) findViewById(R.id.list_album_item_textView);
        trackTextView = (TextView) findViewById(R.id.list_track_item_textView);
        imageView = (ImageView) findViewById(R.id.list_track_item_imageView);

        Intent intent = getIntent();
        String trackId = intent.getStringExtra(EXTRA_TRACK_ID);

        Log.v(LOG_TAG_PARENT, "Track Id: " + trackId);

        SpotifyGetTrack spotifyGetTrack = new SpotifyGetTrack();
        spotifyGetTrack.execute(trackId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track, menu);
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


    public class SpotifyGetTrack extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = SpotifyGetTrack.class.getSimpleName();
        private Exception exception;

        @Override
        protected String doInBackground(String... params) {
            try {
                SpotifyApi spotifyApi = new SpotifyApi();
                SpotifyService spotify = spotifyApi.getService();

                //Look for Track
                Track track = spotify.getTrack(params[0]);

                StringBuilder result = new StringBuilder();
                result.append(track.album.name + ",");
                result.append(track.name + ",");
                result.append(track.album.images.get(track.album.images.size() - 1).url);

                Log.v(LOG_TAG + " doInBackground: ", result.toString());

                return result.toString();
            }
            catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            String[] trackInfo = result.split(",");

            albumTextView.setText(trackInfo[0]);
            trackTextView.setText(trackInfo[1]);
            if (trackInfo[2] != "") {
                Log.v(LOG_TAG + "Image: ", trackInfo[2]);
                Picasso.with(TrackActivity.this).load(trackInfo[2]).into(imageView);
            }
        }
    }
}
