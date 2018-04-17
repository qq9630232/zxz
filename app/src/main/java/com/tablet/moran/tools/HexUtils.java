package com.tablet.moran.tools;

import java.io.UnsupportedEncodingException;

/**
 * Created by Stone on 2017/12/9.
 */

public class HexUtils
{
    private static final boolean DEBUG = true;
    private static final String TAG = "<<";
    private static String[] binaryArray = { "0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111" };
    private static String hexStr;
    private static String hexString = "0123456789ABCDEF";

    static
    {
        hexStr = "0123456789ABCDEF";
    }

    public static void LOGD(String paramString)
    {
        SLogger.d(TAG, paramString);
    }

    public static byte[] SetPrintModel(int paramInt1, int paramInt2)
    {
        int i = paramInt2 + paramInt1 * 16;
        String str = "1D 21 " + Integer.toHexString(i);
        LOGD("---------SetPrintModel-------------tempStr==" + str);
        return hexStr2Bytesnoenter(str);
    }

    public static String bin2HexStr(byte[] paramArrayOfByte)
    {
        String str1 = "";
        for (int i = 0; i < paramArrayOfByte.length; i++)
        {
            String str2 = String.valueOf(hexStr.charAt((0xF0 & paramArrayOfByte[i]) >> 4));
            String str3 = str2 + String.valueOf(hexStr.charAt(0xF & paramArrayOfByte[i]));
            str1 = str1 + str3;
        }
        return str1;
    }

    public static String bytes2BinStr(byte[] paramArrayOfByte)
    {
        String str1 = "";
        int i = paramArrayOfByte.length;
        for (int j = 0; j < i; j++)
        {
            int k = paramArrayOfByte[j];
            int m = (k & 0xF0) >> 4;
            String str2 = str1 + binaryArray[m];
            int n = k & 0xF;
            str1 = str2 + binaryArray[n];
        }
        return str1;
    }

    public static String encodeCN(String paramString)
    {
        try
        {
            byte[] arrayOfByte = paramString.getBytes("gbk");
            StringBuilder localStringBuilder = new StringBuilder(2 * arrayOfByte.length);
            for (int i = 0; i < arrayOfByte.length; i++)
            {
                localStringBuilder.append(hexString.charAt((0xF0 & arrayOfByte[i]) >> 4));
                localStringBuilder.append(hexString.charAt((0xF & arrayOfByte[i]) >> 0));
                localStringBuilder.append(" ");
            }
            String str = localStringBuilder.toString();
            return str;
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
            localUnsupportedEncodingException.printStackTrace();
        }
        return "";
    }

    public static String encodeStr(String paramString)
    {
        String localObject = "";
        try
        {
            byte[] arrayOfByte = paramString.getBytes("gbk");
            for (int i = 0; i < arrayOfByte.length; i++)
            {
                String str1 = (String)localObject + Integer.toString(256 + (0xFF & arrayOfByte[i]), 16).substring(1);
                String str2 = str1 + " ";
                localObject = str2;
            }
            return localObject;
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
            localUnsupportedEncodingException.printStackTrace();
        }
        return "";
    }

    public static String getHexResult(String paramString)
    {
        StringBuilder localStringBuilder = new StringBuilder();
        int i = paramString.length();
        if (i > 0)
        {
            int j = 0;
            while (j < i)
            {
                String str = String.valueOf(paramString.charAt(j));
                if (isCN(str))
                    localStringBuilder.append(encodeCN(str));
                else
                {
                    localStringBuilder.append(encodeStr(str));
                }
                j++;
            }
        }
        return localStringBuilder.toString();
    }

    public static byte[] hexStr2BinArr(String paramString)
    {
        int i = paramString.length() / 2;
        byte[] arrayOfByte = new byte[i];
        for (int j = 0; j < i; j++)
            arrayOfByte[j] = ((byte)((byte)(hexStr.indexOf(paramString.charAt(j * 2)) << 4) | (byte)hexStr.indexOf(paramString.charAt(1 + j * 2))));
        return arrayOfByte;
    }

    public static String hexStr2BinStr(String paramString)
    {
        return bytes2BinStr(hexStr2BinArr(paramString));
    }

    public static byte[] hexStr2Bytesnoenter(String paramString)
    {
        String[] arrayOfString = paramString.split(" ");
        int i = arrayOfString.length;
        byte[] arrayOfByte = new byte[i + 3];
        for (int j = 0; j < i; j++)
            arrayOfByte[j] = Integer.decode("0x" + arrayOfString[j]).byteValue();
        return arrayOfByte;
    }

    public static boolean isCN(String paramString)
    {
        boolean bool1 = paramString.matches("^[一-龥]*$");
        boolean bool2 = false;
        if (bool1)
            bool2 = true;
        return bool2;
    }

    public static void main(String[] paramArrayOfString)
    {
        LOGD("--------------main-----------getHexResult()==" + getHexResult("单号:1105165644984"));
        LOGD("--------------main-----------getHexResult()==" + getHexResult("蓝牙:Print"));
    }
}