package cn.air.doopen.utli;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 处理http POST json请求
 * 
 */
public class IotPost {

    private final static String TAG = "IotPost:";

    /**
     * 发送POST请求
     * 
     * @param url
     *            请求的API地址
     * @param param
     *            JSON对象的参数
     * @return null 或 json对象的返回,使用时要记得判断是否null
     */
    public static JSONObject post(String url, JSONObject param) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        // 请求超时 30秒
        httpclient.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
        // 读取超时 10秒
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                10000);
        MyLog.i(TAG+"url", url);
        MyLog.i(TAG+"postdata", param.toString());
        try {

            // 构建POST数据(JSON字符串)
            StringEntity se = new StringEntity(param.toString(), HTTP.UTF_8);
            httppost.setEntity(se);
            // 设置POST头信息
            httppost.setHeader("Content-Type", "text/html");
            httppost.setHeader("User-Agent", "cn.xieweiming.app");
            httppost.setHeader("Accept-Encoding", "deflate");
            // 发出Post请求,获取返回数据；
            HttpResponse response;
            response = httpclient.execute(httppost);
            // http返回正确码200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 获得服务器返回的文本
                String tmp = EntityUtils.toString(response.getEntity(), "utf-8");
//                MyLog.i("tmp=",tmp);
                // MyLog.d("html", tmp);
                // 转成Json对象
                JSONObject result = new JSONObject(tmp);
                MyLog.i(TAG+"respdata", result.toString());
                return result;
            } else {
                // 否则是服务器异常返回,检测服务器状态和代码
                MyLog.e(TAG+"posterr", "无法返回数据，服务器异常");
            }
        } catch (JSONException e) {
            // TODO JSON 异常,当返回的内容无法转换成JSON对象时,出现此异常
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO 编码异常
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO 客户端协议异常
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 输入输出异常
            e.printStackTrace();
        }
        return null;
    }
}
