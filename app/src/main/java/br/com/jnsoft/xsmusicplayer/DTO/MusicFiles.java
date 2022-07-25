package br.com.jnsoft.xsmusicplayer.DTO;

public class MusicFiles {
    String id;
    String titulo;
    String artista;
    String album;
    String path;
    String duracao;

    public MusicFiles(String id, String titulo, String artista, String album, String path, String duracao) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.album = album;
        this.path = path;
        this.duracao = duracao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }
}