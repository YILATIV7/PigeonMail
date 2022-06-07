package com.vitalytyrenko.pigeonmail;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Sprites {

    private static final Image ICON;
    private static final Image GRASS;
    private static final Image MAILBOX;
    private static final Image MAILBOX_SELECTED;
    private static final Image PIGEON;
    private static final Image PIGEON_SELECTED;
    private static final Image WHITE_PIGEON;
    private static final Image WHITE_PIGEON_SELECTED;
    private static final Image POST_PIGEON_WITH_MAIL;
    private static final Image POST_PIGEON_WITH_MAIL_SELECTED;
    private static final Image POST_PIGEON_WITHOUT_MAIL;
    private static final Image POST_PIGEON_WITHOUT_MAIL_SELECTED;
    private static final Image SUCCESS;
    private static final Image FAILURE;

    static {
        try {
            ICON = from("icon.png");
            GRASS = from("grass.jpg");
            MAILBOX = from("mailbox.png");
            MAILBOX_SELECTED = from("mailbox-selected.png");
            PIGEON = from("pigeon.png");
            PIGEON_SELECTED = from("pigeon-selected.png");
            WHITE_PIGEON = from("white-pigeon.png");
            WHITE_PIGEON_SELECTED = from("white-pigeon-selected.png");
            POST_PIGEON_WITH_MAIL = from("post-pigeon-with-mail.png");
            POST_PIGEON_WITH_MAIL_SELECTED = from("post-pigeon-with-mail-selected.png");
            POST_PIGEON_WITHOUT_MAIL = from("post-pigeon-without-mail.png");
            POST_PIGEON_WITHOUT_MAIL_SELECTED = from("post-pigeon-without-mail-selected.png");
            SUCCESS = from("success.png");
            FAILURE = from("failure.png");

        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Image getIcon() {
        return ICON;
    }

    public static Image getGrass() {
        return GRASS;
    }

    public static Image getMailbox(boolean isSelected) {
        return isSelected ? MAILBOX_SELECTED : MAILBOX;
    }

    public static Image getPigeon(boolean isSelected) {
        return isSelected ? PIGEON_SELECTED : PIGEON;
    }

    public static Image getWhitePigeon(boolean isSelected) {
        return isSelected ? WHITE_PIGEON_SELECTED : WHITE_PIGEON;
    }

    public static Image getPostPigeon(boolean isSelected, boolean hasMail) {
        if (hasMail)
            return isSelected ? POST_PIGEON_WITH_MAIL_SELECTED : POST_PIGEON_WITH_MAIL;
        else
            return isSelected ? POST_PIGEON_WITHOUT_MAIL_SELECTED : POST_PIGEON_WITHOUT_MAIL;
    }

    public static Image getSuccess() {
        return SUCCESS;
    }

    public static Image getFailure() {
        return FAILURE;
    }

    private static Image from(String path) throws FileNotFoundException {
        return new Image(new FileInputStream("src/main/resources/raw/" + path));
    }

    public static void initialize() {
        System.out.println("Resources initialized successfully!");
    }
}
