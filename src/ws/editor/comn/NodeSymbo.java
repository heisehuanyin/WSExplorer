package ws.editor.comn;

import javax.swing.JPopupMenu;

import ws.editor.comn.event.NodeEventListener;
import ws.editor.plugin.FrontWindow;
import ws.editor.plugin.TreeModel;

/**
 * 本接口用于TreeModel，自定义类型，主要拥有以下功能<br>
 * 1.构建树模型，提供图标和标题<br>
 * 2.对内部所有内容（几乎全部是键值对）提供迭代器<br>
 * 3.如果需要将修改行为与其他行为进行关联，需要自行实现*/
public interface NodeSymbo{
	public final static int KindGroup = 0;
	public final static int KindNode = 1;
	public final static String NODENAME_KEY = "NODE_TITLE";
	
	/**
	 * 获取该节点绑定的{@link TreeModel}
	 * @return 绑定的{@link TreeModel}*/
	TreeModel getModel();
	
	/**
	 * 设置键值对,所有的内容都是由键值对存储的
	 * 标题提供的提供由一个特殊的键关联 {@link #NODENAME_KEY}
	 * 设置标题的时候也是设置  {@link #NODENAME_KEY} 关联值
	 * @param key 键名
	 * @param value 值*/
	void setKeyValue(String key, String value);
	
	/**
	 * 获取该索引上绑定的键名，如果超出内容范围，返回NULL
	 * @param i 索引值
	 * @return 返回键名，如果超出范围，返回NULL*/
	String getKey(int i);
	
	/**
	 * 获取绑定在key上的索引值
	 * @param key 键名
	 * @return 返回的值*/
	String getValue(String key);

	/**
	 * 获取实例种类
	 * @return 种类*/
	int kind();
	
	/**
	 * 默认UI触发操作，每个节点能够触发什么操作由节点自行定义
	 * @param owner TODO*/
	void doAction(FrontWindow owner);
	

	/**
	 * 节点被添加到集合节点之后，可以用它初始化本节点的父节点参数
	 * @param parent 父节点*/
	void initParent(GroupSymbo parent);
	
	/**
	 * 文件被添加到集合节点之后，可以用它获取父节点
	 * @return 父节点*/
	GroupSymbo getParent();
	
	
	/**
	 * 获取内置弹出菜单
	 * @param owner TODO
	 * @return 弹出菜单*/
	JPopupMenu getPopupMenu(FrontWindow owner);
	
	/**
	 * 添加节点事件监听器
	 * @param l 监听器*/
	void addNodeEventListener(NodeEventListener l);
}
