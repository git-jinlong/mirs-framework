package com.github.mirs.banxiaoxiao.framework.demo.modules.web;

import com.github.mirs.banxiaoxiao.framework.web.RestControllerSupport;
import com.github.mirs.banxiaoxiao.framework.web.rest.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: bc
 * @date: 2021-07-13 15:40
 **/
@Api(value = "示例应用", tags = "示例应用")
@RestController
public class DemoController extends RestControllerSupport {


    @GetMapping("/demo")
    @ApiOperation(value = "示例请求", httpMethod = "GET")
    public RestResponse demo() {


        return restResponses.ok();
    }
}
