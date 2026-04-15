import java.util.Scanner;

public class Test2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 声明 int 类型的变量 y 用来获取控制台输入的年
        int y = scanner.nextInt();
        // 声明 int 类型的变量 m 用来获取控制台输入的月
        int m = scanner.nextInt();
        // 声明 int 类型的变量 d 用来获取控制台输入的日
        int d = scanner.nextInt();

        if (m == 1 || m == 2){
            m += 12;
            y --;
        }
        int week = (d+2*m+3*(m+1)/5+y+y/4-y/100+y/400) % 7;
        switch (week){
            case 0:
                System.out.println("星期一");
                break;
            case 1:
                System.out.println("星期二");
                break;
            case 2:
                System.out.println("星期三");
                break;
            case 3:
                System.out.println("星期四");
                break;
            case 4:
                System.out.println("星期五");
                break;
            case 5:
                System.out.println("星期六");
                break;
            case 6:
                System.out.println("星期日");
                break;
        }
    }
}
