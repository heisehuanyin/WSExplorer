package ws.editor.plugin;

import ws.editor.comn.PluginFeature;

public interface ConfigPort extends PluginFeature {

	/**
	 * 获取一个新实例，该实例与本实例没有关系
	 * @param path 配置文件位置
	 * @return 返回新的实例
	 * */
	ConfigPort createNewPort(String path);
	
	/**
	 * 设置一个配置属性
	 * @param key 键
	 * @param value 值*/
	void setKeyValue(String key, String value);

	/**
	 * 获取一个配置属性,如果获取不到指定键值，那么就返回默认属性，并将默认属性写入配置文件
	 * @param key 通过此键获取属性
	 * @param defaultValue 默认属性
	 * @return 返回的配置属性*/
	String getValue(String key, String defaultValue);

}
