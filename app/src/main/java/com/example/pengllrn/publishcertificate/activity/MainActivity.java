package com.example.pengllrn.publishcertificate.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.base.BaseNfcActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseNfcActivity implements View.OnClickListener {
    /**
     * TextView选择框
     */
    private TextView mManufacturerTv;//生产厂家
    private TextView mMaterialTv;//材质
    private TextView mGoodsNameTv;//物品名称
    private TextView mModelTv;//规格型号
    private TextView mSnTv;//位号批号
    private TextView mProduceDateTv;//生产日期
    private TextView mPublishNum;//发证数量
    private EditText mTagIdEt;//标签序列号

    /**
     * popup窗口里的ListView
     */
    private ListView mManufacturerLv;//生产厂家
    private ListView mMaterialLv;//材质
    private ListView mGoodsNameLv;//物品名称
    private ListView mModelLv;//规格型号
    private ListView mSnLv;//位号批号
    private ListView mProduceDateLv;//生产日期
    private ListView mPublishNumLv;//发证次数

    /**
     * popup窗口
     */
    private PopupWindow manufacturerSelectPopup;
    private PopupWindow materialSelectPopup;
    private PopupWindow goodnameSelectPopup;
    private PopupWindow modelSelectPopup;
    private PopupWindow snSelectPopup;
    private PopupWindow producedateSelectPopup;
    private PopupWindow mPublishNumPopup;

    /**
     * 模拟的popwindow中各listview中的数据
     */
    private List<String> testData1;
    private List<String> testData2;
    private List<String> testData3;
    private List<String> testData4;
    private List<String> testData5;
    private List<String> testData6;
    private List<Integer> testData7;

    /**
     * 数据适配器
     */
    private ArrayAdapter<String> testDataAdapter1;
    private ArrayAdapter<String> testDataAdapter2;
    private ArrayAdapter<String> testDataAdapter3;
    private ArrayAdapter<String> testDataAdapter4;
    private ArrayAdapter<String> testDataAdapter5;
    private ArrayAdapter<String> testDataAdapter6;
    private ArrayAdapter<Integer> testDataAdapter7;

    /**
     * ImageView
     */
    private ImageView imageView1;   //发证
    private ImageView imageView2;   //复制
    private ImageView imageView3;   //复制比对
    private ImageView imageView4;   //初始化
//    private ImageView imageView5;   //aid写入

    /**
     * 保存随机数
     */
    private String passnum = "11111111";

    private Intent intent;

    /**
     * 保存物品名称等数据
     */
    private String mManufacturer;//生产厂家
    private String mMaterial;//材质
    private String mModel;//规格型号
    private String mSn;//位号组号
    private String mGoodsName;//物品名称
    private String mProduceDate;//生产日期
    private int publishNum = 0;//发证次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoodsNameTv.setText("物资名称");
        mModelTv.setText("规格型号");
        mMaterialTv.setText("材质");
        mSnTv.setText("位号/批号");
        mManufacturerTv.setText("生产厂家");
        mProduceDateTv.setText("生产日期");
        mPublishNum.setText("发证数量");
        mGoodsName = "";
        mModel = "";
        mMaterial = "";
        mSn = "";
        mManufacturer = "";
        mProduceDate = "";
        publishNum = 0;
        SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("goods_name", mGoodsName);
        editor.putString("model",mModel);
        editor.putString("material", mMaterial);
        editor.putString("sn",mSn);
        editor.putString("manufacturer",mManufacturer);
        editor.putString("produce_date",mProduceDate);
        editor.putString("tagid",mTagIdEt.getText().toString());
        editor.putInt("publishNum",publishNum);
        editor.apply();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        mGoodsNameTv = (TextView) findViewById(R.id.tv_select_goodsname);
        mModelTv = (TextView) findViewById(R.id.tv_select_model);
        mMaterialTv = (TextView) findViewById(R.id.tv_select_material);
        mSnTv = (TextView) findViewById(R.id.tv_select_sn);
        mManufacturerTv = (TextView) findViewById(R.id.tv_select_manufacturer);
        mProduceDateTv = (TextView) findViewById(R.id.tv_select_producedata);
        mPublishNum = (TextView) findViewById(R.id.tv_publish_num);
        mTagIdEt = (EditText) findViewById(R.id.center_view);
        imageView1 = (ImageView) findViewById(R.id.my_fa);
        imageView2 = (ImageView) findViewById(R.id.my_copy);
        imageView3 = (ImageView) findViewById(R.id.my_compare);
        imageView4 = (ImageView) findViewById(R.id.my_initialization);
//        imageView5 = (ImageView) findViewById(R.id.center_view);
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        mGoodsNameTv.setOnClickListener(this);
        mModelTv.setOnClickListener(this);
        mMaterialTv.setOnClickListener(this);
        mSnTv.setOnClickListener(this);
        mManufacturerTv.setOnClickListener(this);
        mProduceDateTv.setOnClickListener(this);
        mPublishNum.setOnClickListener(this);
        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        imageView4.setOnClickListener(this);
//        imageView5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_select_goodsname:
                // 点击控件后显示popup窗口
                initGoodsNameSelectPopup();
                // 使用isShowing()检查popup窗口是否在显示状态
                if (goodnameSelectPopup != null && !goodnameSelectPopup.isShowing()) {
                    goodnameSelectPopup.showAsDropDown(mGoodsNameTv, 0, 10);
                }
                break;
            case R.id.tv_select_model:
                // 点击控件后显示popup窗口
                initModelSelectPopup();
                // 使用isShowing()检查popup窗口是否在显示状态
                if (modelSelectPopup != null && !modelSelectPopup.isShowing()) {
                    modelSelectPopup.showAsDropDown(mModelTv, 0, 10);
                }
                break;
            case R.id.tv_select_material:
                // 点击控件后显示popup窗口
                initMaterialSelectPopup();
                // 使用isShowing()检查popup窗口是否在显示状态
                if (materialSelectPopup != null && !materialSelectPopup.isShowing()) {
                    materialSelectPopup.showAsDropDown(mMaterialTv, 0, 10);
                }
                break;
            case R.id.tv_select_sn:
                // 点击控件后显示popup窗口
                initSnSelectPopup();
                // 使用isShowing()检查popup窗口是否在显示状态
                if (snSelectPopup != null && !snSelectPopup.isShowing()) {
                    snSelectPopup.showAsDropDown(mSnTv, 0, 10);
                }
                break;
            case R.id.tv_select_manufacturer:
                // 点击控件后显示popup窗口
                initManufacturerSelectPopup();
                // 使用isShowing()检查popup窗口是否在显示状态
                if (manufacturerSelectPopup != null && !manufacturerSelectPopup.isShowing()) {
                    manufacturerSelectPopup.showAsDropDown(mManufacturerTv, 0, 10);
                }
                break;
            case R.id.tv_select_producedata:
                // 点击控件后显示popup窗口
                initProduceDateSelectPopup();
                // 使用isShowing()检查popup窗口是否在显示状态
                if (producedateSelectPopup != null && !producedateSelectPopup.isShowing()) {
                    producedateSelectPopup.showAsDropDown(mProduceDateTv, 0, 10);
                }
                break;
            case R.id.tv_publish_num:
                //点击控件后显示popup窗口
                iniSelectPopup2();
                // 使用isShowing()检查popup窗口是否在显示状态
                if (mPublishNumPopup != null && !mPublishNumPopup.isShowing()) {
                    mPublishNumPopup.showAsDropDown(mPublishNum, 0, 10);
                }
                break;
            case R.id.my_fa:
                SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("goods_name", mGoodsName);
                editor.putString("model",mModel);
                editor.putString("material", mMaterial);
                editor.putString("sn",mSn);
                editor.putString("manufacturer",mManufacturer);
                editor.putString("produce_date",mProduceDate);
                editor.putString("tagid",mTagIdEt.getText().toString());
                editor.putInt("publishNum",publishNum);
                editor.apply();
                intent = new Intent(MainActivity.this, WriteTextActivity.class);
                startActivity(intent);
                break;
            case R.id.my_copy:
                intent = new Intent(MainActivity.this, CopyActivity.class);
                startActivity(intent);
                break;
            case R.id.my_compare:
                intent = new Intent(MainActivity.this,CompareActivity.class);
                startActivity(intent);
                break;
            case R.id.my_initialization:
                intent = new Intent(MainActivity.this,InitializationActivity.class);
                startActivity(intent);
                break;
//            case R.id.center_view:
//                intent = new Intent(MainActivity.this,AidPublishActivity.class);
//                startActivity(intent);
//                break;
            default:
                break;
        }
    }

    /**
     * 初始化popupwindow1
     */
    private void initManufacturerSelectPopup() {
        mManufacturerLv = new ListView(this);
        testData1 = new ArrayList<String>();
        testData1.add("BSS");
        testData1.add("PGI");
        testData1.add("西普企业");
        testData1.add("伯乐");
        testData1.add("华荣防爆");
        testData1.add("Lone Star");
        testData1.add("北京布莱迪");
        testData1.add("Honey well");
        testData1.add("沧海重工");
        testData1.add("乐山市热工仪表");
        testData1.add("NELES-JAMESBVRY(SHANGHAI)");
        testData1.add("朗华博瑞");
        // 设置适配器
        testDataAdapter1 = new ArrayAdapter<String>(this, R.layout.popup_text_item, testData1);
        mManufacturerLv.setAdapter(testDataAdapter1);

        // 设置ListView点击事件监听
        mManufacturerLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 在这里获取item数据
                String value = testData1.get(position);
                mManufacturer = value;
                // 把选择的数据展示对应的TextView上
                mManufacturerTv.setText(value);
                // 选择完后关闭popup窗口
                manufacturerSelectPopup.dismiss();
            }
        });
        manufacturerSelectPopup = new PopupWindow(mManufacturerLv, mManufacturerTv.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
        // 取得popup窗口的背景图片
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_corner);
        manufacturerSelectPopup.setBackgroundDrawable(drawable);
        manufacturerSelectPopup.setFocusable(true);
        manufacturerSelectPopup.setOutsideTouchable(true);
        manufacturerSelectPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 关闭popup窗口
                manufacturerSelectPopup.dismiss();
            }
        });
    }

    /**
     * 初始化popwindow2
     */
    private void initGoodsNameSelectPopup() {
        mGoodsNameLv = new ListView(this);
        testData2 = new ArrayList<String>();
        testData2.add("4路485集线器/共享器");
        testData2.add("焊接截止阀");
        testData2.add("热收缩带");
        testData2.add("伯乐焊条");
        testData2.add("防爆金属软管");
        testData2.add("截止阀");
        testData2.add("耐震压力表");
        testData2.add("双阀组截止阀");
        testData2.add("内外螺纹活接头");
        testData2.add("报警器");
        testData2.add("凸台");
        testData2.add("三通");
        testData2.add("同心大小头");
        testData2.add("节流截止放空阀");
        testData2.add("防爆接线盒");
        testData2.add("防爆接线箱");
        testData2.add("外螺纹活接头");
        testData2.add("电伴热");
        testData2.add("90度无缝弯头");
        //设置适配器
        testDataAdapter2 = new ArrayAdapter<String>(this, R.layout.popup_text_item, testData2);
        mGoodsNameLv.setAdapter(testDataAdapter2);

        //设置ListView的点击事件
        mGoodsNameLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 在这里获取item数据
                String value = testData2.get(position);
                // 把选择的数据展示对应的TextView上
                mGoodsNameTv.setText(value);
                mGoodsName = value;
                // 选择完后关闭popup窗口
                goodnameSelectPopup.dismiss();
            }
        });

        goodnameSelectPopup = new PopupWindow(mGoodsNameLv, mGoodsNameTv.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_corner);
        goodnameSelectPopup.setBackgroundDrawable(drawable);
        goodnameSelectPopup.setFocusable(true);
        goodnameSelectPopup.setOutsideTouchable(true);
        goodnameSelectPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //关闭popup窗口
                goodnameSelectPopup.dismiss();
            }
        });
    }
    /**
     * 初始化popwindow3
     */
    private void initModelSelectPopup() {
        mModelLv = new ListView(this);
        testData3 = new ArrayList<String>();
        testData3.add("BSS-485HUB-4");
        testData3.add("AK-200YCP-YY6");
        testData3.add("CEP-6");
        testData3.add("E10018-G/E 10045-P2");
        testData3.add("BNGII-700 G50F-G40M");
        testData3.add("V506SCP 1/2PT(M)x1/2NPT(F)");
        testData3.add("0-16mpa");
        testData3.add("A7-537-CO");
        testData3.add("Neotronics Impulse X4");
        testData3.add("PN40 DN200X80");
        testData3.add("PN40 DN200X25");
        testData3.add("PN40 DN50X5");
        testData3.add("PN40 DN50X25X5");
        testData3.add("FJ41 Y-class600 1寸");
        testData3.add("PN100 DN25");
        testData3.add("BHC-F");
        testData3.add("1/2NPT(M)X1/2NPT(M)");
        testData3.add("ZR-ZXW-220V");
        testData3.add("PN40 DN80X5");
        //设置适配器
        testDataAdapter3 = new ArrayAdapter<String>(this, R.layout.popup_text_item, testData3);
        mModelLv.setAdapter(testDataAdapter3);

        //设置ListView的点击事件
        mModelLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 在这里获取item数据
                String value = testData3.get(position);
                // 把选择的数据展示对应的TextView上
                mModelTv.setText(value);
                mModel = value;
                // 选择完后关闭popup窗口
                modelSelectPopup.dismiss();
            }
        });

        modelSelectPopup = new PopupWindow(mModelLv, mModelTv.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_corner);
        modelSelectPopup.setBackgroundDrawable(drawable);
        modelSelectPopup.setFocusable(true);
        modelSelectPopup.setOutsideTouchable(true);
        modelSelectPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //关闭popup窗口
                modelSelectPopup.dismiss();
            }
        });
    }

    /**
     * 初始化popwindow4
     */
    private void initMaterialSelectPopup() {
        mMaterialLv = new ListView(this);
        testData4 = new ArrayList<String>();
        testData4.add("316SS");
        testData4.add("A350");
        testData4.add("WFHY-245");
        testData4.add("A105N");
        //设置适配器
        testDataAdapter4 = new ArrayAdapter<String>(this, R.layout.popup_text_item, testData4);
        mMaterialLv.setAdapter(testDataAdapter4);

        //设置ListView的点击事件
        mMaterialLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 在这里获取item数据
                String value = testData4.get(position);
                // 把选择的数据展示对应的TextView上
                mMaterialTv.setText(value);
                mMaterial = value;
                // 选择完后关闭popup窗口
                materialSelectPopup.dismiss();
            }
        });

        materialSelectPopup = new PopupWindow(mMaterialLv, mMaterialTv.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_corner);
        materialSelectPopup.setBackgroundDrawable(drawable);
        materialSelectPopup.setFocusable(true);
        materialSelectPopup.setOutsideTouchable(true);
        materialSelectPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //关闭popup窗口
                materialSelectPopup.dismiss();
            }
        });
    }

    /**
     * 初始化popwindow5
     */
    private void initSnSelectPopup() {
        mSnLv = new ListView(this);
        testData5 = new ArrayList<String>();
        testData5.add("2121349");
        testData5.add("00D023-PI-7002");
        testData5.add("15687-7");
        testData5.add("386-72");
        testData5.add("15380-40");
        testData5.add("15386-64");
        testData5.add("F1004416");
        //设置适配器
        testDataAdapter5 = new ArrayAdapter<String>(this, R.layout.popup_text_item, testData5);
        mSnLv.setAdapter(testDataAdapter5);

        //设置ListView的点击事件
        mSnLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 在这里获取item数据
                String value = testData5.get(position);
                // 把选择的数据展示对应的TextView上
                mSnTv.setText(value);
                mSn = value;
                // 选择完后关闭popup窗口
                snSelectPopup.dismiss();
            }
        });

        snSelectPopup = new PopupWindow(mSnLv, mSnTv.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_corner);
        snSelectPopup.setBackgroundDrawable(drawable);
        snSelectPopup.setFocusable(true);
        snSelectPopup.setOutsideTouchable(true);
        snSelectPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //关闭popup窗口
                snSelectPopup.dismiss();
            }
        });
    }

    /**
     * 初始化popwindow6
     */
    private void initProduceDateSelectPopup() {
        mProduceDateLv = new ListView(this);
        testData6 = new ArrayList<String>();
        testData6.add("2010.9");
        testData6.add("2010.7");
        testData6.add("2010.7.19");
        testData6.add("2011.4");
        testData6.add("2010.5");
        testData6.add("2010.6");
        testData6.add("2016.11.22");
        //设置适配器
        testDataAdapter6 = new ArrayAdapter<String>(this, R.layout.popup_text_item, testData6);
        mProduceDateLv.setAdapter(testDataAdapter6);

        //设置ListView的点击事件
        mProduceDateLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 在这里获取item数据
                String value = testData6.get(position);
                // 把选择的数据展示对应的TextView上
                mProduceDateTv.setText(value);
                mProduceDate = value;
                // 选择完后关闭popup窗口
                producedateSelectPopup.dismiss();
            }
        });

        producedateSelectPopup = new PopupWindow(mProduceDateLv, mProduceDateTv.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_corner);
        producedateSelectPopup.setBackgroundDrawable(drawable);
        producedateSelectPopup.setFocusable(true);
        producedateSelectPopup.setOutsideTouchable(true);
        producedateSelectPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //关闭popup窗口
                producedateSelectPopup.dismiss();
            }
        });
    }

    /**
     * 初始化发证数量popwindow
     */
    private void iniSelectPopup2() {
        mPublishNumLv = new ListView(this);
        testData7 = new ArrayList<Integer>();
        testData7.add(1);
        testData7.add(2);
        testData7.add(3);
        testData7.add(4);
        testData7.add(5);
        //设置适配器
        testDataAdapter7 = new ArrayAdapter<Integer>(this, R.layout.popup_text_item, testData7);
        mPublishNumLv.setAdapter(testDataAdapter7);

        //设置ListView点击事件
        mPublishNumLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 在这里获取item数据
                int value = testData7.get(position);
                // 把选择的数据展示对应的TextView上
                mPublishNum.setText(value + "");
                publishNum = value;
                // 选择完后关闭popup窗口
                mPublishNumPopup.dismiss();
            }
        });
        mPublishNumPopup = new PopupWindow(mPublishNumLv, mPublishNum.getWidth(), ActionBar.LayoutParams.WRAP_CONTENT, true);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_corner);
        mPublishNumPopup.setBackgroundDrawable(drawable);
        mPublishNumPopup.setFocusable(true);
        mPublishNumPopup.setOutsideTouchable(true);
        mPublishNumPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //关闭popup窗口
                mPublishNumPopup.dismiss();
            }
        });
    }
}
