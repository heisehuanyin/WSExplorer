package ws.editor.plugin.treemodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import ws.editor.WsProcessor;
import ws.editor.comn.GroupSymbo;
import ws.editor.comn.NodeSymbo;
import ws.editor.comn.event.AbstractGroupSymbo;
import ws.editor.comn.event.AbstractNodeSymbo;
import ws.editor.plugin.FrontWindow;
import ws.editor.plugin.TreeModel;

public class DefaultProjectModel extends AbstractProjectModel {

	public final static String XML_ATTR_NODENAME = "name";
	public static final String XML_ATTR_NODEFILEPATH = "filepath";
	public static final String XML_ATTR_FILEPATH_PREFIX = "projectdir";
	public static final String XML_ATTR_NODEENCODING = "encoding";

	public static final String XML_NODE_TAGNAME = "Node";
	public static final String XML_GROUP_TAGNAME = "Group";

	private WsProcessor core;
	private String pPath;
	private Document doc;
	private SimpleGroupNode mNode;

	/**
	 * 解析document节点，生成完整{@link NodeSymbo} 树内容
	 * 
	 * @param doc
	 *            文档节点
	 */
	private void parseDom4Model(Document doc) {
		this.doc = doc;

		Element root = doc.getDocumentElement();
		root.setAttribute(DefaultProjectModel.XML_ATTR_NODEFILEPATH, 
				new File(this.pPath).getParent() + File.separator + "ProjectInfo.txt");
		this.mNode = new SimpleGroupNode(this, root);
		this.mNode.setKeyValue(SimpleFileNode.NODENAME_KEY, root.getAttribute(DefaultProjectModel.XML_ATTR_NODENAME));
		this.mNode.setKeyValue(SimpleFileNode.FILEPATH, root.getAttribute(DefaultProjectModel.XML_ATTR_NODEFILEPATH));
		this.mNode.setKeyValue(SimpleFileNode.FILEENCODING,
				root.getAttribute(DefaultProjectModel.XML_ATTR_NODEENCODING));

		this.loop4ChildNodeAndContent(mNode, root);
	}

	/**
	 * 根据DOM内容信息，迭代添加母节点下每个节点下子节点
	 * 
	 * @param node
	 *            母节点
	 * @param domNode
	 *            穆姐点对应{@link Element}节点
	 */
	private void loop4ChildNodeAndContent(NodeSymbo node, Element domNode) {
		if (node.kind() == NodeSymbo.KindNode)
			return;

		NodeList nList = domNode.getChildNodes();
		for (int i = 0; i < nList.getLength(); ++i) {
			Node one = nList.item(i);
			if (!(one instanceof Element)) {
				continue;
			}

			String tag = ((Element) one).getTagName();
			NodeSymbo x = null;
			if (tag.equals(DefaultProjectModel.XML_GROUP_TAGNAME)) {
				x = new SimpleGroupNode(this, ((Element) one));
			} else if (tag.equals(DefaultProjectModel.XML_NODE_TAGNAME)) {
				x = new SimpleFileNode(this, ((Element) one));
			} else {
				this.core.instance_GetDefaultLogPort().errorLog(this, "项目文件中发现未知节点：" + tag + ",无法解析，我选择崩溃。");
				System.exit(0);
			}

			x.setKeyValue(NodeSymbo.NODENAME_KEY, ((Element) one).getAttribute(DefaultProjectModel.XML_ATTR_NODENAME));

			// Path translate=========================================================
			String caseStr = ((Element) one).getAttribute(DefaultProjectModel.XML_ATTR_NODEFILEPATH);
			String truePath = new File(this.pPath).getParent()
					+ caseStr.substring(this.XML_ATTR_FILEPATH_PREFIX.length());

			x.setKeyValue(SimpleFileNode.FILEPATH, truePath);
			x.setKeyValue(SimpleFileNode.FILEENCODING,
					((Element) one).getAttribute(DefaultProjectModel.XML_ATTR_NODEENCODING));

			((GroupSymbo) node).insertChildAtIndex(x, ((GroupSymbo) node).getChildCount());

			this.loop4ChildNodeAndContent(x, (Element) one);
		}
	}

	@Override
	protected TreeModel openProject(WsProcessor core, String pjtPath) {
		DefaultProjectModel rtn = new DefaultProjectModel();
		rtn.core = core;
		rtn.pPath = pjtPath;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(rtn.pPath);

			rtn.parseDom4Model(doc);

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return rtn;
	}

	@Override
	public NodeSymbo getNodeSymbo() {
		return this.mNode;
	}

	@Override
	public JMenu getCustomMenu() {
		return new JMenu("ProjectModel");
	}

	@Override
	public void saveOperation() {
		DOMImplementation impl = doc.getImplementation();
		DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
		LSSerializer ser = implLS.createLSSerializer();

		LSOutput out = implLS.createLSOutput();
		out.setEncoding("UTF-8");
		try {
			out.setByteStream(new FileOutputStream(this.pPath));
			ser.write(doc, out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class SimpleGroupNode extends AbstractGroupSymbo {

		private Element elm;

		public SimpleGroupNode(TreeModel m, Element elm) {
			super(m);
			this.elm = elm;
		}

		@Override
		public JPopupMenu getPopupMenu(FrontWindow owner) {
			JPopupMenu rtn = new JPopupMenu("DefaultProjectModel");

			rtn.add("Item_Group");
			rtn.add("Item_Group");
			rtn.add("Item_Group");

			return rtn;
		}

		@Override
		protected void removeChild_External(NodeSymbo one) {
			NodeList x = this.elm.getChildNodes();
			for (int i = 0; i < x.getLength(); ++i) {
				if (((SimpleFileNode) one).getBaseElement() == x.item(i)) {
					this.elm.removeChild(x.item(i));
				}
			}
		}

		@Override
		protected void insertChildAtIndex_External(NodeSymbo node, int index) {
			NodeList x = this.elm.getChildNodes();
			int i = 0;
			for (i = 0; i < x.getLength(); ++i) {
				if ((node instanceof SimpleFileNode) && ((SimpleFileNode) node).getBaseElement() == x.item(i))
					return;
				if ((node instanceof SimpleGroupNode) && ((SimpleGroupNode) node).getBaseElement() == x.item(i))
					return;
			}
			Document doc = this.elm.getOwnerDocument();
			Element newone = doc
					.createElement((node.kind() == NodeSymbo.KindGroup) ? DefaultProjectModel.XML_GROUP_TAGNAME
							: DefaultProjectModel.XML_NODE_TAGNAME);

			newone.setAttribute(DefaultProjectModel.XML_ATTR_NODENAME, node.getValue(SimpleFileNode.NODENAME_KEY));
			newone.setAttribute(DefaultProjectModel.XML_ATTR_NODEFILEPATH, node.getValue(SimpleFileNode.FILEPATH));
			newone.setAttribute(DefaultProjectModel.XML_ATTR_NODEENCODING, node.getValue(SimpleFileNode.FILEENCODING));

			this.elm.appendChild(newone);
		}

		/**
		 * 本类基于Dom作为基础模型，此函数获取基础模型
		 * 
		 * @return 内部基础模型
		 */
		public Element getBaseElement() {
			return this.elm;
		}

		@Override
		protected void setKeyValue_Exteral(String key, String value) {
			if (key.equals(NodeSymbo.NODENAME_KEY)) {
				this.elm.setAttribute(DefaultProjectModel.XML_ATTR_NODENAME, value);
			}
			if (key.equals(SimpleFileNode.FILEPATH)) {
				this.elm.setAttribute(DefaultProjectModel.XML_ATTR_NODEFILEPATH, value);
			}
			if (key.equals(SimpleFileNode.FILEENCODING)) {
				this.elm.setAttribute(DefaultProjectModel.XML_ATTR_NODEENCODING, value);
			}
		}

		@Override
		public void doAction(FrontWindow owner) {
			// TODO Auto-generated method stub

		}

	}

	class SimpleFileNode extends AbstractNodeSymbo {

		public static final String FILEENCODING = "Encoding";
		public static final String FILEPATH = "FilePath";

		private Element elm;

		public SimpleFileNode(TreeModel owner, Element elm) {
			super(owner);
			this.elm = elm;
		}

		@Override
		public JPopupMenu getPopupMenu(FrontWindow owner) {
			JPopupMenu rtn = new JPopupMenu("DefaultProjectModel");

			rtn.add("Item_Node");
			rtn.add("Item_Node");
			rtn.add("Item_Node");

			return rtn;
		}

		/**
		 * 本类基于Dom作为基础模型，此函数获取基础模型
		 * 
		 * @return 内部基础模型
		 */
		public Element getBaseElement() {
			return this.elm;
		}

		@Override
		protected void setKeyValue_Exteral(String key, String value) {
			if (key.equals(NodeSymbo.NODENAME_KEY)) {
				this.elm.setAttribute(DefaultProjectModel.XML_ATTR_NODENAME, value);
			}
			if (key.equals(SimpleFileNode.FILEPATH)) {
				String caseStr = value.replaceFirst(new File(pPath).getParent(),
						DefaultProjectModel.XML_ATTR_FILEPATH_PREFIX);
				this.elm.setAttribute(DefaultProjectModel.XML_ATTR_NODEFILEPATH, caseStr);
			}
			if (key.equals(SimpleFileNode.FILEENCODING)) {
				this.elm.setAttribute(DefaultProjectModel.XML_ATTR_NODEENCODING, value);
			}
		}

		@Override
		public void doAction(FrontWindow owner) {
			core.service_OpenFile(this.getValue(SimpleFileNode.FILEPATH), owner);
		}

	}
}
