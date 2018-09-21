package ws.editor.plugin.window;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import ws.editor.WsProcessor;
import ws.editor.comn.ItemsKey;
import ws.editor.comn.PluginFeature;
import ws.editor.plugin.ContentView;
import ws.editor.plugin.FrontWindow;

public abstract class AbstractWindow extends JFrame implements FrontWindow{

	@Override
	public int pluginMark() {
		return PluginFeature.UI_Window;
	}

	@Override
	public int upStreamMark() {
		return PluginFeature.IO_NoUpStream;
	}
	
	@Override
	public void service_ResetMenuBar(JMenuBar mbar) {
		this.service_ResetMenuBar2(mbar);
		this.validate();
	}
	
	protected abstract void service_ResetMenuBar2(JMenuBar mbar);
	
	@Override
	public void placeView(String viewTitle, ContentView comp) {
		this.placeView2(viewTitle, comp);
		this.validate();
	}
	protected abstract void placeView2(String viewTitle, ContentView comp);
	
	
	
	class CommonWindowsListener implements WindowListener{
		private WsProcessor wsp = null;

		public CommonWindowsListener(WsProcessor core) {
			this.wsp = core;
		}

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosing(WindowEvent e) {
			
		}

		@Override
		public void windowClosed(WindowEvent e) {
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}}
	
	
	class CommonComponentListener implements ComponentListener{
		private WsProcessor core;
		
		public CommonComponentListener(WsProcessor core) {
			this.core = core;
		}
		
		@Override
		public void componentResized(ComponentEvent e) {
			JFrame win = (JFrame) e.getSource();
			int w = win.getWidth();
			int h = win.getHeight();
			this.core.instance_GetMainConfigUnit().setKeyValue(ItemsKey.WindowWidth, ""+w);
			this.core.instance_GetMainConfigUnit().setKeyValue(ItemsKey.WindowHeight, ""+h);
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentShown(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void componentHidden(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
}
