package sample.Tools;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PathToImageView {
    String imageName;
    InputStream inputStream;
    String currentDirectory = System.getProperty("user.dir") + File.separator +
            "src" + File.separator +
            "sample" + File.separator +
            "images" + File.separator +
            "Icons" + File.separator;
    Image image;
    ImageView imageView;
    Integer width = 64;
    Integer height = 64;

    public PathToImageView(String imageName) {
        this.imageName = imageName;
        inputStream = getClass().getResourceAsStream(imageName);
        image = new Image(inputStream);
        imageView = new ImageView(image);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setOnMouseEntered(e->{
            imageView.setScaleX(1.1);
            imageView.setScaleY(1.1);
        });
        imageView.setOnMouseExited(e->{
            imageView.setScaleX(1.0);
            imageView.setScaleY(1.0);
        });
    }

    public PathToImageView(String imageName, Integer width, Integer height) {
        this.imageName = imageName;
        this.width = width;
        this.height = height;
        inputStream = getClass().getResourceAsStream(imageName);
        image = new Image(inputStream);
        imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setOnMouseEntered(e->{
            imageView.setScaleX(1.1);
            imageView.setScaleY(1.1);
        });
        imageView.setOnMouseExited(e->{
            imageView.setScaleX(1.0);
            imageView.setScaleY(1.0);
        });
    }

    public ImageView getImageView() { return imageView; }
    public void setImageView(ImageView imageView) { this.imageView = imageView; }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }
}
