package ethio.islamic.durus.ustaz;

public class UstazObject {
    String name, img;

    public UstazObject() {
    }

    public UstazObject(String name, String img) {
        this.name = name;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
