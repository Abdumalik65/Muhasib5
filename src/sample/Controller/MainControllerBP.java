package sample.Controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sample.Config.MySqlDBGeneral;
import sample.Config.SqliteDB;
import sample.Data.Standart;
import sample.Data.User;
import sample.Enums.ServerType;
import sample.Model.StandartModels;
import sample.Temp.QaydAndoza;
import sample.Tools.DasturlarRoyxati;
import sample.Tools.GetDbData;
import sample.Tools.PathToImageView;
import sample.Tools.SetHVGrow;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

public class MainControllerBP extends Application {
    Stage stage;
    BorderPane borderpane = new BorderPane();
    MenuBar mainMenu;
    FlowPane flowPane = new FlowPane();
    FlowPane centerPane;
    Connection connection;
    ObservableList<Standart> mavqeList = FXCollections.observableArrayList();
    LoginUserController loginUserController;
    User user;

    public static void main(String[] args) {
        launch(args);
    }

    public MainControllerBP() {
        connection = new MySqlDBGeneral(ServerType.REMOTE).getDbConnection();
        GetDbData.initData(connection);
        loginUserController = new LoginUserController(connection);
        user = loginUserController.login();
        String classSimpleName = getClass().getSimpleName();
        DasturlarRoyxati.dastur(connection, user, classSimpleName);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ibtido();
        initStage(primaryStage);


/*************************************************
 **                  Past                       **
 *************************************************/
    }

    private void initStage(Stage stage) {
        stage.setOnCloseRequest(event -> {
            loginUserController.logOut();
        });
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setTitle("Hisobot");
        stage.setResizable(false);
        Scene scene = new Scene(borderpane);
//        scene.getStylesheets().add("/sample/styles/caspian.css");
        stage.setScene(scene);
        stage.show();
    }

    private void ibtido() {
        initData();
        initTopPane();
        initLeftPane();
        initCenterPane();
//        initRightPane();
        initBottomPane();
        initBorderPane();
    }

    private void initData() {
        StandartModels standartModels = new StandartModels("XodimMavqesi");
        mavqeList = standartModels.get_data(connection);
    }

    private void initTopPane() {
        initMainMenu();
    }

    private void initLeftPane() {}

    private void initCenterPane() {
/*
        if (user.getStatus().equals(99)) {
            centerPane = initCenterFlowPane();
            SetHVGrow.VerticalHorizontal(centerPane);
        }
*/
    }

    private VBox initRightPane() {
        VBox vBox = new VBox(3);
        SetHVGrow.VerticalHorizontal(vBox);
        ToggleGroup toggleGroup = new ToggleGroup();
        ObservableList<RadioButton> radioButtons = FXCollections.observableArrayList(
                new RadioButton("Eng ko`p sotilgan"),
                new RadioButton("Sotilmagan"),
                new RadioButton( "Qarzdorlar"),
                new RadioButton("Haqdorlar"),
                new RadioButton( "Analitika")
        );
        Integer i = 1;
        for (RadioButton rb: radioButtons) {
            rb.setToggleGroup(toggleGroup);
            rb.setId(i.toString());
            vBox.getChildren().add(rb);
            i++;
        }
        return  vBox;
    }

    private void initBottomPane() {
        initFlowPane();
    }

    private void initFlowPane() {
        flowPane.setPadding(new Insets(25));
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        ImageView imageView1 = new PathToImageView("/sample/images/Icons/loan.png", 64, 64).getImageView();
        flowPane.getChildren().add(imageView1);
        imageView1.setOnMouseClicked(event -> {
            show(1);
        });

        ImageView imageView2 = new PathToImageView("/sample/images/Icons/shopping_cart.png", 64, 64).getImageView();
        flowPane.getChildren().add(imageView2);
        imageView2.setOnMouseClicked(event -> {
            show(2);
        });

        ImageView imageView3 = new PathToImageView("/sample/images/Icons/wire_transfer.png", 64, 64).getImageView();
        flowPane.getChildren().add(imageView3);
        imageView3.setOnMouseClicked(event -> {
            show(3);
        });

        ImageView imageView4 = new PathToImageView("/sample/images/Icons/uzs.jpg", 64, 64).getImageView();
        flowPane.getChildren().add(imageView4);
        imageView4.setOnMouseClicked(event -> {
            QaydAndoza qaydAndoza = new QaydAndoza(connection, user);
            qaydAndoza.display();
        });

        ImageView imageView5 = new PathToImageView("/sample/images/Icons/container.png", 64, 64).getImageView();
        flowPane.getChildren().add(imageView5);
        imageView5.setOnMouseClicked(event -> {
            ContainerController containerController = new ContainerController(connection, user);
            containerController.display();
        });


/*************************************************
 **                Umumiy kod                   **
 *************************************************/
        HBox.setHgrow(flowPane, Priority.ALWAYS);
        VBox.setVgrow(flowPane, Priority.ALWAYS);
        flowPane.setAlignment(Pos.CENTER);

    }

    private void initBorderPane() {
        SetHVGrow.VerticalHorizontal(borderpane);
        borderpane.setTop(mainMenu);
        borderpane.setLeft(null);
        borderpane.setCenter(centerPane);
//        borderpane.setRight(initRightPane());
        borderpane.setBottom(flowPane);
    }

    private void initMainMenu() {
        mainMenu = new MenuBar();
        mainMenu.setPadding(new Insets(5));
        System.out.println(user.getStatus());
        mainMenu.getMenus().add(getDasturMenu());
        mainMenu.getMenus().add(getUserMenu());
        mainMenu.getMenus().add(getHisobMenu());
        mainMenu.getMenus().add(getPulMenu());
        mainMenu.getMenus().add(getTovarMenu());
        mainMenu.getMenus().add(getAmalMenu());
        mainMenu.getMenus().add(getSavdoMenu());
        mainMenu.getMenus().add(getHisobotMenu());
    }

    private Menu getDasturMenu() {
        Connection printerConnection = new SqliteDB().getDbConnection();
        Menu dasturMenu = new Menu("Dastur");
        MenuItem printerMenuItem = new MenuItemStandart(printerConnection, "Printers", "Printerlar");
        if (user.getStatus().equals(99)) {
            dasturMenu.getItems().add(getDasturlarRoyxatiMenuItem());
            dasturMenu.getItems().add(serverSozlashMenu());
            dasturMenu.getItems().add(getSozlovMenuItem());
        }
        dasturMenu.getItems().add(printerMenuItem);
        return dasturMenu;
    }

    private Menu getUserMenu() {
        Menu menu = new Menu("Xodim");
        if (user.getStatus().equals(99)) {
            menu.getItems().add(getXodimMavqesiMenuItem());
            menu.getItems().add(getUserMenuItem());
        }
        menu.getItems().add(getChangeUserMenuItem());
        return menu;
    }

    private Menu getHisobMenu() {
        Menu hisobMenu = new Menu("Hisob");
        MenuItem korxonaMenuItem = new MenuItem("Bizning korxona...");
        SeparatorMenuItem separatorHisobGuruhlariItem = new SeparatorMenuItem();
        hisobMenu.getItems().add(getHisobMenuItem());
        hisobMenu.getItems().add(kirshCheklanganHisoblar());
        hisobMenu.getItems().add(getMurakkabHisobMenuItem());
        hisobMenu.getItems().add(getBizningDokonlarMenuItem());
        hisobMenu.getItems().add(getHisobGuruhlariMenuItem());
        hisobMenu.getItems().add(separatorHisobGuruhlariItem);
        hisobMenu.getItems().add(getYordamchiHisoblarMenu());
        return hisobMenu;
    }

    private MenuItem getHisobMenuItem() {
        MenuItem hisobMenuItem = new MenuItem("Hisoblar");
        hisobMenuItem.setOnAction(e -> {
            HisobController hisobController = new HisobController();
            hisobController.display(connection, user);
        });
        return hisobMenuItem;
    }

    private MenuItem getMurakkabHisobMenuItem() {
        MenuItem menuItem = new MenuItem("Murakkab hisob");
        menuItem.setOnAction(e -> {
            MurakkabHisob murakkabHisob = new MurakkabHisob(connection, user);
            murakkabHisob.display();
        });
        return menuItem;
    }

    private MenuItem getBizningDokonlarMenuItem() {
        MenuItem menuItem = new MenuItem("Bizning do`konlar");
        menuItem.setOnAction(e -> {
            BizningDokonlar bizningDokonlar = new BizningDokonlar(connection, user);
            bizningDokonlar.display();
        });
        return menuItem;
    }

    private MenuItem getHisobot1MenuItem() {
        MenuItem hisobot1MenuItem = new MenuItem("Umumiy hisobot");
        hisobot1MenuItem.setOnAction(e -> {
            Hisobot1 hisobot1 = new Hisobot1(connection, user);
            hisobot1.display();
        });
        return hisobot1MenuItem;
    }

    private MenuItem getHisobot2MenuItem() {
        MenuItem hisobot2MenuItem = new MenuItem("Tovar hisoboti");
        hisobot2MenuItem.setOnAction(event -> {
            HisobotYigma hisobotYigma = new HisobotYigma(connection, user);
            hisobotYigma.display();
        });

        return hisobot2MenuItem;
    }

    private MenuItem getHisobot3MenuItem() {
        MenuItem hisobot3MenuItem = new MenuItem("Shtrixkod hisoboti");
        hisobot3MenuItem.setOnAction(event -> {
            HisobotYigma2 hisobotYigma2 = new HisobotYigma2(connection, user);
            hisobotYigma2.display();
        });

        return hisobot3MenuItem;
    }

    private MenuItem getHisobotPulMenuItem() {
        MenuItem hisobot3MenuItem = new MenuItem("Pul hisoboti");
        hisobot3MenuItem.setOnAction(event -> {
            PulHisobotiYigma pulHisobotiYigma = new PulHisobotiYigma(connection, user);
            pulHisobotiYigma.display();
        });

        return hisobot3MenuItem;
    }

    private MenuItem getHisobot4MenuItem() {
        MenuItem hisobot4MenuItem = new MenuItem("Sochma hisobot");
        hisobot4MenuItem.setOnAction(event -> {
            HisobotSochma2 hisobotSochma = new HisobotSochma2(connection, user);
            hisobotSochma.display();
        });

        return hisobot4MenuItem;
    }

    private MenuItem getZaxiraMenuItem() {
        MenuItem hisobot4MenuItem = new MenuItem("Zaxiradagi tovarlar");
        hisobot4MenuItem.setOnAction(event -> {
            Zaxira zaxira = new Zaxira(connection, user);
            zaxira.display();
        });

        return hisobot4MenuItem;
    }

    private MenuItem getFoydaTaqsimoti() {
        MenuItem foydaTaqsimotiMenuItem = new MenuItem("Foyda Hisoboti");
        foydaTaqsimotiMenuItem.setOnAction(event -> {
            FoydaHisoboti foydaHisoboti = new FoydaHisoboti(connection, user);
            foydaHisoboti.display();
        });
        return foydaTaqsimotiMenuItem;
    }

    private MenuItem getXaridJadvaliMenuItem() {
        MenuItem menuItem = new MenuItem("Xaridlar jadvali");
        menuItem.setOnAction(event -> {
            XaridlarJadvali xaridlarJadvali = new XaridlarJadvali(connection, user);
            xaridlarJadvali.display();
        });
        return menuItem;
    }

    private MenuItem getSavdoMenuItem() {
        MenuItem menuItem = new MenuItem("Savdo");
        menuItem.setOnAction(event -> {
            SotuvchiBP sotuvchi = new SotuvchiBP(connection, user);
            sotuvchi.display();
        });
        return menuItem;
    }

    private MenuItem getTaminotchi() {
        MenuItem menuItem = new MenuItem("Ta`minotchilar hisoboti");
        menuItem.setOnAction(event -> {
            Taminotchi taminotchi = new Taminotchi(connection, user);
            taminotchi.display();
        });
        return menuItem;
    }

    private MenuItem getHisobGuruhlariMenuItem() {
        MenuItem hisobGuruhlariMenuItem = new MenuItem("Hisob guruhlari");
        hisobGuruhlariMenuItem.setOnAction(event -> {
            HisobGuruhlari hisobGuruhlari = new HisobGuruhlari(connection, user);
            hisobGuruhlari.display();
        });
        return hisobGuruhlariMenuItem;
    }

    private Menu getYordamchiHisoblarMenu() {
        Menu yordamchiHisoblarMenu = new Menu("Yordamchi hisoblar");
        yordamchiHisoblarMenu.getItems().addAll(
                getTransitHisobMenuItem(),
                getFoydaHisobiMenuItem(),
                getZararHisobiMenuItem(),
                getXaridorHisobiMenuItem(),
                getPulHisobiMenuItem(),
                getNdsHisobiMenuItem(),
                getChegirmaHisobiMenuItem(),
                getBankHisobiMenuItem(),
                getBankXizmatiMenuItem(),
                getBojxonaMenuItem(),
                getTasdiqMenuItem()
        );
        return yordamchiHisoblarMenu;
    }

    private MenuItem getTransitHisobMenuItem() {
        MenuItem transitHisobMenuItem = new MenuItem("Keldi-ketdi hisobi");
        transitHisobMenuItem.setOnAction(event -> {
            Standart2Controller standart2Controller = new Standart2Controller("TranzitHisob", "TranzitHisobGuruhi", "Keldi-ketdi hisoblari");
            try {
                standart2Controller.start(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        return transitHisobMenuItem;
    }

    private MenuItem getFoydaHisobiMenuItem() {
        MenuItem foydaHisobiMenuItem = new MenuItem("Foyda hisobi");
        foydaHisobiMenuItem.setOnAction(event -> {
            Standart2Controller standart2Controller = new Standart2Controller("FoydaHisobi", "FoydaHisobiGuruhi", "Foyda hisoblari");
            try {
                standart2Controller.start(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return foydaHisobiMenuItem;
    }

    private MenuItem getZararHisobiMenuItem() {
        MenuItem zararHisobiMenuItem = new MenuItem("Zarar hisobi");
        zararHisobiMenuItem.setOnAction(event -> {
            Standart2Controller standart2Controller = new Standart2Controller("Zarar", "ZararGuruhi", "Zarar hisoblari");
            try {
                standart2Controller.start(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return zararHisobiMenuItem;
    }

    private MenuItem getNdsHisobiMenuItem() {
        MenuItem ndsHisobiMenuItem = new MenuItem("NDS hisobi");
        ndsHisobiMenuItem.setOnAction(event -> {
            Standart2Controller standart2Controller = new Standart2Controller("NDS1", "NDS2", "NDS hisoblari");
            try {
                standart2Controller.start(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return ndsHisobiMenuItem;
    }

    private MenuItem getXaridorHisobiMenuItem() {
        MenuItem ndsHisobiMenuItem = new MenuItem("Xaridor hisobi");
        ndsHisobiMenuItem.setOnAction(event -> {
            Standart2Controller standart2Controller = new Standart2Controller("Xaridor", "Xaridor1", "Xaridor hisobi");
            try {
                standart2Controller.start(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return ndsHisobiMenuItem;
    }

    private MenuItem getPulHisobiMenuItem() {
        MenuItem ndsHisobiMenuItem = new MenuItem("Pul hisobi");
        ndsHisobiMenuItem.setOnAction(event -> {
            Standart2Controller standart2Controller = new Standart2Controller("PulHisobi", "PulHisobi1", "Pul hisobi");
            try {
                standart2Controller.start(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return ndsHisobiMenuItem;
    }

    private MenuItem getChegirmaHisobiMenuItem() {
        MenuItem chegirmaHisobiMenuItem = new MenuItem("Chegirma hisobi");
        chegirmaHisobiMenuItem.setOnAction(event -> {
            Standart2Controller standart2Controller = new Standart2Controller("Chegirma", "ChegirmaGuruhi", "Chegirma hisoblari");
            try {
                standart2Controller.start(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return chegirmaHisobiMenuItem;
    }

    private MenuItem getBankHisobiMenuItem() {
        MenuItem bankHisobiMenuItem = new MenuItem("Bank hisobi");
        bankHisobiMenuItem.setOnAction(event -> {
            Standart2Controller standart2Controller = new Standart2Controller("Bank", "Bank1", "Bank hisoblari");
            try {
                standart2Controller.start(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return bankHisobiMenuItem;
    }

    private MenuItem getBankXizmatiMenuItem() {
        MenuItem bankXizmatiMenuItem = new MenuItem("Bank xizmati hisobi");
        bankXizmatiMenuItem.setOnAction(event -> {
            Standart2Controller standart2Controller = new Standart2Controller("BankXizmati", "BankXizmati1", "Bank xizmati hisoblari");
            try {
                standart2Controller.start(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return bankXizmatiMenuItem;
    }

    private MenuItem getBojxonaMenuItem() {
        MenuItem bojXonaSoligi = new MenuItem("Bojxona solig`i hisobi");
        bojXonaSoligi.setOnAction(event -> {
            Standart2Controller standart2Controller = new Standart2Controller("Bojxona", "Bojxona2", "Bojxona hisoblari");
            try {
                standart2Controller.start(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return bojXonaSoligi;
    }

    private MenuItem getTasdiqMenuItem() {
        MenuItem tasdiq = new MenuItem("Tasdiq kitish hisobi");
        tasdiq.setOnAction(event -> {
            Standart2Controller standart2Controller = new Standart2Controller("Tasdiq", "Tasdiq2", "Tasdiq kitish hisobi");
            try {
                standart2Controller.start(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return tasdiq;
    }

    private MenuItem getUserMenuItem() {
        MenuItem userMenuItem = new MenuItem("Dastur yurituvchilar");
        userMenuItem.setOnAction(event -> {
            UsersController usersController = new UsersController(connection, user);
            usersController.display();
        });
        return userMenuItem;
    }

    private MenuItem getXodimMavqesiMenuItem() {
        MenuItem userMenuItem = new MenuItem("Xodim mavqesi");
        userMenuItem.setOnAction(event -> {
            StandartController standartController = new StandartController(connection, user, "XodimMavqesi");
            standartController.showAndWait();
        });
        return userMenuItem;
    }

    private MenuItem getDasturlarRoyxatiMenuItem() {
        MenuItem menuItem = new MenuItem("Dasturlar ro`yxati");
        menuItem.setOnAction(event -> {
            StandartController standartController = new StandartController(connection, user, "Dasturlar");
            standartController.showAndWait();
        });
        return menuItem;
    }

    private MenuItem getSozlovMenuItem() {
        MenuItem menuItem = new MenuItem("Sozlovlar");
        menuItem.setOnAction(event -> {
            Sozlovlar sozlovlar = new Sozlovlar(connection, user);
            sozlovlar.display();
        });
        return menuItem;
    }

    private MenuItem kirshCheklanganHisoblar() {
        MenuItem kirishCheklovi = new MenuItem("Kirish cheklangan hisoblar");
        kirishCheklovi.setOnAction(event -> {
            HisobCheklovlari hisobCheklovlari = new HisobCheklovlari(connection, user);
            hisobCheklovlari.display();
        });
        return kirishCheklovi;
    }

    private MenuItem getChangeUserMenuItem() {
        MenuItem changeUserMenuItem = new MenuItem("Dastur yurituvchini alishtir");
        changeUserMenuItem.setOnAction(event -> {
            loginUserController.logOut();
            loginUserController.login();
        });
        return changeUserMenuItem;
    }

    private Menu serverSozlashMenu() {
        Menu menu = new Menu("Server sozlash");
        MenuItem menuItem1 = new MenuItem("Local server");
        MenuItem menuItem2 = new MenuItem("Remote server");

        menuItem1.setOnAction(event -> {
            ServerLocalController serverLocalController = new ServerLocalController();
            serverLocalController.display();;
        });

        menuItem2.setOnAction(event -> {
            ServerRemoteController serverRemoteController = new ServerRemoteController();
            serverRemoteController.display();
        });
        menu.getItems().addAll(menuItem1, menuItem2);
        return menu;
    }

    private Menu getPulMenu() {
        Menu pulMenu = new Menu("Pul");
        MenuItem pulMenuItem = new MenuItem("Pullar");
        MenuItem pulMavqesiMenuItem = new MenuItemStandart("PulMavqesi", "Pul mavqesi");
        MenuItem kurslarMenuItem = new MenuItem("Kurslar");
        SeparatorMenuItem separatorPulGuruhlariItem = new SeparatorMenuItem();
        MenuItem valutaGuruhlariMenuItem = new MenuItemStandart("ValutaGuruhlari", "Pul guruhlari");
        MenuItem pulHarakatlariMenuItem = new MenuItem("Pul harakatlari");
        pulMenu.getItems().add(pulMenuItem);
        pulMenu.getItems().add(pulMavqesiMenuItem);
        pulMenu.getItems().add(kurslarMenuItem);
        pulMenu.getItems().add(pulHarakatlariMenuItem);
        pulMenu.getItems().add(valutaGuruhlariMenuItem);

        pulMenuItem.setOnAction(e -> {
            ValutaController valutaController = new ValutaController();
            valutaController.display(connection, user);
        });

        kurslarMenuItem.setOnAction(e -> {
            try {
                KursController kursController = new KursController(connection, user);
                kursController.showAndWait();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (ParseException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        pulHarakatlariMenuItem.setOnAction(event -> {
            show(1);
        });

        return pulMenu;
    }

    private Menu getTovarMenu() {
        Menu tovarMenu = new Menu("Tovar");
        MenuItem tovarMenuItem = new MenuItem("Tovarlar");
        MenuItem yangiTovarMenuItem = new MenuItem("Yangi tovar");
        MenuItem tartibMenuItem = new MenuItem("Tovar sotish tartibi");
        MenuItem ndsMenuItem = new MenuItem("Tovar NDS miqdori");
        MenuItem xnMenuItem = new MenuItem("Tovar xarid narhlari");
        SeparatorMenuItem separatorTovarGuruhlariItem = new SeparatorMenuItem();
        MenuItem tovarNarhlariMenuItem = new MenuItem("TovarNarhlari");
        MenuItem tovarGuruhlariMenuItem = new MenuItem("Tovar guruhlari");
        MenuItem tovarXaridiMenuItem = new MenuItem("Tovar xaridi");
        MenuItem tovarHarakatlariMenuItem = new MenuItem("Tovar harakatlari");
        MenuItem birlikMenuItem = new MenuItemStandart("Birlik", "O`lchov birliklari");
        MenuItem jumlaMenuItem = new MenuItemStandart("Jumla", "Tovar jamlanmasi");
        MenuItem serialNumberMenuItem = new MenuItem("Seriya raqami");
        tovarMenu.getItems().addAll(tovarMenuItem, yangiTovarMenuItem, tartibMenuItem, ndsMenuItem, separatorTovarGuruhlariItem, birlikMenuItem, tovarNarhlariMenuItem, tovarXaridiMenuItem, tovarHarakatlariMenuItem, tovarGuruhlariMenuItem, serialNumberMenuItem);

        tovarMenuItem.setOnAction(e -> {
            TovarController tovarController = new TovarController(connection, user);
            tovarController.display();
        });
        yangiTovarMenuItem.setOnAction(event ->{
            YangiTovar1 yangiTovar = new YangiTovar1(connection, user);
            yangiTovar.display();
        });
        tartibMenuItem.setOnAction(event -> {
            TartibController tartibController = new TartibController(connection, user);
            tartibController.display();
        });
        ndsMenuItem.setOnAction(event -> {
            NdsController ndsController = new NdsController(connection, user);
            ndsController.display();
        });
        tovarXaridiMenuItem.setOnAction(event -> {
            show(2);
        });

        tovarHarakatlariMenuItem.setOnAction(event -> {
            show(3);
        });

        tovarNarhlariMenuItem.setOnAction(event -> {
            TovarNarhiController tovarNarhiController = new TovarNarhiController(connection, user);
            try {
                tovarNarhiController.display();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        tovarGuruhlariMenuItem.setOnAction(event -> {
            TovarGuruhlari tovarGuruhlari = new TovarGuruhlari(connection, user);
            tovarGuruhlari.display();
        });

        serialNumberMenuItem.setOnAction(event -> {
            SerialNumbersController serialNumbersController = new SerialNumbersController(connection, user);
            serialNumbersController.display();
        });



        return tovarMenu;
    }

    private Menu getAmalMenu() {
        Menu amalMenu = new Menu("Amal");
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem amalMenuItem = new MenuItemStandart("Amal", "Amallar");
        MenuItem qarzAmalMenuItem = new MenuItemStandart("QarzAmallari", "Qarz amallari");
        MenuItem menuAmalGuruhlariItem = new MenuItemStandart("AmalGuruhlari", "Amal guruhlari");
        amalMenu.getItems().addAll(amalMenuItem, separatorMenuItem, qarzAmalMenuItem, menuAmalGuruhlariItem);
        return amalMenu;
    }

    private Menu getSavdoMenu() {
        Menu savdoMenu = new Menu("Savdo");
        MenuItem mijozTurlariMenu = new MenuItemStandart("MijozTuri", "Mijoz turlari");
        MenuItem narhTurlariMenu = new MenuItemStandart("NarhTuri", "Savdo turlari");
        MenuItem savdoTurlariMenu = new MenuItemStandart("SavdoTuri", "Savdo turlari");
        MenuItem chiqimShakliMenuItem = new MenuItemStandart("ChiqimShakli", "Chiqim shakli");
        MenuItem tolovShakliMenuItem = new MenuItemStandart("TolovShakli", "To`lov shakli");
        MenuItem savdoNuqtalariMenuItem = new MenuItem("Savdo nuqtalari");
        MenuItem savdoBolimlariMenuItem = new MenuItem("Savdo bo`limlari");
        savdoMenu.getItems().addAll(narhTurlariMenu, chiqimShakliMenuItem, tolovShakliMenuItem, savdoNuqtalariMenuItem, savdoBolimlariMenuItem, getXaridJadvaliMenuItem());
        savdoNuqtalariMenuItem.setOnAction(e -> {
            Kassalarim kassalarim = new Kassalarim(connection, user);
            kassalarim.display();
        });

        savdoBolimlariMenuItem.setOnAction(event -> {
            Bolimlar bolimlar = new Bolimlar(connection, user);
            bolimlar.display();
        });

        return savdoMenu;
    }

    private Menu getHisobotMenu() {
        Menu menu = new Menu("Hisobot");
        menu.getItems().addAll(
                getHisobotPulMenuItem(),
                getHisobot2MenuItem(),
                getHisobot3MenuItem(),
                getHisobot4MenuItem(),
                getZaxiraMenuItem(),
                getFoydaTaqsimoti(),
                getTaminotchi()
                );
        return menu;
    }

    public void show(Integer amalTuri) {
        try {
            HKCont hkCont = new HKCont(connection, user, amalTuri);
            hkCont.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*************************************************
     **                Classlar                     **
     *************************************************/
    class MenuItemStandart extends MenuItem {
        String tableName = "";
        String titleName = "";

        public MenuItemStandart(String tableName, String titleName) {
            this.tableName = tableName;
            this.titleName = titleName;
            setText(titleName);
            setOnAction(e -> {
                StandartController standartController = new StandartController(connection, user, tableName);
                standartController.showAndWait();
            });
        }
        public MenuItemStandart(Connection connection, String tableName, String titleName) {
            this.tableName = tableName;
            this.titleName = titleName;
            setText(titleName);
            setOnAction(e -> {
                StandartController standartController = new StandartController(connection, user, tableName);
                standartController.showAndWait();
            });
        }
    }
    class MenuItemGuruhlar extends MenuItem {
        String tableName = "";
        String titleName = "";

        public MenuItemGuruhlar(String tableName, String titleName) {
            this.tableName = tableName;
            this.titleName = titleName;
            setText(titleName);
            setOnAction(e -> {
                GuruhlarController guruhlarController = new GuruhlarController(connection, user, tableName);
                    guruhlarController.show();
            });
        }
    }

    private FlowPane initCenterFlowPane() {
        FlowPane flowPane = new FlowPane();
        flowPane.setMinHeight(500);
        Sotilgan sotilgan = new Sotilgan(connection, user);
        flowPane.getChildren().addAll(sotilgan.getCenterPane());

        SotilmaganTovarlar sotilmaganTovarlar = new SotilmaganTovarlar(connection, user);
        flowPane.getChildren().add(sotilmaganTovarlar.getCenterPane());

        Haqdor haqdor = new Haqdor(connection, user);
        flowPane.getChildren().add(haqdor.getCenterPane());

        Qarzdor qarzdor = new Qarzdor(connection, user);
        flowPane.getChildren().add(qarzdor.getCenterPane());
        return flowPane;
    }
}
