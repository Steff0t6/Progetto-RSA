import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.security.PublicKey;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    private PublicKey serverPublicKey;
    private DataOutputStream out;
    private DataInputStream in;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }

    public Client() {
        JFrame frame = new JFrame("Indovina il Numero - Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        // Tema scuro
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Inserisci un numero tra 1 e 100:");
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField inputField = new JTextField();
        inputField.setMaximumSize(new Dimension(200, 30));
        inputField.setHorizontalAlignment(JTextField.CENTER);

        JButton sendButton = new JButton("Invia");
        JLabel responseLabel = new JLabel("");
        responseLabel.setForeground(Color.WHITE);
        responseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(20));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(inputField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(sendButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(responseLabel);
        frame.add(panel);

        connectToServer(responseLabel);

        sendButton.addActionListener(e -> {
            try {
                String guess = inputField.getText().trim();
                if (!guess.matches("\\d+")) {
                    responseLabel.setText("Inserisci un numero valido!");
                    return;
                }

                String encrypted = RSAUtils.encrypt(guess, serverPublicKey);
                out.writeUTF(encrypted);

                String response = in.readUTF();
                responseLabel.setText(response);

            } catch (Exception ex) {
                responseLabel.setText("Errore: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
    }

    private void connectToServer(JLabel statusLabel) {
        try {
            Socket socket = new Socket(HOST, PORT);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            String pubKeyBase64 = in.readUTF();
            serverPublicKey = RSAUtils.base64ToPublicKey(pubKeyBase64);

            statusLabel.setText("Connesso al server.");
        } catch (Exception e) {
            statusLabel.setText("Errore connessione: " + e.getMessage());
        }
    }
}
