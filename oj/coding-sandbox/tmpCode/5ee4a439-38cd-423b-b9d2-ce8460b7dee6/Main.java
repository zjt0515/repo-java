import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int c = scanner.nextInt();
        int []arr = new int[n];
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < n; ++i){
            arr[i] = scanner.nextInt();
            // 记录值出现的次数
            // 如果原本没有出现，就写入1次，原本出现了，再原本的基础上+1次，总之就是+1
            map.put(arr[i], map.getOrDefault(arr[i],0)+1);
        }
        long res = 0;//存储结果
        // 遍历数组得到所有b的值(b=a-c
        for (int i = 0; i < n; i++){
            int b = arr[i] - c;
            res+=map.getOrDefault(b, 0);
        }
        System.out.print(res);
    }
}
