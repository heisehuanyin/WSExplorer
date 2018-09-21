package ws.editor.plugin.logport;

import ws.editor.comn.PluginFeature;
import ws.editor.plugin.LogPort;
/**
 * 用于输出log到文件中
 * LogPort虽然也被设计成为插件模式，但是软件本身只需要一种log文件格式，可以利用配置文件进行配置*/
public abstract class AbstractLogPort  implements LogPort{

	@Override
	public int pluginMark() {
		return PluginFeature.Service_LogPort;
	}

	@Override
	public int upStreamMark() {
		return PluginFeature.IO_NoUpStream;
	}


}
