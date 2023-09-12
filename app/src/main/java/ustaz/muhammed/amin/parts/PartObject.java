package ustaz.muhammed.amin.parts;

public class PartObject {
    String name, music, youtube;

    public PartObject() {
    }

    public PartObject(String name, String music, String youtube) {
        this.name = name;
        this.music = music;
        this.youtube = youtube;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }
}
