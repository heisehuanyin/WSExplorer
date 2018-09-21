package ws.editor.plugin;

import ws.editor.comn.PluginFeature;

/**
 * 本插件的唯一用途就是占位，让许多插件具有相似的接口，不因为特异化造成无法互操作*/
public interface FileSymbo extends PluginFeature {
	/**
	 * 构建一个新的文件符号
	 * @param fPath 文件路径
	 * @return 创建的符号实例*/
	FileSymbo openFileModel(String fPath);
	
	/**
	 * 获取文件路径
	 * @return 路径*/
	String getFilePath();
}
