import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class CollectionTest {


    @Test
    public void IntersectionTest(){
        Set<Integer> originSet=new HashSet<>();
        Set<Integer> endSet=new HashSet<>();

        originSet.add(48);
        originSet.add(432);
        originSet.add(56);

        endSet.add(320);
        endSet.add(348);
        endSet.add(48);
        endSet.add(45);

        Set<Integer> nonstopSet =new HashSet<>();
        nonstopSet.addAll(CollectionUtils.intersection(originSet, endSet));
        nonstopSet.forEach(x-> System.out.println(x));



    }

}
