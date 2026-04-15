import java.util.Scanner;

public class Test3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        double sum = 0;
        int[] score = new int[n];
        for (int i = 0; i < n; i++){
            score[i] = scanner.nextInt();
            sum += score[i];
        }
        double avg = sum / n;
        System.out.println("数学平均成绩为：" + avg);
    }

}
