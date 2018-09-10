package com.ruanko.view;

import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import com.ruanko.service.UpdateThread;

public class Start {
    public static void main(String[] args) {
    	try {
    		org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
    		BeautyEyeLNFHelper.frameBorderStyle=BeautyEyeLNFHelper.FrameBorderStyle.translucencyAppleLike;
    		UIManager.put("RootPane.setupButtonVisible",false);
    		
    		//自定义JToolBar ui的border
    		Border bd = new org.jb2011.lnf.beautyeye.ch8_toolbar.BEToolBarUI.ToolBarBorder(
    		        UIManager.getColor("ToolBar.shadow")     //Floatable时触点的颜色
    		        , UIManager.getColor("ToolBar.highlight")//Floatable时触点的阴影颜色
    		        , new Insets(0, 0, 0, 0));              //border的默认insets
    		UIManager.put("ToolBar.border",new BorderUIResource(bd));
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
        JMainFrame frame = new JMainFrame();
        frame.setVisible(true);

        UpdateThread updateThread = new UpdateThread();
        updateThread.run();
    }
}
