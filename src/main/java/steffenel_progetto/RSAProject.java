package steffenel_progetto;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RSAProject {
    private static final Logger logger = LogManager.getLogger(RSAProject.class);
    private static final String TOKEN = ";";

    public static void main(String[] args) {
        logger.info("Avvio del programma RSA");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Inserisci il testo da criptare: ");
        String plaintext = scanner.nextLine();

        // Generazione chiavi
        logger.info("Generazione delle chiavi RSA");
        Random rng = new Random();
        BigInteger p = BigInteger.probablePrime(512, rng);
        BigInteger q = BigInteger.probablePrime(512, rng);
        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = generateCoprime(phi);
        BigInteger d = e.modInverse(phi);

        logger.info("Chiave pubblica: (e={}, n={})", e, n);
        logger.info("Chiave privata: (d={}, n={})", d, n);

        // Criptazione
        logger.info("Crittografia del testo: {}", plaintext);
        String ciphertext = encrypt(plaintext, e, n);
        logger.info("Testo criptato: {}", ciphertext);

        // Decriptazione
        logger.info("Decriptazione del testo");
        String decryptedText = decrypt(ciphertext, d, n);
        logger.info("Testo decriptato: {}", decryptedText);

        scanner.close();
    }

    private static BigInteger generateCoprime(BigInteger phi) {
        Random rng = new Random();
        BigInteger e;
        do {
            e = BigInteger.probablePrime(256, rng);
        } while (!phi.gcd(e).equals(BigInteger.ONE));
        return e;
    }

    private static String encrypt(String plaintext, BigInteger e, BigInteger n) {
        StringBuilder ciphertext = new StringBuilder();
        for (char c : plaintext.toCharArray()) {
            BigInteger encryptedChar = BigInteger.valueOf((int) c).modPow(e, n);
            ciphertext.append(encryptedChar).append(TOKEN);
        }
        return ciphertext.toString();
    }

    private static String decrypt(String ciphertext, BigInteger d, BigInteger n) {
        String[] encryptedChars = ciphertext.split(TOKEN);
        StringBuilder plaintext = new StringBuilder();
        for (String encryptedChar : encryptedChars) {
            if (!encryptedChar.isEmpty()) {
                BigInteger decryptedChar = new BigInteger(encryptedChar).modPow(d, n);
                plaintext.append((char) decryptedChar.intValue());
            }
        }
        return plaintext.toString();
    }
}
