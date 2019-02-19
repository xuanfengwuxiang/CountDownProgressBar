# CountDownProgressBar
首次提交

这是一个带有，圆环动画的倒计时控件，很常用。

示例：
<com.xuanfeng.countdownprogressview.CountDownProgressBar
        app:text_ring_space="0dp"
        android:background="#ffff00"
        android:id="@+id/count_down"
        app:progress_width="5dp"
        app:count_down_time="15"
        app:default_ring_width="5dp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
         />

使用：

allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}

implementation 'com.github.xuanfengwuxiang:CountDownProgressBar:1.3'


![控件样子](https://github.com/xuanfengwuxiang/CountDownProgressBar/blob/master/photo/demo.png)
