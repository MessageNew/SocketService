import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mysteriouseyes on 2018/9/20.
 */
public class Test {
    public static void main(String[] args){
        List list = new ArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        String lists = list.toString();
        System.out.printf(lists.substring(1, lists.length()-1));
    }
}
