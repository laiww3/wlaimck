package com.wlai.bzl.controller;

import com.wlai.bzl.service.amap.IAampService;
import com.wlai.common.result.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/amap")
public class AmapController {

    @Autowired
    private IAampService aampService;

    @GetMapping("/utm/distance")
    public AjaxResult pointInGeometries(@RequestParam double latitude, @RequestParam double longitude){
        double distance = aampService.pointInGeometriesDistanceUtm(latitude, longitude);
        AjaxResult ajax = AjaxResult.success();
        if(distance > 0){
            ajax.put("utm_distance", distance);
        }else if(distance == -1){
            ajax.put("utm_distance", distance);
            ajax.put(AjaxResult.MSG_TAG, "不在中国境内！！");
        }else {
            ajax.put("utm_distance", distance);
            ajax.put(AjaxResult.MSG_TAG, "读取文件失败 ~ ~ ");
        }
        return ajax;
    }
}
