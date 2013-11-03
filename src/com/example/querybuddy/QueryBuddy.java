package com.example.querybuddy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.querybuddy.ArrayWheelAdapter;
import com.example.querybuddy.WheelView;
import com.example.querybuddy.OnWheelChangedListener;
import com.example.querybuddy.OnWheelScrollListener;

public class QueryBuddy extends Activity implements OnChangedListener{

	private final static String TAG = "WheelDemo";
	private final static String IP="192.168.1.110";
	
	private String buildingNum[] = new String[]{
			"不限","教一楼", "教二楼", "教三楼", "教四楼"};
	private String classroomNum[] = new String[]{"0","1", "2", "3","4", "5", "6","7", "8", "9"};
	private String dayOfWeek[] = new String[]{"不限","星期一", "星期二", "星期三", "星期四", "星期五"};
	private String timeInterval[]=new String[]{"不限","1~2","3~4","5~6","7~8","9~10"};
	private String district="宏福",building="教一楼",classroom="000",weekDay="星期一",timeItv="1~2";
	private String tmpString=null;
	// Wheel scrolled flag
	private boolean wheelScrolled = false;
	private TextView resultText;
	private Button sendB;
	private SlipButton slipButton;
	private Handler uiHandler,netHandler;
	private UIThread uiThead;
	private NetThread netThread;
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_buddy);

		initWheel(R.id.buildingNum, buildingNum);
		initWheel(R.id.classroomNumH, classroomNum);
		initWheel(R.id.classroomNumT, classroomNum);
		initWheel(R.id.classroomNumU, classroomNum);
		initWheel(R.id.dayOfWeek, dayOfWeek);
		initWheel(R.id.timeInterval, timeInterval);

		resultText = (TextView) this.findViewById(R.id.result);
		resultText.setText("宏福"+" - "+"不限"+" - "+"不限"+" - "+"不限"+" - "+"不限");
		
		sendB=(Button) this.findViewById(R.id.sendB);
		sendB.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				new Thread(netThread).start();
			}
		});
		
		//layoutInflater=getLayoutInflater();
		//view=layoutInflater.inflate(R.layout.query_result,null);//?
		//listView=(ListView) view.findViewById(R.id.listView);
		
		slipButton = (SlipButton) this.findViewById(R.id.slipButton);
		slipButton.SetOnChangedListener(this);
		
		//intent=new Intent(QueryBuddy.this,ResultList.class);
		
		uiThead=new UIThread();
		netThread=new NetThread();
		
		uiHandler=new Handler(){
			public void handleMessage(Message msg){
				building=buildingNum[getWheel(R.id.buildingNum).getCurrentItem()];
				classroom=classroomNum[getWheel(R.id.classroomNumH).getCurrentItem()]+
						classroomNum[getWheel(R.id.classroomNumT).getCurrentItem()]+
						classroomNum[getWheel(R.id.classroomNumU).getCurrentItem()];
				weekDay=dayOfWeek[getWheel(R.id.dayOfWeek).getCurrentItem()];
				timeItv=timeInterval[getWheel(R.id.timeInterval).getCurrentItem()];
				resultText.setText(district+" - "+building+" - "+classroom+" - "+weekDay+" - "+timeItv);
			}
		};
		
		netHandler=new Handler(){
			public void handleMessage(Message msg){
				//do something when the server return some results
				
				if (tmpString==null){
					resultText.setText("null");
				}
				else if ("".equals(tmpString)){
					resultText.setText("empty");
				}
				else resultText.setText(tmpString);
				
				tmpString="";//only need when use send() method*/
				super.handleMessage(msg);
			}
		};
	}

	//the method of interface of OnChangedListener
	@Override
	public void OnChanged(boolean CheckState) {
		// TODO Auto-generated method stub
		if(CheckState){
			district="本部";
			new Thread(uiThead).start();
		}
		else {
			district="宏福";
			new Thread(uiThead).start();
		}
	}

	class UIThread implements Runnable{
		public void run(){
			Message msg=new Message();
			uiHandler.sendMessage(msg);
		}
	}
	
	class NetThread implements Runnable{
		public void run(){
			send1();
			Bundle bundle=new Bundle();
			bundle.putString("data",tmpString);
			//intent.putExtras(bundle);
			//startActivity(intent);
			
			Message msg=new Message();
			netHandler.sendMessage(msg);
		}
	}
	//unused
	private void send(){
		String target ="http://"+IP+":80/phpdb1.php"; 
		URL url;
		try {
			url = new URL(target);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setRequestMethod("POST"); 
			urlConn.setDoInput(true); 
			urlConn.setDoOutput(true); 
			urlConn.setUseCaches(false); 
			urlConn.setInstanceFollowRedirects(true);	
			urlConn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded"); 
			DataOutputStream out = new DataOutputStream(
					urlConn.getOutputStream()); 
			String param = "district="
					+ URLEncoder.encode(district, "utf-8")
					+"&building="
					+URLEncoder.encode(building, "utf-8")
					+"&classroom="
					+URLEncoder.encode(classroom, "utf-8")
					+"&weekDay="
					+URLEncoder.encode(weekDay, "utf-8")
					+"&timeItv="
					+URLEncoder.encode(timeItv, "utf-8");
			out.writeBytes(param);
			out.flush();	
			out.close();	
			
			// 判断是否响应成功
			if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStreamReader in = new InputStreamReader(
						urlConn.getInputStream()); 
				BufferedReader buffer = new BufferedReader(in);
				String inputLine = null;
				while ((inputLine = buffer.readLine()) != null) {
					tmpString += inputLine + "\n";
					//get the respond returned by the server
				}
				in.close();	
			}
			urlConn.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void send1(){
		HttpPost httpPost=new HttpPost("http://"+IP+":80/phpdb1.php");

		try{
			List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(5);
			nameValuePairs.add(new BasicNameValuePair("district",district));
			nameValuePairs.add(new BasicNameValuePair("building",building));
			nameValuePairs.add(new BasicNameValuePair("classroom",classroom));
			nameValuePairs.add(new BasicNameValuePair("weekDay",weekDay));
			nameValuePairs.add(new BasicNameValuePair("timeItv",timeItv));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"GBK"));
			
			HttpClient httpClient=new DefaultHttpClient();
			HttpResponse response=httpClient.execute(httpPost);
			HttpEntity entity=response.getEntity();
			
			if (response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				//getContent1(entity);
				getContent2(entity);
				//the methods above are used to get the results from the server with the format of JSON
			}
			else{
				System.out.print("connection failed");
			}
		}catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }
	}
	//unused
	private void getContent1(HttpEntity entity){
		try {
			tmpString=EntityUtils.toString(entity);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getContent2(HttpEntity entity){
		InputStream is;
		try {
			is = entity.getContent();
			BufferedReader reader=new BufferedReader(new InputStreamReader(is,"GBK"),8);
			StringBuilder sb=new StringBuilder();
			String line=null;
			while((line=reader.readLine())!=null){
				sb.append(line+"/n");
			}
			is.close();
			tmpString=sb.toString();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Wheel scrolled listener
		OnWheelScrollListener scrolledListener = new OnWheelScrollListener()  {
			@Override
			public void onScrollingStarted(WheelView wheel) {
				wheelScrolled = true;
			}
			@Override
			public void onScrollingFinished(WheelView wheel) {
				wheelScrolled = false;
				updateStatus();
			}
		};

		// Wheel changed listener
		private final OnWheelChangedListener changedListener = new OnWheelChangedListener() {
			@Override    
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				Log.d(TAG, "onChanged, wheelScrolled = " + wheelScrolled);    
				if (!wheelScrolled) {
					updateStatus();
				}
			}
		};

		/**
		 * Updates entered PIN status
		 */
		private void updateStatus() {
			new Thread(uiThead).start();
		}

		/**
		 * Initializes wheel
		 *
		 * @param id
		 *          the wheel widget Id
		 */
		private void initWheel(int id, String[] wheelMenu1) {
			WheelView wheel = (WheelView) findViewById(id);
			wheel.setAdapter(new ArrayWheelAdapter<String>(wheelMenu1));
			wheel.setVisibleItems(3);
			wheel.setCurrentItem(0);
			wheel.setCyclic(true);
			wheel.addChangingListener(changedListener);
			wheel.addScrollingListener(scrolledListener);
		}

		/**
		 * Returns wheel by Id
		 *
		 * @param id
		 *          the wheel Id
		 * @return the wheel with passed Id
		 */
		private WheelView getWheel(int id) {
			return (WheelView) findViewById(id);
		}
		
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.activity_query_buddy, menu);
			return true;
		}
}