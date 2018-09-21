package ws.editor.plugin.tablemodel;

import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.event.TableModelListener;

import ws.editor.WsProcessor;
import ws.editor.comn.PluginFeature;
import ws.editor.plugin.FrontWindow;
import ws.editor.plugin.TableModel;
import ws.editor.plugin.TextModel;

public class DefaultCSVModel extends AbstractCSVModel{
	private ArrayList<TableModelListener> lcon = new ArrayList<>();
	private WsProcessor core ;
	private TextModel upS;
	private ArrayList<ArrayList<String>> tcon = new ArrayList<>();
	private int col_MaxNum = 0;

	private void parserTextModel(TextModel upS2) {
		int lineC = upS2.getRowsCount();
		
		for(int i=0;i<lineC;++i) {
			String line = upS2.getLine(i);
			String[] vals = line.split(",");
			
			ArrayList<String> line_val = new ArrayList<>();
			for(String v:vals) {
				line_val.add(v);
			}
			
			if(this.col_MaxNum < line_val.size())
				this.col_MaxNum = line_val.size();
			
			this.tcon.add(line_val);
		}
	}
	
	@Override
	public TableModel openNewTableModel(WsProcessor core, PluginFeature upStream) {
		DefaultCSVModel x = new DefaultCSVModel();
		x.core = core;
		x.upS = (TextModel) upStream;
		
		x.parserTextModel(x.upS);
		
		return x;
	}


	@Override
	public JPopupMenu getPopupMenu(int row, int col, FrontWindow owner) {
		return new JPopupMenu("null");
	}

	@Override
	public JMenu getCustomMenu() {
		return new JMenu("CSVTable");
	}

	@Override
	public void saveOperation() {
			this.upS.removeLines(0);
			
		for(ArrayList<String> row:this.tcon) {
			String lineContent = "";
			
			for(String val:row) {
				lineContent += val + ",";
			}
			
			this.upS.insertLine(this.upS.getRowsCount(), lineContent);
		}
	}

	@Override
	public int getRowCount() {
		return this.tcon.size();
	}

	@Override
	public int getColumnCount() {
		return this.col_MaxNum;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return ""+columnIndex;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return this.tcon.get(rowIndex).get(columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		this.tcon.get(rowIndex).set(columnIndex, (String) aValue);
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		this.lcon.add(l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		this.lcon.remove(l);
	}

}
