import java.io.*;
import java.net.Socket;
import java.security.KeyPair;

public class ClientHandler extends Thread {
    private Socket socket;
    private KeyPair keyPair;
    private int numeroSegreto;

    public ClientHandler(Socket socket, KeyPair keyPair, int numeroSegreto) {
        this.socket = socket;
        this.keyPair = keyPair;
        this.numeroSegreto = numeroSegreto;
    }

    public void run() {
        try (
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            // Invio della chiave pubblica al client
            out.writeUTF(RSAUtils.publicKeyToBase64(keyPair.getPublic()));

            boolean indovinato = false;
            while (!indovinato) {
                String encrypted = in.readUTF();
                String guessStr = RSAUtils.decrypt(encrypted, keyPair.getPrivate());
                int guess = Integer.parseInt(guessStr);
                System.out.println("Tentativo ricevuto: " + guess);

                if (guess > numeroSegreto) {
                    out.writeUTF("Troppo alto");
                } else if (guess < numeroSegreto) {
                    out.writeUTF("Troppo basso");
                } else {
                    out.writeUTF("Corretto!");
                    indovinato = true;
                }
            }

        } catch (Exception e) {
            System.err.println("Errore comunicazione: " + e.getMessage());
        }
    }
}
