/*
 * Created by JFormDesigner on Thu Apr 08 08:35:13 CST 2021
 */

package client;

import prtc.Response;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import java.awt.*;
import java.awt.event.ActionEvent;

public class OperateUI extends JPanel {
    private static final int MAX_WORD_LENGTH = 25;

    protected Client client;
    public OperateUI(Client client) {
        this.client = client;
        initComponents();
    }

    private boolean searchBarFormatCheck() {
        StringBuilder sb = new StringBuilder(searchBar.getText().toLowerCase());
        boolean isCorrect = true;
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) < 'a' || sb.charAt(i) > 'z') {
                isCorrect = false;
                sb.deleteCharAt(i);
                i--;
            }
        }
        searchBar.setText(sb.toString());
        if (!isCorrect){
            client.formatWarning(
                    "the word must not contain non-alphabet characters.\n" +
                            "We have corrected for you, please resubmit.");
        }
        return isCorrect;
    }

    private boolean meaningsFormatCheck() {
        StringBuilder sb = new StringBuilder(meaningsText.getText());
        boolean isCorrect = true;
        // Add '-' to the beginning.
        if (sb.charAt(0) != '-') sb.insert(0, '-');
        // Delete all empty meanings, i.e delete all new line characters
        // following a '-' mark. Also delete all slashes '/'.
        for (int i = 1; i < sb.length(); i++) {
            if (sb.charAt(i) == '\n' && sb.charAt(i - 1) == '-'
                    || sb.charAt(i) == '/') {
                sb.deleteCharAt(i);
                i--;
            }
        }
        // Ensure all '-' except the first one start from a new line
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '-') {
                if (i != 0 && sb.charAt(i - 1) != '\n')
                    sb.insert(i, '\n');
                while (i < sb.length() - 1 && sb.charAt(i + 1) == '-')
                    i++;
            }
        }
        // The last line of meanings could be empty, detect and delete
        int j = sb.length() - 1;
        while(j >= 0) {
            if (sb.charAt(j) == '-' || sb.charAt(j) == '\n') {
                sb.deleteCharAt(j);
                j--;
            } else
                break;
        }
        // Revise all continuous '-' to have length of 2.
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '-') {
                i++;
                if (i == sb.length()) {
                    sb.append('-');
                } else if (sb.charAt(i) != '-') {
                    sb.insert(i, '-');
                } else {
                    while (i + 1 < sb.length() && sb.charAt(i + 1) == '-') {
                        sb.deleteCharAt(i + 1);
                    }
                }
            }
        }
        isCorrect = sb.toString().equals(meaningsText.getText());
        meaningsText.setText(sb.toString());
        if (!isCorrect)
            client.formatWarning(
                    "Your meanings input has incorrect format\n" +
                            "We have corrected for you, please double check.\n" +
                            "Remember: each meaning starts with -- from a " +
                            "new line.\nNo empty meaning allowed.\n" +
                            "No slash marks (/) allowed\n" +
                            "No trailing new line or dashes allowed.");
        return isCorrect;
    }


    private void searchBarCaretUpdate(CaretEvent e) {
        String searchBarText = searchBar.getText();
        String meaningsAreaText = meaningsText.getText();
        searchButton.setEnabled(!searchBarText.isEmpty());
        deleteButton.setEnabled(!searchBarText.isEmpty());
        addButton.setEnabled(!searchBarText.isEmpty() && !meaningsAreaText.isEmpty());
        updateButton.setEnabled(!searchBarText.isEmpty() && !meaningsAreaText.isEmpty());

        if (searchBarText.length() > MAX_WORD_LENGTH)
            SwingUtilities.invokeLater(
                    () -> searchBar.setText(searchBarText.substring(0, MAX_WORD_LENGTH)));
    }


    private void meaningsTextCaretUpdate(CaretEvent e) {
        String searchBarText = searchBar.getText();
        String meaningsAreaText = meaningsText.getText();
        addButton.setEnabled(!searchBarText.isEmpty() && !meaningsAreaText.isEmpty());
        updateButton.setEnabled(!searchBarText.isEmpty() && !meaningsAreaText.isEmpty());
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    private void searchButtonActionPerformed(ActionEvent e) {
        if (searchBarFormatCheck()) {
            String[] reply = client.sendRequest(
                    "search//" + searchBar.getText()).split("//");
            if (parseResponse(reply))
                meaningsText.setText(reply[1]);
            else
                meaningsText.setText("");
        }
    }


    private void deleteButtonActionPerformed(ActionEvent e) {
        if (searchBarFormatCheck()) {
            String[] reply = client.sendRequest(
                    "delete//" + searchBar.getText()).split("//");
            parseResponse(reply);
            meaningsText.setText("");
        }
    }


    private void addButtonActionPerformed(ActionEvent e) {
        if (searchBarFormatCheck() && meaningsFormatCheck()) {
            String[] reply = client.sendRequest(
                    "add//" + searchBar.getText() + "//" + meaningsText.getText()
            ).split("//");
            parseResponse(reply);
        }
    }

    private void updateButtonActionPerformed(ActionEvent e) {
        if (searchBarFormatCheck() && meaningsFormatCheck()) {
            String[] reply = client.sendRequest(
                    "update//" + searchBar.getText() + "//" + meaningsText.getText()
            ).split("//");
            parseResponse(reply);
        }
    }

    private boolean parseResponse(String reply) {
        switch (reply) {
            case "Error":
                client.connectionError(reply[1]);
                break;
            case "Fail":
                client.contentWarning( reply[1]);
                break;
            case "Success":
                client.successMessage(this);
                return true;
            default:
                client.contentWarning(this,
                        "Cannot decode reply message");
        }
        return  false;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        searchTitle = new JTextField();
        searchBar = new JTextField();
        searchButton = new JButton();
        meaningsSpace = new JScrollPane();
        meaningsText = new JTextArea();
        addButton = new JButton();
        updateButton = new JButton();
        deleteButton = new JButton();

        //======== this ========
        setName("this");
        setLayout(new GridBagLayout());
        ((GridBagLayout)getLayout()).columnWidths = new int[] {505, 200, 0};
        ((GridBagLayout)getLayout()).rowHeights = new int[] {65, 65, 65, 65, 60, 0};
        ((GridBagLayout)getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0E-4};
        ((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

        //---- searchTitle ----
        searchTitle.setEditable(false);
        searchTitle.setFont(new Font("Mistral", Font.BOLD | Font.ITALIC, 48));
        searchTitle.setText("Yongchun Li");
        searchTitle.setHorizontalAlignment(SwingConstants.CENTER);
        searchTitle.setName("searchTitle");
        add(searchTitle, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- searchBar ----
        searchBar.setToolTipText("Type your word here (Maximum 25 chars)");
        searchBar.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 36));
        searchBar.setName("searchBar");
        searchBar.addCaretListener(e -> searchBarCaretUpdate(e));
        add(searchBar, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- searchButton ----
        searchButton.setText("Search");
        searchButton.setMnemonic('S');
        searchButton.setToolTipText("Alt+s to search");
        searchButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 36));
        searchButton.setEnabled(false);
        searchButton.setName("searchButton");
        searchButton.addActionListener(e -> searchButtonActionPerformed(e));
        add(searchButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

        //======== meaningsSpace ========
        {
            meaningsSpace.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            meaningsSpace.setViewportBorder(new BevelBorder(BevelBorder.LOWERED));
            meaningsSpace.setName("meaningsSpace");

            //---- meaningsText ----
            meaningsText.setFont(new Font("Roboto Light", Font.PLAIN, 30));
            meaningsText.setLineWrap(true);
            meaningsText.setName("meaningsText");
            meaningsText.addCaretListener(e -> meaningsTextCaretUpdate(e));
            meaningsSpace.setViewportView(meaningsText);
        }
        add(meaningsSpace, new GridBagConstraints(0, 2, 1, 3, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

        //---- addButton ----
        addButton.setText("Add");
        addButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 36));
        addButton.setToolTipText("Add a new word with meanings");
        addButton.setMnemonic('A');
        addButton.setEnabled(false);
        addButton.setName("addButton");
        addButton.addActionListener(e -> addButtonActionPerformed(e));
        add(addButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

        //---- updateButton ----
        updateButton.setText("Update");
        updateButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 36));
        updateButton.setMnemonic('U');
        updateButton.setToolTipText("Update an existing word");
        updateButton.setEnabled(false);
        updateButton.setName("updateButton");
        updateButton.addActionListener(e -> updateButtonActionPerformed(e));
        add(updateButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

        //---- deleteButton ----
        deleteButton.setText("Delete");
        deleteButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 36));
        deleteButton.setToolTipText("Delete an existing word");
        deleteButton.setMnemonic('D');
        deleteButton.setEnabled(false);
        deleteButton.setName("deleteButton");
        deleteButton.addActionListener(e -> deleteButtonActionPerformed(e));
        add(deleteButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTextField searchTitle;
    private JTextField searchBar;
    private JButton searchButton;
    private JScrollPane meaningsSpace;
    private JTextArea meaningsText;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
