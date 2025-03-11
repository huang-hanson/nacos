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

package com.alibaba.nacos.naming.healthcheck;

import com.alibaba.nacos.naming.misc.GlobalExecutor;
import com.alibaba.nacos.naming.misc.Loggers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Health check reactor.
 *
 * @author nacos
 */
@SuppressWarnings("PMD.ThreadPoolCreationRule")
public class HealthCheckReactor {
    
    private static Map<String, ScheduledFuture> futureMap = new ConcurrentHashMap<>();
    
    /**
     * Schedule health check task.
     *
     * @param task health check task
     * @return scheduled future
     */
    public static ScheduledFuture<?> scheduleCheck(HealthCheckTask task) {
        task.setStartTime(System.currentTimeMillis());
        return GlobalExecutor.scheduleNamingHealth(task, task.getCheckRtNormalized(), TimeUnit.MILLISECONDS);
    }
    
    /**
     * Schedule client beat check task with a delay.
     * 将客户端心跳检查任务安排为延迟。
     *
     * 假设你有一个服务需要定期检查客户端的心跳状态，可以使用 ClientBeatCheckTask 来封装具体的检查逻辑，并通过上述方法将其调度为周期性任务。
     * 这样可以确保每个检查任务只被调度一次，并且能够按照设定的时间间隔自动执行。
     *
     * @param task client beat check task
     */
    public static void scheduleCheck(ClientBeatCheckTask task) {
        // 该方法的作用是根据任务的唯一键（taskKey）来决定是否调度一个新的周期性任务。
        // 如果任务尚未调度，则通过 GlobalExecutor.scheduleNamingHealth 方法进行调度，并将生成的 ScheduledFuture 对象存入 futureMap 中。
        // 这种设计避免了重复调度相同任务的问题，同时利用 ConcurrentHashMap 确保了线程安全。
        futureMap.computeIfAbsent(task.taskKey(),
                k -> GlobalExecutor.scheduleNamingHealth(task, 5000, 5000, TimeUnit.MILLISECONDS));
    }
    
    /**
     * Cancel client beat check task.
     *
     * @param task client beat check task
     */
    public static void cancelCheck(ClientBeatCheckTask task) {
        ScheduledFuture scheduledFuture = futureMap.get(task.taskKey());
        if (scheduledFuture == null) {
            return;
        }
        try {
            scheduledFuture.cancel(true);
            futureMap.remove(task.taskKey());
        } catch (Exception e) {
            Loggers.EVT_LOG.error("[CANCEL-CHECK] cancel failed!", e);
        }
    }
    
    /**
     * Schedule client beat check task without a delay.
     *
     * @param task health check task
     * @return scheduled future
     */
    public static ScheduledFuture<?> scheduleNow(Runnable task) {
        return GlobalExecutor.scheduleNamingHealth(task, 0, TimeUnit.MILLISECONDS);
    }
}
