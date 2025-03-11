package com.alibaba.nacos.client;

import com.alibaba.nacos.client.naming.beat.BeatInfo;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class BaseTest {

    public final Map<String, BeatInfo> dom2Beat = new ConcurrentHashMap<String, BeatInfo>();

    @Test
    public void test_map_put_same_data() {
        BeatInfo beatInfo = new BeatInfo();
        beatInfo.setServiceName("test");
        beatInfo.setIp("11.11.11.11");
        beatInfo.setPort(1234);
        beatInfo.setCluster("clusterName");
        beatInfo.setWeight(1);
        beatInfo.setMetadata(new HashMap<String, String>());
        beatInfo.setScheduled(false);
        beatInfo.setPeriod(1000L);
        System.out.println(dom2Beat.put("test", beatInfo));
        System.out.println(dom2Beat.put("test", beatInfo));
    }

    @Test
    public void test_remainder() {
        String server = "127.0.0.1:8848,127.0.0.1:8849,127.0.0.1:8850,127.0.0.1:8851,127.0.0.1:8852,127.0.0.1:8853";
        String[] servers = server.split(",");
        Random random = new Random(System.currentTimeMillis());
        int index = random.nextInt(servers.length);

        for (int i = 0; i < servers.length; i++) {
            index = (index + 1) % servers.length;
            System.out.println(index);
        }
    }
}