package cn.air.doopen.wifi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.air.doopen.hy.R;

public class WifiListActivity extends Activity {

    WifiAdmin wifiAdmin = null;
    private ArrayList<HashMap<String, String>> list;
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);
        wifiAdmin = new WifiAdmin(this);
        list = new ArrayList<HashMap<String, String>>();
        RefreshList();

        final ListView listView = (ListView) findViewById(R.id.list_view2);
        adapter = new BaseAdapter() {
            public View getView(int position, View convertView, ViewGroup parent) {
                LinearLayout layout = new LinearLayout(getApplicationContext());
                layout.setOrientation(LinearLayout.VERTICAL);  //垂直布局
                HashMap<String, String> map = list.get(position);
                TextView txtName = new TextView(getApplicationContext());
                TextView txtSignal = new TextView(getApplicationContext());
                txtName.setText(map.get("wifi_name"));
                txtName.setTextSize(20);
                txtName.setTextColor(Color.rgb(0, 0, 0));
                txtSignal.setText(map.get("wifi_signal"));
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
        
        listView.setAdapter(adapter);
//        listView.setOnDropDownListener(new OnDropDownListener() {
//            
//            @Override
//            public void onDropDown() {
//                new AsyncTask<Void, Void, Void>() {
//                    protected Void doInBackground(Void... params) {
//                        try {
//                            Thread.sleep(1000);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        list.clear();
//                        RefreshList();
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void result) {
//                        adapter.notifyDataSetChanged();
//                        listView.onDropDownComplete();
//                    }
//
//                }.execute(null, null, null);
//            }
//        });
        
    }
    
    private void RefreshList(){
        wifiAdmin.startScan(); // 扫描wifi热点，前提是wifi已经打开
        List<ScanResult> wifiList = wifiAdmin.getWifiList();
        for (int index = 0; index < wifiList.size(); index++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("wifi_name", (wifiList.get(index)).SSID);
            map.put("wifi_signal", "BSSID: "
                    + (wifiList.get(index)).BSSID);
            list.add(map);
        }
    }
}
