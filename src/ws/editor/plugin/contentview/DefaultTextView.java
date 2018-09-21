package ws.editor.plugin.contentview;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ws.editor.WsProcessor;
import ws.editor.comn.PluginFeature;
import ws.editor.plugin.ContentView;
import ws.editor.plugin.TextModel;

public class DefaultTextView extends AbstractTextView {
	private JTextArea view = new JTextArea();
	private JScrollPane sview = new JScrollPane(view);
	private TextModel upStream ;
	private WsProcessor core;

	private void loadAllContent(TextModel source) {
		
		int rc = source.getRowsCount();
		for(int i=0;i<rc;++i) {
			this.view.append(source.getLine(i));
			this.view.append("\n");
		}
	}
	
	@Override
	public ContentView openContentView(WsProcessor core, PluginFeature upStream) {
		DefaultTextView rtn = new DefaultTextView();
		
		rtn.upStream = (TextModel) upStream;
		rtn.core = core;
		
		rtn.loadAllContent((TextModel) upStream);
		
		return rtn;
	}

	@Override
	public Component getView() {
		return this.sview;
	}

	@Override
	public JMenu getCustomMenu() {
		return new JMenu("TextView");
	}

	@Override
	public void saveOperation() {
		String tc = this.view.getText();
		String[] lines = tc.split("\n");
		this.upStream.removeLines(0);
		for(String line:lines) {
			this.upStream.insertLine(this.upStream.getRowsCount(), line);
		}
	}

}
