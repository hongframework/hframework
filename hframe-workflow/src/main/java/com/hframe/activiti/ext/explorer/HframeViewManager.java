package com.hframe.activiti.ext.explorer;

import org.activiti.explorer.DefaultViewManager;
import org.activiti.explorer.ViewManager;
import org.activiti.explorer.ui.AbstractPage;
import org.activiti.explorer.ui.process.MyProcessInstancesPage;

import java.io.Serializable;

/**
 * Created by zhangquanhong on 2017/1/23.
 */
public class HframeViewManager extends DefaultViewManager implements ViewManager, Serializable {

    protected BlankWindow blankWindow;

    public void showMyProcessInstancesPage(String processInstanceId) {
        MyProcessInstancesPage page = new MyProcessInstancesPage(processInstanceId);
        currentPage = page;
        mainWindow = blankWindow;
        blankWindow.switchView(page);
    }

    public BlankWindow getBlankWindow() {
        return blankWindow;
    }

    public void setBlankWindow(BlankWindow blankWindow) {
        this.blankWindow = blankWindow;
    }

    protected void switchView(AbstractPage page, String mainMenuActive, String subMenuActive) {
        currentPage = page;
//        mainWindow.setMainNavigation(mainMenuActive);
        mainWindow.switchView(page);
//        if (subMenuActive != null && page.getToolBar() != null) {
//            page.getToolBar().setActiveEntry(subMenuActive); // Must be set AFTER adding page to window (toolbar will be created in atach())
//        }
    }
}
