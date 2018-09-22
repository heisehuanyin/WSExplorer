package ws.editor.plugin.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import ws.editor.WsProcessor;
import ws.editor.comn.ItemsKey;
import ws.editor.comn.PluginFeature;
import ws.editor.plugin.ConfigPort;
import ws.editor.plugin.ContentView;
import ws.editor.plugin.FrontWindow;

public class FSBrowser extends AbstractWindow {
	private String gId;
	private WsProcessor core;
	private ContentView leftV = null;
	private ContentView rightV = null;
	private JSplitPane center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JPanel(), new JPanel());
	private ConfigPort cfgu = null;
	private String CFG_PATH = "./FSB.wscfg";

	private String PREFIX_VIEWPOSITION = "view_position.";
	private int VPOSITION_LEFT = 0;
	private int VPOSITION_RIGHT = 1;
	private String LEFTVISIBLE_CTRL = "LeftVisible_Ctrl";
	private String LEFT_WIDTH_KEY = "LeftWidth.DiverderLocation";
	private JCheckBoxMenuItem leftVisible = new JCheckBoxMenuItem("左视图可见");

	@Override
	public FrontWindow openWindow(WsProcessor schedule, String gId) {
		FSBrowser win = new FSBrowser();

		win.gId = gId;
		win.core = schedule;
		win.cfgu = schedule.service_GetPluginManager().instance_GetConfigUnit(CFG_PATH);
		win.addWindowListener(new CommonWindowsListener(win.core));
		win.addComponentListener(new CommonComponentListener(win.core));
		win.customWindow();

		return win;
	}

	private void customWindow() {
		this.setTitle("WSExplorer - " + this.gId + " ");
		this.core.service_Refresh_MenuBar(this);

		// WindowSize====================
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		String wStr = this.core.instance_GetMainConfigUnit().getValue(ItemsKey.WindowWidth, "800");
		String hStr = this.core.instance_GetMainConfigUnit().getValue(ItemsKey.WindowHeight, "600");
		this.setSize(Integer.parseInt(wStr), Integer.parseInt(hStr));

		// CustomSplitPane==============
		this.customJSplitPane(this.center);
		String leftLocation = this.cfgu.getValue(this.LEFT_WIDTH_KEY, "80");
		this.center.setDividerLocation(Integer.parseInt(leftLocation));

		// LeftViewVisible=============
		String vctrl = this.core.instance_GetMainConfigUnit().getValue(this.LEFTVISIBLE_CTRL, "true");
		boolean vcb = Boolean.parseBoolean(vctrl);
		this.leftVisible.setState(vcb);
		if (!this.leftVisible.getState() && this.rightV != null)
			this.getContentPane().add(new JPanel(), BorderLayout.CENTER);
		else
			this.getContentPane().add(this.center, BorderLayout.CENTER);

		this.leftVisible.addItemListener(
				new ViewCubeControl(leftVisible, this.center, this.LEFT_WIDTH_KEY));

		this.setVisible(true);
	}

	private void customJSplitPane(JSplitPane splitp) {
		SplitPaneUI sp = splitp.getUI();
		BasicSplitPaneDivider dr = ((BasicSplitPaneUI) sp).getDivider();
		dr.setBorder(new LineBorder(Color.gray, 1, false));
		splitp.setDividerSize(4);
		splitp.setBorder(new EmptyBorder(0, 0, 0, 0));
	}

	@Override
	public String getGroupId() {
		return gId;
	}

	@Override
	public void closeView(ContentView comp) {
		if (leftV == comp) {
			this.center.remove(this.center.getLeftComponent());
			leftV = null;
		}

		if (rightV == comp) {
			this.center.remove(this.center.getRightComponent());
			rightV = null;
		}

	}

	@Override
	public ArrayList<? extends ContentView> getActivedViews() {
		ArrayList<ContentView> rtn = new ArrayList<>();

		if (leftV != null)
			rtn.add(leftV);
		if (rightV != null)
			rtn.add(rightV);

		return rtn;
	}

	@Override
	public JMenu getCustomMenu() {
		JMenu x = new JMenu("Window");
		x.add(leftVisible);

		return x;
	}

	@Override
	protected void placeView2(String viewTitle, ContentView comp) {
		if (comp == this.leftV || comp == this.rightV)
			return;
		
		int lwidth = this.center.getDividerLocation();

		String position = this.cfgu.getValue(this.PREFIX_VIEWPOSITION + comp.getClass().getName(),
				"" + this.VPOSITION_RIGHT);

		if (Integer.parseInt(position) == this.VPOSITION_LEFT) {
			this.center.setLeftComponent(comp.getView());
			this.leftV = comp;
		}
		if (Integer.parseInt(position) == this.VPOSITION_RIGHT) {
			this.center.setRightComponent(comp.getView());
			this.rightV = comp;
		}
		
		this.center.setDividerLocation(lwidth);

	}

	@Override
	public void saveOperation() {
		int w = this.center.getDividerLocation();
		this.cfgu.setKeyValue(this.LEFT_WIDTH_KEY, "" + w);
		this.cfgu.setKeyValue(this.LEFTVISIBLE_CTRL, "" + this.leftVisible.getState());
	}

	@Override
	protected void service_ResetMenuBar2(JMenuBar mbar) {
		this.setJMenuBar(mbar);
	}

	private class ViewCubeControl implements ItemListener {
		private JSplitPane split = null;
		private JCheckBoxMenuItem t = null;
		private String widthCtrlKey = null;

		public ViewCubeControl(JCheckBoxMenuItem t, Component jsplit, String widthCtrl_Key) {
			this.t = t;
			this.split = (JSplitPane) jsplit;
			this.widthCtrlKey = widthCtrl_Key;
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			int state = e.getStateChange();

			int lw = split.getDividerLocation();

			if (state == ItemEvent.SELECTED) {
				Component[] cons = FSBrowser.this.getContentPane().getComponents();
				for (Component c : cons) {
					if (rightV != null && c == rightV.getView())
						FSBrowser.this.remove(c);
				}
				FSBrowser.this.getContentPane().add(split, BorderLayout.CENTER);

				split.setRightComponent(rightV==null?new JPanel():rightV.getView());
				split.setDividerLocation(lw);

			} else {
				Component[] cons = FSBrowser.this.getContentPane().getComponents();
				for (Component c : cons) {
					if (c == split)
						FSBrowser.this.remove(c);
				}
				FSBrowser.this.getContentPane().add(rightV==null?new JPanel():rightV.getView(), BorderLayout.CENTER);

			}

			FSBrowser.this.validate();
		}
	}
}
