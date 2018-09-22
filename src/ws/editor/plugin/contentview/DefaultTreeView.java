package ws.editor.plugin.contentview;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ws.editor.WsProcessor;
import ws.editor.comn.GroupSymbo;
import ws.editor.comn.NodeSymbo;
import ws.editor.comn.PluginFeature;
import ws.editor.comn.event.NodeGeneralEvent;
import ws.editor.comn.event.NodeModifyEvent;
import ws.editor.plugin.ContentView;
import ws.editor.plugin.TreeModel;

public class DefaultTreeView extends AbstractTreeView {

	private TreeModel upstream = null;
	private WsProcessor core = null;
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("预设为不可见");
	private DefaultTreeModel tm = new DefaultTreeModel(root);
	private JTree tree = new JTree(tm);
	private JScrollPane sview = new JScrollPane(tree);

	private void initTreeView(NodeSymbo nodeSymbo) {
		nodeSymbo.addNodeEventListener(this);
		TreeCellRenderer xx = tree.getCellRenderer();
		((DefaultTreeCellRenderer)xx).setLeafIcon(((DefaultTreeCellRenderer)xx).getClosedIcon());
		tree.setCellRenderer(xx);

		root.setAllowsChildren(true);
		tm.setAsksAllowsChildren(true);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addMouseListener(new JTreeMouseListener(tree));

		this.loopInsert(root, nodeSymbo);
	}

	private void loopInsert(DefaultMutableTreeNode parent, NodeSymbo dnode) {

		CustomNode xa = new CustomNode(dnode);
		this.tm.insertNodeInto(xa, parent, parent.getChildCount());

		TreeNode[] nodes = this.tm.getPathToRoot(xa);
		TreePath path = new TreePath(nodes);
		tree.makeVisible(path);

		if (dnode.kind() == NodeSymbo.KindGroup) {
			GroupSymbo gnode = (GroupSymbo) dnode;
			for (int i = 0; i < gnode.getChildCount(); ++i) {
				NodeSymbo xx = gnode.getChildAtIndex(i);
				this.loopInsert(xa, xx);
			}
		}
	}

	@Override
	public ContentView openContentView(WsProcessor core, PluginFeature upStream) {
		DefaultTreeView rtn = new DefaultTreeView();

		rtn.upstream = (TreeModel) upStream;
		rtn.core = core;

		rtn.initTreeView(rtn.upstream.getNodeSymbo());

		return rtn;
	}

	@Override
	public Component getView() {
		// TODO Auto-generated method stub
		return sview;
	}

	@Override
	public JMenu getCustomMenu() {
		return new JMenu("TreeView");
	}

	@Override
	public void saveOperation() {

	}

	@Override
	public void nodeInsert(NodeGeneralEvent e) {
		NodeSymbo x = e.getSource();
		GroupSymbo xp = x.getParent();
		int index = xp.getChildIndex(x);
		CustomNode node = new CustomNode(x);

		DefaultMutableTreeNode p = this.loopAndSearchTreeNodeFrontNodeEvent(root, e);
		if (p == null) {
			this.core.instance_GetDefaultLogPort().errorLog(this, "TreeModel插入节点事件包含着一个错误路径，" + "无法定位插入节点，我选择崩溃。");
			System.exit(0);
		}

		this.tm.insertNodeInto(node, p, index);

		TreeNode[] nodes = this.tm.getPathToRoot(node);
		TreePath path = new TreePath(nodes);
		tree.scrollPathToVisible(path);
	}

	private DefaultMutableTreeNode loopAndSearchTreeNodeFrontNodeEvent(DefaultMutableTreeNode parent,
			NodeGeneralEvent e) {

		for (int i = 0; i < parent.getChildCount(); ++i) {
			CustomNode one = (CustomNode) parent.getChildAt(i);
			NodeSymbo nodeSymbo = one.getNodeSymbo();
			if (e.nodeContain(nodeSymbo)) {
				if (e.getSource() == nodeSymbo)
					return one;

				return this.loopAndSearchTreeNodeFrontNodeEvent(one, e);
			}

		}

		return null;
	}

	@Override
	public void nodeRemove(NodeGeneralEvent e) {
		NodeSymbo x = e.getSource();

		DefaultMutableTreeNode pSNode = this.loopAndSearchTreeNodeFrontNodeEvent(root, e);
		for (int i = 0; i < pSNode.getChildCount(); ++i) {
			CustomNode one = (CustomNode) pSNode.getChildAt(i);
			if (one.getNodeSymbo() == e.getSource()) {
				this.tm.removeNodeFromParent(one);
				return;
			}
		}
	}

	@Override
	public void nodeMidified(NodeModifyEvent e) {
		CustomNode s = (CustomNode) this.loopAndSearchTreeNodeFrontNodeEvent(root, e);

		if (!s.refreshNodeState(e))
			return;

		this.tm.nodeChanged(s);
	}

	private class CustomNode extends DefaultMutableTreeNode {

		private NodeSymbo node;

		/**
		 * 新建自定义的树节点，节点UI由此定义
		 * 
		 * @param node
		 *            本节点的绑定数据树节点
		 */
		public CustomNode(NodeSymbo node) {
			super(node.getValue(NodeSymbo.NODENAME_KEY));
			this.node = node;
			this.setAllowsChildren((node.kind() == NodeSymbo.KindGroup) ? true : false);
		}

		/**
		 * 获取本节点绑定的数据树节点，多用于判定视图树与数据树的同步问题
		 * 
		 * @return 数据树节点
		 */
		public NodeSymbo getNodeSymbo() {
			return this.node;
		}

		/**
		 * 根据{@link NodeModifyEvent}信息，自由判断是否刷新，并返回操作结果指示
		 * 
		 * @param e
		 *            数据树节点修改事件
		 * @return 刷新结果，true本节点已刷新，false本节点未刷新
		 */
		public boolean refreshNodeState(NodeModifyEvent e) {
			if (!e.getModifiedKey().equals(NodeSymbo.NODENAME_KEY))
				return false;
			String title = this.node.getValue(e.getModifiedKey());
			this.setUserObject(title);
			return true;
		}
	}

	private class JTreeMouseListener implements MouseListener {

		private JTree holder;

		public JTreeMouseListener(JTree t) {
			this.holder = t;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			TreePath path = holder.getPathForLocation(e.getX(), e.getY()); // 关键是这个方法的使用
			if (path == null) { // JTree上没有任何项被选中
				return;
			}
			holder.setSelectionPath(path);
			
			if(e.getButton() == 3) {
				Object x = this.holder.getLastSelectedPathComponent();
				if ((x == null) || !(x instanceof CustomNode)) {
					return;
				}
				CustomNode x2 = (CustomNode) x;
				JPopupMenu popM = x2.getNodeSymbo().getPopupMenu(null);
				
				this.holder.setComponentPopupMenu(popM);
				popM.show(holder, e.getX(), e.getY());
			}

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}
}
