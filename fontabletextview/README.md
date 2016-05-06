# FontableTextView

支持设置自定义字体的TextView,默认字体的路径为assets\fonts目录;
也可以通过`FontManager.initFontFolder(getFilesDir().getParentFile());`设置字体存放的目录


# 1.通过app:fontPath直接设置字体
```[xml]
<com.rdshoep.android.view.FontableTextView
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"
       app:fontPath="BebasNeue Bold.ttf" />
```


# 2.也可以通过样式设置字体,先在style.xml中定义样式BoldNumberTextStyle
```
<style name="BoldNumberTextStyle">
    <item name="fontPath">BebasNeue Bold.ttf</item>
</style>
```
然后在具体需要使用的位置设置样式即可
```
<com.rdshoep.android.view.FontableTextView
   android:layout_width="wrap_content"
   android:layout_height="wrap_content"
   style="@style/BoldNumberTextStyle" />
```
