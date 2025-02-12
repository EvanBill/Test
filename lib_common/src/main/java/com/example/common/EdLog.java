package com.example.common;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class EdLog {
    public static boolean isLog = true;

    private static String DEFAULTTAG = "videoshow";

    public static void init(Context context) {
        try {
            isLog = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(String tag, String msg) {
        if (isLog) {
            if (msg == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.e(tag, msg);
            } else {
                Log.e(DEFAULTTAG, msg);
            }

        }
    }

    public static void e(String tag, Throwable e) {
        if (isLog) {
            if (e == null) {
                return;
            }
            String msg = getStackTrace(e);
            if (msg == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.e(tag, msg);
            } else {
                Log.e(DEFAULTTAG, msg);
            }
        }
    }

    public static void e(String tag, String msg, Throwable msg2) {
        if (isLog) {
            if (msg == null || msg2 == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.e(tag, msg, msg2);
            } else {
                Log.e(DEFAULTTAG, msg, msg2);
            }
        }
    }

    public static void i(String tag, String msg) {
        if (isLog) {
            if (msg == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.i(tag, msg);
            } else {
                Log.i(DEFAULTTAG, msg);
            }
        }
    }
    public static void i(String tag, String msg,boolean isToast) {
        if (isLog) {
            if (msg == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.i(tag, msg);
            } else {
                Log.i(DEFAULTTAG, msg);
            }
        }
    }
    public static void i(String tag, Throwable tr) {
        if (isLog) {
            if (tr == null) {
                return;
            }
            String msg = getStackTrace(tr);
            if (msg == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.i(tag, msg);
            } else {
                Log.i(DEFAULTTAG, msg);
            }
        }
    }


    public static void i(String tag, String msg, Throwable tr) {
        if (isLog) {
            if (msg == null || tr == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.i(tag, msg, tr);
            } else {
                Log.i(DEFAULTTAG, msg, tr);
            }
        }
    }

    public static void w(String tag, String msg) {
        if (isLog) {
            if (msg == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.w(tag, msg);
            } else {
                Log.w(DEFAULTTAG, msg);
            }
        }
    }


    public static void w(String tag, Throwable e) {
        if (isLog) {
            if (e == null) {
                return;
            }
            String msg = getStackTrace(e);
            if (msg == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.w(tag, msg);
            } else {
                Log.w(DEFAULTTAG, msg);
            }
        }
    }

    public static void w(String tag, String msg, Throwable msg2) {
        if (isLog) {
            if (msg == null || msg2 == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.w(tag, msg, msg2);
            } else {
                Log.w(DEFAULTTAG, msg, msg2);
            }
        }
    }

    public static void d(String tag, String msg) {
        if (isLog) {
            if (msg == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.d(tag, msg);
            } else {
                Log.d(DEFAULTTAG, msg);
            }
        }
    }

    public static void d(String tag, Throwable e) {
        if (isLog) {
            if (e == null) {
                return;
            }
            String msg = getStackTrace(e);
            if (msg == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.d(tag, msg);
            } else {
                Log.d(DEFAULTTAG, msg);
            }
        }
    }

    public static void d(String tag, String msg, Throwable msg2) {
        if (isLog) {
            if (msg == null || msg2 == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.d(tag, msg, msg2);
            } else {
                Log.d(DEFAULTTAG, msg, msg2);
            }
        }
    }

    public static void v(String tag, String msg) {
        if (isLog) {
            if (msg == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.v(tag, msg);
            } else {
                Log.v(DEFAULTTAG, msg);
            }
        }
    }


    public static void v(String tag, Throwable e) {
        if (isLog) {
            if (e == null) {
                return;
            }
            String msg = getStackTrace(e);
            if (msg == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.v(tag, msg);
            } else {
                Log.v(DEFAULTTAG, msg);
            }
        }
    }

    public static void v(String tag, String msg, Throwable msg2) {
        if (isLog) {
            if (msg == null || msg2 == null) {
                return;
            }
            msg += " | " + Thread.currentThread().getName();
            if (tag != null && tag.length() > 0) {
                Log.v(tag, msg, msg2);
            } else {
                Log.v(DEFAULTTAG, msg, msg2);
            }
        }
    }

    public static String getStackTrace(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    public static String getArrayListTrace(ArrayList<Object> logArr) {
        if (logArr == null || logArr.size() == 0) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < logArr.size(); i++) {
            buffer.append(logArr.get(i));
            if (i != logArr.size() - 1) {
                buffer.append(" | ");
            }
        }
        return buffer.toString();
    }

    public static String getArrayTrace(Object[] logArr) {
        if (logArr == null || logArr.length == 0) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < logArr.length; i++) {
            buffer.append(logArr[i]);
            if (i != logArr.length - 1) {
                buffer.append(" | ");
            }
        }
        return buffer.toString();
    }

    public static String getArrayTrace(int[] logArr) {
        if (logArr == null || logArr.length == 0) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < logArr.length; i++) {
            buffer.append(logArr[i]);
            if (i != logArr.length - 1) {
                buffer.append(" | ");
            }
        }
        return buffer.toString();
    }

    public static String getArrayTrace(float[] logArr) {
        if (logArr == null || logArr.length == 0) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < logArr.length; i++) {
            buffer.append(logArr[i]);
            if (i != logArr.length - 1) {
                buffer.append(" | ");
            }
        }
        return buffer.toString();
    }
}
