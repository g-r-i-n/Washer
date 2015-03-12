package merloni.android.washer.util;

import android.os.Environment;

public class FIle {

    public static final int IMAGE  = 0;
    public static final int VIDEO  = 1;
    public static final int TEXT   = 2;
    public static final int AUDIO  = 3;
    public static final int ROOT   = 4;
    public static final int FOLDER = 5;
    public static final int BACK   = 6;
    public static final int NONE   = 7;

    public static final int STATE_OPENED = 0;
    public static final int STATE_CLOSED = 1;

    public static final int NET_UPLOADING        = 0;
    public static final int NET_DOWNLOADING      = 1;
    public static final int NET_WAITING_UPLOAD   = 2;
    public static final int NET_WAITING_SOWNLOAD = 3;

    public int saveMode;

    public int state,
               netState;

    private String dir,
                   name,
                   ext,
                   category,
                   date,
                   time;

    private int type,
                byteCounter,
                progressLength,
                progressStep;
    
    private long size;

    private long milliseconds;

    private boolean isBusy,
                    shouldRepaint;

    public byte[] data;

    public FIle(String dir, String name) {
        this.dir = dir;
        this.name = name;
        isBusy = false;
        progressLength = 100;
        initName(dir + name);//TODO timely
    }

    public FIle(String pathAndName, boolean relative) {
        isBusy = false;
        progressLength = 100;
        if (relative) {
            initName(Environment.getExternalStorageDirectory() + "/" + pathAndName);
        } else {
            initName(pathAndName);
        }
    }
    
    public void initName(String pathAndName) {
    	int i = pathAndName.lastIndexOf('/');
    	dir = pathAndName.substring(0, i + 1);
    	name = pathAndName.substring(i + 1);
    	ext = "";
    	int j = pathAndName.lastIndexOf('.');
    	if (j > i) {
    		name = pathAndName.substring(i + 1, j);
    		ext = pathAndName.substring(j + 1);
    	}
    }

    public void setDir(       String val) {dir = val;        }
    public void setName(       String val) {name = val;        }
    public void setExt(        String val) {ext = val;         }
    public void setCategory(   String val) {category = val;    }

    public String getDir()        {return dir;					}
    public String getName()        {return name;					}

    public void setSize(long val) {
        size = val;
        progressStep = (int)(size / progressLength);
        System.out.println("FIle.progressStep = " + progressStep);
    }

    public String getPathAndName() {
        String result = dir + name;
        if (ext != null && ext.length() > 0) {
            result += "." + ext;
        }
        return result;
    }
    public String getFullName() {
        String result = name;
        if (ext != null && ext.length() > 0) {
            result += "." + ext;
        }
        return result;
    }
    public String getExt()         {return ext;						}
    public String getCategory()    {return category;				}
    public long getSize()          {return size;        			}


    public void setBusy(boolean value) {
        isBusy = value;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setCurByteCounter(int counter) {
        byteCounter = counter;
    }

    public int getCurByteLevel() {
        return byteCounter / progressStep;
    }

    public void shouldRepaint(boolean value) {
        shouldRepaint = value;
    }

    public boolean shouldRepaint() {
        boolean result = shouldRepaint;
        if (!result) {
            if (byteCounter % progressStep == 0) {
                result = true;
            }
        }
        return result;
    }

    public void setProgressLength(int progressLength) {
        this.progressLength = progressLength;
        progressStep = (int)(size / progressLength);
System.out.println("FIle.progressStep = " + progressStep);
    }

    public int getProgressLength() {
        return progressLength;
    }

    public void save() {
        FilesManager.getInstance().savePack(this);
    }

}