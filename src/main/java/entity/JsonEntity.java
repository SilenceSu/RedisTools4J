package entity;

import com.alibaba.fastjson.JSON;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

/**
 * Created by Silence on 2016/12/24.
 */
public class JsonEntity {

    private long db;

    private String key;

    private long ttl;

    private String type;

    private String value;

    private int size;

    Response rTtl;

    Response rType;

    Response rValue;


    public JsonEntity(long db) {
        this.db = db;
    }

    public JsonEntity(long db, String key) {
        this.db = db;
        this.key = key;
    }

    public long getDb() {
        return db;
    }

    public void setDb(long db) {
        this.db = db;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.size = this.value.getBytes().length;

    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }


    public void initType(Pipeline pipeline) {

        rType = pipeline.type(this.key);

    }


    public void initResponse(Pipeline pipeline) {

        this.rTtl = pipeline.ttl(key);

        this.type = (String) this.rType.get();

        switch (type) {
            case "hash":
                this.rValue = (pipeline.hgetAll(key));
                break;
            case "list":
                this.rValue = (pipeline.lrange(key, 0, -1));
                break;
            case "set":
                this.rValue = (pipeline.smembers(key));
                break;
            case "zset":
                this.rValue = pipeline.zrange(key, 0, -1);
                break;
            default:
                this.rValue = pipeline.get(key);
                break;

        }


    }

    public void finish() {
        this.ttl = (long) this.rTtl.get();
        setValue(JSON.toJSONString(this.rValue.get()));

    }


}
