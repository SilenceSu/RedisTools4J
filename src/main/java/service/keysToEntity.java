package service;

import entity.JsonEntity;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * redis keys to json entity
 * Created by Silence on 2016/12/28.
 */
public class keysToEntity {


    public static List<JsonEntity> findJsonEntitys(long db, Pipeline pipeline, List<String> keys) {


        return toEntitys(db, pipeline, keys);
    }


    /**
     * 先初始化 type
     *
     * @param db
     * @param pipeline
     * @param keys
     * @return
     */
    private static List<JsonEntity> toEntitys(long db, Pipeline pipeline, List<String> keys) {

        //init type
        List<JsonEntity> entities = new ArrayList<>();
        for (String key : keys) {
            JsonEntity entity = new JsonEntity(db, key);
            entity.initType(pipeline);
            entities.add(entity);
        }
        pipeline.sync();

        return initEntitiys(pipeline, entities);

    }


    /**
     * 在初始化其他数据
     *
     * @param pipeline
     * @param entities
     * @return
     */
    private static List<JsonEntity> initEntitiys(Pipeline pipeline, List<JsonEntity> entities) {

        for (JsonEntity entity : entities) {
            entity.initResponse(pipeline);

        }
        pipeline.sync();

        for (JsonEntity entity : entities) {

            entity.finish();
        }

        return entities;
    }




}
