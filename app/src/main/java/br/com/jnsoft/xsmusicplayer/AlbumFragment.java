package br.com.jnsoft.xsmusicplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.jnsoft.xsmusicplayer.Adapter.AlbumAdapter;
import br.com.jnsoft.xsmusicplayer.Adapter.MusicAdapter;

import static br.com.jnsoft.xsmusicplayer.MainActivity.albuns;
import static br.com.jnsoft.xsmusicplayer.MainActivity.musicFiles;

public class AlbumFragment extends Fragment {

    RecyclerView rvAlbuns;
    AlbumAdapter albumAdapter;
    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        rvAlbuns = view.findViewById(R.id.album_rvAlbuns);
        rvAlbuns.setHasFixedSize(true);

        if(!(albuns.size() < 1)){
            albumAdapter = new AlbumAdapter(getContext(), albuns);
            rvAlbuns.setAdapter(albumAdapter);
            rvAlbuns.setLayoutManager(new GridLayoutManager(getContext(),2));
        }
        return view;
    }

}
