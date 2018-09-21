package ws.editor.comn.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ws.editor.comn.GroupSymbo;
import ws.editor.comn.NodeSymbo;
import ws.editor.plugin.TreeModel;

public abstract class AbstractNodeSymbo implements NodeSymbo{
	private List<NodeEventListener> ls = new ArrayList<>();
	private GroupSymbo parent = null;
	private Map<String,String> contents = new HashMap<String,String>();
	private TreeModel model = null;

	public AbstractNodeSymbo(TreeModel m) {
		this.model = m;
	}
	
	@Override
	public void setKeyValue(String key, String value) {
		this.contents.put(key, value);
		NodeModifyEvent e = new NodeModifyEvent(key, this, "修改了节点的属性：" + key);
		this.pushModifyEvent(e);
	}
	/**
	 * 在修改节点属性值的时候被自动调用，节点属性值填充工作由Abstract类自动完成，本方法可以用来引发一些额外工作。
	 * @param key 节点属性键
	 * @param value 节点属性值*/
	protected abstract void setKeyValue_Exteral(String key, String value);

	//====================================
	@Override
	public TreeModel getModel() {
		return this.model;
	}
	@Override
	public String getKey(int i) {
		if(i >= this.contents.size())
			return null;
		
		Set<String> keyS = this.contents.keySet();
		ArrayList<String> KA = new ArrayList<String>(keyS);
		
		return KA.get(i);
	}
	@Override
	public String getValue(String key) {
		return this.contents.get(key);
	}
	@Override
	public int kind() {
		return NodeSymbo.KindNode;
	}
	@Override
	public void initParent(GroupSymbo parent) {
		this.parent = parent;
	}
	@Override
	public GroupSymbo getParent() {
		return this.parent;
	}
	
	/**
	 * 向所有监听器推送节点修改事件
	 * @param e 修改事件*/
	private void pushModifyEvent(NodeModifyEvent e) {
		if(e.getSource() == this) {
			for(NodeEventListener l:ls) {
				l.nodeMidified(e);
			}
		}else {
			e.recordTreePath(this);
		}
		
		NodeSymbo x = this.getParent();
		if(x == null)
			return;
		((AbstractNodeSymbo)x).pushModifyEvent(e);
	}
	
	/**
	 * 获取节点上所有的监听器，主要给AbstractGroupSymbo使用，沟通两个继承类
	 * @return 所有监听器*/
	protected List<NodeEventListener> getNodeEventListenerList() {
		return this.ls;
	}
	

	@Override
	public void addNodeEventListener(NodeEventListener l) {
		if(l==null)
			return;
		this.ls.add(l);
	}
	
}
