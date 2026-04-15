import java.util.Scanner;

public class Test5 {
    public static void main(String[] args) {
        String str="3月详单：话费35元，流量费60元，短信费10元，代付费20元。";
        //按题目要求解析str字符串
        Scanner scanner = new Scanner(str).useDelimiter("\\D+");
        int mounth = scanner.nextInt();
        int sum = 0;
        while(scanner.hasNext()){
            sum += scanner.nextInt();
        }
        System.out.print(mounth +"月总通信费用：" + sum + "元");


    }
}
