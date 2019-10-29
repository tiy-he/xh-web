package com.xiaohe66.web.code.security.service;

import com.xiaohe66.web.base.base.impl.AbstractService;
import com.xiaohe66.web.base.data.Final;
import com.xiaohe66.web.base.exception.param.IllegalParamException;
import com.xiaohe66.web.base.exception.param.MissingParamException;
import com.xiaohe66.web.base.util.Check;
import com.xiaohe66.web.base.util.StrUtils;
import com.xiaohe66.web.code.security.mapper.UserRoleMapper;
import com.xiaohe66.web.code.security.po.UserRole;
import com.xiaohe66.web.code.sys.helper.SysCfgHelper;
import org.springframework.stereotype.Service;

/**
 * 用户角色关联service
 *
 * @author xh
 * @version 1.0
 * @time 2018-08-20 16:54
 */
@Service
public class UserRoleService extends AbstractService<UserRoleMapper, UserRole> {

    public void addDefaultUsrRole(Integer userId) {
        if (userId == null) {
            throw new MissingParamException("userId");
        }
        // todo:需要优化掉string转Integer代码
        String cfgKeysStr = SysCfgHelper.getString(Final.Str.DEFAULT_ROLE_IDS_KEY);
        String[] roleStrIds = cfgKeysStr.split(",");
        Integer[] roleIds = new Integer[roleStrIds.length];
        for (int i = 0; i < roleStrIds.length; i++) {
            roleIds[i] = StrUtils.toIntNotException(roleStrIds[i]);
        }
        this.addUsrRoles(userId, roleIds);
    }

    public void addUsrRoles(Integer usrId, Integer[] roleIds) {
        if (usrId == null) {
            throw new IllegalParamException("userId is null");
        }
        if (!Check.isNotEmpty(roleIds)) {
            throw new IllegalParamException("roleIds is null or size is 0");
        }
        baseMapper.addUsrRoles(usrId, roleIds);
    }
}