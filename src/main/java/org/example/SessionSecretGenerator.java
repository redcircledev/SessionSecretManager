package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;

public class SessionSecretGenerator extends JFrame {
    private JTextField lengthField;
    private JTextField countField;
    private JCheckBox upperCaseCheckBox;
    private JCheckBox lowerCaseCheckBox;
    private JCheckBox numbersCheckBox;
    private JCheckBox specialCharsCheckBox;
    private JCheckBox extendedSpecialCharsCheckBox;
    private JCheckBox base64CheckBox;
    private JTextArea resultArea;
    private JButton generateButton;
    private JButton copyButton;
    private JButton trustButton;

    public SessionSecretGenerator() {
        setTitle("Session Secret Generator");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set the application icon
        try {
            URL iconURL = getClass().getResource("/icon.png"); // Ensure the icon is in the resources folder
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(ImageIO.read(iconURL));
                setIconImage(icon.getImage());
            } else {
                System.out.println("Icon image not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(9, 2));

        inputPanel.add(new JLabel("Secret Length:"));
        lengthField = new JTextField();
        inputPanel.add(lengthField);

        inputPanel.add(new JLabel("Number of Secrets:"));
        countField = new JTextField();
        inputPanel.add(countField);

        upperCaseCheckBox = new JCheckBox("Include Upper Case");
        inputPanel.add(upperCaseCheckBox);

        lowerCaseCheckBox = new JCheckBox("Include Lower Case");
        inputPanel.add(lowerCaseCheckBox);

        numbersCheckBox = new JCheckBox("Include Numbers");
        inputPanel.add(numbersCheckBox);

        specialCharsCheckBox = new JCheckBox("Include Special Characters");
        inputPanel.add(specialCharsCheckBox);

        extendedSpecialCharsCheckBox = new JCheckBox("Include Extended Special Characters");
        inputPanel.add(extendedSpecialCharsCheckBox);

        base64CheckBox = new JCheckBox("Base64 Encode");
        base64CheckBox.setSelected(true); // Enable by default
        inputPanel.add(base64CheckBox);

        add(inputPanel, BorderLayout.NORTH);

        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        generateButton = new JButton("Generate");
        copyButton = new JButton("Copy to Clipboard");
        trustButton = new JButton("I Trust You");

        buttonPanel.add(generateButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(trustButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateSecrets();
            }
        });

        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard();
            }
        });

        trustButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trustMeGenerate();
            }
        });
    }

    private void generateSecrets() {
        int length;
        int count;

        try {
            length = Integer.parseInt(lengthField.getText());
            count = Integer.parseInt(countField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for length and count.");
            return;
        }

        if (length <= 0 || count <= 0) {
            JOptionPane.showMessageDialog(this, "Length and count must be positive numbers.");
            return;
        }

        List<String> characterSets = new ArrayList<>();
        if (upperCaseCheckBox.isSelected()) {
            characterSets.add("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        }
        if (lowerCaseCheckBox.isSelected()) {
            characterSets.add("abcdefghijklmnopqrstuvwxyz");
        }
        if (numbersCheckBox.isSelected()) {
            characterSets.add("0123456789");
        }
        if (specialCharsCheckBox.isSelected()) {
            characterSets.add("!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~");
        }
        if (extendedSpecialCharsCheckBox.isSelected()) {
            characterSets.add("¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿×÷");
        }

        if (characterSets.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one character set.");
            return;
        }

        SecureRandom random = new SecureRandom();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < count; i++) {
            StringBuilder secret = new StringBuilder();

            // Ensure at least one character from each selected set is included
            for (String set : characterSets) {
                secret.append(set.charAt(random.nextInt(set.length())));
            }

            // Fill the rest of the secret length with random characters from all selected sets
            String allChars = String.join("", characterSets);
            for (int j = characterSets.size(); j < length; j++) {
                secret.append(allChars.charAt(random.nextInt(allChars.length())));
            }

            // Shuffle the characters to ensure randomness
            String shuffledSecret = shuffleString(secret.toString(), random);
            if (base64CheckBox.isSelected()) {
                shuffledSecret = Base64.getEncoder().encodeToString(shuffledSecret.getBytes());
            }
            result.append(shuffledSecret).append("\n");
        }

        resultArea.setText(result.toString());
    }

    private String shuffleString(String input, Random random) {
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        while (!characters.isEmpty()) {
            output.append(characters.remove(random.nextInt(characters.size())));
        }
        return output.toString();
    }

    private void copyToClipboard() {
        String text = resultArea.getText();
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        JOptionPane.showMessageDialog(this, "Copied to clipboard!");
    }

    private void trustMeGenerate() {
        Random random = new Random();
        int length = random.nextInt(11) + 10; // Random length between 10 and 20
        lengthField.setText(String.valueOf(length));

        // Ensure at least one character set is selected
        boolean atLeastOneSelected = false;
        while (!atLeastOneSelected) {
            upperCaseCheckBox.setSelected(random.nextBoolean());
            lowerCaseCheckBox.setSelected(random.nextBoolean());
            numbersCheckBox.setSelected(random.nextBoolean());
            specialCharsCheckBox.setSelected(random.nextBoolean());
            extendedSpecialCharsCheckBox.setSelected(random.nextBoolean());

            atLeastOneSelected = upperCaseCheckBox.isSelected() || lowerCaseCheckBox.isSelected() ||
                    numbersCheckBox.isSelected() || specialCharsCheckBox.isSelected() || extendedSpecialCharsCheckBox.isSelected();
        }

        generateSecrets();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SessionSecretGenerator().setVisible(true);
            }
        });
    }
}
