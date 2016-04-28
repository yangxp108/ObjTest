package com.example.he.NetAsyncTask;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.he.ListviewAdapter.vhAdapter;
import com.example.he.batteryinfoActivity.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HE on 2016/1/25.
 * 这是一个联网获得二级表（电池信息）的线程
 */
public class getTableTwo extends AsyncTask<Object, Object, String>{
    // WSDL文档中的命名空间
    private static final String targetNameSpace = "http://tempuri.org/";
    // WSDL文档中的URL
    private static final String WSDL = "http://192.168.1.111:1666/Datebase.asmx";

    // 需要调用的方法名
    private static final String methodname = "getTableTwo";
    private List<Map<String,String>> tlistItems = new ArrayList<Map<String,String>>();
    private List<Map<String,String>> listItems = new ArrayList<Map<String,String>>();
    private ListView mtitlelistview,mListView;
    private Context mcontext;
    private String mExpid,mid;
    private Handler mhandler;
    private int x,y;
    ProgressDialog pd;

    public getTableTwo(Context context, String Expid, String id, ListView titleListview, ListView listView, Handler handler){
        mcontext = context;
        mExpid = Expid;
        mid = id;
        mtitlelistview = titleListview;
        mListView = listView;
        mhandler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mcontext);
        pd.setMessage("载入中…");
        pd.setIndeterminate(false);// 在最大值最小值中移动
        pd.setCancelable(true);// 可以取消
        pd.show();
    }

    @Override
    protected void onPostExecute(String result) {
        //耗时计算
        Date dt1= new Date();
        Long t1= dt1.getTime();

        pd.dismiss();
        //Toast.makeText(mcontext,"加载表格",Toast.LENGTH_SHORT).show();
        if (result.equals("success")) {
            //列表适配器
            SimpleAdapter tsimpleAdapter = new SimpleAdapter(mcontext, tlistItems, R.layout.tabletitle_item,
                    new String[] {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14"},
                    new int[]{R.id.t0,R.id.t1,R.id.t2,R.id.t3,R.id.t4,R.id.t5,R.id.t6,R.id.t7,R.id.t8,R.id.t9,R.id.t10,R.id.t11,R.id.t12,R.id.t13,R.id.t14});
            mtitlelistview.setAdapter(tsimpleAdapter);
//            vhAdapter msimpleAdapter = new vhAdapter(mcontext, listItems, R.layout.table_item,
//                    new String[] {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14"},
//                    new int[]{R.id.t0,R.id.t1,R.id.t2,R.id.t3,R.id.t4,R.id.t5,R.id.t6,R.id.t7,R.id.t8,R.id.t9,R.id.t10,R.id.t11,R.id.t12,R.id.t13,R.id.t14});
//            mListView.setAdapter(msimpleAdapter);
//            vhAdapter tsimpleAdapter = new vhAdapter(mcontext,tlistItems);
//            mtitlelistview.setAdapter(tsimpleAdapter);
            vhAdapter msimpleAdapter = new vhAdapter(mcontext,listItems);
            mListView.setAdapter(msimpleAdapter);

            Date dt2= new Date();
            long t2 = dt2.getTime();
            Log.d("time-adapter",String.valueOf(t2-t1));

        }else {
            Toast.makeText(mcontext, "载入失败", Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(result);
    }

    @Override
    protected String doInBackground(Object... params) {
        // 根据命名空间和方法得到SoapObject对象
        SoapObject soapObject = new SoapObject(targetNameSpace,
                methodname);
        soapObject.addProperty("expid",mExpid);
        soapObject.addProperty("id",mid);
        // 通过SOAP1.1协议得到envelop对象
        SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // 将soapObject对象设置为envelop对象，传出消息

        envelop.dotNet = true;
        envelop.setOutputSoapObject(soapObject);
        // 或者envelop.bodyOut = soapObject;
        HttpTransportSE httpSE = new HttpTransportSE(WSDL);

        //耗时计算
        Date dt1= new Date();
        Long t1= dt1.getTime();

        // 开始调用远程方法
        try {
            httpSE.call(targetNameSpace + methodname, envelop);
            // 得到远程方法返回的SOAP对象
            SoapObject resultObj = (SoapObject) envelop.getResponse();
            // 得到服务器传回的数据
            int num = Integer.parseInt(resultObj.getProperty(0).toString());
            int count = Integer.parseInt(resultObj.getProperty(1).toString());
            x = Integer.parseInt(resultObj.getProperty(2).toString());
            y = Integer.parseInt(resultObj.getProperty(3).toString());

            int cursor = 3;
            Map<String,String> tlistItem = new HashMap<String, String>();
            for (int i=0; i < count; i++) {
                cursor++;
                tlistItem.put(String.valueOf(i), resultObj.getProperty(cursor).toString());
            }
            tlistItems.add(tlistItem);
            num = num/count;
            for (int j=0;j<num;j++){
                Map<String,String> listItem = new HashMap<String, String>();
                for (int i= 0;i<count;i++){
                    cursor++;
                    listItem.put(String.valueOf(i), resultObj.getProperty(cursor).toString());
                }
                listItems.add(listItem);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException";
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return "XmlPullParserException";
        }

        Date dt2= new Date();
        long t2 = dt2.getTime();
        Log.d("time-getresultObj",String.valueOf(t2-t1));

        //用handler向主线程传数据
        Message message = Message.obtain();
        message.what = 999;
        message.obj = listItems;
        message.arg1 = x;
        message.arg2 = y;
        mhandler.sendMessage(message);
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.sendMessage(message);

        return "success";
    }
}
