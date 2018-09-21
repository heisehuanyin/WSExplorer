package ws.editor.comn.event;

import java.util.ArrayList;

import ws.editor.comn.NodeSymbo;

public class NodeGeneralEvent {

	private NodeSymbo source;
	private String msg;
	private ArrayList<NodeSymbo> ePath = new ArrayList<>();

	public NodeGeneralEvent(NodeSymbo obj, String msg) {
		this.source = obj;
		this.msg = msg;
	}

	public NodeSymbo getSource() {
		return this.source;
	}

	public String getMsg() {
		return this.msg;
	}

	public void recordTreePath(NodeSymbo obj) {
		this.ePath .add(0, obj);
	}

	public boolean nodeContain(NodeSymbo obj) {
		return this.ePath.contains(obj);
	}

}