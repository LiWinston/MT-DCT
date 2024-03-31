package server;

import javax.swing.*;
import java.awt.*;

/**
 * Shizhan Xu, 771900
 * University of Melbourne
 * All rights reserved
 */
public class ServerLogger {
    static void logGeneralErr(String msg) {
        System.err.println(STR."Err: \{msg}");
    }

    static void logInvalidArgumentErr(String arg) {
        System.err.println(STR."Argument : \{arg} invalid! ");
        System.exit(1);
    }

    public static void connectionError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent,
                "Connection error: " + msg,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void IOError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent,
                "IO error: " + msg,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

}
