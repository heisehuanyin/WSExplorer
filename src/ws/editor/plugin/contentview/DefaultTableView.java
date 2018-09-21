package ws.editor.plugin.contentview;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import ws.editor.WsProcessor;
import ws.editor.comn.PluginFeature;
import ws.editor.plugin.ContentView;
import ws.editor.plugin.TableModel;

public class DefaultTableView extends AbstractTableView{
	private TableModel upS;
	private WsProcessor core;
	private JTable table = new JTable();
	private JScrollPane jsp = new JScrollPane(table);

	@Override
	public ContentView openContentView(WsProcessor core, PluginFeature upStream) {
		DefaultTableView x = new DefaultTableView();
		
		x.upS = (TableModel) upStream;
		x.core = core;
		x.table.setModel(x.upS);
		
		return x;
	}

	@Override
	public Component getView() {
		return this.jsp;
	}

	@Override
	public JMenu getCustomMenu() {
		return new JMenu("TableView");
	}

	@Override
	public void saveOperation() {
		
	}

}
