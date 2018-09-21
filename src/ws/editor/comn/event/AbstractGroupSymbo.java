package ws.editor.comn.event;

import java.util.ArrayList;
import java.util.List;

import ws.editor.comn.GroupSymbo;
import ws.editor.comn.NodeSymbo;
import ws.editor.plugin.TreeModel;

public abstract class AbstractGroupSymbo extends AbstractNodeSymbo implements GroupSymbo{
	private ArrayList<NodeSymbo> con = new ArrayList<>();
	
	
	public AbstractGroupSymbo(TreeModel m) {
		super(m);
	}

	@Override
	public int kind() {
		return NodeSymbo.KindGroup;
	}

	@Override
	public int getChildCount() {
		return this.con.size();
	}

	@Override
	public NodeSymbo getChildAtIndex(int index) {
		return this.con.get(index);
	}
	
	@Override
	public int getChildIndex(NodeSymbo child) {
		return this.con.indexOf(child);
	}
	
	//=================================================

	@Override
	public void removeChild(NodeSymbo one) {
		this.con.remove(one);
		NodeGeneralEvent e = new NodeGeneralEvent(this,"移除子节点："+one.getClass().getName());
		this.pushRemoveEvent(e);
		this.removeChild_External(one);
		
		one.initParent(null);
	}
	protected abstract void removeChild_External(NodeSymbo one);

	private void pushRemoveEvent(NodeGeneralEvent e) {
		if(e.getSource() == this) {
			List<NodeEventListener> ls = this.getNodeEventListenerList();
			for(NodeEventListener l:ls) {
				l.nodeRemove(e);
			}
		}else {
			e.recordTreePath(this);
		}
		
		GroupSymbo x = this.getParent();
		if(x==null)
			return;
		((AbstractGroupSymbo)x).pushRemoveEvent(e);
	}

	@Override
	public void insertChildAtIndex(NodeSymbo node, int index) {
		this.con.add(index, node);
		NodeGeneralEvent e = new NodeGeneralEvent(this, "插入子节点："+ node.getClass().getName());
		this.pushInsertEvent(e);
		this.insertChildAtIndex_External(node, index);
		
		node.initParent(this);
	}
	protected abstract void insertChildAtIndex_External(NodeSymbo node, int index);

	
	private void pushInsertEvent(NodeGeneralEvent e) {
		if(e.getSource() == this) {
			List<NodeEventListener> ls= this.getNodeEventListenerList();
			for(NodeEventListener l:ls) {
				l.nodeInsert(e);
			}
		}else {
			e.recordTreePath(this);
		}
		
		GroupSymbo x = this.getParent();
		if(x == null)
			return;
		
		((AbstractGroupSymbo)x).pushInsertEvent(e);
	}
	
}
