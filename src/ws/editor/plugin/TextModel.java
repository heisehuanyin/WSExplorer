package ws.editor.plugin;

import ws.editor.WsProcessor;
import ws.editor.comn.PluginFeature;

public interface TextModel extends PluginFeature {
	/**
	 * 获取内容，构建一个数据模型，传入上游模块和字符编码
	 * @param core 核心模块
	 * @param upStream 上游模型插件
	 * @param encoding 传入字符编码
	 * @return 数据模型*/
	TextModel openTextModel(WsProcessor core, PluginFeature upStream, String encoding);
	
	/**
	 * 获取内容行总数
	 * @return 总数*/
	int getRowsCount();
	
	
	/**
	 * 获取指定一行
	 * @param index 行序
	 * @return 指定行内容*/
	String getLine(int index);

	/**
	 * 更新指定行号的内容
	 * @param index 指名行序号
	 * @param str 指定内容*/
	void updateLine(int index, String str);

	/**
	 * 将指定内容插入特定行位
	 * @param index 指定行序
	 * @param str 特定内容*/
	void insertLine(int index, String str);
	
	/**
	 * 删除指定行及其之后的所有内容
	 * @param indexStart 行序，从本行到最后一行全删*/
	void removeLines(int indexStart);
	
}
