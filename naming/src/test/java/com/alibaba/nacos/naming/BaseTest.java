/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.naming;

import com.alibaba.nacos.naming.consistency.persistent.raft.RaftCore;
import com.alibaba.nacos.naming.consistency.persistent.raft.RaftPeer;
import com.alibaba.nacos.naming.consistency.persistent.raft.RaftPeerSet;
import com.alibaba.nacos.naming.core.DistroMapper;
import com.alibaba.nacos.naming.core.Service;
import com.alibaba.nacos.naming.core.ServiceManager;
import com.alibaba.nacos.naming.healthcheck.HealthCheckProcessorDelegate;
import com.alibaba.nacos.naming.misc.NetUtils;
import com.alibaba.nacos.naming.misc.SwitchDomain;
import com.alibaba.nacos.naming.push.PushService;
import com.alibaba.nacos.sys.env.EnvUtil;
import com.alibaba.nacos.sys.utils.ApplicationUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class BaseTest {

    protected static final String TEST_CLUSTER_NAME = "test-cluster";

    protected static final String TEST_SERVICE_NAME = "DEFAULT_GROUP@@test-service";

    protected static final String TEST_GROUP_NAME = "test-group-name";

    protected static final String TEST_NAMESPACE = "test-namespace";

    @Mock
    public ServiceManager serviceManager;

    @Mock
    public RaftPeerSet peerSet;

    @Mock
    public RaftCore raftCore;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Spy
    protected ConfigurableApplicationContext context;

    @Mock
    protected DistroMapper distroMapper;

    @Spy
    protected SwitchDomain switchDomain;

    @Mock
    protected HealthCheckProcessorDelegate delegate;

    @Mock
    protected PushService pushService;

    @Spy
    private MockEnvironment environment;

    @Before
    public void before() {
        EnvUtil.setEnvironment(environment);
        ApplicationUtils.injectContext(context);
    }

    protected void mockRaft() {
        RaftPeer peer = new RaftPeer();
        peer.ip = NetUtils.localServer();
        raftCore.setPeerSet(peerSet);
        Mockito.when(peerSet.local()).thenReturn(peer);
        Mockito.when(peerSet.getLeader()).thenReturn(peer);
        Mockito.when(peerSet.isLeader(NetUtils.localServer())).thenReturn(true);
    }

    protected void mockInjectPushServer() {
        doReturn(pushService).when(context).getBean(PushService.class);
    }

    protected void mockInjectHealthCheckProcessor() {
        doReturn(delegate).when(context).getBean(HealthCheckProcessorDelegate.class);
    }

    protected void mockInjectSwitchDomain() {
        doReturn(switchDomain).when(context).getBean(SwitchDomain.class);
    }

    protected void mockInjectDistroMapper() {
        doReturn(distroMapper).when(context).getBean(DistroMapper.class);
    }

    @Test
    public void test_regex() {
        Pattern ONLY_DIGIT_AND_DOT = Pattern.compile("(\\d|\\.)+");
        assert ONLY_DIGIT_AND_DOT.matcher("1.1.1.1").matches();
        assert ONLY_DIGIT_AND_DOT.matcher("1234").matches();
        assert ONLY_DIGIT_AND_DOT.matcher(".").matches();
        assert !ONLY_DIGIT_AND_DOT.matcher("123abc.a").matches();
        assert !ONLY_DIGIT_AND_DOT.matcher("127.0.0.1:8848").matches();
    }

    @Test
    public void test_map_putIfAbsent() {
        Map<String, Map<String, String>> map = new ConcurrentHashMap<>();
        Map<String, String> mapValue = new HashMap<>();
        mapValue.put("valueKey", "valueValue");
        map.put("key1", mapValue);
        System.out.println(map);
        map.get("key1").putIfAbsent("valueKey2", "valueValue2");
        System.out.println(map);
        System.out.println(map.get("key1").putIfAbsent("valueKey2", "valueValue2"));
    }

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    @Test
    public void test_incrementAndGet() {
        // 线程1
        Thread t1 = new Thread(() -> {
            int value = atomicInteger.incrementAndGet();
            System.out.println("Thread 1: " + value);
        });

        // 线程2
        Thread t2 = new Thread(() -> {
            int value = atomicInteger.incrementAndGet();
            System.out.println("Thread 2: " + value);
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final value: " + atomicInteger.get());
    }


    @Test
    public void test_getAndIncrement() {
        // 线程1
        Thread t1 = new Thread(() -> {
            int value = atomicInteger.getAndIncrement();
            System.out.println("Thread 1: " + value);
        });

        // 线程2
        Thread t2 = new Thread(() -> {
            int value = atomicInteger.getAndIncrement();
            System.out.println("Thread 2: " + value);
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final value: " + atomicInteger.get());
    }
}
