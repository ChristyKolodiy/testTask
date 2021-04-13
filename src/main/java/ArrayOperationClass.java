import java.util.*;
import java.util.stream.IntStream;
import static java.util.Arrays.stream;

public class ArrayOperationClass implements ArrayOperation {

    public boolean equals(int[] a, int[] b) {
        if (a.length == b.length) {
            return IntStream.range(0, a.length).allMatch(i -> a[i] == b[i]);
        }
        return false;
    }

    public int[] union(int[] a, int[] b) {
        int maxSize = a.length + b.length;
        int[] resultArray = new int[maxSize];
        System.arraycopy(a, 0, resultArray, 0, a.length);
        System.arraycopy(b, 0, resultArray, a.length, b.length);
        resultArray = Arrays.stream(resultArray).distinct().toArray();
        return resultArray;
    }

    public int[] subtract(int[] a, int[] b) {
        return Arrays.stream(a)
                .distinct()
                .filter(x -> Arrays.stream(b).noneMatch(y -> x == y))
                .toArray();
    }

    public int[] intersect(int[] a, int[] b) {
        return Arrays.stream(a)
                .distinct()
                .filter(x -> Arrays.stream(b).anyMatch(y -> x == y))
                .toArray();
    }

    public int[] symmetricSubtract(int[] a, int[] b) {
        int[] unionArray = union(a,b);
        int[] intersectArray = intersect(a,b);
        return Arrays.stream(unionArray)
                .distinct()
                .filter(x -> Arrays.stream(intersectArray).noneMatch(y -> x == y))
                .toArray();
    }

    public void printMe(int[] arr) {
        stream(arr).forEach(el -> System.out.println(el));
    }

    public static void main(String[] args) {
        ArrayOperation operation = new ArrayOperationClass();
        int[] a = {1,2,3,4,5,6,7};
        int[] b = {1,3,4,5,8};
        System.out.println("Array a:");
        operation.printMe(a);
        System.out.println("Array b:");
        operation.printMe(b);
        System.out.println("---------------------------------------------------");
        System.out.println("Two array equals:");
        boolean result = operation.equals(a,b);
        System.out.println(result);
        System.out.println("---------------------------------------------------");
        System.out.println("Union for two arrays:");
        int [] result1 = operation.union(a,b);
        operation.printMe(result1);
        System.out.println("---------------------------------------------------");
        System.out.println("Substraction  for two arrays:");
        int [] result2 = operation.subtract(a,b);
        operation.printMe(result2);
        System.out.println("---------------------------------------------------");
        System.out.println("Intersection for two arrays:");
        int [] result3 = operation.intersect(a,b);
        operation.printMe(result3);
        System.out.println("---------------------------------------------------");
        System.out.println("Symetric substract for two arrays:");
        int [] result4 = operation.symmetricSubtract(a,b);
        operation.printMe(result4);
        System.out.println("---------------------------------------------------");
    }
}
