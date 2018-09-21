package ws.editor.plugin;

import javax.swing.JPopupMenu;

import ws.editor.WsProcessor;
import ws.editor.comn.PluginFeature;

public interface TableModel extends PluginFeature , javax.swing.table.TableModel{
	/**
	 * 传入插件参数，打开新的{@link TableModel}实例
	 * @param core 核心模块
	 * @param upStream 上游模块
	 * @return 模型实例*/
	TableModel openNewTableModel(WsProcessor core, PluginFeature upStream);
	
	/**
	 * 获取针对每个单元格的弹出菜单
	 * @param row 行序
	 * @param col 列序
	 * @param owner TODO
	 * @return 弹出菜单*/
	JPopupMenu getPopupMenu(int row, int col, FrontWindow owner);
}
