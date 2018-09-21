package ws.editor.plugin.logport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import ws.editor.WsProcessor;
import ws.editor.comn.PluginFeature;
/**
 * 用于输出log到文件中
 * LogPort虽然也被设计成为插件模式，但是软件本身只需要一种log文件格式，
 * 因此，不需要多种实例共存，软件中只保留一个logport插件，factory类id恒为LogPort.class.getName()*/
public class DefaultLogPort extends AbstractLogPort{
	private BufferedWriter port = null;
	
	/**
	 * 慎重，本方法只适用构造工厂类，通过本方法构造的实例所有id都为默认id
	 * 实例化方法new LogWriter().createNewPort(path);*/
	public DefaultLogPort() {}


	/**
	 * 有参构造器用于构建定制实例,利用已有实例构建出新实例，不会初始化已有实例
	 * @param path 实际地址作为唯一参数
	 * @return 文件路径合法返回新实例，不合法返回null
	 */
	@Override
	public AbstractLogPort createNewPort(String path) {
		File log = new File(path);
		
		if(! log.getParentFile().exists()) {
			log.getParentFile().mkdirs();
		}
		
		if(! log.exists()) {
			try {
				log.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		DefaultLogPort rtn = new DefaultLogPort();
		
		try {
			rtn.port = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),"UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rtn;
	}
	

	/**
	 * 写log信息接口,每次调用接口，将log信息按照规定格式写入log文件
	 * @param obj 写信息的对象
	 * @param msg 具体信息*/
	@Override
	public void writeLog(Object obj, String msg) {
		msg = "Log:"+obj.getClass().getName() + ":" + msg;
		
		try {
			this.port.write(msg);
			this.port.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public void errorLog(Object obj, String msg) {
		msg = "Error:"+obj.getClass().getName() + ":" + msg;
		
		try {
			this.port.write(msg);
			System.out.println(msg);
			this.port.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//====================================================================

		/**
		 * 保存过程调用flush
		 * 在框架中被设计成自动调用*/
		@Override
		public void saveOperation() {
			try {
				this.port.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public JMenu getCustomMenu() {
			//TODO 详细定制菜单需要完善
			return new JMenu(this.getClass().getName());
		}


		@Override
		public void echoLog(Object obj, String msg) {
			System.out.println(msg);
			msg = "[printEcho]" + msg;
			this.writeLog(obj, msg);
		}

}
