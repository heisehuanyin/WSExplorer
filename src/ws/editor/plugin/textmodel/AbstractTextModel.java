package ws.editor.plugin.textmodel;

import ws.editor.comn.PluginFeature;
import ws.editor.plugin.TextModel;

public abstract class AbstractTextModel implements TextModel {
	
	@Override
	public int pluginMark() {
		return PluginFeature.IO_TextModel;
	}

	@Override
	public int upStreamMark() {
		return PluginFeature.IO_FileSymbo;
	}
	
}
