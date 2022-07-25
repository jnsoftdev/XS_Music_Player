package br.com.jnsoft.xsmusicplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import br.com.jnsoft.xsmusicplayer.AlbumDetalhes;
import br.com.jnsoft.xsmusicplayer.DTO.MusicFiles;
import br.com.jnsoft.xsmusicplayer.PlayerActivity;
import br.com.jnsoft.xsmusicplayer.R;

public class AlbumFilesAdapter extends RecyclerView.Adapter<AlbumFilesAdapter.MyHolder>{
    Context context;
    public static ArrayList<MusicFiles> albumFiles;

    public AlbumFilesAdapter(Context context, ArrayList<MusicFiles> albumFiles) {
        this.context = context;
        this.albumFiles = albumFiles;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView lblTitulo;
        ImageView imgAlbum, btnMenu;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            lblTitulo =  itemView.findViewById(R.id.itemMusic_titulo);
            imgAlbum =  itemView.findViewById(R.id.itemMusic_imgAlbum);
            btnMenu =  itemView.findViewById(R.id.itemMusic_btnMenu);

        }
    }

    @NonNull
    @Override
    public AlbumFilesAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumFilesAdapter.MyHolder(LayoutInflater.from(context).inflate(R.layout.row_music_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumFilesAdapter.MyHolder holder, int position) {
        holder.lblTitulo.setText(albumFiles.get(position).getTitulo());
        byte[] image = getAlbumArt(Uri.parse(albumFiles.get(position).getPath()));

        if (image != null){
            Glide.with(context).asBitmap().load(image).into(holder.imgAlbum);
        }else{
            Glide.with(context).load(R.drawable.icone_music).into(holder.imgAlbum);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("sender", "albumDetalhes");
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    //Outras funcoes bem legais
    private byte[] getAlbumArt(Uri url){
        MediaMetadataRetriever retriver = new MediaMetadataRetriever();
        retriver.setDataSource(url.toString());
        byte[] art = retriver.getEmbeddedPicture();
        return art;
    }
}
