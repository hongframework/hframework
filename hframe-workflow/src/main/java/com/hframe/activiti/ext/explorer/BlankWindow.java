package com.hframe.activiti.ext.explorer;

import com.vaadin.ui.Component;
import org.activiti.explorer.ui.MainWindow;
import org.activiti.explorer.ui.mainlayout.ExplorerLayout;
import org.activiti.explorer.ui.mainlayout.MainLayout;

/**
 * Created by zhangquanhong on 2017/1/23.
 */
public class BlankWindow  extends MainWindow {

    public void showDefaultContent() {
        showingLoginPage = false;
        removeStyleName(ExplorerLayout.STYLE_LOGIN_PAGE);
        addStyleName("Default style"); // Vaadin bug: must set something or old style (eg. login page style) is not overwritten

        // init general look and feel
        mainLayout = new MainLayout(){
            @Override
            protected void initHeader() {
            }

//            @Override
//            protected void initMain() {
//            }

            @Override
            protected void initFooter() {
            }

//            @Override
//            protected void initMainMenuBar() {
//            }
        };
        setContent(mainLayout);

        // init hidden components
        initHiddenComponents();
    }

    public void switchView(Component component) {
        if(mainLayout == null) {
            showDefaultContent();
        }
        mainLayout.setMainContent(component);
    }

}
