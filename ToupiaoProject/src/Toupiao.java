import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;


public class Toupiao {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		
		
		Timer mytimer=new Timer();
		mytimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				task();//定时执行任务
			}
		}, 0,shuffle());//1min
		
	}
	
	private static void task(){
		Timer timer=new Timer();
		execute();
	}
	
	private static long shuffle(){
		long time=1000*60*1;
		Random random=new Random();
		time += random.nextInt(5000);
		System.out.println("next time is = "+time);
		return time;
	}
	
	private static boolean execute(){
		boolean success=false;
		try {
			ArrayList<String> proxy = getProxy(null);
			//顶上去一次
			if(proxy.size()==2){
				getOne(proxy.get(0),8088);
				success=true;
			}else {
				System.out.println("代理出错");
				System.out.println(proxy);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return success;
	}
	// http://www.njxjyj.com/yxjs/ajax_dc.asp?action=d&id=111
	private static boolean getOne(String host,int port) throws ClientProtocolException, IOException{
		 Executor executor = Executor.newInstance();

	        // Execute a GET with timeout settings and return response content as String.
	        String txt = executor.execute(Request.Get("http://www.njxjyj.com/yxjs/ajax_dc.asp?action=d&id=111")
	                .connectTimeout(10000)
	                .socketTimeout(10000)
	                .viaProxy(new HttpHost(host, port))
	                ).returnContent().asString();
	        System.out.println(txt);
	        if(txt.contains("成功"))
	        	return true;
	        return false;
	}
	//http://www.tkdaili.com/api/getiplist.aspx?vkey=0BD06E292F31222BA38F46E53EA3D09B&num=1&country=CN&port=8088&style=3
	private static ArrayList<String> getProxy(String req) throws ClientProtocolException, IOException{
		if( req==null){
			req="http://www.tkdaili.com/api/getiplist.aspx?vkey=0BD06E292F31222BA38F46E53EA3D09B&num=1&country=CN&port=8088&style=3";
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
