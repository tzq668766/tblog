package com.tangzq.service;

import com.tangzq.model.Category;

import java.util.List;

public interface CategoryService {

    /**
     * 新增栏目
     * @param cat
     */
    Category addCategory(Category cat);


    /**
     * 使用栏目ID找到栏目信息
     * @param id
     * @return
     */
    Category findById(String id);

    /**
     * 找到所有栏目信息
     * @return
     */
    List<Category> findAll();

    /**
     * 判断栏目是否已经存在
     * @param catDir
     * @return
     */
    boolean isCategoryExisted(String catDir);


    /**
     * 更新栏目信息
     * @param cat
     * @param id
     * @return
     */
    Category updateById(Category cat,String id);


    /**
     * 删除指定栏目
     * @param id
     * //TODO 删除栏目同时也要删除该栏目下文章
     */
    void deleteCategory(String id);
}