package com.example.mydbdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import android.webkit.WebView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static String DB_NAME="mydb";
    private EditText et_word;
    private EditText et_mean;
    private EditText et_egg;
    private ArrayList<Map<String,Object>>data;
    private dbHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private SimpleAdapter listAdapter;
    private View view;
    private ListView listView;
    private Button selBtn,addBtn,updBtn,delBtn;
    private Map<String,Object>item;
    private String selID;
    private ContentValues selCV;
    private EditText queText;
    private Button queButtonn;

    //查询按钮申明
    private Button myButton01;
    //清空按钮申明
    private Button myButton02;
    //输入框申明
    private EditText mEditText1;
    //加载数据的WebView申明
    private WebView mWebView1;
    // private SearchView searchView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.help_item:
                Toast.makeText(this,"这是帮助",Toast.LENGTH_SHORT).show();
                break;

            default:

        }
        return true;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(this.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT){

            setContentView(R.layout.main);
        }
        else{
            setContentView(R.layout.landscapet);
        }

        //获得布局的几个控件
        myButton01 = (Button)findViewById(R.id.myButton01);
        myButton02 = (Button) findViewById(R.id.myButton02);
        mEditText1 = (EditText) findViewById(R.id.myEditText1);
        mWebView1 = (WebView) findViewById(R.id.myWebView1);
        queButtonn=(Button) findViewById(R.id.queButton);
        queText=(EditText) findViewById(R.id.queEditText);

        et_word=(EditText) findViewById(R.id.et_word);
        et_mean=(EditText) findViewById(R.id.et_mean);
        et_egg=(EditText) findViewById(R.id.et_egg);
        listView=(ListView) findViewById(R.id.listView);
        selBtn=(Button) findViewById(R.id.bt_query);
        addBtn=(Button) findViewById(R.id.bt_add);
        updBtn=(Button) findViewById(R.id.bt_modify);
        delBtn=(Button) findViewById(R.id.bt_del);

        queButtonn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String questr=(queText.getText().toString());
                ArrayList<Map<String, String>> items=null;
                //既可以使用Sql语句查询，也可以使用方法查询
                SearchUseSql(questr);
                // items=Search(txtSearchWord);
            }
        });
/*
        queButtonn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String questr=(queText.getText().toString());
                ArrayList<Map<String, String>> items=null;
                //既可以使用Sql语句查询，也可以使用方法查询
                items=SearchUseSql(questr);
                // items=Search(txtSearchWord);

                if(items.size()>0) {
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("result",items);
                    Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else
                    Toast.makeText(MainActivity.this,"没有找到",Toast.LENGTH_LONG).show();
            }
        });
*/


        myButton01.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View arg0)
            {
                String strURI = (mEditText1.getText().toString());
                strURI = strURI.trim();
                //如果查询内容为空提示
                if (strURI.length() == 0)
                {
                    Toast.makeText(MainActivity.this, "查询内容不能为空!", Toast.LENGTH_LONG)
                            .show();
                }
                //否则则以参数的形式从http://dict.youdao.com/m取得数据，加载到WebView里.
                else
                {
                    String strURL = "http://dict.youdao.com/m/search?keyfrom=dict.mindex&q="
                            + strURI;
                    mWebView1.loadUrl(strURL);
                }
            }
        });
        myButton02.setOnClickListener(new Button.OnClickListener()
        {
            public void onClick(View arg0)
            {

                //否则则以参数的形式从http://dict.youdao.com/m取得数据，加载到WebView里.

                    String strURL ="http://m.enread.com";
                    mWebView1.loadUrl(strURL);


            }
        });
        //http://dict.youdao.com/m/search?keyfrom=dict.mindex&q=happy
        //清空按钮添加事件，将EditText置空

        selBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbFindAll();


            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAdd();
                dbFindAll();

            }
        });
        updBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbUpdate();
                dbFindAll();

            }
        });
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbDel();
                dbFindAll();

            }
        });
        dbHelper=new dbHelper(this,DB_NAME,null,1);
        db=dbHelper.getWritableDatabase();
        data=new ArrayList<Map<String,Object>>();
        dbFindAll();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,Object>listItem=(Map<String,Object>)
                        listView.getItemAtPosition(position);
                et_word.setText((String) listItem.get("word"));
                et_mean.setText((String) listItem.get("mean"));
                et_egg.setText((String) listItem.get("egg"));
                selID=(String) listItem.get("_id");
                Log.i("mydbDemo","id="+selID);
            }
        });
    }
    //数据删除
    protected void dbDel(){
        String where="_id="+selID;
        int i=db.delete(com.example.mydbdemo.dbHelper.TB_NAME,where,null);
        if(i>0)
            Log.i("myDbDemo","数据删除成功！");
        else
            Log.i("myDbDemo","数据未删除！");
    }
    //更新列表中的数据
    private void showList(){
        listAdapter=new SimpleAdapter(this,data,
                R.layout.listview,
                new String[]{"_id","word","mean","egg"},
                new int[]{R.id.tvID,R.id.tvWord,R.id.tvMean,R.id.tvEgg});
        listView.setAdapter(listAdapter);
    }
    //数据更新
    protected void dbUpdate(){
        ContentValues values=new ContentValues();
        values.put("word",et_word.getText().toString().trim());
        values.put("mean",et_mean.getText().toString().trim());
        values.put("egg",et_egg.getText().toString().trim());
        String where="_id="+selID;
        int i=db.update(com.example.mydbdemo.dbHelper.TB_NAME,values,where,null);
        if(i>0)
            Log.i("myDbDemo","数据更新成功！");
        else
            Log.i("myDbDemo","数据未更新！");
    }
    //插入数据
    protected void dbAdd(){
        ContentValues values=new ContentValues();
        values.put("word",et_word.getText().toString().trim());
        values.put("mean",et_mean.getText().toString().trim());
        values.put("egg",et_egg.getText().toString().trim());
        long rowid=db.insert(com.example.mydbdemo.dbHelper.TB_NAME,null,values);
        if(rowid!=-1)
            Log.i("myDbDemo","数据插入失败！");
        else
            Log.i("myDbDemo","数据插入成功！");
    }
    //查询数据
    protected void dbFindAll(){
        data.clear();
        cursor=db.query(com.example.mydbdemo.dbHelper.TB_NAME,null,null,null,null,
                null,"_id ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String id=cursor.getString(0);
            String word=cursor.getString(1);
            String mean=cursor.getString(2);
            String egg=cursor.getString(3);
            item=new HashMap<String, Object>();
            item.put("_id",id);
            item.put("word",word);
            item.put("mean",mean);
            item.put("egg",egg);
            data.add(item);
            cursor.moveToNext();

        }
        showList();
    }
    protected void dbFindWord(){
        data.clear();
        String sql  = "select * from " +com.example.mydbdemo.dbHelper.TB_NAME+
                " where word like ?";
        String [] selectionArgs  = new String[]{"%"+et_word+"%"};
        cursor = db.rawQuery(sql, selectionArgs);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String id=cursor.getString(0);
            String word=cursor.getString(1);
            String mean=cursor.getString(2);
            String egg=cursor.getString(3);
            item=new HashMap<String, Object>();
            item.put("_id",id);
            item.put("word",word);
            item.put("mean",mean);
            item.put("egg",egg);
            data.add(item);
            cursor.moveToNext();        }
        showList();
    }
    //使用Sql语句查找
    //private ArrayList<Map<String, String>> SearchUseSql(String strWordSearch)
    private void SearchUseSql(String strWordSearch) {
        //SQLiteDatabase db = dbHelper.getReadableDatabase();
        data.clear();
        String sql="select * from words where word like ? order by word desc";
        //Cursor c=db.rawQuery(sql,new String[]{"%"+strWordSearch+"%"});
        cursor=db.rawQuery(sql,new String[]{"%"+strWordSearch+"%"});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String id=cursor.getString(0);
            String word=cursor.getString(1);
            String mean=cursor.getString(2);
            String egg=cursor.getString(3);
            item=new HashMap<String, Object>();
            item.put("_id",id);
            item.put("word",word);
            item.put("mean",mean);
            item.put("egg",egg);
            data.add(item);
            cursor.moveToNext();

        }
        showList();
        //return ConvertCursor2List(c);
    }

    protected void dbqueFindAll(){
        data.clear();
        cursor=db.query(com.example.mydbdemo.dbHelper.TB_NAME,null,null,null,null,
                null,"_id ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String id=cursor.getString(0);
            String word=cursor.getString(1);
            String mean=cursor.getString(2);
            String egg=cursor.getString(3);
            item=new HashMap<String, Object>();
            item.put("_id",id);
            item.put("word",word);
            item.put("mean",mean);
            item.put("egg",egg);
            data.add(item);
            cursor.moveToNext();

        }
        showList();
    }
}
