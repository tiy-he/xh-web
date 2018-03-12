package com.xiaohe66.web.text.service;

import com.xiaohe66.web.common.base.impl.AbstractService;
import com.xiaohe66.web.common.data.CodeEnum;
import com.xiaohe66.web.common.exception.XhException;
import com.xiaohe66.web.common.util.Check;
import com.xiaohe66.web.text.dao.TextCategoryDao;
import com.xiaohe66.web.text.po.TextCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaohe
 * @time 17-11-12 012
 */
@Service
public class TextCategoryService extends AbstractService<TextCategory> {
    private TextCategoryDao categoryDao;

    public TextCategoryService(){}

    @Autowired
    public TextCategoryService(TextCategoryDao textCategoryDao){
        super(textCategoryDao);
        this.categoryDao = textCategoryDao;
    }

    public List<TextCategory> findByPid(Long pid){
        if(Check.isOneNull(pid)){
            throw new XhException(CodeEnum.NULL_EXCEPTION,"pid is null");
        }
        return categoryDao.findByPid(pid);
    }
}
