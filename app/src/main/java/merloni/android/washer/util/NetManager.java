package merloni.android.washer.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetManager {

    private static final String TAG = NetManager.class.getSimpleName();
	
//	public static final String SERVER_ADDRESS = "sw19.ru/online/scanmerloni/android.php";
public static final String SERVER_ADDRESS = "http://10.0.2.2/washer";
	protected static final int DEFAULT_BUILDER_CAPACITY = 65536;
	public static final int SOCKET_TIMEOUT_IN_MILLIS = 10000;
	public static final int CONNECTION_TIMEOUT_IN_MILLIS = 10000;
	protected static final int STATUS_OK = 200;
	
	public static String noConnectionString;
	public static String clientServerErrorString;
	public static String internalErrorString;
	
	private HttpClient client;
	public static boolean busy;
	private static NetManager instance;
	
	private NetManager() {
		
	}
	
	public static NetManager getInstance() {
		if (instance == null) {
			instance = new NetManager();
		}
		return instance;
	}
	
    private static void readStream(InputStream in, StringBuffer result) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				result.append(line);
//				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    } 

    public static boolean isOnline(Context context, boolean strongNeed) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (strongNeed) {
        	return netInfo != null && netInfo.isConnected();
        } else {
        	return netInfo != null && netInfo.isConnectedOrConnecting();
        }
    }

    public static String sendGet(Context context, String addr, String encoding) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(addr);
        try {
            HttpResponse getResponse = client.execute(getRequest);
            final int statusCode = getResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity getResponseEntity = getResponse.getEntity();
            if (getResponseEntity != null) {
                return EntityUtils.toString(getResponseEntity, encoding);
            }
        }
        catch (IOException e) {
            getRequest.abort();
            Log.w(TAG, "Error GET for URL " + addr, e);
        }
        return null;
    }

    public static String sendPost(Context context, String addr, String post, String encoding) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost postRequest = new HttpPost(addr);
        try {
            StringEntity entity = new StringEntity(post, encoding);
            postRequest.setEntity(entity);
            HttpResponse postResponse = client.execute(postRequest);
            final int statusCode = postResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity getResponseEntity = postResponse.getEntity();
            if (getResponseEntity != null) {
                return EntityUtils.toString(getResponseEntity, encoding);
            }
        }
        catch (IOException e) {
            postRequest.abort();
            Log.w(TAG, "Error POST for URL " + addr, e);
        }
        return null;
    }

    public static void loadFile(String source, String dest) {
        try {
            URL url = new URL(source);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.connect();
            File file = new File(dest);
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
//    	        int totalSize = urlConnection.getContentLength();
//    	        int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
//    	                downloadedSize += bufferLength;
//    	                updateProgress(downloadedSize, totalSize);

            }
            fileOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void sendFileByPostReceiveText(NetPackage np) {
////    	String lineEnd = "\r\n";
////    	String twoHyphens = "--";
////    	String boundary =  "*****";
//        int maxBufferSize = 128 * 1024;
//        try {
//            URL url = new URL(sendingAddress);
//            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//            connection.setUseCaches(false);
//            connection.setRequestMethod("POST");
/////			connection.setRequestProperty("Connection", "Keep-Alive");
/////			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//
//            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
/////			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
//////			outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + file.getFullName() +"\"" + lineEnd);
/////			outputStream.writeBytes(np.getScriptParams());
////			outputStream.writeBytes("Content-Disposition: form-data; " + np.getScriptParams() + lineEnd);//			outputStream.writeBytes("Content-Disposition: form-data; " + np.getScriptParams() + lineEnd);
/////			outputStream.writeBytes(lineEnd);
//
//            FIle file = (FIle)np.getData(0);
//            FileInputStream fileInputStream = new FileInputStream(new File(file.getPathAndName()));
//            int bytesAvailable = fileInputStream.available();
//            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            byte[] buffer = new byte[bufferSize];
//            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//            while (bytesRead > 0) {
//                outputStream.write(buffer, 0, bufferSize);
//                bytesAvailable = fileInputStream.available();
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//            }
/////			outputStream.writeBytes(lineEnd);
/////			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//            int serverResponseCode = connection.getResponseCode();
//            String serverResponseMessage = connection.getResponseMessage();
//            System.out.println("serverResponseMessage: " + serverResponseMessage + ". serverResponseCode = " + serverResponseCode);
//            fileInputStream.close();
//            outputStream.flush();
//            outputStream.close();
//
////			DataInputStream inputStream = new DataInputStream(connection.getInputStream());
//            StringBuffer result = new StringBuffer();
//            InputStream is = connection.getInputStream();
//            readStream(is, result);
//            is.close();
//            connection.disconnect();
//            np.setResultText(result);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

}
