package br.com.jnsoft.xsmusicplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import br.com.jnsoft.xsmusicplayer.Adapter.MusicAdapter;
import br.com.jnsoft.xsmusicplayer.DTO.MusicFiles;

import static br.com.jnsoft.xsmusicplayer.MainActivity.musicFiles;

public class SongsFragment extends Fragment {

    RecyclerView rvFiles;
    static MusicAdapter musicAdapter;
    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        rvFiles = view.findViewById(R.id.songs_rvFiles);
        rvFiles.setHasFixedSize(true);

        if(!(musicFiles.size() < 1)){
            musicAdapter = new MusicAdapter(getContext(), musicFiles);
            rvFiles.setAdapter(musicAdapter);
            rvFiles.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }

        return view;
    }

}
