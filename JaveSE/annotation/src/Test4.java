import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Test4 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 声明 int 类型的变量 y 用来获取控制台输入的年
        ArrayList<Integer> small = new ArrayList<Integer>(Arrays.asList(4, 6, 9, 11));
        ArrayList<Integer> large = new ArrayList<Integer>(Arrays.asList(1, 3, 5, 7, 8, 10, 12));

        int y = scanner.nextInt();
        while (y < 1900) {
            System.out.println("请输入大于或等于1900的年份");
            return;
        }

        // 声明 int 类型的变量 m 用来获取控制台输入的月
        int m = scanner.nextInt();
        if (m > 12 || m < 1){
            System.out.println("请输入正确的月份");
        }
        int d = 1;

        int days = 31;
        if (small.contains(m)) {
            days = 30;
        } else if (m == 2) {
            if (y % 4 == 0 && y % 400 != 0) {
                days = 29;
            } else {
                days = 28;
            }
        }

        if (m == 1 || m == 2) {
            m += 12;
            y--;
        }
        int week = (d + 2 * m + 3 * (m + 1) / 5 + y + y / 4 - y / 100 + y / 400) % 7;
        for (int i = 50; i > 0; i--) {
            System.out.print("=");
        }
        System.out.println();
        System.out.println("日" + "\t" + "一" + "\t"  + "二" + "\t" + "三" + "\t" + "四"+ "\t" +"五"+ "\t" +"六");

        // week+1个空格就是每月1号前面空出的
        for (int i = 0; i < (week+1) % 7 ; i ++) {
            System.out.print("\t");
        }

        for (int day = 1; day <= days; day++){
            System.out.print(day + "\t");
            if ( (day + week + 1) %  7 == 0){
                System.out.println();
            };
        }
        System.out.println();



        for (int i = 50; i > 0; i--) {
            System.out.print("=");
        }


    }
}
