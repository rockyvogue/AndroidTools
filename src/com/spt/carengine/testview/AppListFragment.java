
package com.spt.carengine.testview;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.spt.carengine.R;
import com.spt.carengine.view.arc.MyArcDrawable;
import com.spt.carengine.view.arc.MyArcSelDrawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AppListFragment extends Fragment implements OnItemClickListener {

    private ArrayList<String> appList = new ArrayList<String>(); // 用来存储获取的应用信息数据

    private ListView listview = null;
    private List<AppInfo> mlistAppInfo = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_app_list, null);
        ListView parent = (ListView) v.findViewById(R.id.listView1);

        MyArcDrawable md = new MyArcDrawable(getActivity()).asMain();
        parent.setBackgroundDrawable(md);
        parent.setDrawingCacheEnabled(true);
        Drawable selector = parent.getSelector();
        // MyArcSelDrawable.printDW( selector , "-before-main-");
        selector = MyArcSelDrawable.copyFrom(selector, md.getArcAble());
        // MyArcSelDrawable.printDW( selector , "-main-");
        parent.setSelector(selector);

        // 初始化列表数据
        mlistAppInfo = new ArrayList<AppInfo>();
        queryAppInfo(); // 查询所有应用程序信息

        AppAdapter browseAppAdapter = new AppAdapter(getActivity(),
                mlistAppInfo);
        parent.setAdapter(browseAppAdapter);
        parent.setOnItemClickListener(this);

        return v;
    }

    // 获得所有APP的信息
    public void queryAppInfo() {
        PackageManager pm = getActivity().getPackageManager(); // 获得PackageManager对象
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
                PackageManager.GET_UNINSTALLED_PACKAGES);
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos,
                new ResolveInfo.DisplayNameComparator(pm));
        if (mlistAppInfo != null) {
            mlistAppInfo.clear();
            for (ResolveInfo reInfo : resolveInfos) {
                String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
                if (!pkgName.equals("com.example.testview")) {// 过滤自己
                    String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
                    String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
                    Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
                    // 为应用程序的启动Activity 准备Intent
                    Intent launchIntent = new Intent();
                    launchIntent.setComponent(new ComponentName(pkgName,
                            activityName));
                    // 创建一个AppInfo对象，并赋值
                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppLabel(appLabel);
                    appInfo.setPkgName(pkgName);
                    appInfo.setAppIcon(icon);
                    appInfo.setIntent(launchIntent);
                    mlistAppInfo.add(appInfo); // 添加至列表中
                    System.out.println(appLabel + " activityName---"
                            + activityName + " pkgName---" + pkgName);
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position,
            long arg3) {
        // TODO Auto-generated method stub
        Intent intent = mlistAppInfo.get(position).getIntent();
        startActivity(intent);
    }
}
