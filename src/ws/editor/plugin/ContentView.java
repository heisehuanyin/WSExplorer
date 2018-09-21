package ws.editor.plugin;

import java.awt.Component;

import ws.editor.WsProcessor;
import ws.editor.comn.PluginFeature;

public interface ContentView extends PluginFeature{
	/**
	 * 获取一个新的实例
	 * @param core 核心模块
	 * @param upStream 上游模块
	 * @param gId TODO
	 * @return 返回的视图实例*/
	ContentView openContentView(WsProcessor core, PluginFeature upStream);
	
	/**
	 * 返回一个实际工作的swing组件
	 * @return swing组件*/
	Component getView();
	
	/**
	 * 设置GroupId，本函数被框架调用，当视图被放置到窗口时候自动设置,其他人不要调用
	 * @param gId GroupId*/
	void __setGroupId(String gId);
	
	/**
	 * 获取组件绑定的GroupId，可以用来获取父FrontWindow
	 * @return GroupId*/
	String getGroupId();
}
