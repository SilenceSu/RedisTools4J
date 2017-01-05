package tools;

import common.RedisToolsConfig;
import entity.JsonEntity;
import redis.RedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanResult;
import service.keysToEntity;
import util.JsonWriteThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Silence on 2016/12/24.
 */
public class HostDump implements Dump {


    private Pipeline pipeline;


    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public HostDump() {
    }

    public void toJsonFile(String file) throws IOException {
        Jedis jedis = RedisPool.getJedis();

        /*
          scan keys
         */
        List<String> keys = selectKeys(jedis);


        /*
         * process
         */
        System.out.println("process  data ...");


        //开启管道
        pipeline = jedis.pipelined();
        long db = jedis.getDB();


        //开启写入线程队列
        JsonWriteThread jsonWrite = new JsonWriteThread(file, countDownLatch);
        jsonWrite.start();


        //数据分批次写入
        List<String> tmpKeysList = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            tmpKeysList.add(keys.get(i));
            if (i % RedisToolsConfig.batchNum == 0) {
                initDataAndWrite(jsonWrite, tmpKeysList, db);
                tmpKeysList.clear();
            }

        }
        //剩下数据全部写入
        initDataAndWrite(jsonWrite, tmpKeysList, db);


        //数据全部处理完、等待写入完毕
        jsonWrite.finish();
        jedis.close();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * select redis keys
     *
     * @param jedis
     * @return List<Key>
     */
    private List<String> selectKeys(Jedis jedis) {
        System.out.println("scan keys ...");
        List<String> keys = new ArrayList<>();
        String cursor = "0";
        do {
            ScanResult<String> scanResult = jedis.scan(cursor);
            for (String s : scanResult.getResult()) {
                keys.add(s);
            }
            cursor = scanResult.getStringCursor();
        } while (!cursor.equals("0"));
        return keys;
    }

    /**
     * 初始化数据、并且写入
     *
     * @param jsonWrite
     * @param keys
     * @param db
     */
    private void initDataAndWrite(JsonWriteThread jsonWrite, List<String> keys, long db) {
        List<JsonEntity> entities = keysToEntity.findJsonEntitys(db, pipeline, keys);
        jsonWrite.add(entities);
    }

}
