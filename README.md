## FloatItemView - 两步 一行代码轻松实现RecyclerView的条目悬浮吸顶效果。

#### 第一步完成XML布局文件
```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.kelin.floatitemdemo.MainActivity" >
    <!--RecyclerView-->
    <android.support.v7.widget.RecyclerView
        android:id="@+id/rvList"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <!--悬浮系统条目控件-->
    <com.kelin.floatitemdemo.FloatItemView
        android:id="@+id/flItem"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
</RelativeLayout>
```

#### 第二部 通过代码关联RecyclerView。
```
//这行代码执行时要保证已经为RecyclerView设置了适配器。
((FloatItemView)findViewById(R.id.flItem)).attachToRecyclerView(recyclerView, R.layout.item_title_layout, new FloatItemView.DataBinding() {
    @Override
    public void onBind(@NonNull FloatItemView floatItemView, int position) {
        //这里是为悬浮控件绑定数据
        ((TextView) floatItemView.findViewById(R.id.tvTitle)).setText((CharSequence) items.get(position));
    }
});
```

#### 第三步 恭喜你已经完成了悬浮吸顶效果的列表。