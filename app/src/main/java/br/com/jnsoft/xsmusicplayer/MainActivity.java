
package br.com.jnsoft.xsmusicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import br.com.jnsoft.xsmusicplayer.DTO.MusicFiles;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static final int REQUEST_CODE = 1;
    static ArrayList<MusicFiles> musicFiles;
    static ArrayList<MusicFiles> albuns = new ArrayList<>();

    static boolean shuffleB = false, repeatB = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPermission();

        //initViewPager();
    }

    private void initPermission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
        }else{
            //Toast.makeText(this, "Permição Adquirida com Exito!", Toast.LENGTH_SHORT).show();
            musicFiles = getAllAudio(this);
            initViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Toast.makeText(this, "Permição Adquirida com Exito!", Toast.LENGTH_SHORT).show();
                musicFiles = getAllAudio(this);
                initViewPager();
            }else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
            }
        }
    }

    private void initViewPager() {
        TabLayout tabLayout = findViewById(R.id.frmMain_tabLayout);
        ViewPager viewPager = findViewById(R.id.frmMain_viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragments(new SongsFragment(),"Músicas");
        adapter.addFragments(new AlbumFragment(),"Álbuns");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titulos;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titulos = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String titulo){
            fragments.add(fragment);
            titulos.add(titulo);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titulos.get(position);
        }
    }

    public static ArrayList<MusicFiles> getAllAudio(Context context){
        ArrayList<String> duplicado = new ArrayList<>();
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();

        Uri url = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DATA, // É O PATCH
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = context.getContentResolver().query(url,projection, null,null,null);

        if(cursor != null){
            while (cursor.moveToNext()){
                String id = cursor.getString(0);
                String titulo = cursor.getString(1);
                String artista = cursor.getString(2);
                String album = cursor.getString(3);
                String path = cursor.getString(4);
                String duracao = cursor.getString(5);

                MusicFiles music = new MusicFiles(id, titulo, artista, album, path, duracao);
                tempAudioList.add(music);

                if(!duplicado.contains(album)){
                    albuns.add(music);
                    duplicado.add(album);
                }
            }

            cursor.close();
        }
        return tempAudioList;
    }

    //Menu de Toolbar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.search_buscar);

        SearchView search = (SearchView) item.getActionView();
        search.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userText = newText.toLowerCase();
        ArrayList<MusicFiles> myFiles = new ArrayList<>();

        for(MusicFiles song : musicFiles){
            if(song.getTitulo().toLowerCase().contains(userText)){
                myFiles.add(song);
            }
        }
        SongsFragment.musicAdapter.updateList(myFiles);
        return true;
    }

}
