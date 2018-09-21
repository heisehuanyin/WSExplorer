package ws.editor.comn.event;

public interface NodeEventListener {
	/**
	 * 当被监听节点插入子节点后，此函数被调用
	 * @param e 节点事件*/
	void nodeInsert(NodeGeneralEvent e);
	
	/**
	 * 当被监听节点移除子节点后，此函数被调用
	 * @param e 节点事件*/
	void nodeRemove(NodeGeneralEvent e);
	
	/**
	 * 当被监听节点完成修改属性，此函数被调用
	 * @param e 节点修改事件*/
	void nodeMidified(NodeModifyEvent e);
}
