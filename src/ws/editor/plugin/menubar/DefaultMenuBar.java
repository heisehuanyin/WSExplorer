package ws.editor.plugin.menubar;

import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import ws.editor.WsProcessor;
import ws.editor.comn.PluginFeature;
import ws.editor.plugin.MenuBar;

public class DefaultMenuBar extends JMenuBar implements MenuBar{
	private WsProcessor sch = null;

	@Override
	public int pluginMark() {
		return PluginFeature.UI_MenuBar;
	}
	@Override
	public int upStreamMark() {
		return PluginFeature.IO_NoUpStream;
	}
	
	

	@Override
	public JMenu getCustomMenu() {
		// TODO 创建自定义菜单
		return new JMenu(this.getClass().getName());
	}
	
	@Override
	public MenuBar getNewInstance(WsProcessor schedule) {
		DefaultMenuBar rtn = new DefaultMenuBar();
		rtn.sch = schedule;
		
		return rtn;
	}
	
	@Override
	public JMenuBar rebuildMenuBar(ArrayList<JMenu> elseMenus) {
		this.removeAll();
		if(elseMenus != null) {
			for(JMenu i:elseMenus) {
				this.add(i);
			}
		}
		return this;
	}
	
	

	@Override
	public void saveOperation() {
		
	}


}
