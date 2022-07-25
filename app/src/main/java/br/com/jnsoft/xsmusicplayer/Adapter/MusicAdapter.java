package br.com.jnsoft.xsmusicplayer.Adapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.jnsoft.xsmusicplayer.DTO.MusicFiles;
import br.com.jnsoft.xsmusicplayer.PlayerActivity;
import br.com.jnsoft.xsmusicplayer.R;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ItemViewHolder>{
    Context context;
    public static ArrayList<MusicFiles> mFiles;

    public MusicAdapter(Context context, ArrayList<MusicFiles> mFiles) {
        this.context = context;
        this.mFiles = mFiles;
    }

    public final class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView lblTitulo, lblDescricao;
        ImageView imgAlbum, btnMenu;
        LinearLayout layoutPlay;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            lblTitulo =  itemView.findViewById(R.id.itemMusic_titulo);
            lblDescricao =  itemView.findViewById(R.id.itemMusic_descricao);
            imgAlbum =  itemView.findViewById(R.id.itemMusic_imgAlbum);
            btnMenu =  itemView.findViewById(R.id.itemMusic_btnMenu);

        }
    }

    @NonNull
    @Override
    public MusicAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.row_music_item, parent,false));
    }

    @Override
    public void onBindViewHolder(MusicAdapter.ItemViewHolder holder, int position) {
        holder.lblTitulo.setText(mFiles.get(position).getTitulo());
        holder.lblDescricao.setText(mFiles.get(position).getAlbum() + "/" + mFiles.get(position).getArtista());

        //Pega metadados da musica e captura a imagem se ouver;
        byte[] image = getAlbumArt(Uri.parse(mFiles.get(position).getPath()));

        if (image != null){
            Glide.with(context).asBitmap().load(image).into(holder.imgAlbum);
        }else{
            Glide.with(context).load(R.drawable.icone_music).into(holder.imgAlbum);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });

        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context,v);
                popup.getMenuInflater().inflate(R.menu.song_item_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.song_shared:
                                Toast.makeText(context, "Compartilhar", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.song_send:
                                Toast.makeText(context, "Enviar para...", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.song_delete:
                                deleteFile(position, v);
                                //Toast.makeText(context, "C tem certeza que deseja deletar essa musica?", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.song_info:
                                Toast.makeText(context, "Informações da Musica", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });

                //Toast.makeText(context, "Deletar", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    //Outras funcoes bem legais
    private byte[] getAlbumArt(Uri url){
        MediaMetadataRetriever retriver = new MediaMetadataRetriever();
        retriver.setDataSource(url.toString());
        byte[] art = retriver.getEmbeddedPicture();
        return art;
    }

    //Deleta o arquivo selecionado
    private void deleteFile(int position, View v){

        Uri urlSong = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(mFiles.get(position).getId()));

        File file = new File(mFiles.get(position).getPath());

        boolean deleted = file.delete();

        if(deleted){
            context.getContentResolver().delete(urlSong, null, null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mFiles.size());
            Snackbar.make(v, "Arquivo Apagado!", Snackbar.LENGTH_LONG).show();
        }else{
            Snackbar.make(v, "Arquivo não Existe!", Snackbar.LENGTH_LONG).show();
        }


    }

    //Carregar nova lista Atualizar"
    public void updateList(ArrayList<MusicFiles> musicAdapterArrayList){
        mFiles = new ArrayList<>();
        mFiles.addAll(musicAdapterArrayList);
        notifyDataSetChanged();
    }


}
