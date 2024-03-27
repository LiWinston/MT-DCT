import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
/*
 * Created by JFormDesigner on Wed Mar 27 19:11:48 AEDT 2024
 *
 * @author yongchunLi 1378156
 */
public class MTDCT_Server_Monitor extends JFrame {
    public MTDCT_Server_Monitor() {
        initComponents();

        // 添加窗口大小变化事件监听器
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustComponents();
            }
        });
    }

    private void adjustComponents() {
        // 获取内容面板
        Container contentPane = getContentPane();
        // 获取内容面板大小
        Dimension size = contentPane.getSize();
        // 获取标签组件
        Component[] components = contentPane.getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                // 设置标签位置为内容面板中心
                JLabel label = (JLabel) component;
                int labelWidth = label.getPreferredSize().width;
                int labelHeight = label.getPreferredSize().height;
                int x = (size.width - labelWidth) / 2;
                int y = (size.height - labelHeight) / 2;
                label.setBounds(x, y, labelWidth, labelHeight);
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        menu2 = new JMenu();
        menu3 = new JMenu();
        label1 = new JLabel();

        //======== this ========
        setTitle("MTDCT");
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
            }
            menuBar1.add(menu1);

            //======== menu2 ========
            {
                menu2.setText("text");
                menu2.setName("menu2");
            }
            menuBar1.add(menu2);

            //======== menu3 ========
            {
                menu3.setText("text");
                menu3.setName("menu3");
            }
            menuBar1.add(menu3);
        }
        setJMenuBar(menuBar1);

        //---- label1 ----
        label1.setText("MTDCT Server Monitor");
        label1.setName("label1");
        contentPane.add(label1);
        label1.setBounds(new Rectangle(new Point(0, 0), label1.getPreferredSize()));

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
    private JMenu menu2;
    private JMenu menu3;
    private JLabel label1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
