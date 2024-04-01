/*
 * Created by JFormDesigner on Mon Apr 01 16:50:15 AEDT 2024
 */

package server.UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;


/**
 * @author Winston
 */
public class ServerMonitor extends JFrame {
    public static void main(String[] args) {
        ServerMonitor serverMonitor = new ServerMonitor();
        serverMonitor.setVisible(true);
    }
    public ServerMonitor() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        AcceptNewConnectionButton = new JCheckBoxMenuItem();

        //======== this ========
        setName("this");
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== menuBar1 ========
        {
            menuBar1.setName("menuBar1");

            //======== menu1 ========
            {
                menu1.setText("ServerSettings");
                menu1.setName("menu1");

                //---- AcceptNewConnectionButton ----
                AcceptNewConnectionButton.setText("AcceptNewConnection");
                AcceptNewConnectionButton.setSelected(true);
                AcceptNewConnectionButton.setName("AcceptNewConnectionButton");
                menu1.add(AcceptNewConnectionButton);
            }
            menuBar1.add(menu1);
        }
        setJMenuBar(menuBar1);

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JCheckBoxMenuItem AcceptNewConnectionButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
