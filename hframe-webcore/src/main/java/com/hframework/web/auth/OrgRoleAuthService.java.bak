package com.hframework.base.service;

import com.google.common.collect.Lists;
import com.hframe.domain.model.*;
import com.hframe.service.interfaces.*;
import com.hframework.base.bean.AuthContext;
import com.hframework.common.ext.CollectionUtils;
import com.hframework.common.ext.Mapper;
import com.hframework.web.SessionKey;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhangquanhong on 2016/11/2.
 */
@Service
public class OrgRoleAuthService implements AuthService {

    @Resource
    private IHfsecUserAuthorizeSV hfsecUserAuthorizeSV;
    @Resource
    private IHfsecRoleAuthorizeSV hfsecRoleAuthorizeSV;

    @Resource
    private IHfsecOrganizeSV hfsecOrganizeSV;

    @Resource
    private IHfsecRoleSV hfsecRoleSV;

    @Resource
    private IHfsecMenuSV hfsecMenuSV;


    public AuthContext initAuthContext(HttpServletRequest request) throws Exception {
        HfsecUser hfsecUser = (HfsecUser) request.getSession().getAttribute(SessionKey.USER);
        AuthContext context = new AuthContext();
        context.setUser(hfsecUser);

        //设置数据单元关联信息
        HfsecOrganize hfsecOrganize = new HfsecOrganize();
        HfsecOrganize_Example example = new HfsecOrganize_Example();
        Map<Long, List< HfsecOrganize>> treeMap =
                hfsecOrganizeSV.getHfsecOrganizeTreeByParentId(hfsecOrganize, example);
        setAuthDataUnitRelFromOrgTree(treeMap.get(-1l), context, treeMap);

        List<HfsecMenu> hfsecMenuAll = hfsecMenuSV.getHfsecMenuAll();
        for (HfsecMenu hfsecMenu : hfsecMenuAll) {
            context.getAuthFunctionManager().put(hfsecMenu.getUrl(), hfsecMenu.getHfsecMenuId());
        }

        Long superOperatorRoleId = -1L;
        List<HfsecRole> hfsecRoleAll = hfsecRoleSV.getHfsecRoleAll();
        for (HfsecRole hfsecRole : hfsecRoleAll) {
            if("super_operator".equals(hfsecRole.getHfsecRoleCode())) {
                superOperatorRoleId = hfsecRole.getHfsecRoleId();
            }
        }

        //设置用户功能-数据授权信息（去重）
        Long userId = hfsecUser.getHfsecUserId();
        HfsecUserAuthorize_Example hfsecUserAuthorizeExample = new HfsecUserAuthorize_Example();
        hfsecUserAuthorizeExample.createCriteria().andHfsecUserIdEqualTo(userId);
        List<HfsecUserAuthorize> userAuthorizeList =
                hfsecUserAuthorizeSV.getHfsecUserAuthorizeListByExample(hfsecUserAuthorizeExample);

        for (HfsecUserAuthorize hfsecUserAuthorize : userAuthorizeList) {
            Long hfsecRoleId = hfsecUserAuthorize.getHfsecRoleId();
            //如果是超级操作员，添加所有菜单给授权
            if(superOperatorRoleId == hfsecRoleId) {
                for (HfsecMenu hfsecMenu : hfsecMenuAll) {
                    context.getAuthManager().add(hfsecUserAuthorize.getHfsecOrganizeId(), hfsecMenu.getHfsecMenuId());
                }
                continue;
            }
            HfsecRoleAuthorize_Example roleAuthorizeExample = new HfsecRoleAuthorize_Example();
            roleAuthorizeExample.createCriteria().andHfsecRoleIdEqualTo(hfsecRoleId);
            List<HfsecRoleAuthorize> roleAuthorizeList =
                    hfsecRoleAuthorizeSV.getHfsecRoleAuthorizeListByExample(roleAuthorizeExample);
            for (HfsecRoleAuthorize hfsecRoleAuthorize : roleAuthorizeList) {
                    context.getAuthManager().add(hfsecUserAuthorize.getHfsecOrganizeId(), hfsecRoleAuthorize.getHfsecMenuId());
            }
        }

        context.getAuthManager().addAuthDataClass(HfsecOrganize.class);
        context.getAuthManager().addAuthFunctionClass(HfsecMenu.class);

        request.getSession().setAttribute(SessionKey.AUTH, context);
        return context;
    }


    public List<Long> getFunctionIds(HttpServletRequest request) throws Exception {
        AuthContext context = (AuthContext) request.getSession().getAttribute(SessionKey.AUTH);
        Set<Long> functionIds = context.getAuthManager().keySet();
        return Lists.newArrayList(functionIds);
    }

    public AuthContext getAuthContext(HttpServletRequest request) throws Exception {
        AuthContext context = (AuthContext) request.getSession().getAttribute(SessionKey.AUTH);
        return context;
    }

    private void setAuthDataUnitRelFromOrgTree(List<HfsecOrganize> hfsecOrganizes, AuthContext context, Map<Long, List<HfsecOrganize>> treeMap) {

        if(hfsecOrganizes == null || hfsecOrganizes.size() == 0) return;

        for (HfsecOrganize organize : hfsecOrganizes) {
            //如果存在上级元素，将上级元素的值放入其中
            if(context.getAuthDataUnitRelManager().containsKey(organize.getParentHfsecOrganizeId())) {
                context.getAuthDataUnitRelManager().addAll(organize.getHfsecOrganizeId(),
                        context.getAuthDataUnitRelManager().get(organize.getParentHfsecOrganizeId()));
            }
            context.getAuthDataUnitRelManager().add(organize.getHfsecOrganizeId(), organize.getParentHfsecOrganizeId());
            setAuthDataUnitRelFromOrgTree(treeMap.get(organize.getHfsecOrganizeId()), context, treeMap);
        }
    }

}
