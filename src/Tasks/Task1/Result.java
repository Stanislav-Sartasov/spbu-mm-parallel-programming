public class Result {
    private int totalNumbers;
    private int[] array;

    public Result(int totalNumbers, int[] array) {
        this.totalNumbers = totalNumbers;
        this.array = array;
    }

    public int getTotalNumbers() {
        return totalNumbers;
    }

    public int[] getArray() {
        return array;
    }
}