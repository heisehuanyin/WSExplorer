package ws.editor.plugin.treemodel;

import java.io.File;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import ws.editor.WsProcessor;
import ws.editor.comn.NodeSymbo;
import ws.editor.comn.event.AbstractGroupSymbo;
import ws.editor.comn.event.AbstractNodeSymbo;
import ws.editor.plugin.FrontWindow;
import ws.editor.plugin.TreeModel;

public class DirectoryView extends AbstractProjectModel {

	private WsProcessor core;
	private String pjtp;
	private ParentDir root;

	@Override
	public NodeSymbo getNodeSymbo() {
		return this.root;
	}

	@Override
	public JMenu getCustomMenu() {
		return new JMenu("FSB");
	}

	@Override
	public void saveOperation() {
		// TODO Auto-generated method stub

	}

	@Override
	protected TreeModel openProject(WsProcessor core, String pjtPath) {
		DirectoryView x = new DirectoryView();
		
		x.core = core;
		x.pjtp = pjtPath;
		x.root = new ParentDir(x);
		x.root.setKeyValue(NodeSymbo.NODENAME_KEY, "系统文件树");
		
		File[] roots = File.listRoots();
		for(File node:roots) {
			ParentDir pn = new ParentDir(x);
			pn.setKeyValue(NodeSymbo.NODENAME_KEY, node.getName());
			try {
				pn.setKeyValue(ParentDir.PATH_KEY, node.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			x.root.insertChildAtIndex(pn, x.root.getChildCount());
		}
		
		return x;
	}

	class ParentDir extends AbstractGroupSymbo {
		private static final String PATH_KEY = "PATH_KEY";

		public ParentDir(TreeModel m) {
			super(m);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void doAction(FrontWindow owner) {
			String path = this.getValue(this.PATH_KEY);
			core.service_OpenFile(path, owner);
		}

		@Override
		public JPopupMenu getPopupMenu(FrontWindow owner) {
			return new JPopupMenu();
		}

		@Override
		protected void removeChild_External(NodeSymbo one) {
			String p = one.getValue(PATH_KEY);
			new File(p).delete();
		}

		@Override
		protected void insertChildAtIndex_External(NodeSymbo node, int index) {
			new File(node.getValue(PATH_KEY)).mkdir();
		}

		@Override
		protected void setKeyValue_Exteral(String key, String value) {
			if(key.equals(NodeSymbo.NODENAME_KEY)) {
				File x = new File(this.getValue(PATH_KEY));
				x.renameTo(new File(x.getParent() + File.separator + value));
			}
		}

		@Override
		public void GroupExplanded() {
			
		}

		@Override
		public void GroupFolded() {
			// TODO Auto-generated method stub
			
		}

	}

	class ChildDir extends AbstractNodeSymbo {
		private static final String PATH_KEY = "PATH_KEY";

		public ChildDir(TreeModel m) {
			super(m);
		}

		@Override
		public void doAction(FrontWindow owner) {
			// TODO Auto-generated method stub

		}

		@Override
		public JPopupMenu getPopupMenu(FrontWindow owner) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void setKeyValue_Exteral(String key, String value) {
			// TODO Auto-generated method stub

		}

	}

}
