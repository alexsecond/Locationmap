package comm.model;

import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Alexander on 26/10/2017.
 */

public class HttpConnection {

    private String url;

    private String response;

    private static final String METHOD_POST = "POST";
    private static final String METHOD_GET = "GET";

    public HttpConnection(String url) {

        this.url = url;
    }

    public String sendGetMethodRequest(Map<String, String> params) throws IOException {
        String res = "";
        String urlTarget = this.url + "?";

        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlTarget += entry.getKey() + "=" + entry.getValue();
        }

        URL urlRequest = new URL(urlTarget);
        HttpURLConnection connection = (HttpURLConnection) urlRequest.openConnection();

        connection.setRequestMethod(METHOD_GET);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        connection.disconnect();

        res = response.toString();
        return res;
    }

    public void sendPostMethodRequest(Map<String, String> params) throws IOException {

        this.response = null;

        String values = "";
        int length = params.size();
        int i = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (length - i > 1) {
                values += entry.getKey() + "=" + entry.getValue() + "&";
            } else {
                values += entry.getKey() + "=" + entry.getValue();
            }
            i++;
        }

        try {
            URL url = new URL(this.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(values);
            wr.flush();
            wr.close();

            conn.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            this.response = response.toString();

            conn.disconnect();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    public String getResponse() {
        return response;
    }
}