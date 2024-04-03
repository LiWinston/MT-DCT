/*
 * Created by JFormDesigner on Mon Apr 01 23:46:57 AEDT 2024
 */

package client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * @author Winston
 */
public class UI extends JFrame {
    Client client;
    public UI(Client cl) {
        this.client = cl;
        initComponents();
        setVisible(true);
    }

    private void connectButtonActionPerformed(ActionEvent a) {
        try {
            client.connect();
        } catch (UnknownHostException e) {
            System.out.println("server address cannot be reached");
        } catch (IllegalArgumentException e) {
            System.out.println("port number over range");
        } catch (ConnectException e) {
            System.out.println("Connection declined or no server found, consider port number availability");
            client.connectionError("Refused or no server found");
        } catch (IOException e) {
            System.out.println("The server is down");
        }
        if (client.socket != null) {
            System.out.println("Client socket not null");
            if (client.socket.isConnected()) {
//            new UI(client);
                Thread clientThread = new Thread(client);
                clientThread.start();
            }
        }
        JPanel searchPanel = new OperateUI(client);
        add(searchPanel);
        remove(Welcome);
        pack();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        Welcome = new JPanel();
        WelcomeMsg = new JTextField();
        connectButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("MT-DCT");
        setResizable(false);
        setName("this");
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== Welcome ========
        {
            Welcome.setName("Welcome");
            Welcome.setLayout(new GridBagLayout());
            ((GridBagLayout)Welcome.getLayout()).columnWidths = new int[] {600, 0};
            ((GridBagLayout)Welcome.getLayout()).rowHeights = new int[] {205, 105, 0, 0};
            ((GridBagLayout)Welcome.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout)Welcome.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0E-4};

            //---- WelcomeMsg ----
            WelcomeMsg.setEditable(false);
            WelcomeMsg.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 14));
            WelcomeMsg.setText("Welcome to MT-DCT, Click Connect to  Start");
            WelcomeMsg.setHorizontalAlignment(SwingConstants.CENTER);
            WelcomeMsg.setMinimumSize(null);
            WelcomeMsg.setFocusable(false);
            WelcomeMsg.setName("WelcomeMsg");
            Welcome.add(WelcomeMsg, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

            //---- connectButton ----
            connectButton.setText("Connect");
            connectButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 18));
            connectButton.setName("connectButton");
            connectButton.addActionListener(e -> connectButtonActionPerformed(e));
            Welcome.add(connectButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));
        }
        contentPane.add(Welcome, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel Welcome;
    private JTextField WelcomeMsg;
    private JButton connectButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
