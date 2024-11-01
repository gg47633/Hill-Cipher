import java.util.Scanner;

public class HillCipher {

  // Character set and mapping
  private static final char[] alphabet = {
          ' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
          'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
          'v', 'w', 'x', 'y', 'z'
  };

  private static int charToInt(char c) {
    for (int i = 0; i < alphabet.length; i++) {
      if (alphabet[i] == c) {
        return i;
      }
    }
    throw new IllegalArgumentException("Invalid character: " + c);
  }

  private static char intToChar(int num) {
    if (num >= 0 && num < alphabet.length) {
      return alphabet[num];
    }
    throw new IllegalArgumentException("Invalid number: " + num);
  }

  // Function to calculate modular inverse of a number modulo m
  private static int modInverse(int a, int m) {
    a = a % m;
    for (int x = 1; x < m; x++) {
      if ((a * x) % m == 1)
        return x;
    }
    throw new ArithmeticException("Modular inverse does not exist for " + a + " modulo " + m);
  }

  // Function to compute the determinant of a 2x2 matrix
  private static int determinant(int[][] matrix) {
    return (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]);
  }

  // Function to compute the inverse of a 2x2 matrix modulo m
  private static int[][] invertKeyMatrix(int[][] keyMatrix, int modulo) {
    int det = determinant(keyMatrix);
    det = ((det % modulo) + modulo) % modulo; // Ensure positive determinant modulo

    int detInv = modInverse(det, modulo);

    int[][] inverse = new int[2][2];
    inverse[0][0] = keyMatrix[1][1];
    inverse[1][1] = keyMatrix[0][0];
    inverse[0][1] = -keyMatrix[0][1];
    inverse[1][0] = -keyMatrix[1][0];

    // Apply determinant inverse and modulo
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        inverse[i][j] = ((inverse[i][j] * detInv) % modulo + modulo) % modulo;
      }
    }

    return inverse;
  }

  // Function to encrypt plaintext
  public static String encrypt(String plaintext, int[][] keyMatrix) {
    plaintext = plaintext.toLowerCase();
    StringBuilder ciphertext = new StringBuilder();

    // Ensure plaintext length is even by adding space if necessary
    if (plaintext.length() % 2 != 0) {
      plaintext += ' ';
    }

    for (int i = 0; i < plaintext.length(); i += 2) {
      int[] vector = {
              charToInt(plaintext.charAt(i)),
              charToInt(plaintext.charAt(i + 1))
      };

      int[] encryptedVector = new int[2];
      encryptedVector[0] = (keyMatrix[0][0] * vector[0] + keyMatrix[0][1] * vector[1]) % 27;
      encryptedVector[1] = (keyMatrix[1][0] * vector[0] + keyMatrix[1][1] * vector[1]) % 27;

      ciphertext.append(intToChar(encryptedVector[0]));
      ciphertext.append(intToChar(encryptedVector[1]));
    }

    return ciphertext.toString();
  }

  // Function to decrypt ciphertext
  public static String decrypt(String ciphertext, int[][] keyMatrix) {
    int[][] inverseKeyMatrix = invertKeyMatrix(keyMatrix, 27);
    StringBuilder plaintext = new StringBuilder();

    for (int i = 0; i < ciphertext.length(); i += 2) {
      int[] vector = {
              charToInt(ciphertext.charAt(i)),
              charToInt(ciphertext.charAt(i + 1))
      };

      int[] decryptedVector = new int[2];
      decryptedVector[0] = (inverseKeyMatrix[0][0] * vector[0] + inverseKeyMatrix[0][1] * vector[1]) % 27;
      decryptedVector[1] = (inverseKeyMatrix[1][0] * vector[0] + inverseKeyMatrix[1][1] * vector[1]) % 27;

      // Ensure positive values
      decryptedVector[0] = (decryptedVector[0] + 27) % 27;
      decryptedVector[1] = (decryptedVector[1] + 27) % 27;

      plaintext.append(intToChar(decryptedVector[0]));
      plaintext.append(intToChar(decryptedVector[1]));
    }

    return plaintext.toString();
  }


  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    // Sample key matrix (ensure determinant is relatively prime to 27)
    int[][] keyMatrix = new int[2][2];

    System.out.println("Enter 4 numbers for the key matrix (0-26) [00,10,01,11]: ");
    for (int i = 0; i < 4; i++) {
      keyMatrix[i / 2][i % 2] = scanner.nextInt();
      if (keyMatrix[i / 2][i % 2] < 0 || keyMatrix[i / 2][i % 2] > 26) {
        System.out.println("Invalid key matrix element. Must be between 0 and 26.");
        return;
      }
    }

    // Check if determinant is relatively prime to 27
    int det = determinant(keyMatrix);
    det = ((det % 27) + 27) % 27; // Ensure positive determinant modulo 27

    if (gcd(det, 27) != 1) {
      System.out.println("Invalid key matrix. Determinant is not relatively prime to 27.");
      return;
    }

    scanner.nextLine(); // Consume newline

    System.out.println("Enter plaintext message (letters and spaces only): ");
    String plaintext = scanner.nextLine();

    // Remove invalid characters
    plaintext = plaintext.toLowerCase().replaceAll("[^a-z ]", "");

    String ciphertext = encrypt(plaintext, keyMatrix);
    System.out.println("Encrypted message: " + ciphertext);

    String decryptedText = decrypt(ciphertext, keyMatrix);
    System.out.println("Decrypted message: " + decryptedText);

    scanner.close();
  }

  // Function to compute GCD using Euclidean algorithm
  private static int gcd(int a, int b) {
    if (b == 0)
      return a;
    else
      return gcd(b, a % b);
  }
}
