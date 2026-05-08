import java.util.Scanner;
import java.util.Arrays;
import java.math.BigDecimal;

public class Main {
    static class Student {
        int id;
        double score;
        String scoreStr;

        Student(int id, String scoreStr) {
            this.id = id;
            this.scoreStr = scoreStr;
            this.score = Double.parseDouble(scoreStr);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();
        int k = sc.nextInt();

        Student[] students = new Student[n];

        for (int i = 0; i < n; i++) {
            int id = sc.nextInt();
            String scoreStr = sc.next();
            students[i] = new Student(id, scoreStr);
        }

        Arrays.sort(students, (a, b) -> Double.compare(b.score, a.score));

        Student ans = students[k - 1];

        BigDecimal bd = new BigDecimal(ans.scoreStr);
        String scoreOutput = bd.stripTrailingZeros().toPlainString();

        System.out.println(ans.id + " " + scoreOutput);
    }
}