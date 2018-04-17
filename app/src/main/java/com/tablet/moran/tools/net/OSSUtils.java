package com.tablet.moran.tools.net;

/**
 * Created by ASUS on 2016/6/27.
 */
public class OSSUtils {

//    private static OSSUtils instance;
//    public static final int PHOTO = 0;
//    public static final int VIDEO = 1;
//
//    //--------------------OSS related---------------
//    private String bucket_name;
//    private String access_key_id;
//    private String access_key_secret;
//    private String endpoint;
//    private Context mContext;
//    public final static String IMAGE_PATH = "image/";
//    public final static String VIDEO_PATH = "video/";
//    //------------------------------------------------
//    private OSSCredentialProvider ossCredentialProvider;
//    private OSS oss;
//    private List<OSSAsyncTask> taskList;
//    private OssCallBack ossCallBack;
//
//    /**
//     * @param context  这里是applicationContext
//     * @param endpoint
//     * @param bucket_name
//     * @param access_key_id
//     * @param access_key_secret
//     */
//    private OSSUtils(Context context, String endpoint,
//                     String bucket_name, String access_key_id, String access_key_secret) {
//        this.bucket_name = bucket_name;
//        this.access_key_id = access_key_id;
//        this.access_key_secret = access_key_secret;
//        this.endpoint = endpoint;
//        this.mContext = context;
//        taskList = new ArrayList<>();
//
//        ClientConfiguration conf = new ClientConfiguration();
//        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
//        conf.setSocketTimeout(30 * 1000); // socket超时，默认15秒
//        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
//        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
//
//        ossCredentialProvider = new OSSPlainTextAKSKCredentialProvider(access_key_id, access_key_secret);
//        oss = new OSSClient(context, endpoint, ossCredentialProvider, conf);
//
//    }
//
////    public static OSSUtils init(Context context, String endpoint,
////                            String bucket_name, String access_key_id, String access_key_secret) {
////        return getInstance(context, endpoint, bucket_name, access_key_id, access_key_secret);
////    }
//
//
//    public static OSSUtils getInstance(Context context, String endpoint,
//                                       String bucket_name, String access_key_id, String access_key_secret) {
//        if (instance == null) {
//            synchronized (RetrofitUtils.class) {
//                if (instance == null) {
//                    instance = new OSSUtils(context, endpoint, bucket_name, access_key_id, access_key_secret);
//                    return instance;
//                }else
//                    return instance;
//            }
//        }else
//            return instance;
//    }
//
//    /**
//     * 上传
//     */
//    public void ossUpload(final String originalPath, final int flag, final OssCallBack ossCallBack){
//
//        final File md5File = new File(originalPath);
//        if(!md5File.exists()){
//            SLogger.d("<<", "-->>>不存在" + originalPath);
//
//            return;
//        }
//        SLogger.d("<<", "-->>>>beforeName-->" + md5File.getName());
//
//        final String parentPath = md5File.getParent();
//        final File newFile = new File(parentPath + File.separator + MD5Util.MD5Encode("fenghuo" + System.currentTimeMillis(), "") + (flag == OSSUtils.VIDEO ? ".mp4" : ".png"));
//        md5File.renameTo(newFile);
//
//        SLogger.d("<<", "-->>>>afterName-->" + newFile.getName());
//        final String filePath = newFile.getAbsolutePath();
//
//
//        // 构造上传请求
//        PutObjectRequest put = new PutObjectRequest(bucket_name, (flag == 0 ? IMAGE_PATH : VIDEO_PATH) + md5File.getName(), filePath);
//
//        // 异步上传时可以设置进度回调
//        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
//            @Override
//            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                ossCallBack.onProgress(request, currentSize, totalSize);
//                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
//            }
//        });
//
//        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
//            @Override
//            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                newFile.renameTo(md5File);
//                ossCallBack.onSuccess(request, result);
//                Log.d("PutObject", "UploadSuccess");
//            }
//
//            @Override
//            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                newFile.renameTo(md5File);
//                ossCallBack.onFailure(request, clientExcepion, serviceException);
////                // 请求异常
////                if (clientExcepion != null) {
////                    // 本地异常如网络异常等
////                    clientExcepion.printStackTrace();
////                }
////                if (serviceException != null) {
////                    // 服务异常
////                    Log.e("ErrorCode", serviceException.getErrorCode());
////                    Log.e("RequestId", serviceException.getRequestId());
////                    Log.e("HostId", serviceException.getHostId());
////                    Log.e("RawMessage", serviceException.getRawMessage());
////                }
//            }
//        });
//
//        taskList.add(task);
//
//    }
//
//
//    public interface OssCallBack{
//        void onProgress(PutObjectRequest request, long currentSize, long totalSize);
//        void onSuccess(PutObjectRequest request, PutObjectResult result);
//        void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException);
//    }
//
//    /**
//     * 取消上传
//     */
//    public void cancelAll(){
//        if(taskList.size() > 0){
//            for (OSSAsyncTask item : taskList){
//                item.cancel();
//            }
//        }
//    }

}
