import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class ArrayUtils {
    static int alignment = 0;

    public static Result initializeArray(String filePath, int processRank, int processCount) {
        int totalNumbers = 0;
        int[] resultArray;
        String filename = filePath;
        try (Scanner scanner = new Scanner(new File(filename))) {
            totalNumbers = scanner.nextInt();
            int blockCount = processCount;
            int blockSize = (totalNumbers + blockCount - 1) / blockCount;
            resultArray = new int[blockSize];

            // Перемещение курсора в файле на начало блока данных для текущего процесса
            int offset = processRank * blockSize;
            for (int i = 0; i < offset; i++) {
                if (scanner.hasNextInt()) {
                    scanner.nextInt();
                } else {
                    throw new RuntimeException("Недостаточно данных в файле для процесса " + processRank);
                }
            }

            // Чтение данных для текущего процесса
            int length = Math.min(totalNumbers - offset, blockSize);
            for (int i = 0; i < length; i++) {
                if (scanner.hasNextInt()) {
                    resultArray[i] = scanner.nextInt();
                } else {
                    throw new RuntimeException("Недостаточно данных в файле для процесса " + processRank);
                }
            }

            // Если данные для процесса короче blockSize, заполняем оставшиеся места максимальным значением
            if (length < blockSize) {
                for (int i = length; i < blockSize; i++) {
                    resultArray[i] = Integer.MAX_VALUE;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new Result(totalNumbers, resultArray);
    }


    public static int[] mergeAndSort(int[] array1, int[] array2) {
        int[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        Arrays.sort(result);
        return result;
    }

    public static void writeArrayToFile(String filePath, int[] array) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        for (int i = 0; i < array.length; i++) {
            writer.write(array[i] + (i < array.length - 1 ? " " : ""));
        }
        writer.newLine();
        writer.close();
    }
}
