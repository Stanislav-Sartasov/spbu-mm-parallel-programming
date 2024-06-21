import mpi.MPI;
import java.io.IOException;
import java.util.Arrays;

public class ParallelSort {
    static int processRank;
    static int processCount;

    public static void main(String[] args) throws IOException {
        MPI.Init(args);

        processRank = MPI.COMM_WORLD.Rank();
        processCount = MPI.COMM_WORLD.Size();

        Result result = ArrayUtils.initializeArray("input_array.txt", processRank, processCount);

        int[] localArray = result.getArray();
        performParallelSort(localArray);

        int[] sortedArray = new int[result.getTotalNumbers()];

        MPI.COMM_WORLD.Gather(localArray, 0, localArray.length, MPI.INT, sortedArray, 0, localArray.length, MPI.INT, 0);

        if (processRank == 0) {
            ArrayUtils.writeArrayToFile("sorted_output.txt", sortedArray);
        }

        MPI.Finalize();
    }

    private static void performParallelSort(int[] array) {
        for (int phase = 0; phase < processCount; phase++) {
            int partnerRank = determinePartnerRank(phase);

            if (partnerRank < 0 || partnerRank >= processCount) {
                continue;
            }

            int[] receivedArray = new int[array.length];
            if (processRank % 2 == 0) {
                MPI.COMM_WORLD.Send(array, 0, array.length, MPI.INT, partnerRank, 0);
                MPI.COMM_WORLD.Recv(receivedArray, 0, array.length, MPI.INT, partnerRank, 0);
            } else {
                MPI.COMM_WORLD.Recv(receivedArray, 0, array.length, MPI.INT, partnerRank, 0);
                MPI.COMM_WORLD.Send(array, 0, array.length, MPI.INT, partnerRank, 0);
            }

            int[] mergedArray = ArrayUtils.mergeAndSort(array, receivedArray);

            if (processRank < partnerRank) {
                System.arraycopy(mergedArray, 0, array, 0, array.length);
            } else {
                System.arraycopy(mergedArray, array.length, array, 0, array.length);
            }
        }
    }

    private static int determinePartnerRank(int phase) {
        int partner;
        if (phase % 2 == 0) {
            partner = (processRank % 2 == 0) ? processRank + 1 : processRank - 1;
        } else {
            partner = (processRank % 2 == 0) ? processRank - 1 : processRank + 1;
        }
        return partner;
    }
}
