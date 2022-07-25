package br.com.jnsoft.xsmusicplayer;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import br.com.jnsoft.xsmusicplayer.DTO.MusicFiles;

import static br.com.jnsoft.xsmusicplayer.Adapter.AlbumFilesAdapter.albumFiles;
import static br.com.jnsoft.xsmusicplayer.Adapter.MusicAdapter.mFiles;
import static br.com.jnsoft.xsmusicplayer.MainActivity.musicFiles;
import static br.com.jnsoft.xsmusicplayer.MainActivity.repeatB;
import static br.com.jnsoft.xsmusicplayer.MainActivity.shuffleB;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener{
    private View decorView;

    TextView lblTitulo, lblArtista, lblDuration, lblDurationTotal;
    ImageView imgCover, btnNext, btnPrevius, btnShuffle, btnRepeat;
    FloatingActionButton btnPlayPause;
    SeekBar seekBar;
    int postion = -1;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri url;
    static MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    Thread playThread, prevThread, nextThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_player);

        initScreenMethod();

        initView();
        getIntentMethod();

        initComplite();
        progressBar();

        initBottons();


        initToolbar();

    }


    //Iniciar Toolbar
    private void initToolbar(){
        TextView barTitulo = findViewById(R.id.toolbar_title);
        ImageButton barBack = findViewById(R.id.toolbar_back);
        ImageButton barMore = findViewById(R.id.toolbar_more);

        barTitulo.setText(listSongs.get(postion).getTitulo());

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

    //Inicia o Modo Screen
//Screen Methods
    private void initScreenMethod() {
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility == 0){
                    decorView.setSystemUiVisibility(hideMenuBars());
                }
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            decorView.setSystemUiVisibility(hideMenuBars());
        }
    }

    private int hideMenuBars(){
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
    //Fim do modo Screen










    //Inciar componetes
    private void initView() {
        //App
        lblTitulo = findViewById(R.id.mPlayer_musicName);
        lblArtista = findViewById(R.id.mPlayer_musicArtist);
        lblDuration = findViewById(R.id.mPlayer_duration);
        lblDurationTotal = findViewById(R.id.mPlayer_durationTotal);

        imgCover = findViewById(R.id.mPlayer_imgCover);
        btnNext = findViewById(R.id.mPlayer_next);
        btnPrevius = findViewById(R.id.mPlayer_previus);
        btnShuffle = findViewById(R.id.mPlayer_shuffle);
        btnRepeat = findViewById(R.id.mPlayer_repeat);

        btnPlayPause = findViewById(R.id.mPlayer_playPause);
        seekBar = findViewById(R.id.mPlayer_progress);

    }

    private void initComplite() {
        lblTitulo.setText(listSongs.get(postion).getTitulo());
        lblArtista.setText(listSongs.get(postion).getArtista());
        mediaPlayer.setOnCompletionListener(this);
    }

    private void getIntentMethod() {
        postion = getIntent().getIntExtra("position",-1);
        String album = getIntent().getStringExtra("sender");

        if (album != null && album.equals("albumDetalhes")){
            listSongs = albumFiles;
        }else{
            //listSongs = musicFiles;
            listSongs = mFiles;
        }

        if (listSongs != null){
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            url = Uri.parse(listSongs.get(postion).getPath());
        }

        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(),url);
            mediaPlayer.start();
        }else{
            mediaPlayer = MediaPlayer.create(getApplicationContext(),url);
            mediaPlayer.start();
        }

        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        metaData(url);

    }

    //ProgressBar
    private void progressBar() {

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int mCurrentPosition  = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    lblDuration.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });

    }

    private String formattedTime(int mCurrentPosition) {
        String totalOut  = "";
        String totalNew  = "";
        String segundos  = String.valueOf(mCurrentPosition % 60);
        String minutos  = String.valueOf(mCurrentPosition / 60);
        totalOut = minutos + ":" + segundos;
        totalNew= minutos + ":" + "0" + segundos;

        if (segundos.length() == 1){
            return totalNew;
        }else{
            return totalOut;
        }
    }

    //Image Cover + Animation
    private void metaData(Uri url){
        Bitmap bitmap;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(url.toString());
        //seto o tempo total de duração
        int durationTotal = Integer.parseInt(listSongs.get(postion).getDuracao()) / 1000;
        lblDurationTotal.setText(formattedTime(durationTotal));
        //seto a imagem cover.
        byte[] art = retriever.getEmbeddedPicture();

        if(art != null){
            //Glide.with(this).asBitmap().load(art).into(imgCover);

            bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
            imageAnimation(this, imgCover, bitmap);

            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();

                    if(swatch != null){
                        ImageView gredient = findViewById(R.id.mPlayer_imgGradient);
                        ConstraintLayout mContainer = findViewById(R.id.mPlayer_content);
                        gredient.setBackgroundResource(R.drawable.gradient_bgcolor);

                        mContainer.setBackgroundResource(R.drawable.main_bg);

                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), 0x00000000});
                        gredient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);

                        lblTitulo.setTextColor(swatch.getTitleTextColor());
                        lblArtista.setTextColor(swatch.getBodyTextColor());

                    }else{
                        ImageView gredient = findViewById(R.id.mPlayer_imgGradient);
                        ConstraintLayout mContainer = findViewById(R.id.mPlayer_content);
                        gredient.setBackgroundResource(R.drawable.gradient_bgcolor);

                        mContainer.setBackgroundResource(R.drawable.main_bg);

                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0x00000000});
                        gredient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0xff000000});
                        mContainer.setBackground(gradientDrawableBg);

                        lblTitulo.setTextColor(Color.WHITE);
                        lblArtista.setTextColor(Color.DKGRAY);
                    }

                }
            });

        }else{
            Glide.with(this).load(R.drawable.icone_music).into(imgCover);
            //imageAnimation(this, imgCover, R.drawable.icone_music);

            ImageView gredient = findViewById(R.id.mPlayer_imgGradient);
            ConstraintLayout mContainer = findViewById(R.id.mPlayer_content);
            gredient.setBackgroundResource(R.drawable.gradient_bgcolor);
            mContainer.setBackgroundResource(R.drawable.main_bg);

            lblTitulo.setTextColor(Color.WHITE);
            lblArtista.setTextColor(Color.DKGRAY);

        }

    }

    public void imageAnimation(Context context, ImageView imageView, Bitmap bitmap){
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    //New
    @Override
    protected void onResume(){
        super.onResume();
        btnPlayThread();
        btnNextThread();
        btnPrevThread();

    }

    private void btnPlayThread(){
        playThread = new Thread(){
            @Override
            public void run(){
                super.run();
                btnPlayPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    private void playPauseClicked() {
        if(mediaPlayer.isPlaying()){
            btnPlayPause.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition  = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });
        }else{

            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition  = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });

        }
    }

    private void btnNextThread(){
        nextThread = new Thread(){
            @Override
            public void run(){
                super.run();
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void nextClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleB && !repeatB){
                postion = getRandom( listSongs.size() -1);
            }else if(!shuffleB && !repeatB){
            postion = ((postion + 1) % listSongs.size());
            }
            //else position will be position
            url = Uri.parse(listSongs.get(postion).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),url);

            metaData(url);
            initComplite();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition  = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });

            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();

        }else{
            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleB && !repeatB){
                postion = getRandom( listSongs.size() -1);
            }else if(!shuffleB && !repeatB){
                postion = ((postion + 1) % listSongs.size());
            }

            //postion = ((postion + 1) % listSongs.size());
            url = Uri.parse(listSongs.get(postion).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),url);

            metaData(url);
            initComplite();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition  = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });

            //Testar senao comenta
            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    private void btnPrevThread(){
        prevThread = new Thread(){
            @Override
            public void run(){
                super.run();
                btnPrevius.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void prevClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleB && !repeatB){
                postion = getRandom( listSongs.size() -1);
            }else if(!shuffleB && !repeatB){
                postion = ((postion - 1) < 0 ? (listSongs.size() -1 ): (postion -1));
            }

            //postion = ((postion - 1) < 0 ? (listSongs.size() -1 ): (postion -1));
            //postion = ((postion - 1) % listSongs.size());
            url = Uri.parse(listSongs.get(postion).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),url);

            metaData(url);
            initComplite();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition  = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });

            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();

        }else{
            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleB && !repeatB){
                postion = getRandom( listSongs.size() -1);
            }else if(!shuffleB && !repeatB){
                postion = ((postion - 1) < 0 ? (listSongs.size() -1 ): (postion -1));
            }

            url = Uri.parse(listSongs.get(postion).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),url);

            metaData(url);
            initComplite();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition  = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,1000);
                }
            });

            //Testar senao comenta
            mediaPlayer.setOnCompletionListener(this);
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    //Outros butoes
    private void initBottons() {
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleB){
                    shuffleB = false;
                    btnShuffle.setImageResource(R.drawable.ic_shuffle_off);
                }else{
                    shuffleB = true;
                    btnShuffle.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatB){
                    repeatB = false;
                    btnRepeat.setImageResource(R.drawable.ic_repeat_all_off);
                }else{
                    repeatB = true;
                    btnRepeat.setImageResource(R.drawable.ic_repeat_all_on);
                }
            }
        });
    }


    //Funçoes extras
    private int getRandom(int i){
        Random rd = new Random();
        return rd.nextInt(i +1);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextClicked();
        if(mediaPlayer != null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(),url);
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    }



































