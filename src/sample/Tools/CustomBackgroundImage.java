package sample.Tools;

import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.InputStream;

public class CustomBackgroundImage {
    String pathImage;
    Background background;

    public CustomBackgroundImage(String pathImage) {
        this.pathImage = pathImage;
    }

    public Background getBackground() {
        InputStream inputStream = getClass().getResourceAsStream(pathImage);
        Image image = new Image(inputStream);
        BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
        background = new Background(new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                bSize));
        return background;
    }

    public void setBackground(Background background) {
        this.background = background;
    }
}
