package com.hframe.activiti.ext.explorer;

import org.activiti.explorer.DefaultViewManager;
import org.activiti.explorer.Environments;
import org.activiti.explorer.ViewManager;
import org.activiti.explorer.ui.alfresco.AlfrescoViewManager;
import org.springframework.beans.factory.FactoryBean;

import java.io.Serializable;

/**
 * Created by zhangquanhong on 2017/1/23.
 */
public class ViewManagerFactoryBean extends org.activiti.explorer.ViewManagerFactoryBean implements FactoryBean<ViewManager>, Serializable {

    protected BlankWindow blankWindow;


    public ViewManager getObject() throws Exception {
        DefaultViewManager viewManagerImpl;
        if (environment.equals(Environments.ALFRESCO)) {
            viewManagerImpl = new AlfrescoViewManager();
        } else {
            viewManagerImpl = new HframeViewManager();
            ((HframeViewManager)viewManagerImpl).setBlankWindow(blankWindow);
        }
        viewManagerImpl.setMainWindow(mainWindow);
        return viewManagerImpl;
    }

    public BlankWindow getBlankWindow() {
        return blankWindow;
    }

    public void setBlankWindow(BlankWindow blankWindow) {
        this.blankWindow = blankWindow;
    }
}
