package ws.editor.plugin.configport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

import javax.swing.JMenu;
/**
 * 用于获取配置文件中的信息；<br>
 *  {@link ws.editor.plugin.ConfigPort}虽然也被设计成为插件模式，但是软件本身只需要一种配置文件格式，
 * 因此，不需要多种实例共存，软件中只保留一个 {@link ws.editor.plugin.ConfigPort} 插件*/
public class DefaultConfigPort extends AbstractConfigPort{
	private Properties prop = new Properties();
	private String cfgPath;
	
	public DefaultConfigPort() {}

	private DefaultConfigPort(String cfgPath) {
		this.cfgPath = cfgPath;
	}
	
	
	@Override
	public void setKeyValue(String key, String value) {
		this.prop.setProperty(key, value);
	}
	
	@Override
	public String getValue(String key, String defaultValue) {
		String v = this.prop.getProperty(key);
		if(v == null) {
			this.prop.setProperty(key, defaultValue);
			return defaultValue;
		}
		
		return v;
	}
	
	
	
	
	
	/**
	 * 获取一个新实例，该实例与本实例没有关系
	 * @param path 配置文件位置
	 * @return 返回新的实例
	 * */
	@Override
	public DefaultConfigPort createNewPort(String path) {

		File cfgfile = new File(path);
		
		if(! cfgfile.getParentFile().exists()) {
			cfgfile.getParentFile().mkdirs();
		}
		
		if(! cfgfile.exists()) {
			try {
				cfgfile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		DefaultConfigPort rtn = new DefaultConfigPort(path);
		
		try {
			rtn.prop.load(new InputStreamReader(new FileInputStream(cfgfile),"UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rtn;
	}
	/**
	 * 保存操作*/
	@Override
	public void saveOperation() {
		try {
			this.prop.store(new OutputStreamWriter(new FileOutputStream(this.cfgPath),"UTF-8"), "config items as list");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JMenu getCustomMenu() {
		JMenu custom = new JMenu(this.getClass().getName());
		//TODO 详细定制菜单需要完善
		return custom;
	}
}
