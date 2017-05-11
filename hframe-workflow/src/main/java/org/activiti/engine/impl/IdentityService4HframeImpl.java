package org.activiti.engine.impl;

import com.google.common.collect.Lists;
import com.hframework.beans.controller.ResultData;
import com.hframework.common.frame.ServiceFactory;
import com.hframework.common.util.JavaUtil;
import com.hframework.common.util.ReflectUtils;
import com.hframework.web.CreatorUtil;
import com.hframework.web.auth.AuthContext;
import com.hframework.web.context.DataSetDescriptor;
import com.hframework.web.context.WebContext;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.util.IoUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.List;

/**
 * Created by zhangquanhong on 2017/1/23.
 */
public class IdentityService4HframeImpl extends IdentityServiceImpl implements IdentityService {
    @Override
    public Picture getUserPicture(String userId) {
//        return new Picture(IoUtil.readInputStream(this.getClass().getClassLoader().getResourceAsStream("org/activiti/explorer/images/fozzie.jpg"), null), "image/jpeg");
//        try{
//            picture = super.getUserPicture(userId);
//        }catch (Exception e) {
//            if(picture == null) {
//                byte[] pictureBytes = IoUtil.readInputStream(this.getClass().getClassLoader().getResourceAsStream("org/activiti/explorer/images/fozzie.jpg"), null);
//                picture = new Picture(pictureBytes, "image/jpeg");
//            }
//        }
        return ((UserEntity)this.createUserQuery().userId(userId).singleResult()).getPicture();
    }

    @Override
    public UserQuery createUserQuery() {

        return new UserQueryImpl(((ProcessEngineConfigurationImpl) ProcessEngines.getDefaultProcessEngine().
                getProcessEngineConfiguration()).getCommandExecutor()){
            @Override
            public List<User> executeList(CommandContext commandContext, Page page) {
                String id = super.getId();
                if(StringUtils.isNoneBlank(id)) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(id);
                    userEntity.setFirstName("");
                    userEntity.setLastName("unknown");
                    Class userClass = AuthContext.AuthUser.userClass;
                    DataSetDescriptor dataSet = WebContext.get().getDataSet(userClass);
                    String moduleCode = dataSet.getDataSet().getModule();
                    String eventObjectCode = dataSet.getDataSet().getEventObjectCode();
                    try {
                        Object po = userClass.newInstance();
                        ReflectUtils.setFieldValue(po, JavaUtil.getJavaVarName(dataSet.getKeyField().getCode()), Long.valueOf(id));
                        com.hframework.beans.class0.Class defControllerClass = CreatorUtil.getDefControllerClass(WebContext.get().getProgram().getCompany(),
                                WebContext.get().getProgram().getCode(), moduleCode, eventObjectCode);
                        Object controller = ServiceFactory.getService(defControllerClass.getClassName().substring(0, 1).toLowerCase() + defControllerClass.getClassName().substring(1));
                        Object userInfo = ReflectUtils.invokeMethod(controller, "detail", new java.lang.Class[]{userClass}, new Object[]{po});
                        userInfo = ((ResultData)userInfo).getData();
                        try{
                            if(userClass.getDeclaredField("avatar") != null) {
                                String avatar = (String) ReflectUtils.getFieldValue(userInfo, "avatar");
                                Picture picture = null;
                                byte[] pictureBytes = IoUtil.readInputStream(new URL(avatar).openStream(), null);
                                picture = new Picture(pictureBytes, "image/jpeg");
                                userEntity.setPicture(picture);
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
//                        com.hframework.beans.class0.Class  defServiceClass = CreatorUtil.getDefServiceClass("", WebContext.get().getProgram().getCode(), moduleCode, eventObjectCode);
//                        Object service = ServiceFactory.getService(defServiceClass.getClassName().substring(0, 1).toLowerCase() + defServiceClass.getClassName().substring(1));
//                        Object userInfo = ReflectUtils.invokeMethod(service, "get" + userClass.getSimpleName() + "ByPK", new Class[]{Long.class}, new Object[]{Long.valueOf(id)});


                        userEntity.setLastName(BeanUtils.getProperty(userInfo, JavaUtil.getJavaVarName(dataSet.getNameField().getCode())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    userEntity.setPicture(new Picture());
                    return Lists.newArrayList((User)userEntity);
                }else {
                    return super.executeList(commandContext, page);
                }
            }
        };
    }
}
