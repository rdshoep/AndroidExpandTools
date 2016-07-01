# RulerView
## 支持功能
1.支持数字大小排序方式（小-->大 或者 大-->小）
2.支持水平布局和数值布局方式
3.支持最小刻度的值和刻度之间的间隔，与功能4配合实现自定义的刻度布局方式
4.支持自定义的刻度模式（默认3111121111），支持方便支持显示刻度数值的刻度值（默认只显示最大类型刻度的数值）
5.支持刻度数值的颜色、大小
6.自定义指针的Drawable（需要根据水平和数值方向确定图像资源的宽和高）
7.自定义刻度布局方向（水平方式支持上、下；竖直方向支持左、右）
8.支持封面
9.支持为每个级别的刻度都设置相对应的指针图像资源

可以直接添加<code>compile 'com.rdshoep.android:rulerview:1.3'</code>，将RulerView添加到项目中去

```[xml]

    <attr name="minValue" format="float" />
    <attr name="maxValue" format="float" />
    <attr name="initValue" format="float" />

    <!--刻度显示的精度-->
    <attr name="tickValue" format="float" />
    <!--刻度间隔-->
    <attr name="interval" format="dimension|reference" />
    <!--刻度显示类别格式-->
    <attr name="tickFormat" format="string|reference" />
    <!--刻度组单位起始参考值，用于处理其实值非整数的问题-->
    <attr name="unitStartValue" format="float" />

    <!--刻度默认颜色-->
    <attr name="tickColor" format="color|reference" />
    <!--刻度默认宽度-->
    <attr name="tickWidth" format="dimension|reference" />
    <!--单位高度-->
    <attr name="unitHeight" format="dimension|reference" />
    <!--显示数值的Level,和tickFormat中的值相对应-->
    <attr name="displayValueLevel" format="integer" />

    <!--刻度值字体大小-->
    <attr name="labelFontSize" format="dimension" />
    <!--刻度值字体颜色-->
    <attr name="labelFontColor" format="color|reference" />
    <!--刻度值字体-->
    <attr name="labelFont" format="string" />
    <!--文字偏移量-->
    <attr name="labelOffset" format="dimension|reference" />

    <!--指针-->
    <attr name="pointerDrawable" format="reference" />
    <!--封面-->
    <attr name="frontDrawable" format="reference" />
    <!--5种指针Drawable设置-->
    <attr name="tick0" format="reference" />
    <attr name="tick1" format="reference" />
    <attr name="tick2" format="reference" />
    <attr name="tick3" format="reference" />
    <attr name="tick4" format="reference" />

    <!--布局方向   水平/竖直-->
    <attr name="direction" format="enum">
        <enum name="horizontal" value="1" />
        <enum name="vertical" value="2" />
    </attr>

    <!--刻度布局方式-->
    <attr name="align" format="enum">
        <!--水平方向时，向上布局；数值方向时，向下布局-->
        <enum name="start" value="1" />
        <!--水平方向时，向下布局；数值方向时，向上布局-->
         <enum name="end" value="2" />
    </attr>

    <!--数值顺序  从大到小/从小到大-->
    <attr name="order" format="enum">
        <enum name="asc" value="1" />
        <enum name="desc" value="2" />
    </attr>
'''

### Change list
#1.2
* Remove rulerStyle attribute "contentPadding,contentPaddingLeft,contentPaddingRight,contentPaddingTop,contentPaddingBottom"
* change "orientation" ==> "direction", solve duplicate attribute problem with other aar package

### Todo List
* 尺子的起始值不为整数倍的集合单元（不为整数值）


### Bugs
* 蓄力滑动存在问题，蓄力滑动某些情况下会导致跳跃现象


