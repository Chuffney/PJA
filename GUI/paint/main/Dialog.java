package main;

import javax.swing.*;
import java.awt.Component;

public class Dialog {
    public enum Response {
        YES,
        NO,
        CANCEL
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static Response askForSave(Component parent) {
        int response = JOptionPane.showConfirmDialog(parent, "Do you want to save changes?", "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
        return Response.values()[response];
    }

    public static Response askForConfirm(Component parent, String message, String title) {
        int response = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION);
        return Response.values()[response];
    }
}
