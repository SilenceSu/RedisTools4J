package util;

import com.alibaba.fastjson.JSONWriter;
import entity.JsonEntity;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * json entitiy 写入线程
 * Created by Silence on 2016/12/29.
 */
public class JsonWriteThread extends Thread {


    private String fileName;


    private CountDownLatch countDownLatch;

    private boolean finish = false;

    private Queue<List<JsonEntity>> queue = new ConcurrentLinkedQueue();


    public JsonWriteThread(String fileName, CountDownLatch countDownLatch) {
        this.fileName = fileName;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        System.out.println("write db ...");
        JSONWriter writer = null;
        try {
            writer = new JSONWriter(new FileWriter(fileName));


            writer.startArray();


            while (true) {

                if (finish && queue.size() <= 0) {
                    break;

                }

                List<JsonEntity> aa = queue.poll();
                if (aa != null) {
                    for (JsonEntity entity : aa) {
                        writer.writeValue(entity);
                    }


                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.endArray();

            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
        }


    }


    public void add(List<JsonEntity> list) {
        queue.add(list);
    }

    public void finish() {
        this.finish = true;
    }
}
