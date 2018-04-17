package com.tablet.moran.tools.photos;

import android.content.Context;
import android.os.AsyncTask;

import com.tablet.moran.tools.MD5Util;

import java.io.File;

/**
 * Created by stone on 15/12/14.
 */

public class PhotoCompresser extends AsyncTask<Void, Void, File> {


    public CompressCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(CompressCallBack callBack) {
        this.callBack = callBack;
    }

    private CompressCallBack callBack;


    private int needSize;
    private File inputFile;
    private Context context;

    public int getNeedSize() {
        return needSize;
    }

    public void setNeedSize(int needSize) {
        this.needSize = needSize;
    }


    public PhotoCompresser(int needSize, File inputFile, CompressCallBack compressCallBack, Context context) {
        this.needSize = needSize;
        this.callBack = compressCallBack;
        this.inputFile = inputFile;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected File doInBackground(Void... params) {
        if (inputFile == null) {
            throw new UnsupportedOperationException("输入图片的路径呢？");
        }
        String imgName = MD5Util.MD5Encode(inputFile.getName(), "") +".jpg";
        File baseFile = PhotoUtils.getDiskCacheDir(context, "imgCache");
        if (baseFile == null)
            return null;
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }
        File outPutFile = new File(baseFile,imgName);
        boolean isDone = PhotoUtils.compressImageWithSize(inputFile, outPutFile, needSize, context);
        if (isDone){
            return outPutFile;
        }
        return null;
    }

    @Override
    protected void onPostExecute(File f) {
        super.onPostExecute(f);
        if(f!=null&&callBack!=null){
            callBack.onCompressDone(f);
        }

    }

    public interface CompressCallBack {
        void onCompressDone(File outputFile);
    }
}
