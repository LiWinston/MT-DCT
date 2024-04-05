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
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static client.SymbolMatch.checkParenthesesMatchandDeleteIllegal;

public class OperateUI extends JPanel {
    private static final int MAX_WORD_LENGTH = 30;

    boolean enalbeParenthesesMatchCheck = true;
    protected Client client;
    public OperateUI(Client client) {
        this.client = client;
        initComponents();
    }

    private boolean wordFormatter() {
        String str = searchBar.getText().trim().toLowerCase();
//        StringBuilder sb = new StringBuilder();

        boolean formatted = str.chars().allMatch(Character::isLetter);

        if (!formatted){
            str = str.replaceAll("[^A-Za-z]", "");
            client.formatWarning(
                    "Only alphabet allowed.\n" +
                            "Non-alphabet chars eliminated.");
        }
        searchBar.setText(str);
        return formatted;
    }

    private boolean meaningsFormatter() {
        String originalText = meaningsText.getText().trim(); // Get the meaning text input by the user and remove the leading and trailing spaces
        StringBuilder sb = new StringBuilder();
        boolean formatted = true;

        // Remove extra semicolons and make sure there is only one semicolon between each meaning
        String[] meanings = originalText.split(";");
        ArrayList<String> trimmedMeanings = new ArrayList<>();
        for (String meaning : meanings) {
            String trimmedMeaning = meaning.trim(); // Remove leading and trailing spaces of meaning
            if (!trimmedMeaning.isEmpty()) {
//                sb.append(trimmedMeaning).append(";"); // Add meaning and semicolon
                trimmedMeanings.add(trimmedMeaning);
            }
        }
        //illegal usage of character elimination
        for(String meaning : trimmedMeanings) {
            if(enalbeParenthesesMatchCheck){
                sb.append(checkParenthesesMatchandDeleteIllegal(meaning)).append(";");
            }else {
                sb.append(meaning).append(";");
            }
        }

        // Remove the last semicolon if it exists
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ';') {
            sb.deleteCharAt(sb.length() - 1);
        }



        // Check if the meaning text has been modified
        formatted = sb.toString().equals(originalText);

        // Update the contents of the meaning text box
        meaningsText.setText(sb.toString());

        // If the format is incorrect, display a warning message
        if (!formatted) {
            client.formatWarning("Reformatting complete, please resubmit.");
        }
        return formatted;
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

///////////////////////////////////////////////////////////////////////////////////////////////////
    private void searchButtonActionPerformed(ActionEvent e) {
        if (wordFormatter()) {
            String req = client.localReqHdl.createSearchRequest(searchBar.getText());
            CompletableFuture<String> res = null;
            try {
                res = client.sendRequest(req);
            } catch (ExecutionException ex) {
                throw new RuntimeException(ex);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            parseResponse(Objects.requireNonNull(res.join()), req);
            meaningsText.setText(Response.getMeaningsString(res.join()));
        }
    }


    private void deleteButtonActionPerformed(ActionEvent e) {
        if (wordFormatter()) {
            String req = client.localReqHdl.createDeleteRequest(searchBar.getText());
            CompletableFuture<String> res = null;
            try {
                res = client.sendRequest(req);
            } catch (ExecutionException ex) {
                throw new RuntimeException(ex);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            parseResponse(Objects.requireNonNull(res.join()), req);
            meaningsText.setText("");
        }
    }


    private void addButtonActionPerformed(ActionEvent e) {
        if (wordFormatter() && meaningsFormatter()) {
            String req = client.localReqHdl.createAddRequest(
                    searchBar.getText(), meaningsText.getText().split("\n")
            );
            CompletableFuture<String> res = null;
            try {
                res = client.sendRequest(req);
            } catch (ExecutionException ex) {
                throw new RuntimeException(ex);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            parseResponse(Objects.requireNonNull(res.join()), req);
            // seems not necessary after adding since user may want to see the present meanings and conduct update then
//            meaningsText.setText("");
            meaningsText.setText(Response.getMeaningsString(res.join()));
        }
    }

    private void updateButtonActionPerformed(ActionEvent e) {
        if (wordFormatter() && meaningsFormatter()) {
            String req = client.localReqHdl.createUpdateRequest(
                    searchBar.getText(), meaningsText.getText().split("\n")
            );
            CompletableFuture<String> res = null;
            try {
                res = client.sendRequest(req);
            } catch (ExecutionException ex) {
                throw new RuntimeException(ex);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            parseResponse(Objects.requireNonNull(res.join()), req);
            if (Objects.equals(Response.getStatusString(res.join()), "SUCCESS")) {
                meaningsText.setText(Response.getMeaningsString(res.join()));
            }
        }
    }

    //Invoke with response and request, where request is only used for indicate the action within Failure Dialogs.
    private void parseResponse(String response,String request) {
        System.out.println(STR."received response: \{response}");
        switch (Objects.requireNonNull(Response.getStatusString(response))) {
//            case "Error":
//                client.connectionError(reply[1]);
//                break;
            case "FAIL":
                client.FailDialog(Response.getMessageString(response) + Response.getMeaningsString(response), STR."\{String.valueOf(client.localReqHdl.getAction(request))} Failure");
                break;
            case "SUCCESS":
                client.SuccessDialog(Response.getMessageString(response) + Response.getMeaningsString(response), STR."\{String.valueOf(client.localReqHdl.getAction(request))} Success");
                break;
            default:
                client.FailDialog("Broken response");
                break;
        }
    }

    private void EnableParenthesesActionPerformed(ActionEvent e) {
        enalbeParenthesesMatchCheck = EnableParenthesesCheckBox.isSelected();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        searchTitle = new JTextField();
        EnableParenthesesCheckBox = new JCheckBox();
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
        searchTitle.setFont(new Font("High Tower Text", Font.BOLD, 35));
        searchTitle.setText("Segment meanings by \";\"");
        searchTitle.setHorizontalAlignment(SwingConstants.CENTER);
        searchTitle.setEditable(false);
        searchTitle.setFocusable(false);
        searchTitle.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        searchTitle.setBackground(new Color(0x30cfcce5, true));
        searchTitle.setName("searchTitle");
        add(searchTitle, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- EnableParenthesesCheckBox ----
        EnableParenthesesCheckBox.setText("EnableParenthesesCheck");
        EnableParenthesesCheckBox.setFont(EnableParenthesesCheckBox.getFont().deriveFont(EnableParenthesesCheckBox.getFont().getStyle() & ~Font.ITALIC, EnableParenthesesCheckBox.getFont().getSize() + 2f));
        EnableParenthesesCheckBox.setSelected(true);
        EnableParenthesesCheckBox.setName("EnableParenthesesCheckBox");
        EnableParenthesesCheckBox.addActionListener(e -> EnableParenthesesActionPerformed(e));
        add(EnableParenthesesCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

        //---- searchBar ----
        searchBar.setToolTipText("Type your word here (Maximum 25 chars)");
        searchBar.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 36));
        searchBar.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        searchBar.setName("searchBar");
        searchBar.addCaretListener(e -> searchBarCaretUpdate(e));
        add(searchBar, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 5), 0, 0));

        //---- searchButton ----
        searchButton.setText("Search");
        searchButton.setMnemonic('S');
        searchButton.setToolTipText("Alt+s to search");
        searchButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 36));
        searchButton.setEnabled(false);
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
        add(meaningsSpace, new GridBagConstraints(0, 2, 1, 3, 3.0, 3.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

        //---- addButton ----
        addButton.setText("Add");
        addButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 36));
        addButton.setToolTipText("Add a new word with meanings");
        addButton.setMnemonic('A');
        addButton.setEnabled(false);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setName("addButton");
        addButton.addActionListener(e -> addButtonActionPerformed(e));
        add(addButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 1.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

        //---- updateButton ----
        updateButton.setText("Update");
        updateButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 36));
        updateButton.setMnemonic('U');
        updateButton.setToolTipText("Update an existing word");
        updateButton.setEnabled(false);
        updateButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        updateButton.setName("updateButton");
        updateButton.addActionListener(e -> updateButtonActionPerformed(e));
        add(updateButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 1.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 5, 0), 0, 0));

        //---- deleteButton ----
        deleteButton.setText("Delete");
        deleteButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 36));
        deleteButton.setToolTipText("Delete an existing word");
        deleteButton.setMnemonic('D');
        deleteButton.setEnabled(false);
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteButton.setName("deleteButton");
        deleteButton.addActionListener(e -> deleteButtonActionPerformed(e));
        add(deleteButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 1.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTextField searchTitle;
    private JCheckBox EnableParenthesesCheckBox;
    private JTextField searchBar;
    private JButton searchButton;
    private JScrollPane meaningsSpace;
    private JTextArea meaningsText;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
