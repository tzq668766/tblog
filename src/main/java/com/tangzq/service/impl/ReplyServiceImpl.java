package com.tangzq.service.impl;

import com.tangzq.model.Reply;
import com.tangzq.repository.ReplyRepository;
import com.tangzq.service.ReplyService;
import com.tangzq.service.TopicService;
import com.tangzq.service.UserService;
import com.tangzq.vo.ReplyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author tangzhiqiang
 */
@Service
public class ReplyServiceImpl implements ReplyService {


    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private TopicService topicService;

    @Autowired
    private UserService userService;

    @Override
    public Reply getReply(String replyID) {
        return replyRepository.findById(replyID).get();
    }

    @Override
    public Reply addReply(ReplyVo vo) {
        Reply savedReply=replyRepository.save(convertVoToReply(vo));
        if(null!=savedReply&&savedReply.getId()!=null){
            //更新帖子评论次数
            topicService.increaseReplyCount(savedReply.getTopicId());
            return savedReply;
        }
        return null;
    }

    @Override
    public Reply updateReplyContent(String replyId, String contentMD, String contentHTML) {
        Reply replyInDB=getReply(replyId);
        if(null==replyInDB){
            return null;
        }
        replyInDB.setContentMD(contentMD);
        replyInDB.setContentHTML(contentHTML);
        replyInDB.setUpdateAt(new Date());
        return replyRepository.save(replyInDB);
    }


    private Reply convertVoToReply(ReplyVo vo){
        if(null==vo){
            return null;
        }
        Reply reply=new Reply();
        reply.setContentMD(vo.getContentMD());
        reply.setContentHTML(vo.getContentHTML());
        reply.setTopicId(vo.getTopicId());
        reply.setReplyUser(userService.getUser(vo.getAuthorId()));
        reply.setReplyId(null);
        reply.setContentIsHTML(Boolean.TRUE);
        reply.setThumbsUPCount(0);
        reply.setDeleted(Boolean.FALSE);
        reply.setCreateAt(new Date());
        reply.setUpdateAt(new Date());
        return reply;
    }

    @Override
    public List<Reply> findReplyByTopicId(String topicId) {
        return replyRepository.findAllByTopicId(topicId);
    }

    @Override
    public void deleteReply(String replyId,String topicId) {
        replyRepository.deleteById(replyId);
        topicService.decreaseReplyCount(topicId);
    }
}
