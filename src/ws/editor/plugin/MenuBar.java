package ws.editor.plugin;

import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import ws.editor.WsProcessor;
import ws.editor.comn.PluginFeature;

public interface MenuBar extends PluginFeature {

	/**
	 * 获取新的空白实例
	 * @param schedule 调度模块
	 * @return 返回新实例*/
	public abstract MenuBar getNewInstance(WsProcessor schedule);

	/**
	 * 构建MenuBar实例
	 * @param elseMenus 刷新过程中将额外菜单加入MenuBar
	 * @return 返回合适的实例
	 * */
	public abstract JMenuBar rebuildMenuBar(ArrayList<JMenu> elseMenus);
}
