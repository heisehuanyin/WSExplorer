package ws.editor.plugin.tablemodel;

import ws.editor.comn.PluginFeature;
import ws.editor.plugin.TableModel;

/**
 * 因为CSV是逗号分割的，文本文件，本身并未标注数据格式，
 * 因此返回数据类型统一为{@link String}*/
public abstract class AbstractCSVModel implements TableModel{

	@Override
	public int pluginMark() {
		return PluginFeature.IO_TableModel;
	}

	@Override
	public int upStreamMark() {
		return PluginFeature.IO_TextModel;
	}

}
