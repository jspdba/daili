import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;


public class ChangeIeProxy {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		//获取代理
		ArrayList<String> proxy = getProxy(null);
		//得到代理
		if(proxy!=null && proxy.size()==2){
			createBatFile(proxy.get(0),8088);
		}
		
	}
	
	private static void createBatFile(String host,int port){
		String open = "bat/openDaili.bat";
		String openCmd="@echo off \r\n"
			+"echo '打开代理' \r\n"
			+"reg add \"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable /t REG_DWORD /d "
			+1
			+" /f \r\n"
			+"@echo '设置完毕' \r\n";
		
		createFile(open,openCmd,false);
		
		String close="bat/closeDaili.bat";
		String closeCmd="@echo off  \r\n"
			+"echo '关闭代理' \r\n"
			+"reg add \"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable /t REG_DWORD /d "
			+0
			+" /f \r\n"
			+"@echo '设置完毕' \r\n";
		
		createFile(close,closeCmd,false);
		
		String change="bat/change.bat";
		String changeCmd="reg add \"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable /t REG_DWORD /d 1 /f\r\n"
			+"reg add \"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyServer /t REG_SZ /d "+host+":"+port+" /f";
		//必须允许覆盖源文件
		createFile(change, changeCmd, true);
		
		String openIE="bat/openIE.bat";
		String openIECmd="@echo off \r\n"
			+"echo 正在打开ie \r\n"
			+"start \"\" \"c:\\Program Files\\Internet Explorer\\iexplore.exe\" www.njxjyj.com/yxjs/view.asp?id=111 ";
		createFile(openIE, openIECmd ,false);
		
		String closeIE="bat/closeIE.bat";
		String closeIECmd="@echo off \r\n"
			+"echo 正在关闭ie \r\n"
			+"taskkill /f /im iexplore.exe ";
		createFile(closeIE, closeIECmd, false);
		
	}
	/**
	 * 创建文件，不追加
	 * @param filepath 文件路径
	 * @param content 文件内容
	 */
	private static void createFile(String filepath,String content,boolean cover){
		
		File f=new File(filepath);
		
		if(!cover){//如果不允许覆盖，且文件已经存在，则退出
			
			if(f.exists())
				return;
		}
		//开始写文件
		FileWriter fwriter = null;
		try {
			fwriter = new FileWriter(f);
			fwriter.write(content);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				fwriter.flush();
				fwriter.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		System.out.println(f.getAbsolutePath()+",创建完毕!!!");
	}
	
	private static ArrayList<String> getProxy(String req) throws ClientProtocolException, IOException{
		if( req==null){
			//全国(包括台湾)
//			req="http://www.tkdaili.com/api/getiplist.aspx?vkey=0BD06E292F31222BA38F46E53EA3D09B&num=1&country=CN&port=8088&style=3";
			//北京
//			req="http://www.tkdaili.com/api/getiplist.aspx?vkey=0BD06E292F31222BA38F46E53EA3D09B&num=1&country=CN&filter=%b1%b1%be%a9&port=8088&style=3";
			//石家庄
			req="http://www.tkdaili.com/api/getiplist.aspx?vkey=0BD06E292F31222BA38F46E53EA3D09B&num=1&country=CN&filter=%ca%af%bc%d2%d7%af&port=8088&style=3";
			//邢台
//			req="http://www.tkdaili.com/api/getiplist.aspx?vkey=0BD06E292F31222BA38F46E53EA3D09B&num=1&country=CN&filter=%d0%cf%cc%a8&port=8088&style=3";
		}
		Executor executor = Executor.newInstance();
        // Execute a GET with timeout settings and return response content as String.
        String txt = executor.execute(Request.Get(req)
                .connectTimeout(5000)
                .socketTimeout(5000)
                .addHeader("User-agent", "Mozilla/5.0 (Windows NT 5.2; rv:5.0.1) Gecko/20100101 Firefox/5.0.1")
                ).returnContent().asString();
        System.out.println(txt);
        logInfile(null, txt+"\r\n");
        ArrayList<String> hostAndPort=new ArrayList<String>();
        String[] temp = null;
        if(txt!=null && !txt.isEmpty()){
        	temp = txt.split(":");
        }
        if(temp!=null && temp.length==2){
        	hostAndPort.add(temp[0]);
        	hostAndPort.add(temp[1]);
        }
        return hostAndPort;
	}
	
	/**
	 * 保存代理数据到文件中
	 * 
	 * @throws IOException
	 */
	public static void logInfile(String filePath, String txt)
			throws IOException {
		
		if(filePath==null || filePath.isEmpty()){
			filePath="e:\\wuchaofei\\bak\\daili.log";
		}
		File f = new File(filePath);
		FileWriter fwriter = null;
		try {
			fwriter = new FileWriter(f,true);
			fwriter.write(txt);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				fwriter.flush();
				fwriter.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
