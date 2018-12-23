package com.tangzq.controller.api;

import com.tangzq.model.Comment;
import com.tangzq.model.User;
import com.tangzq.response.Result;
import com.tangzq.service.CommentService;
import com.tangzq.service.UserService;
import com.tangzq.utils.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 评论接口
 */
@RestController
@RequestMapping("/api/comment")
@Api(value = "评论API", description = "博客评论接口",tags = "Comment",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiCommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @ApiOperation(value="获取帖子相关的评论", notes="根据url的id来获取帖子的所有评论")
    @ApiImplicitParam(name = "topicId", value = "帖子ID", required = true, dataType = "String")
    @RequestMapping(value="/public/{topicId}", method=RequestMethod.GET)
    public Result detail(@PathVariable String topicId) {
        return Result.ok("成功",commentService.getAllItemComments(topicId));
    }

    @ApiOperation(value="评论点赞", notes="评论点赞，每个用户只能点赞一次")
    @ApiImplicitParam(name = "commentId", value = "评论ID", required = true, dataType = "String")
    @RequestMapping(value="/thumbsup/{commentId}",method = RequestMethod.PUT)
    public Result thumbsupComment(
            @PathVariable("commentId")String commentId, HttpServletRequest request){

        Comment comment= commentService.getComment(commentId);
        if(null==comment){
            Result.fail("评论不存在");
        }

        User loginUser=userService.getUser((String)request.getAttribute(Constants.API_LOGIN_USER_ID_KEY));
        if(null==loginUser){
            return Result.fail("用户还未登陆");
        }

        commentService.updateThumbsUpData(commentId, loginUser);
        return Result.ok("点赞成功");
    }

    @ApiOperation(value="删除评论", notes="根据url的id来指定删除评论")
    @ApiImplicitParam(name = "commentId", value = "评论ID", required = true, dataType = "String")
    @RequestMapping(value="/{commentId}", method=RequestMethod.DELETE)
    public Result deleteTopic(@PathVariable String commentId) {
        Comment comment= commentService.getComment(commentId);
        if(null==comment){
            Result.fail("评论不存在");
        }
        commentService.deleteComment(commentId);
        return Result.ok("删除评论成功");
    }

}