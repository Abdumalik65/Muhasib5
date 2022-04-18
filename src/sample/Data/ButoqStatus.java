package sample.Data;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.InputStream;

public class ButoqStatus {
    private Integer id;
    private String status;
    private String picturePath;
    private InputStream inputStream;
    private Image image;
    private ImageView imageView;

    public ButoqStatus(Integer id, String status, String picturePath) throws IOException {
        this.id = id;
        this.status = status;
        this.picturePath = picturePath;
        inputStream = getClass().getResourceAsStream(picturePath);
        image = new Image(inputStream);
        imageView = new ImageView(image);
        inputStream.close();
    }

    public ButoqStatus(String status, String picturePath) throws IOException {
        this.status = status;
        this.picturePath = picturePath;
        inputStream = getClass().getResourceAsStream(picturePath);
        image = new Image(inputStream);
        imageView = new ImageView(image);
        inputStream.close();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public String toString() {
        return  status;
    }
}
