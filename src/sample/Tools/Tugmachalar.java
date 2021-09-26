package sample.Tools;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.io.InputStream;

public class Tugmachalar extends HBox {
    private Button add = new Button("Qo`sh");
    private Button delete = new Button("O`chir");
    private Button edit = new Button("O`zgartir");
    private Button excel = new Button("Excel");
    private InputStream inputStream;
    private Image image;
    private ImageView imageView;

    public Button getAdd() {
        return add;
    }

    public void setAdd(Button add) {
        this.add = add;
    }

    public Button getDelete() {
        return delete;
    }

    public void setDelete(Button delete) {
        this.delete = delete;
    }

    public Button getEdit() {
        return edit;
    }

    public void setEdit(Button edit) {
        this.edit = edit;
    }

    public Button getExcel() {
        return excel;
    }

    public void setExcel(Button excel) {
        this.excel = excel;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public Tugmachalar() {
        try {
            inputStream = getClass().getResourceAsStream("/sample/images/Icons/add.png");
            image = new Image(inputStream);
            imageView = new ImageView(image);
            add.setGraphic(imageView);
            inputStream.close();
            image = null;
            inputStream = getClass().getResourceAsStream("/sample/images/Icons/delete.png");
            image = new Image(inputStream);
            imageView = new ImageView(image);
            delete.setGraphic(imageView);
            inputStream.close();
            image = null;
            inputStream = getClass().getResourceAsStream("/sample/images/Icons/edit.png");
            image = new Image(inputStream);
            imageView = new ImageView(image);
            edit.setGraphic(imageView);
            inputStream.close();
            image = null;
            inputStream = getClass().getResourceAsStream("/sample/images/Icons/excel.png");
            image = new Image(inputStream);
            imageView = new ImageView(image);
            excel.setGraphic(imageView);
            inputStream.close();
            image = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        getChildren().addAll(add, delete, edit, excel);
    }

    public void setDisableAll() {
        for (Node b: this.getChildren()) {
            b.setDisable(true);
        }
    }
    public void setEnableAll() {
        for (Node b: this.getChildren()) {
            b.setDisable(false);
        }
    }
}
