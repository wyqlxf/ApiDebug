# ApiDebugUpgraded
它是一个接口管理（展示/查询）、接口模拟请求以及监听我们主应用程序请求接口的实时情况的一款开发调试工具

![image](https://github.com/WyqOrganization/ApiDebugUpgraded/blob/master/image/Introduction.png)


# 功能描述
 1. 展示了Android/iOS/触屏端接口名和参数 ；
 2. 拥有接口描述和各个字段的名词解释说明 ；
 3. 允许直接访问接口，并且支持JSON数据视图化 ；
 4. 请求返回的数据支持查看cookie、请求头和响应头 ；
 5. 模拟请求支持接口名和参数的修改，然后再次请求 ；
 6. 支持安卓端的接口监控，可以实时查看安卓端应用程式的接口调用情况 ；
 7. 支持接口收藏，可以收藏你需要的接口，方便二次查阅 ；
 8. 支持应用程式“最新测试版”的下载，方便测试工程师下载最新版本测试 ；
 9. 支持接口过滤，把不需要显示的接口加入黑名单，同时也支持移除过滤 ；
 10. 支持环境切换，可以把接口一键切换成debug或者线上；
 

# 注意事项
下载项目之后，需要对自己的项目进行简单的配置。
在项目中找到Config.kt文件，配置你自己的文件路径名、本地数据的加密秘钥以及接口域名和主应用程序的包名和类名即可。

主应用程序需要在接口请求之前调用以下代码：
    
    // get请求时，保存接口日志
    public static void saveLog(String url) {
        if (!TextUtils.isEmpty(url)) {
            String text = "type=Get;url=" + url + ";time=" + System.currentTimeMillis();
            save(text);
        }
    }

    // post请求时候，保存接口日志
    public static void saveLog(String url, String parameter) {
        if (!TextUtils.isEmpty(url)) {
            String text = "type=Post;url=" + url + ";parameter=" + parameter + ";time=" + System.currentTimeMillis();
            save(text);
        }
    }

    // 接口日志保存到本地
    private synchronized static void save(String text) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ActivityCompat.checkSelfPermission(BaseApplication.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // 如果是android6.0以上，并且没有权限
                return;
            }
            if (!TextUtils.isEmpty(text)) {
                String aesEncryptString = AesEncryptUtil.INSTANCE.aesEncryptString(text, "YourEncryptionKey");
                if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                    File fileDir = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/YourFileName/encrypt/");
                    if (!fileDir.exists()) {
                        fileDir.mkdirs();
                    }
                    File file = new File(fileDir, System.currentTimeMillis() + ".txt");
                    if (file.exists()) {
                        file.delete();
                    }
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                    out.write(aesEncryptString);
                    out.flush();
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
   # 更新日志
    
