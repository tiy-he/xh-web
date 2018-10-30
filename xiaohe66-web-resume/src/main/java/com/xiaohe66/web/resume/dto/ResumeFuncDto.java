package com.xiaohe66.web.resume.dto;

import com.xiaohe66.web.base.base.BaseDtoDetailed;

/**
 * 简历-功能开发
 *
 * @author xh
 * @date 18-10-12 012
 */
public class ResumeFuncDto extends BaseDtoDetailed {

    private String funcName;
    private Long articleId;

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }
}
