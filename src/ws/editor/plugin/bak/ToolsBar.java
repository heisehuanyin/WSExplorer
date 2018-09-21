package ws.editor.plugin.bak;

import ws.editor.WsProcessor;
import ws.editor.comn.PluginFeature;
import ws.editor.plugin.MenuBar;

public interface ToolsBar extends PluginFeature{

	/**
	 * 获取新实例，用于构架界面
	 * @param wsProcessor 核心调度模块
	 * @param string 实例id
	 * @return ToolsBar实例*/
	ToolsBar getInstance(WsProcessor wsProcessor, String string);

	/**
	 * 根据配置文件记载，利用menubar中的菜单作为来源，构建toolsbar按钮
	 * @param menubar 按钮来源
	 * */
	void buildToolsBarContent(MenuBar menubar);

}
