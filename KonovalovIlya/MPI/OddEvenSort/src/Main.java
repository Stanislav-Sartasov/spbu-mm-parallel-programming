import mpi.MPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static int MY_RANK;
    static int PROC_COUNT;
    static int alignment = 0;


    public static void main(String[] args) {
        MPI.Init(args);

        MY_RANK = MPI.COMM_WORLD.Rank();
        PROC_COUNT = MPI.COMM_WORLD.Size();

        int[] arr = getArrayForProcess(args);
        print("I am started, my part: " + stringOfArrayWithAlignment(arr));

        sort(arr);

        print("I am finished, my result: " + stringOfArrayWithAlignment(arr));

        MPI.Finalize();
    }

    private static void sort(int[] arr) {
        for (int i = 0; i < PROC_COUNT; i++) {
            int partnerRank = getPartnerRank(i);

            if (partnerRank < 0 || partnerRank >= PROC_COUNT) {
                continue;
            }

            int[] received = new int[arr.length];
            if (MY_RANK % 2 == 0) {
                MPI.COMM_WORLD.Send(arr, 0, arr.length, MPI.INT, partnerRank, 0);
                MPI.COMM_WORLD.Recv(received, 0, arr.length, MPI.INT, partnerRank, 0);
            } else {
                MPI.COMM_WORLD.Recv(received, 0, arr.length, MPI.INT, partnerRank, 0);
                MPI.COMM_WORLD.Send(arr, 0, arr.length, MPI.INT, partnerRank, 0);
            }

            int[] merged = mergeAndSort(arr, received);

            if (MY_RANK < partnerRank) {
                System.arraycopy(merged, 0, arr, 0, arr.length);
            } else {
                System.arraycopy(merged, arr.length, arr, 0, arr.length);
            }
        }
    }

    private static int getPartnerRank(int phase) {
        int partner;
        if (phase % 2 == 0) {
            if (MY_RANK % 2 == 0) {
                partner = MY_RANK + 1;
            } else {
                partner = MY_RANK - 1;
            }
        } else {
            if (MY_RANK % 2 == 0) {
                partner = MY_RANK - 1;
            } else {
                partner = MY_RANK + 1;
            }
        }
        return partner;
    }

    private static int[] mergeAndSort(int[] arr1, int[] arr2) {
        int[] result = Arrays.copyOf(arr1, arr1.length + arr2.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        Arrays.sort(result);
        return result;
    }

    private static void print(String str) {
        System.out.println("Process " + MY_RANK + ": " + str);
    }

    private static String stringOfArrayWithAlignment(int[] arr) {
        return Arrays.toString(MY_RANK == PROC_COUNT - 1 ? Arrays.copyOfRange(arr, 0, alignment) : arr);
    }

    private static int[] getArrayForProcess(String[] args) {
        int[] res;
        if (args.length > 3) { // read from file
            String filename = args[3];
            try (Scanner sc = new Scanner(new File(filename))) {
                int totalNums = sc.nextInt();
                int blockCount = PROC_COUNT;
                int blockSize = (totalNums + blockCount - 1) / blockCount;
                res = new int[blockSize];
                int offset = MY_RANK * blockSize;
                for (int i = 0; i < offset; i++) {
                    sc.nextInt(); // pass first <offset> nums
                }
                int length = Math.min(totalNums - offset, blockSize);
                for (int i = 0; i < length; i++) {
                    res[i] = sc.nextInt();
                }

                if (length < blockSize) {
                    alignment = length;
                    for (int i = alignment; i < blockSize; i++) {
                        res[i] = Integer.MAX_VALUE;
                    }
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else { // generate
            Random random = new Random(MY_RANK);
            final int blockSize = 3;
            res = new int[blockSize];
            for (int i = 0; i < blockSize; i++) {
                res[i] = random.nextInt(PROC_COUNT * blockSize);
            }
        }
        return res;
    }
}