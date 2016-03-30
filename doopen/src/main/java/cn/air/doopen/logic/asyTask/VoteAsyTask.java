package cn.air.doopen.logic.asyTask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import cn.air.doopen.socket.IotUser;
/**删除消息异步任务；*/
public class VoteAsyTask {
	Context context;
	VoteAsyTask asyTask;
	public VoteAsyTask getinfo(){
		if (asyTask!=null) {
			asyTask=new VoteAsyTask(context);
		}
		return asyTask;
	}
	public VoteAsyTask(Context context) {
		this.context=context;
	}
	public void voretask(int msgId,int answerid){
		new voreTask().execute(msgId,answerid);
	}
	public class voreTask extends AsyncTask<Integer,Integer,Integer>{
		//后面尖括号内分别是参数（例子里是线程休息时间），进度(publishProgress用到)，返回值 类型
		protected Integer doInBackground(Integer... params) {
			IotUser iotUser=new IotUser(context);
			int msg=iotUser.vote(params[0], params[1]);
			return msg;
		}
	}
	public int deleteshareMsg(int msgid){
		new deleteshareMsg().execute(msgid);
		return code;
	}
	//删除
	private int code;
	public class deleteshareMsg extends AsyncTask< Integer, Integer, Integer>{
		@Override
		protected Integer doInBackground(Integer... arg0) {
			IotUser iotUser=new IotUser(context);
			code=iotUser.deleteidDevmsg(arg0[0]);
			return code;
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (code==0) {
				Toast.makeText(context, "已删除", 2).show();
			}else{
				Toast.makeText(context, "无法删除", 2).show();
			}
		}
	}
}
