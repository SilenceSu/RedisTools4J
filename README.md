# RedisTools4J

##使用

redis 导出为json

```
jar -jar  RedisTools4J.jar -h 127.0.0.1 -p 6379 -f data.json


-h  redis ip
-p  redis port
-f  json file name

``` 

json format

```
long db 		   //db num
String key 		//redis key
long ttl   		//key ttl
String type 	   //key type
String value	   //value json string
int size;    	  //value size 
```