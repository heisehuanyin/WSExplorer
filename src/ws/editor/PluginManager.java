package ws.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ws.editor.comn.ItemsKey;
import ws.editor.comn.WsPair;
import ws.editor.comn.PluginFeature;
import ws.editor.plugin.ConfigPort;
import ws.editor.plugin.ContentView;
import ws.editor.plugin.FileSymbo;
import ws.editor.plugin.FrontWindow;
import ws.editor.plugin.LogPort;
import ws.editor.plugin.MenuBar;
import ws.editor.plugin.TextModel;
import ws.editor.plugin.TreeModel;
import ws.editor.plugin.TableModel;
import ws.editor.plugin.filesymbo.DefaultFileSymbo;
import ws.editor.plugin.menubar.DefaultMenuBar;
import ws.editor.plugin.window.FSBrowser;

/**
 * 用于管理插件，本身不是插件，不需要两步实例化直接实例化得到的就是可用组件
 */
public class PluginManager {
	private Map<String, PluginFeature> factoryContainer = new HashMap<>();
	private Map<String, ArrayList<PluginFeature>> instances = new HashMap<>();
	private WsProcessor schedule;
	private String onlyoneCfgportF_id;
	private String onlyoneLogportF_id;

	public PluginManager(WsProcessor sch) {
		this.schedule = sch;
	}

	/**
	 * 注册插件
	 * 
	 * @param obj
	 *            工厂类实例，用于获取新实例，除了占位本身无实际意义
	 */
	public void factory_RegisterPlugin(PluginFeature obj) {
		if (obj.pluginMark() == PluginFeature.Service_ConfigPort)
			this.onlyoneCfgportF_id = obj.getClass().getName();
		if (obj.pluginMark() == PluginFeature.Service_LogPort)
			this.onlyoneLogportF_id = obj.getClass().getName();

		this.factoryContainer.put(obj.getClass().getName(), obj);
	}

	/**
	 * 获取注册工厂类,如果指定工厂类未注册，返回null
	 * 
	 * @param id_factory
	 *            唯一标志
	 * @return 工厂类，用于构建新实例，未注册则返回null
	 */
	private PluginFeature factory_GetExistsfactory(String id_factory) {
		PluginFeature factory = this.factoryContainer.get(id_factory);
		return factory;
	}

	/**
	 * 对配置文件和默认设置对提供的factory_id进行校验，合法的id组成的factory。校验不通过返回默认Factory。
	 * 
	 * @param itemsKeyDefine
	 *            指明配置项条目作为默认配置项目
	 * @param defaultf_id
	 *            指名配置项目出现意外后的默认factory_id
	 * 
	 * @return 合适的合法的插件工厂
	 */
	private PluginFeature factory_GetConfigComp(String itemsKeyDefine, String defaultf_id) {
		String factory_id;
		// 获取合法的factory_id,未配置的话，启用dafault_id
		factory_id = this.schedule.instance_GetMainConfigUnit().getValue(itemsKeyDefine, defaultf_id);

		// 该factory是否注册,未注册切换为default_id插件
		PluginFeature factory = this.factory_GetExistsfactory(factory_id);
		if (factory == null) {
			factory = this.factory_GetExistsfactory(defaultf_id);
			this.schedule.instance_GetDefaultLogPort().errorLog(this, "配置文件错误，插件" + factory_id + "不存在");
		}
		return factory;
	}

	/**
	 * 用于一次执行所有插件的保存操作
	 */
	public void operate_SaveOperation() {
		Set<String> aset = this.instances.keySet();
		for (String url : aset) {
			ArrayList<PluginFeature> cList = this.instances.get(url);
			this.channel_SaveChannel(cList);
		}
	}

	// source=>module1=>module2=>module3=>module4=>the last one module
	/**
	 * 通过传入的模块链字符串，构建一条内容通道
	 * @param cListStr 通道字符串
	 * @param url 分组标识/文件路径
	 * @return 通道的最后一个模块实例*/
	public PluginFeature operate_BuildInstanceList(String cListStr, String url) {
		String[] modules = cListStr.split("=>");
		PluginFeature instance = null;

		for (String f : modules) {
			PluginFeature x = this.factory_GetExistsfactory(f);
			if (x == null) {
				this.schedule.instance_GetDefaultLogPort().errorLog(this,
						"插件未注册，我选择崩溃。罪魁祸首："+f);
				System.exit(0);
			}
			switch (x.pluginMark()) {
			case PluginFeature.IO_FileSymbo:
				instance = this.instance_GetFileSymbo(url);
				break;
			case PluginFeature.IO_TextModel:
				instance = this.instance_GetTextModelAsDescription(f, url, instance);
				break;
			case PluginFeature.IO_TreeModel:
				instance = this.instance_GetTreeModelAsDescription(f, url, instance);
				break;
			case PluginFeature.UI_ContentView:
				instance = this.instance_GetContentViewAsDescription(f, url, instance);
				break;
			case PluginFeature.IO_TableModel:
				instance = this.instance_GetTableModelAsDescription(f,url,instance);
				break;
			default:
				this.schedule.instance_GetDefaultLogPort().echoLog(this, "未知插件，无法实例化");
				break;
			}

		}

		return instance;
	}
	
	/**
	 * 对传入的一整条通道的插件按照顺序进行保存操作
	 * @param cList，插件通道，插件组*/
	public void channel_SaveChannel(ArrayList<PluginFeature> cList) {
		for (PluginFeature one : cList) {
			one.saveOperation();
		}
	}
	public void channel_CloseChannel_NoSave(ArrayList<PluginFeature> cList) {
		if(!instances.containsValue(cList))
			return;
		
		Set<String> x = instances.keySet();
		for(String s:x) {
			ArrayList<PluginFeature> xxx = instances.get(s);
			if(xxx == cList) {
				instances.remove(s);
				return;
			}
		}
	}
	
	/**
	 * 根据url获取关联的整条通道
	 * 
	 * @param url
	 *            内容来源标识
	 * @return 整个通道的插件组合
	 */
	public ArrayList<PluginFeature> channel_GetExistsChannel(String url) {
		return this.instances.get(url);
	}
	
	/**
	 * 通过已知插件获取整个通道
	 * @param plug 已知插件
	 * @return 通道实例*/
	public ArrayList<PluginFeature> channel_GetExisisChannel(PluginFeature plug){
		Set<String> aset = this.instances.keySet();
		for(String url:aset) {
			ArrayList<PluginFeature> cList = this.instances.get(url);
			if(cList.contains(plug))
				return cList;
		}
		return null;
	}
	

	/**
	 * 自动注册插件实例
	 * 
	 * @param url
	 *            内容来源地址
	 * @param obj
	 *            插件实例
	 */
	private void instance_RegisterPluginInstance(String url, PluginFeature obj) {

		ArrayList<PluginFeature> con1 = this.instances.get(url);
		if (con1 == null) {// 如果未注册过该类插件，连容器也不会有
			con1 = new ArrayList<PluginFeature>();
			this.instances.put(url, con1);
		}

		con1.add(0, obj);
	}

	// 插件实例获取接口:基础===================================================================

	/**
	 * 获取{@link ConfigPort}用于读取和输出配置，如果存在则获取，否则新建一个<br>
	 * 该机制保证只有一种插件能够用来解析configfile，{@link ConfigPort}不接受配置
	 * 
	 * @param path
	 *            配置文件存放路径，插件分组依据
	 * @return 实例
	 */
	public ConfigPort instance_GetConfigUnit(String path) {
		ArrayList<PluginFeature> cList = this.channel_GetExistsChannel(path);
		if (cList != null)
			return (ConfigPort) cList.get(0);

		PluginFeature factory = this.factory_GetExistsfactory(this.onlyoneCfgportF_id);

		ConfigPort config = ((ConfigPort) factory).createNewPort(path);
		this.instance_RegisterPluginInstance(path, config);

		return config;
	}

	/**
	 * 获取{@link LogPort}用于输出log,如果已存在指定的{@link LogPort}则获取，否则新建一个。<br>
	 * 该机制保证只有一种插件能够输出log，保证每次启动输出的log文件格式相同，{@link LogPort}不接受配置
	 * 
	 * @param path
	 *            log文件路径，插件分组依据
	 * @return 实例
	 */
	public LogPort instance_GetLogPort(String path) {
		ArrayList<PluginFeature> cList = this.channel_GetExistsChannel(path);
		if (cList != null)
			return (LogPort) cList.get(0);

		PluginFeature factory = this.factory_GetExistsfactory(this.onlyoneLogportF_id);

		LogPort writer = ((LogPort) factory).createNewPort(path);
		this.instance_RegisterPluginInstance(path, writer);

		return writer;
	}

	/**
	 * 获取{@link FileSymbo}，只有一种{@link FileSymbo}
	 * 
	 * @param path
	 *            文件路径
	 * @return 返回实例
	 */
	private FileSymbo instance_GetFileSymbo(String path) {
		ArrayList<PluginFeature> cList = this.channel_GetExistsChannel(path);
		if (cList != null)
			for (PluginFeature x : cList) {
				if (x.getClass().getName().equals(DefaultFileSymbo.class.getName()))
					return (FileSymbo) x;
			}

		PluginFeature f = this.factory_GetExistsfactory(DefaultFileSymbo.class.getName());
		FileSymbo rtn = ((FileSymbo) f).openFileModel(path);
		this.instance_RegisterPluginInstance(path, rtn);

		return rtn;
	}

	/**
	 * 根据提供的factory_id，获取插件实例<br>
	 * 首先搜寻是否存在实例，如果存在返回实例，不存在的话注册新实例
	 * 
	 * @param f_id
	 *            插件id-插件类名
	 * @param url
	 *            通道标识，插件分组标识
	 * @param upStream
	 *            上游插件
	 * @return 正确的插件实例
	 */
	private TextModel instance_GetTextModelAsDescription(String f_id, String url, PluginFeature upStream) {
		ArrayList<PluginFeature> cList = this.channel_GetExistsChannel(url);

		if (cList != null)
			for (PluginFeature x : cList) {
				if (x.getClass().getName().equals(f_id))
					return (TextModel) x;
			}

		PluginFeature f = this.factory_GetExistsfactory(f_id);
		if (f == null) {
			this.schedule.instance_GetDefaultLogPort().errorLog(this, "参数f_id错误，未能找到注册插件----" + f_id);
			System.exit(0);
		}

		TextModel rtn = ((TextModel) f).openTextModel(schedule, upStream, "UTF-8");
		this.instance_RegisterPluginInstance(url, rtn);

		return rtn;
	}

	/**
	 * 根据提供的factory_id，获取插件实例<br>
	 * 首先搜寻是否存在实例，如果存在返回实例，不存在的话注册新实例
	 * 
	 * @param f_id
	 *            插件id-插件类名
	 * @param url
	 *            通道标识，插件分组标识
	 * @param upStream
	 *            上游插件
	 * @return 正确的插件实例
	 */
	private TreeModel instance_GetTreeModelAsDescription(String f_id, String url, PluginFeature upStream) {
		List<PluginFeature> cList = this.channel_GetExistsChannel(url);
		if (cList != null)
			for (PluginFeature x : cList) {
				if (x.getClass().getName().equals(f_id))
					return (TreeModel) x;
			}
		PluginFeature f = this.factory_GetExistsfactory(f_id);
		if (f == null) {
			this.schedule.instance_GetDefaultLogPort().errorLog(this, "参数f_id错误，未能找到注册插件----" + f_id);
			System.exit(0);
		}

		TreeModel rtn = ((TreeModel) f).openTreeModel(this.schedule, upStream);
		this.instance_RegisterPluginInstance(url, rtn);

		return rtn;
	}
	
	/**
	 * 根据提供的factory_id，获取插件实例<br>
	 * 首先搜寻是否存在实例，如果存在返回实例，不存在的话注册新实例
	 * 
	 * @param f
	 *            插件id-插件类名
	 * @param url
	 *            通道标识，插件分组标识
	 * @param upStream
	 *            上游插件
	 * @return 正确的插件实例
	 */
	private TableModel instance_GetTableModelAsDescription(String f, String url, PluginFeature upStream) {
		List<PluginFeature> cList = this.channel_GetExistsChannel(url);
		if (cList != null)
			for (PluginFeature x : cList) {
				if (x.getClass().getName().equals(f))
					return (TableModel) x;
			}
		
		PluginFeature ff = this.factory_GetExistsfactory(f);
		if(ff == null) {
			this.schedule.instance_GetDefaultLogPort().errorLog(this, "参数f_id错误，未能找到注册插件----" + f);
			System.exit(0);
		}
		
		TableModel rtn = ((TableModel)ff).openNewTableModel(schedule, upStream);
		this.instance_RegisterPluginInstance(url, rtn);
		
		return rtn;
	}


	// UI
	// Component==================================================================
	/**
	 * 根据提供的factory_id，获取视图插件实例<br>
	 * 首先搜索是否存在实例，如果存在，返回实例，不存在的话注册新实例
	 * @param f_id 插件的id
	 * @param url 通道标识，插件分组标识
	 * @param upStream 上游插件
	 * @return 正确的插件实例*/
	private ContentView instance_GetContentViewAsDescription(String f_id, String url, PluginFeature upStream) {
		List<PluginFeature> cList = this.channel_GetExistsChannel(url);
		if(cList != null)
			for(PluginFeature x:cList)
				if(x.getClass().getName().equals(f_id))
					return (ContentView) x;
		
		PluginFeature f = this.factory_GetExistsfactory(f_id);
		if (f == null) {
			this.schedule.instance_GetDefaultLogPort().errorLog(this, "参数f_id错误，未能找到注册插件----" + f_id);
			System.exit(0);
		}
		
		ContentView rtn = ((ContentView)f).openContentView(this.schedule, upStream);
		this.instance_RegisterPluginInstance(url, rtn);
		
		return rtn;
	}

	/**
	 * MenuBar只用来刷新和替换原有MenuBar，因此不需要找到之前的实例，直接替代
	 * 
	 * @param string
	 *            组件的GroupID
	 * @return 实例
	 */
	public MenuBar instance_GetNewDefaultMenubar(String string) {
		PluginFeature f = this.factory_GetConfigComp(ItemsKey.DefaultMenuBar, DefaultMenuBar.class.getName());

		MenuBar rtn = ((MenuBar) f).getNewInstance(this.schedule);
		this.instance_RegisterPluginInstance(string, rtn);

		return rtn;
	}

	/**
	 * 获取FrontWindow实例
	 * 
	 * @param string
	 *            组件的GroupID
	 * @return 实例
	 */
	public FrontWindow instance_GetNewDefaultWindow(String string) {
		PluginFeature f = this.factory_GetConfigComp(ItemsKey.DefaultWindow, FSBrowser.class.getName());

		List<PluginFeature> cList = this.channel_GetExistsChannel(string);
		if (cList != null)
			for (PluginFeature x : cList) {
				if (x.getClass().getName().equals(f.getClass().getName()))
					return (FrontWindow) x;
			}

		FrontWindow rtn = ((FrontWindow) f).openWindow(schedule, string);
		this.instance_RegisterPluginInstance(string, rtn);

		return rtn;
	}

	// Service
	// =============================================================================
	/**
	 * 通过分类碼对{@link PluginManager}进行检索，获取所有此类插件的信息
	 * @param typeMark 分类码
	 * @return 插件信息组*/
	public ArrayList<WsPair<String,Integer>> service_QueryFactoryList(int typeMark) {
		ArrayList<WsPair<String, Integer>> list = new ArrayList<>();
		Set<String> facSet = this.factoryContainer.keySet();
		for(String f:facSet) {
			PluginFeature x = this.factory_GetExistsfactory(f);
			if(x.pluginMark() == typeMark)
				list.add(new WsPair<String, Integer>(f,x.upStreamMark()));
		}
		
		return list;
	}
	
	/**
	 * 检索{@link PluginManager}，获取所有同类插件信息
	 * @param onePlugin 示例插件
	 * @return 插件信息组*/
	public ArrayList<WsPair<String, Integer>> service_QueryFactoryList(String onePlugin){
		int mark = this.factory_GetExistsfactory(onePlugin).pluginMark();
		return this.service_QueryFactoryList(mark);
	}

}
