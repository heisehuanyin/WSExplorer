package ws.editor.plugin.window;

import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;

import ws.editor.WsProcessor;
import ws.editor.plugin.ConfigPort;
import ws.editor.plugin.ContentView;
import ws.editor.plugin.FrontWindow;

public class FSBrowser extends AbstractWindow {
	private String gId;
	private WsProcessor core;
	private ContentView leftV = null;
	private ContentView rightV = null;
	private JSplitPane center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private ConfigPort cfgu = null;
	private String CFG_PATH = "./FSB.wscfg";
	
	
	
	
	@Override
	public FrontWindow openWindow(WsProcessor schedule, String gId) {
		FSBrowser win = new FSBrowser();
		
		win.gId = gId;
		win.core = schedule;
		win.cfgu = schedule.service_GetPluginManager().instance_GetConfigUnit(CFG_PATH);
		win.addWindowListener(new CommonWindowsListener(core));
		win.addComponentListener(new CommonComponentListener(core));
		
		return win;
	}

	@Override
	public String getGroupId() {
		return gId;
	}

	@Override
	public void closeView(ContentView comp) {
		if(leftV == comp) {
			this.center.remove(this.center.getLeftComponent());
			leftV = null;
		}
		
		if(rightV == comp) {
			this.center.remove(this.center.getRightComponent());
			rightV = null;
		}

	}

	@Override
	public ArrayList<? extends ContentView> getActivedViews() {
		ArrayList<ContentView> rtn = new ArrayList<>();
		
		if(leftV != null)
			rtn.add(leftV);
		if(rightV != null)
			rtn.add(rightV);
		
		return rtn;
	}

	@Override
	public JMenu getCustomMenu() {
		return new JMenu("Window");
	}

	@Override
	protected void placeView2(String viewTitle, ContentView comp) {
		

	}

	@Override
	public void saveOperation() {

	}

	@Override
	protected void service_ResetMenuBar2(JMenuBar mbar) {
		this.setJMenuBar(mbar);
	}

}
