package com.tangzq.service.impl;

import com.tangzq.model.Category;
import com.tangzq.model.Topic;
import com.tangzq.repository.CategoryRepository;
import com.tangzq.repository.TopicRepository;
import com.tangzq.service.TopicService;
import com.tangzq.vo.IndexVo;
import com.tangzq.vo.SearchVo;
import com.tangzq.vo.TopicVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * @author tangzhiqiang
 */
@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private static final String TAB_ALL="all";


    public Page<Topic> findByPage(int pageNo, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "create_at");
        Pageable pageable = new PageRequest(pageNo-1, pageSize, sort);
        return topicRepository.findAll(pageable);
    }

    public Page<Topic> findByUserIdAndPage(String userId, int pageNo, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "create_at");
        Pageable pageable = new PageRequest(pageNo-1, pageSize, sort);
        return topicRepository.findByAuthorId(userId,pageable);
    }

    public Page<Topic> findByUsernameAndPage(String username, int pageNo, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "create_at");
        Pageable pageable = new PageRequest(pageNo-1, pageSize, sort);
        return topicRepository.findByAuthorName(username,pageable);
    }

    public Page<Topic> findByTagAndPage(String tagName, int pageNo, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "create_at");
        Pageable pageable = new PageRequest(pageNo-1, pageSize, sort);
        return topicRepository.findByTagsContains(tagName,pageable);
    }

    public Page<Topic> findByPage(IndexVo vo) {
        if(TAB_ALL.equals(vo.getTab())){
            Sort sort = new Sort(Sort.Direction.DESC, "top","create_at");
            Pageable pageable = new PageRequest(vo.getPageNO()-1, vo.getPageSize(), sort);
            return topicRepository.findAll(pageable);
        }else{
            Sort sort = new Sort(Sort.Direction.DESC, "create_at");
            Pageable pageable = new PageRequest(vo.getPageNO()-1, vo.getPageSize(), sort);
            return topicRepository.findByCatDir(vo.getTab(),pageable);
        }
    }

    public Page<Topic> search(SearchVo searchVo) {
        Sort sort = new Sort(Sort.Direction.DESC, "create_at");
        Pageable pageable = new PageRequest(searchVo.getPageNO()-1, searchVo.getPageSize(), sort);

        if(StringUtils.isEmpty(searchVo.getKeywords())){
            return topicRepository.findAll(pageable);
        }else{
            return topicRepository.findByTitleLikeOrDescLike(searchVo.getKeywords(),searchVo.getKeywords(),pageable);
        }
    }

    public Topic addTopic(TopicVo vo) {
        return topicRepository.save(convertToTopic(vo));
    }


    private Topic convertToTopic(TopicVo vo){
        if(null==vo){
            return null;
        }
        Topic topic=new Topic();
        topic.setAuthorId(vo.getAuthorId());
        topic.setAuthorName(vo.getAuthorName());
        topic.setCatId(vo.getCatId());
        Category cat=categoryRepository.findOne(vo.getCatId());
        if(null!=cat){
            topic.setCatName(cat.getCatName());
            topic.setCatDir(cat.getCatDir());
        }
        topic.setTitle(vo.getTitle());
        topic.setDesc(vo.getDesc());
        topic.setThumbURL(vo.getThumbURL());
        if(StringUtils.isNotEmpty(vo.getTags())){
            topic.setTags(Arrays.asList(vo.getTags().split(",")));
        }
        topic.setContentMD(vo.getContentMD());
        topic.setContentHTML(vo.getContentHTML());
        topic.setContentIsHTML(vo.isContentIsHTML());
        topic.setTop(vo.isTop());
        topic.setGood(vo.isGood());
        topic.setCreateAt(new Date());
        topic.setUpdateAt(new Date());
        return topic;
    }

    public Topic findTopicById(String topicId) {
        return topicRepository.findOne(topicId);
    }

    public TopicVo findTopicVoById(String topicId) {
        return convertToVo(findTopicById(topicId));
    }

    private TopicVo convertToVo(Topic topic){
        if(null==topic){
            return null;
        }
        TopicVo vo=new TopicVo();
        vo.setTopicId(topic.getId());
        vo.setAuthorName(topic.getAuthorName());
        vo.setAuthorId(topic.getAuthorId());
        vo.setCatId(topic.getCatId());
        vo.setTitle(topic.getTitle());
        vo.setDesc(topic.getDesc());
        vo.setThumbURL(topic.getThumbURL());
        if(null!=topic.getTags()){
            vo.setTags(StringUtils.join(topic.getTags(),","));
        }
        vo.setContentMD(topic.getContentMD());
        vo.setContentHTML(topic.getContentHTML());
        vo.setContentIsHTML(topic.isContentIsHTML());
        vo.setTop(topic.isTop());
        vo.setGood(topic.isGood());
        return vo;
    }


    public Topic updateById(TopicVo vo, String id) {
        Topic topicInDB=findTopicById(id);
        if(null==topicInDB){
            return null;
        }
        topicInDB.setCatId(vo.getCatId());
        topicInDB.setTitle(vo.getTitle());
        topicInDB.setDesc(vo.getDesc());
        topicInDB.setThumbURL(vo.getThumbURL());
        if(null!=vo.getTags()){
            topicInDB.setTags(Arrays.asList(vo.getTags().split(",")));
        }
        topicInDB.setAuthorId(vo.getAuthorId());
        topicInDB.setAuthorName(vo.getAuthorName());
        topicInDB.setContentMD(vo.getContentMD());
        topicInDB.setContentHTML(vo.getContentHTML());
        topicInDB.setContentIsHTML(vo.isContentIsHTML());
        topicInDB.setTop(vo.isTop());
        topicInDB.setGood(vo.isGood());
        topicInDB.setUpdateAt(new Date());
        return topicRepository.save(topicInDB);
    }

    public void deleteById(String topicId) {
        topicRepository.delete(topicId);
    }

    public void increaseVisitCount(String topicId) {
        Topic topicInDB=findTopicById(topicId);
        if(null==topicInDB){
            return;
        }
        topicInDB.setVisitCount(topicInDB.getVisitCount()+1);
        topicRepository.save(topicInDB);
    }

    public void increaseReplyCount(String topicId) {
        Topic topicInDB=findTopicById(topicId);
        if(null==topicInDB){
            return;
        }
        topicInDB.setReplyCount(topicInDB.getReplyCount()+1);
        topicRepository.save(topicInDB);
    }

    public void decreaseReplyCount(String topicId) {
        Topic topicInDB=findTopicById(topicId);
        if(null==topicInDB){
            return;
        }
        int replyCount=topicInDB.getReplyCount()>1?topicInDB.getReplyCount()-1:0;
        topicInDB.setReplyCount(replyCount);
        topicRepository.save(topicInDB);
    }

    public Topic addCollection(String topicId, String userId) {
        Topic topicInDb=topicRepository.findOne(topicId);
        if(null==topicInDb){
            return null;
        }
        Set<String> collectedUsers=topicInDb.getCollectedUsers();
        if(null==collectedUsers){
            collectedUsers=new HashSet<String>();
        }
        collectedUsers.add(userId);
        topicInDb.setCollectedUsers(collectedUsers);
        topicInDb.setCollectCount(collectedUsers.size());
        return topicRepository.save(topicInDb);
    }

    public Topic removeCollection(String topicId, String userId) {
        Topic topicInDb=topicRepository.findOne(topicId);
        if(null==topicInDb){
            return null;
        }
        Set<String> collectedUsers=topicInDb.getCollectedUsers();
        if(null!=collectedUsers&&collectedUsers.contains(userId)){
            collectedUsers.remove(userId);
            topicInDb.setCollectedUsers(collectedUsers);
            topicInDb.setCollectCount(collectedUsers.size());
        }
        return topicRepository.save(topicInDb);
    }

    public Page<Topic> findCollectedTopicsByUidAndPage(String userId, int pageNo, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "create_at");
        Pageable pageable = new PageRequest(pageNo-1, pageSize, sort);
        return topicRepository.findByCollectedUsersContains(userId,pageable);
    }


}
