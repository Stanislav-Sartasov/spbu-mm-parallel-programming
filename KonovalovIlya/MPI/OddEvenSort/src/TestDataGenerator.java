import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public class TestDataGenerator {
    private static int numberCount = 40;
    private static String outputFilename = "resources/input.txt";
    
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length > 0) {
            numberCount = Integer.parseInt(args[0]);
        }
        try (PrintWriter out = new PrintWriter(outputFilename)) {
            Random random = new Random();
            out.println(numberCount);
            for (int i = 0; i < numberCount; i++) {
                out.print(random.nextInt(numberCount) + " ");
            }
        }
    }
}
