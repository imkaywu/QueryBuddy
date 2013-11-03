package com.example.querybuddy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ResultList extends Activity {
	private ListView listView;
	private List<HashMap<String,Object>> dataList=new ArrayList<HashMap<String,Object>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result_list);
		
		listView=(ListView)this.findViewById(R.id.listView);
		adaptData();
		Bundle bundle=this.getIntent().getExtras();
		String tmpString=bundle.getString("data");
		parseJSON(tmpString);
	}

	private void parseJSON(String tmpString){
		JSONArray jsonArray=null;
		JSONObject jsonObject=null;
		HashMap<String,Object> map=new HashMap<String,Object>();
		try {
			
			jsonArray=new JSONArray(tmpString);
			for(int i=0;i<jsonArray.length();i++){
				jsonObject=jsonArray.getJSONObject(i);
				Log.i("log_info","district"+jsonObject.getString("district")+",building"+jsonObject.getString("building")+",classroom"+jsonObject.getString("classroom")+",totalNum"+jsonObject.getString("totalNum")+",currentNum"+jsonObject.getString("currentNum"));
				map.put("district", jsonObject.getString("district"));
				map.put("building", jsonObject.getString("building"));
				map.put("classroom", jsonObject.getString("classroom"));
				map.put("totalNum", jsonObject.getInt("totalNum"));
				map.put("currentNum", jsonObject.getString("currentNum"));
				dataList.add(map);
			}/*
			jsonObject=new JSONObject(tmpString);
			Log.i("log_info","district:"+jsonObject.getString("district")+",building:"+jsonObject.getString("building")+",classroom:"+jsonObject.getString("classroom")+",totalNum:"+jsonObject.getString("totalNum")+",currentNum:"+jsonObject.getString("currentNum"));
			map.put("district", jsonObject.getString("district"));
			map.put("building", jsonObject.getString("building"));
			map.put("classroom", jsonObject.getString("classroom"));
			map.put("totalNum", jsonObject.getInt("totalNum"));
			map.put("currentNum", jsonObject.getString("currentNum"));
			dataList.add(map);*/
			Log.i("log","district:"+map.get("district")+",building:"+map.get("building")+",classroom:"+map.get("classroom")+",totalNum:"+map.get("totalNum")+",currentNum:"+map.get("currentNum"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void adaptData(){
		SimpleAdapter simpleAdapter=new SimpleAdapter(ResultList.this,dataList,R.layout.query_listview,new String[]{"district","building","classroom","totalNum","currentNum"},
				new int[]{R.id.district,R.id.building,R.id.classroom,R.id.totalNum,R.id.currentNum});
		listView.setAdapter(simpleAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_result_list, menu);
		return true;
	}

}
