package ws.editor.comn.event;

import ws.editor.comn.NodeSymbo;

public class NodeModifyEvent extends NodeGeneralEvent{
	private String mKey;

	public NodeModifyEvent(String modifiedKey, NodeSymbo obj, String exmsg) {
		super(obj, exmsg);
		this.mKey = modifiedKey;
	}

	public String getModifiedKey() {
		return this.mKey;
	}
}
