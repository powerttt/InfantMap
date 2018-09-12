import org.junit.Test;

public class CoverChar {



    @Test
    public void cover(){

        System.out.println("b");
        System.out.println("支");
        System.out.println("高");
        String name="b支高峰线";


        char[] c=name.toCharArray();
        for (int i:c){
            System.out.print(i);
        }



    }


}
