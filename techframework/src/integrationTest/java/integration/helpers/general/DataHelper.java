package integration.helpers.general;

import java.util.Random;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DataHelper {

  private static final Random random;
  private static final String CHARACTERS;

  static {
    random = new Random();
    CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  }

  public static String generateRandomString(int length) {

    int characterLength = CHARACTERS.length();
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int randomIndex = random.nextInt(characterLength);
      char randomChar = CHARACTERS.charAt(randomIndex);
      sb.append(randomChar);
    }
    return sb.toString();
  }

  public static String generateRandomIPv4() {
    return random.nextInt(256)
        + "."
        + random.nextInt(256)
        + "."
        + random.nextInt(256)
        + "."
        + random.nextInt(256);
  }

  public static Integer generateRandomIntegers(Integer range) {
    return random.nextInt(range + 1);
  }
}
