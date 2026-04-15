import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[][] a = new int[100][100];
        for (int i = 0; i <= n; i++){
            a[i][0] = 1;
        }
        for (int i = 1; i <= n; i++){
            for (int j = 1; j <= i; j++){
                a[i][j] = a[i-1][j] + a[i-1][j-1];
            }
        }

        for (int i = 0; i < n; i++) {
            for (int k = n - i - 1; k > 0; k--){
                System.out.print("  ");
            }
            for (int j = 0; j <= i; j++){
                    System.out.printf("%4d", a[i][j]);
            }
            System.out.println();
        }
    }
}