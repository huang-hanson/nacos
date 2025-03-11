package com.alibaba.nacos.core.utils;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseTest {

    @Test
    public void test_namespace_id_decode() {
        // 创建HttpServletRequest的模拟对象
        HttpServletRequest req = mock(HttpServletRequest.class);

        // 创建并设置参数映射
        Map<String, String[]> parameterMap = new HashMap<>();
        parameterMap.put("namespaceId", new String[]{"16eb09c7-20e9-449d-8454-7628391ec946"});

        // 设置getParameterMap的返回值
        when(req.getParameterMap()).thenReturn(parameterMap);

        // 单独设置getParameter的返回值
        when(req.getParameter("namespaceId")).thenReturn("16eb09c7-20e9-449d-8454-7628391ec946");

        // 或者，如果WebUtils.optional是从getParameterMap获取数据，则不需要单独设置getParameter
        // WebUtils.optional内部逻辑需要确认是否依赖于getParameter或getParameterMap

        // 使用WebUtils从请求中获取namespaceId
        String decode = WebUtils.optional(req, "namespaceId", "");

        // 打印结果以便调试
        System.out.println("Decoded value: " + decode);

        // 验证结果是否符合预期
        assert "16eb09c7-20e9-449d-8454-7628391ec946".equals(decode) : "Namespace ID does not match expected value.";
    }

}