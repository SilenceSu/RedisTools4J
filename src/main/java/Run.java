import org.apache.commons.cli.*;
import redis.RedisPool;
import tools.HostDump;

import java.io.File;
import java.io.IOException;

/**
 *   db to json
  * Created by Silence on 2016/12/24.
 */
public class Run {


    public static void main(String[] args) throws IOException {

        long benginTime = System.currentTimeMillis();


        Options options = new Options();
        options.addOption("l", true, "load json by dir ");
        options.addOption("h", false, "input redis host ");
        options.addOption("p", true, "input redis port");
        options.addOption("f", true, "input dir string");
        CommandLine cmd = null;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.out.println("parameter input is error");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("help", options);
            System.exit(0);
        }


        String load = cmd.getOptionValue("load");

        String host = cmd.getOptionValue("h", "127.0.0.1");
        Integer port = Integer.valueOf(cmd.getOptionValue("p", "6379"));

        String file = cmd.getOptionValue("f");
        if (file != null) {
            file = System.getProperty("user.dir") + File.separator + file;
        }


        //init redis pool
        RedisPool.ip = host;
        RedisPool.port = port;


        if (load == null) {


            if (file == null) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("help", options);
                System.exit(0);
            }

            //tools
            HostDump toJson = new HostDump();
            toJson.toJsonFile(file);

        } else {
            //load


        }


        long EndTime = System.currentTimeMillis();
        System.out.println("finish times " + (EndTime - benginTime));
    }

}
