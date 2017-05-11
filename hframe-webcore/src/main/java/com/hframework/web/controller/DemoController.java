package com.hframework.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hframework.common.util.file.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * User: zhangqh6
 * Date: 2016/5/22 16:18:18
 */
@Controller
@RequestMapping(value = "/")
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @RequestMapping(value = "/default.html")
    public ModelAndView gotoDefault(@ModelAttribute("account") String account, @ModelAttribute("password") String password){
        logger.debug("request : {}", account, password);
        ModelAndView mav = new ModelAndView();
        mav.addObject("programName", "客户关系管理系统");
        mav.addObject("menus", JSONArray.parse("[\n" +
                "    {\n" +
                "        url: '1',\n" +
                "        id: '1',\n" +
                "        icon: '',\n" +
                "        name: '用户管理',\n" +
                "        menus: [\n" +
                "            {\n" +
                "                url: 'user/add.html',\n" +
                "                id: '',\n" +
                "                icon: '',\n" +
                "                name: '用户添加'\n" +
                "            },\n" +
                "            {\n" +
                "                url: 'user/query.html',\n" +
                "                id: '',\n" +
                "                icon: '',\n" +
                "                name: '用户查询'\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        url: '1',\n" +
                "        id: '1',\n" +
                "        icon: '',\n" +
                "        name: '资源管理',\n" +
                "        menus: [\n" +
                "            {\n" +
                "                url: 'res/query.html',\n" +
                "                id: '',\n" +
                "                icon: '',\n" +
                "                name: '资源查询'\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        url: 'report/management.html',\n" +
                "        id: '1',\n" +
                "        icon: '',\n" +
                "        name: '报表管理'\n" +
                "    }\n" +
                "]"));



        mav.addObject("staticResourcePath", "/static");
        mav.addObject("pageTemplate", "default.vm");

        mav.setViewName("/default");
        return mav;
    }

    @RequestMapping(value = "/list.html")
    public ModelAndView gotoList(@ModelAttribute("account") String account, @ModelAttribute("password") String password) throws IOException {
        logger.debug("request : {}", account, password);
        ModelAndView modelAndView = this.gotoDefault(account, password);

            modelAndView.addObject("form", JSONObject.parse(
                    FileUtils.readFile(Thread.currentThread().getContextClassLoader().getResource(
                            "program\\demo\\data\\demo\\form.json").getPath())));

            modelAndView.addObject("list", JSONObject.parse(
                    FileUtils.readFile(Thread.currentThread().getContextClassLoader().getResource(
                    "program\\demo\\data\\demo\\list.json").getPath())));

            modelAndView.setViewName("/list");
        return modelAndView;
    }
    @RequestMapping(value = "/citypick.html")
    public ModelAndView citypick( @ModelAttribute("account") String account, @ModelAttribute("password") String password) throws IOException {
        logger.debug("request : {}", account, password);
        ModelAndView modelAndView = this.gotoDefault(account, password);

        modelAndView.addObject("form", JSONObject.parse(
                FileUtils.readFile(Thread.currentThread().getContextClassLoader().getResource(
                        "program\\demo\\data\\demo\\form.json").getPath())));

        modelAndView.addObject("list", JSONObject.parse(
                FileUtils.readFile(Thread.currentThread().getContextClassLoader().getResource(
                        "program\\demo\\data\\demo\\list.json").getPath())));

        modelAndView.setViewName("/" + "citypick");
        return modelAndView;
    }
        @RequestMapping(value = "/citypickdemo.html")
        public ModelAndView gotoModify( @ModelAttribute("account") String account, @ModelAttribute("password") String password) throws IOException {
                logger.debug("request : {}", account, password);
                ModelAndView modelAndView = this.gotoDefault(account, password);

                modelAndView.addObject("form", JSONObject.parse(
                        FileUtils.readFile(Thread.currentThread().getContextClassLoader().getResource(
                                "program\\demo\\data\\demo\\form.json").getPath())));

                modelAndView.addObject("list", JSONObject.parse(
                        FileUtils.readFile(Thread.currentThread().getContextClassLoader().getResource(
                                "program\\demo\\data\\demo\\list.json").getPath())));

                modelAndView.setViewName("/" + "citypickdemo");
                return modelAndView;
        }

    @RequestMapping(value = "/uc/userlist.html")
    public ModelAndView list(@PathVariable("module") String module, @PathVariable("dataset") String dataset, @ModelAttribute("account") String account, @ModelAttribute("password") String password) throws IOException {
        logger.debug("request : {}", account, password);
        ModelAndView modelAndView = this.gotoDefault(account, password);

        modelAndView.addObject("form", JSONObject.parse(
                FileUtils.readFile(Thread.currentThread().getContextClassLoader().getResource(
                        "program\\demo\\data\\demo\\form.json").getPath())));

        modelAndView.addObject("list", JSONObject.parse(
                FileUtils.readFile(Thread.currentThread().getContextClassLoader().getResource(
                        "program\\demo\\data\\demo\\list.json").getPath())));

        modelAndView.setViewName("/" + "");
        return modelAndView;
    }
}
