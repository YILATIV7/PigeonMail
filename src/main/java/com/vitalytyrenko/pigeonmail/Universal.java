package com.vitalytyrenko.pigeonmail;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Universal implements EventHandler<KeyEvent>, Visualizable {

    public static final int WIDTH;
    public static final int HEIGHT;
    public static final Random RANDOMIZER;

    private static final Universal INSTANCE;

    static {
        WIDTH = 3 * 1024;
        HEIGHT = 3 * 1024;
        RANDOMIZER = new Random(555);
        INSTANCE = new Universal();
    }

    public static Universal getInstance() {
        return INSTANCE;
    }

    private final List<Pigeon> freePigeons;
    private final List<MailBox> mailBoxes;
    private final AnchorPane root;
    private final AnchorPane surface;
    private final AnchorPane objectsContainer;
    private final SelectManager selectManager;
    private final Map map;

    private boolean showDebugInfo;
    private boolean isPaused;
    private Stage createPigeonStage;

    {
        freePigeons = new ArrayList<>();
        mailBoxes = new ArrayList<>();
        map = new Map();
        showDebugInfo = false;
        isPaused = false;
    }

    private Universal() {
        selectManager = new SelectManager();

        mailBoxes.add(new MailBox("Макрооб'єкт-1"));
        mailBoxes.add(new MailBox("Макрооб'єкт-2"));
        mailBoxes.add(new MailBox("Макрооб'єкт-3"));

        objectsContainer = new AnchorPane(mailBoxes.stream().map(MailBox::getNode).toList().toArray(new Node[0]));

        for (int i = 0; i < 100; i++) {
            Pigeon p = null;
            int level = RANDOMIZER.nextInt(3);

            switch (level) {
                case 0 -> p = new Pigeon();
                case 1 -> p = new WhitePigeon();
                case 2 -> p = new PostPigeon();
            }

            bindPigeonToScene(p);
        }

        PostPigeon killer = new PostPigeon();
        killer.setMoveType(Pigeon.MOVE_TYPE_CUSTOM);
        bindPigeonToScene(killer);

        // init visualization objects
        surface = new AnchorPane(
                createBackground(),
                objectsContainer
        );
        root = new AnchorPane(surface, map.getNode());

        // screen size change listener
        root.sceneProperty().addListener((obv, s, scene) ->
                scene.widthProperty().addListener((v, v1, v2) -> invalidateSurfacePosition()));
        root.sceneProperty().addListener((obv, s, scene) ->
                scene.heightProperty().addListener((v, v1, v2) -> invalidateSurfacePosition()));
    }

    private Node createBackground() {
        Label label = new Label("Універсальний об'єкт");
        label.setFont(new Font(42));
        label.setPadding(new Insets(0, 20, 0, 20));

        Rectangle rect = new Rectangle(0, 0, Color.MEDIUMAQUAMARINE);
        label.widthProperty().addListener((v, t1, t12) -> rect.setWidth(label.getWidth()));
        label.heightProperty().addListener((v, t1, t2) -> rect.setHeight(label.getHeight()));

        return new AnchorPane(
                new VBox(
                        new HBox(new ImageView(Sprites.getGrass()), new ImageView(Sprites.getGrass()), new ImageView(Sprites.getGrass())),
                        new HBox(new ImageView(Sprites.getGrass()), new ImageView(Sprites.getGrass()), new ImageView(Sprites.getGrass())),
                        new HBox(new ImageView(Sprites.getGrass()), new ImageView(Sprites.getGrass()), new ImageView(Sprites.getGrass()))
                ),
                new AnchorPane(rect, label)
        );
    }

    public void update(double dt) {
        // update map
        map.update();

        // update objects
        freePigeons.forEach(pigeon -> pigeon.update(dt, isPaused));
        mailBoxes.forEach(mailBoxes -> mailBoxes.update(dt, isPaused));

        if (!isPaused) {
            // micro-interaction
            for (Pigeon p1 : getAllPigeons()) {
                for (Pigeon p2 : getAllPigeons()) {
                    if (p1 == p2 || p1.isSelected() || p2.isSelected()) continue;

                    if (p1.collideWith(p2)) {
                        if (p1.getMoveType() == Pigeon.MOVE_TYPE_STANDARD) {
                            if (p2.getMoveType() == Pigeon.MOVE_TYPE_STANDARD) {
                                if (p1.approachTo(p2)) {
                                    bouncePigeons(p1, p2);
                                }
                            } else {
                                p1.markDeleted();
                            }
                        } else {
                            if (p2.getMoveType() == Pigeon.MOVE_TYPE_STANDARD) {
                                p2.markDeleted();
                            } else {
                                p1.markDeleted();
                                p2.markDeleted();
                            }
                        }
                    }
                }
            }
        }

        // deleting marked objects
        for (int i = 0; i < freePigeons.size(); ) {
            if (freePigeons.get(i).isDeleted()) {
                objectsContainer.getChildren().remove(freePigeons.get(i).getNode());
                freePigeons.remove(i);
            } else {
                i++;
            }
        }

        for (MailBox mailBox : mailBoxes) {
            for (int i = 0; i < mailBox.size(); ) {
                if (mailBox.get(i).isDeleted()) {
                    objectsContainer.getChildren().remove(mailBox.get(i).getNode());
                    mailBox.remove(i);
                } else {
                    i++;
                }
            }
        }

        // detach selected pigeons
        for (Pigeon pigeon : getAllPigeons()) {
            if (pigeon.isSelected() &&
                    pigeon instanceof WhitePigeon
                    && ((WhitePigeon) pigeon).isAttachedToMailBox()) {
                ((WhitePigeon) pigeon).detachFromMailBox();
                freePigeons.add(pigeon);
            }
        }

        // attaching and detaching pigeons to mailboxes
        mailBoxes.forEach(mailBox -> {
            // attach pigeons to mailboxes
            for (int i = 0; i < freePigeons.size(); ) {
                if (freePigeons.get(i).isSelected()) {
                    if (freePigeons.get(i) instanceof WhitePigeon pigeon && pigeon.nearTo(mailBox)) {
                        pigeon.attachToMailBox(mailBox);
                        freePigeons.remove(i);
                    } else i++;
                } else {
                    if (freePigeons.get(i) instanceof WhitePigeon pigeon && pigeon.nearTo(mailBox) && pigeon.approachTo(mailBox)) {
                        if (pigeon instanceof PostPigeon postPigeon) {
                            if (postPigeon.isHasMail()) {
                                if (mailBox.getMailCount() < MailBox.MAX_CAPACITY) {
                                    mailBox.putMail();
                                    postPigeon.setHasMail(false);
                                }
                            } else {
                                if (mailBox.getMailCount() > 0) {
                                    mailBox.getMail();
                                    postPigeon.setHasMail(true);
                                }
                            }
                        }

                        pigeon.attachToMailBox(mailBox);
                        freePigeons.remove(i);

                    } else i++;
                }
            }
            // detach pigeons from mailboxes
            for (int i = 0; i < mailBox.size(); ) {
                WhitePigeon pigeon = mailBox.get(i);
                if (pigeon.isTimeOut()) {
                    pigeon.detachFromMailBox();
                    freePigeons.add(pigeon);
                } else i++;
            }
        });

        // show visual debug info
        objectsContainer.getChildren().removeIf(node -> node instanceof Line);
        if (showDebugInfo) {
            for (MailBox mailBox : mailBoxes) {
                for (WhitePigeon pigeon : mailBox) {
                    Line line = new Line();
                    line.setStrokeWidth(2);
                    line.setStartX(mailBox.getX() + MailBox.WIDTH / 2.0);
                    line.setStartY(mailBox.getY() + MailBox.HEIGHT / 2.0);
                    line.setEndX(pigeon.getX() + Pigeon.WIDTH / 2.0);
                    line.setEndY(pigeon.getY() + Pigeon.HEIGHT / 2.0);
                    objectsContainer.getChildren().add(line);
                }
            }
            for (Pigeon pigeon : getAllPigeons()) {
                Line line = new Line();
                line.setStrokeWidth(2);
                line.setStartX(pigeon.getX() + Pigeon.WIDTH / 2.0);
                line.setStartY(pigeon.getY() + Pigeon.HEIGHT / 2.0);
                line.setEndX(pigeon.getX() + Pigeon.WIDTH / 2.0 + pigeon.getMoveVector().x * 40);
                line.setEndY(pigeon.getY() + Pigeon.HEIGHT / 2.0 + pigeon.getMoveVector().y * 40);
                objectsContainer.getChildren().add(line);
            }
        }
    }

    private void bouncePigeons(Pigeon p1, Pigeon p2) {
        double x1 = p1.getX(), y1 = p1.getY(), x2 = p2.getX(), y2 = p2.getY();
        double dx1 = p1.getMoveVector().x, dy1 = p1.getMoveVector().y;
        double dx2 = p2.getMoveVector().x, dy2 = p2.getMoveVector().y;

        double[] v1 = {dx1, dy1};
        double[] v2 = {dx2, dy2};

        double[] p1_to_p2Vector = {x2 - x1, y2 - y1};
        double[] p2_to_p1Vector = {x1 - x2, y1 - y2};
        double[] pv1 = projection(v1, p1_to_p2Vector);
        double[] pv2 = projection(v2, p2_to_p1Vector);

        if (!(p1 instanceof WhitePigeon && ((WhitePigeon) p1).isAttachedToMailBox()))
            p1.setMoveVector(Vector.normalize(new Vector(
                    v1[0] + pv2[0] - pv1[0],
                    v1[1] + pv2[1] - pv1[1]
            )));

        if (!(p2 instanceof WhitePigeon && ((WhitePigeon) p2).isAttachedToMailBox()))
            p2.setMoveVector(Vector.normalize(new Vector(
                    v2[0] + pv1[0] - pv2[0],
                    v2[1] + pv1[1] - pv2[1]
            )));

    }

    public void setCreatePigeonStage(Stage stage) {
        createPigeonStage = stage;
    }

    private List<Pigeon> getAllPigeons() {
        List<Pigeon> list = new ArrayList<>(freePigeons);
        for (MailBox mailBox : mailBoxes)
            for (Pigeon pigeon : mailBox)
                list.add(pigeon);
        return list;
    }

    private void invalidateSurfacePosition() {
        if (surface.getTranslateY() > 0)
            surface.setTranslateY(0);
        if (surface.getTranslateX() > 0)
            surface.setTranslateX(0);
        if (surface.getTranslateY() < -Universal.HEIGHT + root.getScene().getHeight())
            surface.setTranslateY(-Universal.HEIGHT + root.getScene().getHeight());
        if (surface.getTranslateX() < -Universal.WIDTH + root.getScene().getWidth())
            surface.setTranslateX(-Universal.WIDTH + root.getScene().getWidth());
    }

    private void createNewPigeon() {
        boolean prevIsPausedValue = isPaused;
        isPaused = true;
        createPigeonStage.showAndWait();
        isPaused = prevIsPausedValue;
    }

    public void bindPigeonToScene(Pigeon pigeon) {
        freePigeons.add(pigeon);
        objectsContainer.getChildren().add(pigeon.getNode());
        pigeon.getNode().setOnMouseClicked(e -> onMouseClickedOnPigeon(pigeon));
    }

    private void onMouseClickedOnPigeon(Pigeon pigeon) {
        if (pigeon.isSelected()) {
            selectManager.remove(pigeon);
            pigeon.setSelected(false);
        } else {
            selectManager.add(pigeon);
            pigeon.setSelected(true);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("{\n");

        for (Pigeon p : freePigeons) {
            str.append("\t").append(p).append("\n");
        }

        for (MailBox m : mailBoxes) {
            str.append("\n\t").append(m.toString().replaceAll("\n", "\n\t")).append("\n");
        }

        return str + "\n}";
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        final int scrollSpeed = 128;
        final int moveSpeed = 10;

        switch (keyEvent.getCode()) {
            case W, UP -> {
                if (selectManager.hasSelected()) {
                    selectManager.move(new Vector(0, -moveSpeed));
                    if (selectManager.getCenterY() < -surface.getTranslateY() + Universal.HEIGHT + root.getScene().getHeight() / 2.0) {
                        surface.setTranslateY(-selectManager.getCenterY() + root.getScene().getHeight() / 2.0);
                    }
                } else {
                    surface.setTranslateY(surface.getTranslateY() + scrollSpeed);
                }
                invalidateSurfacePosition();
            }
            case A, LEFT -> {
                if (selectManager.hasSelected()) {
                    selectManager.move(new Vector(-moveSpeed, 0));
                    if (selectManager.getCenterX() < -surface.getTranslateX() + Universal.WIDTH + root.getScene().getWidth() / 2.0) {
                        surface.setTranslateX(-selectManager.getCenterX() + root.getScene().getWidth() / 2.0);
                    }
                } else {
                    surface.setTranslateX(surface.getTranslateX() + scrollSpeed);
                }
                invalidateSurfacePosition();
            }
            case S, DOWN -> {
                if (selectManager.hasSelected()) {
                    selectManager.move(new Vector(0, moveSpeed));
                    if (selectManager.getCenterY() > -surface.getTranslateY() + root.getScene().getHeight() / 2.0) {
                        surface.setTranslateY(-selectManager.getCenterY() + root.getScene().getHeight() / 2.0);
                    }
                } else {
                    surface.setTranslateY(surface.getTranslateY() - scrollSpeed);
                }
                invalidateSurfacePosition();
            }
            case D, RIGHT -> {
                if (selectManager.hasSelected()) {
                    selectManager.move(new Vector(moveSpeed, 0));
                    if (selectManager.getCenterX() > -surface.getTranslateX() + root.getScene().getWidth() / 2.0) {
                        surface.setTranslateX(-selectManager.getCenterX() + root.getScene().getWidth() / 2.0);
                    }
                } else {
                    surface.setTranslateX(surface.getTranslateX() - scrollSpeed);
                }
                invalidateSurfacePosition();
            }
            case INSERT -> createNewPigeon();

            case ESCAPE -> selectManager.cancel();
            case DELETE -> selectManager.applyDelete();
            case F -> selectManager.applySwapMoveType();
            case C -> selectManager.applyClone();

            case I -> showDebugInfo = !showDebugInfo;
            case SPACE -> isPaused = !isPaused;
            case M -> map.toggleVisible();

            case F11 -> load();
            case F12 -> save();
        }
    }

    private void load() {
        boolean savedPauseState = isPaused;
        isPaused = true;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Файли ГолубПошти", "*.bin"),
                new FileChooser.ExtensionFilter("Усі файли", "*.*")
        );

        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file == null) {
            isPaused = false;
            return;
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            unbindAllFromScene();
            this.freePigeons.addAll((List<Pigeon>) ois.readObject());
            this.mailBoxes.addAll((List<MailBox>) ois.readObject());
            bindAllToScene();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успіх");
            alert.setContentText("Файл успішно завантажено!");
            alert.setHeaderText(null);
            alert.setGraphic(new ImageView(Sprites.getSuccess()));
            alert.showAndWait();

        } catch (IOException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка");
            alert.setContentText("Не вдалося завантажити файл!");
            alert.setHeaderText(null);
            alert.setGraphic(new ImageView(Sprites.getFailure()));
            alert.showAndWait();
        }

        isPaused = savedPauseState;
    }

    private void save() {
        boolean savedPauseState = isPaused;
        isPaused = true;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Файли ГолубПошти", "*.bin"),
                new FileChooser.ExtensionFilter("Усі файли", "*.*")
        );

        File file = fileChooser.showSaveDialog(root.getScene().getWindow());
        if (file == null) {
            isPaused = false;
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(freePigeons);
            oos.writeObject(mailBoxes);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успіх");
            alert.setContentText("Файл успішно збережено!");
            alert.setHeaderText(null);
            alert.setGraphic(new ImageView(Sprites.getSuccess()));
            alert.showAndWait();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка");
            alert.setContentText("Не вдалося зберегти файл!");
            alert.setHeaderText(null);
            alert.setGraphic(new ImageView(Sprites.getFailure()));
            alert.showAndWait();
        }

        isPaused = savedPauseState;
    }

    private void bindAllToScene() {
        for (MailBox mailBox : mailBoxes) {
            objectsContainer.getChildren().add(mailBox.getNode());

            for (WhitePigeon pigeon : mailBox) {
                objectsContainer.getChildren().add(pigeon.getNode());
                pigeon.getNode().setOnMouseClicked(e -> onMouseClickedOnPigeon(pigeon));
            }
        }

        for (Pigeon pigeon : freePigeons) {
            objectsContainer.getChildren().add(pigeon.getNode());
            pigeon.getNode().setOnMouseClicked(e -> onMouseClickedOnPigeon(pigeon));
        }
    }

    private void unbindAllFromScene() {
        while (freePigeons.size() > 0) {
            objectsContainer.getChildren().remove(freePigeons.get(0).getNode());
            freePigeons.remove(0);
        }

        while (mailBoxes.size() > 0) {
            MailBox mailBox = mailBoxes.get(0);
            while (mailBox.size() > 0) {
                objectsContainer.getChildren().remove(mailBox.get(0).getNode());
                mailBox.remove(0);
            }

            objectsContainer.getChildren().remove(mailBox.getNode());
            mailBoxes.remove(0);
        }
    }

    @Override
    public Parent getNode() {
        return root;
    }

    // Vector Utils

    private static double[] projection(double[] v, double[] p) {
        double len = Math.sqrt(p[0] * p[0] + p[1] * p[1]);
        double projectionLen = (v[0] * p[0] + v[1] * p[1]) / len;

        return new double[]{
                projectionLen * p[0] / len,
                projectionLen * p[1] / len
        };
    }

    private class Map {

        private final Canvas canvas = new Canvas(256, 256);
        private final GraphicsContext ctx = canvas.getGraphicsContext2D();

        private boolean isVisible = true;

        private Map() {
            canvas.setOnMouseClicked(event -> {
                double x = event.getX() - 3;
                double y = event.getY() - 3;
                double surfaceX = -Universal.WIDTH * x / 250 + canvas.getScene().getWidth() / 2;
                double surfaceY = -Universal.HEIGHT * y / 250 + canvas.getScene().getHeight() / 2;

                surface.setTranslateX(surfaceX);
                surface.setTranslateY(surfaceY);
                invalidateSurfacePosition();

                selectManager.move(new Vector(
                        -surfaceX + canvas.getScene().getWidth() / 2 - selectManager.getCenterX(),
                        -surfaceY + canvas.getScene().getHeight() / 2 - selectManager.getCenterY()
                ));
            });
        }

        private void update() {
            // drawing border
            ctx.setFill(Color.DARKBLUE);
            ctx.fillRect(0, 0, 256, 256);

            // drawing micro- and macro-objects
            int scaledPigeonWidth = (int) (250 * Pigeon.WIDTH / (Universal.WIDTH * 1.0));
            int scaledPigeonHeight = (int) (250 * Pigeon.HEIGHT / (Universal.HEIGHT * 1.0));
            int scaledMailBoxWidth = (int) (250 * MailBox.WIDTH / (Universal.WIDTH * 1.0));
            int scaledMailBoxHeight = (int) (250 * MailBox.HEIGHT / (Universal.HEIGHT * 1.0));

            ctx.setFill(Color.WHITE);
            ctx.fillRect(3, 3, 250, 250);

            for (MailBox mailBox : mailBoxes) {
                ctx.setFill(Color.YELLOW);
                ctx.fillRect(
                        3 + 250.0 * mailBox.getX() / Universal.WIDTH,
                        3 + 250.0 * mailBox.getY() / Universal.HEIGHT,
                        scaledMailBoxWidth,
                        scaledMailBoxHeight
                );
            }

            for (Pigeon pigeon : getAllPigeons()) {
                ctx.setFill(pigeon.getMoveType() == Pigeon.MOVE_TYPE_CUSTOM ? Color.RED : Color.GRAY);
                ctx.fillRect(
                        3 + 250 * pigeon.getX() / Universal.WIDTH,
                        3 + 250 * pigeon.getY() / Universal.HEIGHT,
                        scaledPigeonWidth,
                        scaledPigeonHeight
                );
            }

            // drawing viewport
            ctx.setStroke(Color.RED);
            ctx.setLineWidth(3);
            ctx.strokeRect(
                    1.5 + 250 * -surface.getTranslateX() / Universal.WIDTH,
                    1.5 + 250 * -surface.getTranslateY() / Universal.HEIGHT,
                    3 + 250 * canvas.getScene().getWidth() / Universal.WIDTH,
                    3 + 250 * canvas.getScene().getHeight() / Universal.HEIGHT
            );

            // moving canvas if screen size changed
            canvas.setTranslateX(canvas.getScene().getWidth() - 256);
        }

        private void toggleVisible() {
            isVisible = !isVisible;
            canvas.setVisible(isVisible);
        }

        private Node getNode() {
            return canvas;
        }
    }
}
