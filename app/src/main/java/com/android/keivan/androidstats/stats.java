package com.android.keivan.androidstats;

import android.app.ActivityManager;
import android.content.Context;
import android.net.TrafficStats;
import android.os.SystemClock;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Locale;

import static android.content.Context.ACTIVITY_SERVICE;

public class stats
{
    private Context context;
    long prevRxByteCount = 0;
    long prevTxByteCount = 0;
    long time;

    private String statistics = "%s\t%s\n%s\t%s";

    private String dSpeed = "\u2193 %.02f %s";
    private String uSpeed = "\u2191 %.02f %s";
    private String cpu = "CPU %.02f";
    private String ram = "RAM %d M";


    stats(Context context)
    {
        this.context = context;
        time = SystemClock.elapsedRealtime();
    }

    String getStats()
    {
        String R,C,D,U;
        R = RAMusage();
        C = CPUusage();
        U = UploadDataUsage();
        D = DownloadDataUsage();
        time = SystemClock.elapsedRealtime();
        return String.format(Locale.getDefault(),statistics,C,R,D,U);
    }


    private String RAMusage()
    {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;
        long totalMem = mi.totalMem / 1048576L;
        float percent = availableMegs/totalMem*100;
//        Log.i("RAM Usage", Long.toString(availableMegs));
//        Log.i("Total RAM", Long.toString(totalMem));
//        Log.i("Percent", Float.toString((float) availableMegs / totalMem * 100) + " %");
        return String.format(Locale.getDefault(),ram, availableMegs);
    }

    private String CPUusage()
    {
        try
        {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();
            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
            try
            {
                Thread.sleep(360);
            }
            catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();
            toks = load.split(" +");
            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            float percent = (float) (cpu2 - cpu1)*100 / ((cpu2 + idle2) - (cpu1 + idle1));
            return String.format(Locale.getDefault(), cpu, percent);

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return "";
    }

    private String DownloadDataUsage()
    {
        int level = 1;
        long curRxByteCount = TrafficStats.getTotalRxBytes();
        long downloadedData = curRxByteCount - prevRxByteCount;
        prevRxByteCount = curRxByteCount;
        float speed = (float)downloadedData*1000/(SystemClock.elapsedRealtime()-time);
        while(speed>1024)
        {
            speed/=1024;
            level++;
        }
        String l ="";
        switch(level)
        {
            case 1:
                l = "B/s";
                break;
            case 2:
                l = "KB/s";
                break;
            case 3:
                l = "MB/s";
                break;
            case 4:
                l = "GB/s";
                break;
            case 5:
                l = "TB/s";
                break;
            default:
                l = "b/s";
        }
        return String.format(Locale.getDefault(), dSpeed, speed, l);
    }

    private String UploadDataUsage()
    {
        int level = 1;
        long curTxByteCount = TrafficStats.getTotalTxBytes();
        long uploadedData = curTxByteCount - prevTxByteCount;
        prevTxByteCount = curTxByteCount;
        float speed = (float)uploadedData*1000/(SystemClock.elapsedRealtime()-time);
        while(speed>1024)
        {
            speed/=1024;
            level++;
        }
        String l ="";
        switch(level)
        {
            case 1:
                l = "B/s";
                break;
            case 2:
                l = "KB/s";
                break;
            case 3:
                l = "MB/s";
                break;
            case 4:
                l = "GB/s";
                break;
            case 5:
                l = "TB/s";
                break;
            default:
                l = "b/s";
        }
        return String.format(Locale.getDefault(),uSpeed, speed, l);
    }
}
