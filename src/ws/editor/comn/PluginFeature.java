package ws.editor.comn;

import javax.swing.JMenu;

import ws.editor.WsProcessor;
/**
 * 所有插件的共同接口，插件的实例既可以作为插件实体，也可以作为工厂类
 * 注册插件的时候使用的实例，被软件作为工厂类使用，获取新的实例
 * */
public interface PluginFeature {
	/**
	 * 为了保持界面统一，每次启动，相同种类UI组件只能选择其一，可以通过配置文件指定*/
	final static int UI_MenuBar = 0x10;
	/**
	 * 为了保持界面统一，每次启动，相同种类UI组件只能选择其一，可以通过配置文件指定*/
	final static int UI_ToolBar = 0x11;
	/**
	 * 为了保持界面统一，每次启动，相同种类UI组件只能选择其一，可以通过配置文件指定*/
	final static int UI_Window = 0x12;
	/**
	 * 为了保持界面统一，每次启动，相同种类UI组件只能选择其一，可以通过配置文件指定*/
	final static int UI_StatusBar = 0x13;
	/**
	 * 为了保持界面统一，每次启动，相同种类UI组件只能选择其一，可以通过配置文件指定*/
	final static int UI_ContentView = 0x14;
	
	
	
	
	/**
	 * 插件内部标识码
	 * 与源文件接触的第一个插件返回的上游代码就是 {@link #IO_NoUpStream} 
	 * */
	final static int IO_NoUpStream = 0x20;
	/**
	 * {@link #IO_FileSymbo}没有被命名为Model是因为他特殊的不包含状态，
	 * 只提供InputStream和OutputStream获取接口*/
	final static int IO_FileSymbo = 0x21;
	/**
	 * 本插件只规定了输出模型接口，能够将其他模型作为输入，功能如何全看开发者的发挥。
	 * 是插件中的主要类型。*/
	final static int IO_TextModel = 0x22;
	/**
	 * 本插件只规定了输出模型接口，能够将其他模型作为输入，功能如何全看开发者的发挥。
	 * 是插件中的主要类型。*/
	final static int IO_TreeModel = 0x23;
	/**
	 * 本插件只规定了输出模型接口，能够将其他模型作为输入，功能如何全看开发者的发挥。
	 * 是插件中的主要类型。*/
	final static int IO_TableModel=0x24;
	/**
	 * 本插件只规定了输出模型接口，能够将其他模型作为输入，功能如何全看开发者的发挥。
	 * 是插件中的主要类型。*/
	final static int IO_StyleModel = 0x25;
	
	
	
	
	/**
	 * 以下插件由开发者自行维护,因为涉及到插件和配置文件的统一问题<br>
	 * 每次启动，所有组件可以获得的实例类型都是相同的，都是最后一次加载的同类插件<br>
	 * 此插件基本不参加内容处理工具链的构建*/
	final static int Service_ConfigPort = 0x40;
	/**
	 * 以下插件由开发者自行维护,因为涉及到插件和配置文件的统一问题<br>
	 * 每次启动，所有组件可以获得的实例类型都是相同的，都是最后一次加载的同类插件<br>
	 * 此插件基本不参加内容处理工具链的构建*/
	final static int Service_LogPort = 0x41;
	
	/**
	 * 获取组件代码，标定组件类别，详细定义看{@link PluginFeature}
	 * @return 返回的组件标记代码*/
	int pluginMark();
	/**
	 * 获取本插件上游插件类型
	 * @return 返回的上游插件类型*/
	int upStreamMark();
	/**
	 * 获取每个组件都拥有的配置菜单
	 * @return 返回每个组件自定义的定制菜单，供上级调用*/
	JMenu getCustomMenu();
	/**
	 * 组件自带保存操作，需要保存过程的组件需要实现*/
	void saveOperation();
}
