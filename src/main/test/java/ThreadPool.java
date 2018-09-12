import org.junit.Test;

public class ThreadPool {




    @Test
    public void thread1(){
       int i= Runtime.getRuntime().availableProcessors();// 获取处理器数量
        System.out.println(i);
    }


}
