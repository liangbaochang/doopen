package cn.air.doopen.socket;

/**
 * 对于请求从网络获取资料的操作,要在操作执行后,检测执行结果状态
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import cn.air.doopen.config.Deploy;
import cn.air.doopen.hyapp.MyApp;
import cn.air.doopen.logic.ControlToOfficialOrTestServer;
import cn.air.doopen.socket.uploadingimg.UploadUtil;
import cn.air.doopen.utli.IotPost;
import cn.air.doopen.utli.MyLog;

/**所有的http请求接口；统一管理*/
public final class IotUser implements Deploy{
	// 状态标志(要跟服务器统一)
	public static final int IOT_STATE_OK = 0;
	public static final int IOT_STATE_ERR = 1;
	public static final int IOT_STATE_UNKNOWER = 2;
	private static final String TAG = "IotUser:";
	private int mState = IOT_STATE_UNKNOWER; // 默认状态
	private Context context;
	private List<Map<String, Object>> mDevList = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> muserlist = new ArrayList<Map<String, Object>>();
	SharedPreferences config;
	final String url;
	public IotUser(Context context) {
		this.context = context;
		this.config = this.context.getSharedPreferences("config", Context.MODE_PRIVATE);
		url=new ControlToOfficialOrTestServer().http_url();
	}
	public String getToken() {
		return config.getString("token", "");
	}

	/**
	 * 滤芯复位记录；
	 * 
	 * @return
	 * @throws IotApiException
	 */
	public List<Map<String, Object>> filter_historys(int devid) {
		Map<String, Object> map = null;
		int ret = 0;
		try {
			// 提交参数
			JSONObject param = new JSONObject();
			param.put("action", "getfilterreset");
			param.put("devid", devid);
			param.put("token", getToken());
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					mDevList.clear();
					JSONArray jsonObjs = retJson.getJSONArray("filters");
					for (int i = 0; i < jsonObjs.length(); i++) {
						JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
						map = new HashMap<String, Object>();
						map.put("filter_id", jsonObj.getInt("id"));
						map.put("devid", jsonObj.getInt("devid"));
						map.put("filter_name", jsonObj.getString("filtername"));
						map.put("filter_time", jsonObj.getString("time"));
						mDevList.add(map);
						MyLog.i("获取列表", "" + "获取数据成功");
					}
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		Collections.reverse(mDevList);
		return mDevList;
	}
	/**
	 * 获取设备列表
	 * 
	 * @return
	 * @throws IotApiException
	 */
	public List<Map<String, Object>> getDevList() {
		Map<String, Object> map = null;
		int ret = 0;
		try {
			// 提交参数
			JSONObject param = new JSONObject();
			param.put("action", "getdevlist");
			param.put("token", getToken());
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					mDevList.clear();
					JSONArray jsonObjs = retJson.getJSONArray("devlist");
					for (int i = 0; i < jsonObjs.length(); i++) {
						JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
						map = new HashMap<String, Object>();
						map.put("type", jsonObj.getString("model"));
						map.put("id", jsonObj.getInt("devid"));
						map.put("name", jsonObj.getString("name"));
						map.put("owner", jsonObj.getString("owner"));
						map.put("client_id", jsonObj.getInt("client_id"));
						map.put("devkey", jsonObj.getString("devkey"));
						map.put("ver", jsonObj.getString("ver"));
						mDevList.add(map);
						MyLog.i("获取设备列表", "" + "获取数据成功");
					}
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		Collections.reverse(mDevList);
		return mDevList;
	}
	/**获取报修列表*/
	public List<Map<String, Object>> getrepairist() {
		Map<String, Object> map = null;
		int ret = 0;
		try {
			// 提交参数
			JSONObject param = new JSONObject();
			param.put("action", "repairlist");
			param.put("token", getToken());
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					mDevList.clear();
					JSONArray jsonObjs = retJson.getJSONArray("repair");
					for (int i = 0; i < jsonObjs.length(); i++) {
						JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
						map = new HashMap<String, Object>();

						map.put("type", jsonObj.getString("model"));
						map.put("id", jsonObj.getInt("id"));
						map.put("name", "未命名");
						map.put("owner", jsonObj.getString("addr"));
						map.put("client_id", jsonObj.getInt("phone"));
						map.put("devkey", jsonObj.getString("status"));
						map.put("ver", jsonObj.getString("fault"));

						//						map.put("id", jsonObj.getInt("id"));
						//						map.put("devid", jsonObj.getInt("devid"));
						//						map.put("type", jsonObj.getString("model"));
						//						map.put("fault", jsonObj.getString("fault"));
						//						map.put("name", jsonObj.getString("name"));
						//						map.put("phone", jsonObj.getString("phone"));
						//						map.put("addr", jsonObj.getString("addr"));
						//						map.put("status", jsonObj.getInt("status"));
						mDevList.add(map);
						MyLog.i("获取报修列表", "" + "获取报修列表成功");
					}
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		Collections.reverse(mDevList);
		return mDevList;
	}

	public List<Map<String, Object>> getrepair_schedule(int viewrepairid) {
		Map<String, Object> map = null;
		int ret = 0;
		try {
			// 提交参数
			JSONObject param = new JSONObject();
			param.put("action", "viewrepair");
			param.put("token", getToken());
			param.put("id", viewrepairid);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					mDevList.clear();
					JSONArray jsonObjs = retJson.getJSONArray("repair");
					for (int i = 0; i < jsonObjs.length(); i++) {
						JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
						map = new HashMap<String, Object>();
						map.put("status", jsonObj.getInt("status"));
						map.put("progress", jsonObj.getString("progress"));
						map.put("time", jsonObj.getString("time"));
						mDevList.add(map);
						MyLog.i("获取报修列表", "" + "获取报修列表成功");
					}
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		Collections.reverse(mDevList);
		return mDevList;
	}

	/**
	 * 获取个人资料信息；
	 * 
	 * @return
	 * @throws IotApiException
	 */
	public List<Map<String, Object>> getuserMsg() {
		Map<String, Object> map = null;
		int ret = 0;
		try {
			// 提交参数
			JSONObject param = new JSONObject();
			param.put("action", "getmyinfo");
			param.put("token", getToken());
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret==103) {
					return null;
				}
				if (ret == 0) {
					muserlist.clear();
					JSONObject jsonObjs = retJson.getJSONObject("userinfo");
					map = new HashMap<String, Object>();
					map.put("nick", jsonObjs.getString("nick"));      //妮称；
					map.put("sex", jsonObjs.getInt("sex"));  //性别；
					map.put("birthday", jsonObjs.getString("birthday"));//生日；
					map.put("email", jsonObjs.getString("email"));//邮箱；
					map.put("realname", jsonObjs.getString("realname"));//真是姓名；
					map.put("addr", jsonObjs.getString("addr"));//地址；
					map.put("phone", jsonObjs.getLong("phone"));//手机号；
					map.put("headimg", jsonObjs.getString("headimg"));//头像文件名；
					muserlist.add(map);
					MyLog.i("获取个人信息", "" + "获取信息成功");
					mState = IOT_STATE_OK;
					Collections.reverse(muserlist);
					return muserlist;
				} else {
					mState = IOT_STATE_ERR;
					return null;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 分享消息列表
	 * 
	 * @return
	 * @throws IotApiException
	 */
	public List<Map<String, Object>> getShareMessageList() {
		Map<String, Object> map = null;
		int ret = 0;
		try {
			// 提交参数
			JSONObject param = new JSONObject();
			param.put("action", "getmsglist");
			param.put("token", getToken());
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				//				MyLog.i("获取设备列表", "" + retJson.toString());
				ret = retJson.getInt("status");
				if (ret == 0) {
					mDevList.clear();
					JSONArray jsonObjs = retJson.getJSONArray("msglist");
					for (int i = 0; i < jsonObjs.length(); i++) {
						JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
						map = new HashMap<String, Object>();
						map.put("id", jsonObj.getInt("id"));    //消息id
						map.put("type", jsonObj.getInt("type")); ///消息类型，普通消息，账号邀请星系；二维码信息；需显示同意界面；
						map.put("content", jsonObj.getString("content"));//消息内容；
						map.put("devid", jsonObj.getInt("devid"));//设备id；
						map.put("source", jsonObj.getString("source"));//发送者账号；
						map.put("dest", jsonObj.getString("dest"));//接受者账号；
						map.put("time", jsonObj.getString("time"));//请求时间；
						map.put("modelname", jsonObj.getString("modelname"));//请求时间；
						map.put("answer", jsonObj.getInt("answer"));//消息应答状态；
						map.put("status", jsonObj.getInt("status"));//消息状态；
						mDevList.add(map);
						//						MyLog.i("获取设备列表", "" + mDevList.toString());
					}
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		//数据倒置；
		Collections.reverse(mDevList);
		return mDevList;
	}

	//	// 测试是否包含指定设备
	//	public boolean IsExsits(String devkey) {
	//		for (int i = 0; i < mDevList.size(); i++) {
	//			if (mDevList.get(i).get("devkey").equals(devkey)) {
	//				return true;
	//			}
	//		}
	//		return false;
	//	}
	/**删除某条记录*/
	public int delsete_id_filter_history(Integer msgid,int devid) {
		int ret = -1;
		try {
			// 提交参数
			JSONObject param = new JSONObject();
			param.put("action", "delfilterreset");
			param.put("id", msgid);
			param.put("devid", devid);
			param.put("token", getToken());
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}
	/**清空复位记录*/
	public int eliminate_id_filter_history_xulik(int devid) {
		int ret = -1;
		try {
			// 提交参数
			JSONObject param = new JSONObject();
			param.put("action", "cleanfilterreset");
			param.put("devid", devid);
			param.put("token", getToken());
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}
	/**添加滤芯复位记录*/
	public int add_filter_history(String filtername,int devid) {
		int ret = -1;
		try {
			// 提交参数
			JSONObject param = new JSONObject();
			param.put("action", "addfilterreset");
			param.put("filtername", filtername);
			param.put("devid", devid);
			param.put("token", getToken());
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}
	/**登出*/
	public int outlogin() {
		int ret = -1;
		try {
			// 提交参数
			JSONObject param = new JSONObject();
			param.put("action", "userlogout");
			param.put("token", getToken());
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}
	/**分享用户列表；*/
	public List<Map<String, Object>> getDevUserList(int devid) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		int ret = 0;
		try {
			// 提交参数
			JSONObject param = new JSONObject();
			param.put("action", "getdevuser");
			param.put("token", getToken());
			param.put("devid", devid);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			MyLog.i("获取设备列表", "" + retJson.toString());
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					JSONArray jsonObjs = retJson.getJSONArray("devuserlist");
					for (int i = 0; i < jsonObjs.length(); i++) {
						JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
						map = new HashMap<String, Object>();
						map.put("id", jsonObj.getInt("id"));
						map.put("name", jsonObj.getString("username"));
						list.add(map);
					}
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		Collections.reverse(list);
		return list;
	}

	/**
	 * 使用账号密码登录服务器
	 * 
	 * @param name
	 *            账号名
	 * @param pwd
	 *            密码
	 * @return
	 * @throws IotApiException
	 */
	public int login(String name, String pwd,boolean code_server) {
		int ret = -1;
		String token = "";
		for (int i = 0; i <10; i++) {
			MyLog.i("进行了HTTP登录", "=====进行了HTTP登录");
		}
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			// 用帐号密码登录
			param.put("action", "userlogin");
			param.put("name", name);
			param.put("pwd", pwd);
			SharedPreferences.Editor editor_code = config.edit();
			editor_code.putBoolean("code_service", code_server);
			editor_code.commit();
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				MyLog.i("进行了HTTP登录", "进行了HTTP登录");
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("进行了HTTP登录", "进行了HTTP登录");
					SharedPreferences.Editor editor = config.edit();
					token = retJson.getString("token");// 返回token
					editor.putString("token", token);
					editor.putBoolean("code_service", code_server);
					//					MyLog.i("="+code_server);
					editor.commit();
					MyLog.i("Login", "登陆成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 修改密码；
	 * 
	 * @param name
	 *          旧密码
	 * @param pwd
	 *            新密码
	 * @return
	 * @throws IotApiException
	 */
	public int update_pwd(String oldpwd, String newpwd) {
		int ret = 0;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			// 用帐号密码登录
			param.put("action", "userpwd");
			param.put("oldpwd", oldpwd);
			param.put("newpwd", newpwd);
			param.put("token", getToken());
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("Login", "密码修改成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 删除消息
	 * 
	 * @param token
	 */
	public int deleteDevmsg() {
		int ret = 0;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			// 用帐号密码登录
			param.put("action", "clearmsg");
			param.put("token", getToken());
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("Login", "删除成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * 删除消息单个；
	 * 
	 * @param token
	 */
	public int deleteidDevmsg(int msgid) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			// 用帐号密码登录
			param.put("action", "deletemsg");
			param.put("token", getToken());
			param.put("msgid", msgid);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("Login", "删除成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * 以TOKEN进行登录,获取用户信息
	 * 
	 * @param token
	 */
	public void login(String token) {
		MyLog.d("Login", "token获取用怀信息中");
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			// 用帐号密码登录
			param.put("action", "loginbytoken");
			param.put("token", token);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.d("Login", "token获取信息成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
	}
	/**
	 * The firmware update query
	 * 固件升级查询；
	 * 
	 * @param token
	 */
	public  List<Map<String, Object>>getfirmware_update(String ver,String model ) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			// 用帐号密码登录
			param.put("action", "checkDevVer"); 
			param.put("ver",ver);
			param.put("model", model);
			param.put("token", getToken());
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret==0) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("fileId",(int)retJson.getInt("fileId"));//版本记录；
					map.put("info", (String)retJson.getString("info"));//描述；
					map.put("ver",(String)retJson.getString("ver"));//版本；
					map.put("time", (String)retJson.getString("time"));//更新时间；
					mDevList.add(map);
					mState = IOT_STATE_OK;
					MyLog.i("Login", "查询成功!");
					Collections.reverse(mDevList);
					return mDevList;
				}else{
					return null;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return null;
	}
	/**
	 *  添加用户信息；
	 * @param nick  妮称；             
	 * @param sex   性别；
	 * @param birthday   生日；
	 * @param email     邮箱；
	 * @param realname    真实姓名；
	 * @param addr     地址；
	 * @return
	 */
	public  int adduserMsg(String nick,int sex,String birthday,String email ,String realname,String addr) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			// 用帐号密码登录
			param.put("action", "setmyinfo"); 
			param.put("token", getToken());
			param.put("nick",nick);
			param.put("sex", sex);
			param.put("birthday", birthday);
			param.put("email", email);
			param.put("realname", realname);
			param.put("addr", addr);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * 提交报修信息
	 * @param devid
	 * @param model
	 * @param fault
	 * @param name
	 * @param phone
	 * @param addr
	 * @return
	 */
	public  int addrepairs(int devid,String model,String fault ,String name,String phone,String addr) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			// 用帐号密码登录
			param.put("action", "addrepair"); 
			param.put("devid",devid);
			param.put("model", model);
			param.put("token", getToken());
			param.put("fault", fault);
			param.put("name", name);
			param.put("phone", phone);
			param.put("addr", addr);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * 4.23.	APP请求更新设备固件
	 * 
	 * @param token
	 */
	public  int  update_firmware(int fileId,int devid ) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			// 用帐号密码登录
			param.put("action", "romUpdate");
			param.put("fileId", fileId);
			param.put("devid",devid);
			param.put("token", getToken());
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("Login", "查询成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * 同意表决；
	 * 
	 */
	public  int vote (int msgId,int answerid ) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			// 用帐号密码登录
			param.put("action", "answermsg");
			param.put("token", getToken());
			param.put("msgid", msgId);
			param.put("answer",answerid);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("Login", "查询成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 注册帐号,用手机号和密码(由于系统前期考虑欠周,系统里用name字段标识phone字段)
	 * 
	 * @param phone
	 * @param pwd
	 * @throws IotApiException
	 */
	public int register(String phone, String pwd,String identifier,int rand_code) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			// 用帐号密码登录
			param.put("action", "newuserreg");
			param.put("phone", phone);
			param.put("pwd", pwd);
			param.put("identifier", identifier);
			param.put("rand_code", rand_code);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("Register", "注册成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * 找回密码
	 * 
	 * @param phone
	 * @param pwd
	 * @throws IotApiException
	 */
	public int restpwd(String phone, String pwd,String identifier,int rand_code) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			// 用帐号密码登录
			param.put("action", "userresetpwd");
			param.put("phone", phone);
			param.put("newpwd", pwd);
			param.put("identifier", identifier);
			param.put("rand_code", rand_code);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("Register", "注册成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * 发送验证码！
	 * 
	 * @param phone
	 * @param pwd
	 * @throws IotApiException
	 */
	public int sendverification(String phone) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			// 用帐号密码登录
			param.put("action", "usersendcode");
			param.put("phone", phone);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				MyApp.cidentifier= retJson.getString("identifier");
				if (ret == 0) {
					MyLog.i("Register", "发送成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}

	public int checkState() {
		return mState;
	}

	// 反馈；
	public void feedback(String text) {
		int ret = -1;

		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			// 用帐号密码登录
			param.put("action", "feedback");
			param.put("token", getToken());
			param.put("content", text);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("Feedback", "发表反馈成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
	}

	/// smartconfig模式绑定设备
	public int bindDevsm(String devkey) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			param.put("action", "binddevex");
			param.put("token", getToken());
			param.put("devkey", devkey);
			param.put("unbindall", 1);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("binddev", "绑定新设备成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}
	/// AP模式绑定设备
	public int bindDev(String devkey) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			param.put("action", "binddev");
			param.put("token", getToken());
			param.put("devkey", devkey);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("binddev", "绑定新设备成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}

	public void unbindDev(int devid) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			param.put("action", "unbinddev");
			param.put("token", getToken());
			param.put("devid", devid);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("unbinddev", "解绑设备成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
	}

	public void renameDev(String name, int devid) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			param.put("action", "renamedev");
			param.put("token", getToken());
			param.put("devid", devid);
			param.put("name", name);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("renamedev", "重命名成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
	}

	public void shareDev(int uid, int devid) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			param.put("action", "sharedev");
			param.put("token", getToken());
			param.put("devid", devid);
			param.put("userid", uid);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("sharedev", "分享设备成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
	}
	//分享设备；
	public int shareDev(String username, int devid) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			param.put("action", "sharedev");
			param.put("token", getToken());
			param.put("devid", devid);
			param.put("name", username);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("sharedev", "分享设备成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return ret;
	}
	//二维码分享
	public List<Map<String, Object>> shareDevQR(int devid,String username, int overdue,int verify) {
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		Map<String, Object> map=new HashMap<String, Object>();
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		//		{“action”：“sharedevqr”，“username”：“用户名”，“devid”：“设备id”，“token”：“xxxxxxxx”，“overdue”：有效时间，“verify”：是否需要再验证}
		try {
			param.put("action", "sharedevqr");
			param.put("username", username);
			param.put("devid", devid);
			param.put("token", getToken());
			param.put("overdue", overdue);
			param.put("verify", verify);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				map.put("returned", ret);
				list.add(map);
				if (ret == 0) {
					map.put("Quickmark", retJson.getString("code"));
					list.add(map);
					MyLog.i("sharedevqr", "创建设备分享二维码成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return list;
	}
	//二维码获取设备控制权限；
	public void scanDevQR(String qrcode) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			param.put("action", "scanqr");
			param.put("token", getToken());
			param.put("qrcode", qrcode);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					MyLog.i("scanqr", "使用分享二维码添加设备成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
	}

	public void unshareDev(int uid, int devid) {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			param.put("action", "unsharedev");
			param.put("token", getToken());
			param.put("devid", devid);
			param.put("userid", uid);

			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");

				if (ret == 0) {

					MyLog.i("unsharedev", "解除分享成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
	}

	public void unshareDev(String username, int devid) {
		int ret = -1;

		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			param.put("action", "unsharedev");
			param.put("token", getToken());
			param.put("devid", devid);
			param.put("name", username);
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");

				if (ret == 0) {

					MyLog.i("unsharedev", "解除分享成功!");
					mState = IOT_STATE_OK;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
	}

	public int getAppVersion() {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (pi != null) {
			MyLog.i(TAG, "versionCode=" + String.valueOf(pi.versionCode));
			return pi.versionCode;
		} else {
			return 0;
		}
	}

	public String getAppVersionName() {

		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		if (pi != null) {
			return pi.versionName;
		} else {
			return "";
		}
	}


	/*
	 * 检测APP新版本
	 *
	 * @return JSON对象格式的版本信息描述
	 */
	public String[] checkAppUpgrade() {
		int ret = -1;
		JSONObject param = new JSONObject(); // 请求的JSON参数
		try {
			param.put("action", "checkAppVer");
			param.put("token", getToken());
			param.put("name", "多朋家居");
			param.put("ver", getAppVersion());
			JSONObject retJson = IotPost.post(new ControlToOfficialOrTestServer().http_url(), param);
			if (retJson != null) {
				ret = retJson.getInt("status");
				if (ret == 0) {
					String info = retJson.getString("info");
					String time = retJson.getString("time");
					String ver = retJson.getString("ver");
					String fileid = retJson.getString("fileId");
					MyLog.i("CheckAppVer", "检测更新成功!");
					mState = IOT_STATE_OK;
					String[] strings = {"版本号:"+ver+"\n发布时间:"+time+"\n描述:"+info,fileid};
					return strings;
				} else {
					mState = IOT_STATE_ERR;
				}
			} else {
				mState = IOT_STATE_ERR;
			}
		} catch (JSONException e) {
			mState = IOT_STATE_ERR;
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 检测APP新版本
	 *
	 * @return JSON对象格式的版本信息描述
	 */
	public Uri udpatgeappurl(int fileid) {
		Uri URL=Uri.parse(new ControlToOfficialOrTestServer().update_app()+getToken()+"&id="+fileid);
		return URL;
	}
	public int uploadingimg(String fileuri) {
		String imgurl = new ControlToOfficialOrTestServer().add_http_img_url();
		int ret=-1;
		try {
			String urls=imgurl+"?token="+getToken();
			if (fileuri!=null) {
				String inde=fileuri.substring(fileuri.indexOf("."));
				String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
				MyLog.i(TAG+"urls====", urls);
				MyLog.i(TAG+"fileuri======", fileuri);
				MyLog.i(TAG+"inde======", inde);
				final String[] split =BOUNDARY.split("-");
				StringBuffer buffer= new StringBuffer();
				for (String ss : split) {
					String replaceAll= ss.replaceAll("^,|,$", "");
					if (replaceAll.length() > 0) {
						//叠加字符串；
						buffer.append(replaceAll);
					}
				}
				///绑定设备
				MyLog.i(TAG, buffer.toString()+inde);
				String result=UploadUtil.uploadFile(fileuri, buffer.toString()+inde, urls);
				if (result!=null) {
					JSONObject retJson = new JSONObject(result);
					MyLog.i(TAG+"result", ""+result);
					ret = retJson.getInt("status");
					return ret;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;
	}

}
