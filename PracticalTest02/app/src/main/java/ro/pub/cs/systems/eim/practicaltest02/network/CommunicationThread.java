package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.practicaltest02.Information;
import ro.pub.cs.systems.eim.practicaltest02.Utilities;

class CommunicationThread extends Thread{

    private ServerThread serverThread;
    private Socket socket;
    private String updated;


    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    public static long cacheTest(Date now, Date updated) {
        return TimeUnit.MINUTES.convert(new Date().getTime() - updated.getTime(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i("[PracticalTest02]", "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");

             //AICI SE OBTIN INFORMATIILE DE CARE ESTE NEVOIE
            String informationType = bufferedReader.readLine();
            if (informationType == null || informationType.isEmpty()) {
                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }
            HashMap<String, Information> data = serverThread.getData();
            Information information = null;
//            // IN CAZ CA VREA CU HASHMAP, RAMANE PARTEA ASTA

            if (data.containsKey(informationType) && cacheTest(new Date(), new Date(updated)) <= 1) {
                Log.i("[PracticalTest02]", "[COMMUNICATION THREAD] Getting the information from the cache...");
                information = data.get(informationType);
            } else {
                Log.i("[PracticalTest02]", "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                String pageSourceCode = "";


                HttpGet httpGet = new HttpGet("https://api.coindesk.com/v1/bpi/currentprice/" + informationType + ".json");
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);

                }

                if (pageSourceCode == null) {
                    Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                } else
                    Log.i("[PracticalTest02]", pageSourceCode);


                JSONObject content = new JSONObject(pageSourceCode);
                JSONObject main = content.getJSONObject("bpi");
                JSONObject type = main.getJSONObject(informationType);
                String code = type.getString("code");
                String rate = type.getString("rate");
                String description = type.getString("description");
                String rate_float = type.getString("rate_float");
                JSONObject time = content.getJSONObject("time");
                updated = time.getString("updated");
                information = new Information(
                        code, rate, description, rate_float
                );

                information = new Information(code, rate, description, rate_float);
            }

            if (information == null) {
                Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                return;
            }
            String result = information.toString();

            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            ioException.printStackTrace();
        } catch (JSONException jsonException) {
            Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
            jsonException.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("[PracticalTest02]", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    ioException.printStackTrace();
                }
            }
        }
    }
}
