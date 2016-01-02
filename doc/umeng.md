1. 友盟账号
  Android 唯一Appkey为：5680e9b3e0f55a72ce00358f
  iPhone 唯一Appkey为：5680ebb367e58e4945002f59
  iPad 唯一Appkey为：5680ec1a67e58eea48001f48

2. Android下面的activity请extends BaseActivity.这个里面加了统计的逻辑

3. Android平台下打一个记录点的过程如下：
<1> 在StatReporter里面添加事件的名称好比：APP_LAUNCH
<2> 在StatReporter里面添加事件方法：public static void onAppLaunch()
<3> 在要锚点的地方加上：StatReporter.onAppLaunch（）
<4> 最关键的一点去UMENG平台添加事件点：
    * login: http://www.umeng.com/apps
    * 点击我的产品中要统计的产品
    * 点击左下的编辑指标－》管理事件
    * 添加事件名称，好比APP_LAUNCH







