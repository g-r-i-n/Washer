package merloni.android.washer.util;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FilesManager {

    public static final int SAVE_MODE_NONE = 0;
    public static final int SAVE_MODE_NEW = 1;
    public static final int SAVE_MODE_INSERT = 2;
    public static final int SAVE_MODE_OVERWRITE = 3;
    public static final int SAVE_MODE_APPEND = 4;

	private static FilesManager instance;

    private int saveMode;

    public FilesManager() {
		super();
	}

	public static FilesManager getInstance() {
		if (instance == null) {
			instance = new FilesManager();
		}
		return instance;
	}

	private void createFolder(FIle file) {
		System.out.println("FilesManager.createFolder(" + file.getPathAndName() + ")");
		File dir = new File(file.getPathAndName());
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

	public void savePack(FIle fIle) {
        try {
            File file = new File(fIle.getDir(), fIle.getName() + "." + fIle.getExt());
            FileOutputStream fos = null;
            if (fIle.saveMode == SAVE_MODE_APPEND || saveMode == SAVE_MODE_APPEND) {
            	fos = new FileOutputStream(file, true);
            } else if (fIle.saveMode == SAVE_MODE_NEW || saveMode == SAVE_MODE_NEW) {
            	fos = new FileOutputStream(file);
            } else if (fIle.saveMode == SAVE_MODE_INSERT || saveMode == SAVE_MODE_INSERT) {
//            	index = pack.getInsertPosition();
            }
            fos.write(fIle.data);
            fos.close();
            fIle.setSize(file.length());
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
            Log.d("", "File not found: " + e.getMessage());
        } catch (IOException e) {
        	e.printStackTrace();
            Log.d("", "Error accessing file: " + e.getMessage());
        }
	}
	
	public static long getFileSize(String imagePath) {
		long result = -1;
		File file = new File(imagePath);
		if (file != null) {
			result = file.length();
		}
		return result;
	}

}
