package br.com.jnsoft.xsmusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import br.com.jnsoft.xsmusicplayer.Adapter.AlbumFilesAdapter;
import br.com.jnsoft.xsmusicplayer.DTO.MusicFiles;

import static br.com.jnsoft.xsmusicplayer.MainActivity.musicFiles;

public class AlbumDetalhes extends AppCompatActivity {
    private View decorView;

    String albumName;
    //TextView lblTitulo;
    ImageView imgAlbum;
    RecyclerView rvFiles;
    ArrayList<MusicFiles> albumSongs = new ArrayList<>();
    AlbumFilesAdapter albumFilesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_album_detalhes);

        albumName = getIntent().getStringExtra("albumName");
        //lblTitulo = findViewById(R.id.toolbox_title);


        imgAlbum = findViewById(R.id.albumDetalhes_imgAlbum);
        rvFiles = findViewById(R.id.albumDetalhes_rvFiles);

        //albumSongs = musicFiles;
        int j = 0;
        for (int i = 0 ; i < musicFiles.size(); i ++){
            if(albumName.equals(musicFiles.get(i).getAlbum())){
                albumSongs.add(j, musicFiles.get(i));
                j ++;
            }
        }

        //////album
        //lblTitulo.setText(albumSongs.get(0).getAlbum());
        byte[] image = getAlbumArt(Uri.parse(albumSongs.get(0).getPath()));


        if (image != null){
            Glide.with(this).asBitmap().load(image).into(imgAlbum);
        }else{
            Glide.with(this).load(R.drawable.icone_music).into(imgAlbum);
        }


        initToolbar();


    }

    private void initToolbar(){
        TextView barTitulo = findViewById(R.id.toolbar_title);
        ImageButton barBack = findViewById(R.id.toolbar_back);
        ImageButton barMore = findViewById(R.id.toolbar_more);

        barTitulo.setText(albumSongs.get(0).getAlbum());

        barBack.setVisibility(View.VISIBLE);
        barMore.setVisibility(View.INVISIBLE);

        barBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }





    @Override
    protected void onResume() {
        super.onResume();

        if (!(albumSongs.size()<1)){
            albumFilesAdapter = new AlbumFilesAdapter(this, albumSongs);
            rvFiles.setAdapter(albumFilesAdapter);
            rvFiles.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL, false));
        }
    }

    //Outras funcoes bem legais
    private byte[] getAlbumArt(Uri url){
        MediaMetadataRetriever retriver = new MediaMetadataRetriever();
        retriver.setDataSource(url.toString());
        byte[] art = retriver.getEmbeddedPicture();
        return art;
    }
}
