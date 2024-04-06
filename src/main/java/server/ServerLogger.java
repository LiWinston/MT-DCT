/**
 * @Author: 1378156 Yongchun Li
 */

package server;

import javax.swing.*;
import java.awt.*;


public class ServerLogger {
    static void logGeneralErr(String msg) {
        System.err.println(STR."Err: \{msg}");
    }

    static void logInvalidArgumentErr(String arg) {
        System.err.println(STR."Argument : \{arg} invalid! ");
        System.exit(1);
    }

    public static void connectionError(Component parent, String msg, String connectionErrorType) {
        JOptionPane.showMessageDialog(parent,
                STR."Connection error: \{msg}",
                STR."\{connectionErrorType }Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void IOError(Component parent, String msg, String IOErrorType) {
        JOptionPane.showMessageDialog(parent,
                STR."IO error: \{msg}",
                STR."\{IOErrorType}Error",
                JOptionPane.ERROR_MESSAGE);
    }

}
