import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

/**
 * 投票功能分三步:
 * 1)获取代理  √ 
 * 2)获取cookie ×
 * 3)获取验证码，输入验证码 × 
 * 4）提交表单进行投票 ×
 * @author Administrator
 *
 */
public class MainToupiao {
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		
		//创建客户端，创建cookie
		BasicCookieStore cookieStore = new BasicCookieStore();
        //保存cookie
        storeCookie(cookieStore,null);
        getVerify(cookieStore);
        String verify = inputVerify();
        
        if(verify!=null && !verify.isEmpty()){
        	System.out.println("verify="+verify);
//        	postOne(cookieStore,verify,null);
        }
        else{
        	getVerify(cookieStore);
        	String verifyTrue = inputVerify();
        	if(verifyTrue!=null && !verifyTrue.isEmpty()){
//        		postOne(cookieStore,verifyTrue,null);
        	}
        }
		
//		JLabel picture = new JLabel("img/verify.bmp");
//		add (picture);
	}
		/**
		 * 控制台输入
		 * @return
		 */
		private static String inputVerify() {
		// TODO Auto-generated method stub
			//(String) JOptionPane.showInputDialog(null, "输入验证码", "验证码输入", JOptionPane.WARNING_MESSAGE, new ImageIcon("img/verify.bmp"), null, null)
			Scanner scanneer = new  Scanner(System.in);
			return scanneer.nextLine();
//			return JOptionPane.showInputDialog("输入验证码");
	}

		private static void getVerify(BasicCookieStore cookieStore) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		String uri="http://www.njxjyj.com/yxjs/code.asp";
		
		Executor executor = Executor.newInstance();
		executor.cookieStore(cookieStore);//保存cookie
		
		File file = new File("img/verify.bmp");
		executor.execute(Request.Get(uri)
                .connectTimeout(10000)
                .socketTimeout(10000)
//                .viaProxy(new HttpHost(host, port))
                ).saveContent(file);
	}
		/**
		 * 保存cookie信息，用于验证码提取
		 * @throws IOException 
		 * @throws ClientProtocolException 
		 * @throws IOException 
		 * @throws ClientProtocolException 
		 * @throws IOException 
		 * @throws ClientProtocolException 
		 */
		private static void storeCookie(BasicCookieStore cookieStore,String host) throws ClientProtocolException, IOException{
			
			Executor executor = Executor.newInstance();
			executor.cookieStore(cookieStore);//保存cookie

	        // Execute a GET with timeout settings and return response content as String.
	        executor.execute(Request.Get("http://www.njxjyj.com/yxjs/view.asp?id=108")//这里需要修改！！
	                .connectTimeout(10000)
	                .socketTimeout(10000)
//	                .viaProxy(new HttpHost(host, 8088))
	                );
			
            System.out.println("Initial set of cookies:");
            List<Cookie> cookies = cookieStore.getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                }
            }
		}
	
		private static void postOne(BasicCookieStore cookieStore, String vertiry,String host) throws ClientProtocolException, IOException{
			String action="";
			Executor executor = Executor.newInstance();
			executor.cookieStore(cookieStore);
			
			executor.execute(Request.Post("vote.asp")//http://form
					.connectTimeout(1000)
	                .socketTimeout(1000)
					.addHeader("User-agent", "Mozilla/4.0")
	                .viaProxy(new HttpHost(host, 8088))//代理
	                .bodyForm(Form.form()
	                		.add("code", vertiry)//验证码，动态获取，因为是存放在session里，可能比较复杂
	                		.add("id", "114")
	                		.add("t", "1")
	                		.build())
	                ).saveContent(new File("result.dump"));
			
		}
	
		/**
		 * 获取代理
		 * @param req  http://www.tkdaili.com/api/getiplist.aspx?vkey=0BD06E292F31222BA38F46E53EA3D09B&num=1&country=CN&port=8088&style=3
		 * @return
		 * @throws ClientProtocolException
		 * @throws IOException
		 */
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
