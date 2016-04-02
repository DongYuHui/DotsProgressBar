# DotsProgressBar #

该自定义控件来源于一个项目中的需求，虽然当时取消了此功能点，但是个人认为自己实现一下是值得的，多多练习自定义控件非常地有必要。该自定义控件是为了在引导用户的若干个步骤中呈现一个进度条，大致的效果如下：

这里简略记录一下实现的过程：

## 一、明确需求 ##

既然该功能在实际项目中已经取消了，那么需求点我们就自己设置：

1. 可以自定义指示器数量，进度条宽度等属性
2. 可以动态展示当前已经进行到了哪一步，向前或者向后
3. 可以同 ViewPager 结合来展示进度

目前为止（2016.04.02）虽然实现了功能，但是还不完善。此后希望能修改得更人性化，体验更好一些。

## 二、实现过程 ##

### 1. 自定义属性 ###

自己思考了一下，主要需要的自定义属性有如下这些：

- 指示器的数量
- 指示器圆点的半径
- 指示器之间进度条的宽度
- 进度条的背景色与前景色
- 进度条动态展示时的速度

以上中，需要注意，进度条的宽度不能大于整个控件的高度（即圆点的直径）。对于速度的控制，最后决定利用枚举提供给用户快中慢三个选择。最主要的是该采用何种方式来控制动画的速度？思来想去并且通过实践发现，速度可以为一个整型值，代表每两个圆点之间需要重绘的次数，控件通过 postInvalidateDelayed 方法刷新界面，整型值越大，重绘次数越多，每段动画的速度就越慢。

### 2. 绘制背景 ###

此处比较简单，根据自定义属性绘制矩形和圆点即可。

### 3. 绘制动画 ###

动画分为两种，向前时的动画，向后时的动画，我暂时分开计算。除去最开始的单个点，之后的动画每段都是绘制一个矩形和一个节点。我们定死，绘制进度条占该段动画的 9/10 时间，绘制圆点占 1/10 时间。同时在动画进行时的数值采用插值器的方式来给予，这样不仅免去了我们自己计算坐标的琐事，同时也很方便地向外部暴露了控制动画的方式。

具体代码可以查看控件类。

### 4. 调试优化 ###

此处我们对细节以及一些边界做完善。