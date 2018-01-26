package com.tangzq.controller;

import com.tangzq.service.TopicService;
import com.tangzq.service.UserService;
import com.tangzq.vo.PageVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户控制器
 * @author tangzhiqiang
 */
@Controller
@RequestMapping("/pub")
public class PublicController {


    private final Logger logger = LoggerFactory.getLogger(PublicController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TopicService topicService;

    /**
     * 用户详情页
     * @param userId
     * @return
     */
    @RequestMapping(value="/user/{userId}")
    public String userDetail(@PathVariable("userId")String userId, PageVo pageVo, ModelMap model){
        if(StringUtils.isEmpty(userId)||userService.getUser(userId)==null){
            return "error/404";
        }
        model.addAttribute("user",userService.getUser(userId));
        model.addAttribute("pager",
                topicService.findByUserIdAndPage(userId,pageVo.getPageNO(),pageVo.getPageSize()));

        return "user/user_detail";
    }


}