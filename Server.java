import java.io.*;
import java.net.*;
import java.security.KeyPair;
import java.util.Random;

public class Server {
    private static final int PORT = 12345;
    private static KeyPair keyPair;
    private static int numeroSegreto;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            keyPair = RSAUtils.generateKeyPair();
            numeroSegreto = new Random().nextInt(100) + 1;
            System.out.println("Server in ascolto sulla porta " + PORT);
            System.out.println("Numero segreto generato: " + numeroSegreto);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket, keyPair, numeroSegreto).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
