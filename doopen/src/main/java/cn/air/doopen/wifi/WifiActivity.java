package cn.air.doopen.wifi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.air.doopen.hy.R;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//搜索wifi热点后的获取热点列表；点击热点列表返回配置界面；输入相应的热点密码然后传给设备；
public class WifiActivity extends Activity implements
        SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeLayout;
    private ListView mListView;
    WifiAdmin wifiAdmin = null;
    private ArrayList<HashMap<String, Object>> list;
    private BaseAdapter adapter;
	private Button fujin_wifi_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        wifiAdmin = new WifiAdmin(this);
        list = new ArrayList<HashMap<String, Object>>();
        RefreshList();

        adapter = new BaseAdapter() {
            public View getView(int position, View convertView, ViewGroup parent) {
                LinearLayout layout = new LinearLayout(getApplicationContext());
                layout.setOrientation(LinearLayout.VERTICAL); // 垂直布局
                HashMap<String, Object> map = list.get(position);
                TextView txtName = new TextView(getApplicationContext());
                TextView txtSignal = new TextView(getApplicationContext());
                txtName.setText(""+map.get("wifi_name"));
                txtName.setTextSize(20);
                txtName.setTextColor(Color.rgb(0, 0, 0));
                txtSignal.setText(""+map.get("wifi_bssid"));
                layout.addView(txtName);
                layout.addView(txtSignal);
                layout.setPadding(10, 5, 10, 5);
                return layout;
            }

            public long getItemId(int position) {
                return 0;
            }

            public Object getItem(int position) {
                return null;
            }

            public int getCount() {
                return list.size();
            }
        };

        mListView = (ListView) findViewById(R.id.id_listview);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        fujin_wifi_result = (Button) findViewById(R.id.fujin_wifi_result);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeColors(Color.RED);

        mListView.setAdapter(adapter);
        
        // 绑定列表点击事件
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("ssid", (String) list.get(arg2).get("wifi_name"));
                bundle.putString("bssid", (String) list.get(arg2).get("wifi_bssid"));
                bundle.putInt("targchannel", (Integer) list.get(arg2).get("wifi_targchannel"));
                resultIntent.putExtras(bundle);
                WifiActivity.this.setResult(RESULT_OK, resultIntent);
                WifiActivity.this.finish();
            }
        });
        fujin_wifi_result.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
    }

    @Override
    public void onRefresh() {
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                list.clear();
                RefreshList();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                adapter.notifyDataSetChanged();
                mSwipeLayout.setRefreshing(false);
            }

        }.execute(null, null, null);

    }

    private  ArrayList<HashMap<String, Object>> RefreshList() {
        if (wifiAdmin.checkState() == WifiManager.WIFI_STATE_ENABLED) {
            wifiAdmin.startScan(); // 扫描wifi热点，前提是wifi已经打开
            List<ScanResult> wifiList = wifiAdmin.getWifiList();
            for (int index = 0; index < wifiList.size(); index++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("wifi_name", (wifiList.get(index)).SSID);
                map.put("wifi_bssid",  (wifiList.get(index)).BSSID );
                map.put("wifi_targchannel", (WifiAdmin.getChannelByFrequency(wifiList.get(index).frequency) ));
                list.add(map);
            }
        }else{
            Toast.makeText(this, "未开启WIFI", Toast.LENGTH_LONG).show();
        }
		return list;
    }
}
