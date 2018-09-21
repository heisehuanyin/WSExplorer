package ws.editor.plugin.filesymbo;

import java.io.File;
import java.io.IOException;

import javax.swing.JMenu;

import ws.editor.comn.PluginFeature;
import ws.editor.plugin.FileSymbo;

public class DefaultFileSymbo implements FileSymbo{
	private String filePath=null;

	@Override
	public int pluginMark() {
		return PluginFeature.IO_FileSymbo;
	}

	@Override
	public int upStreamMark() {
		return PluginFeature.IO_NoUpStream;
	}

	@Override
	public FileSymbo openFileModel(String fPath) {
		DefaultFileSymbo x = new DefaultFileSymbo();
		
		File xs = new File(fPath);
		try {
			x.filePath = xs.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return x;
	}

	@Override
	public String getFilePath() {
		return this.filePath;
	}

	@Override
	public JMenu getCustomMenu() {
		return null;
	}

	@Override
	public void saveOperation() {}

}
