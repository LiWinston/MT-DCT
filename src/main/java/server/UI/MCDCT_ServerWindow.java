/**
 * @Author: 1378156 Yongchun Li
 */



package server.UI;

import server.ServerDriver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Winston
 */
public class MCDCT_ServerWindow extends JFrame {
    public MCDCT_ServerWindow(ServerDriver serverDriver) {
        this.serverDriver = serverDriver;
        initComponents();
    }
    ServerDriver serverDriver;


    private void checkBoxMenuItem1(ActionEvent e) {
        serverDriver.setAllowNewConnection(checkBoxMenuItem1.isSelected());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        checkBoxMenuItem1 = new JCheckBoxMenuItem();

        //======== this ========
        setName("this");
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== menuBar1 ========
        {
            menuBar1.setName("menuBar1");

            //======== menu1 ========
            {
                menu1.setText("text");
                menu1.setName("menu1");

                //---- checkBoxMenuItem1 ----
                checkBoxMenuItem1.setText("AllowNewConnect");
                checkBoxMenuItem1.setName("checkBoxMenuItem1");
                checkBoxMenuItem1.addActionListener(this::checkBoxMenuItem1);
                menu1.add(checkBoxMenuItem1);
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
    private JCheckBoxMenuItem checkBoxMenuItem1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
