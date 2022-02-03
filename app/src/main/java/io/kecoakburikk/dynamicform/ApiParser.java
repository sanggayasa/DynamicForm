package io.kecoakburikk.dynamicform;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Muhammad Irfan
 * @since 06/01/2022 11.03
 */
public class ApiParser {
    public static JSONObject getForm() throws Exception {
        HttpGet httpGet = new HttpGet("http://192.168.43.111:5003/get");

        HttpClient httpclient = new DefaultHttpClient();

        HttpResponse httpResponse = httpclient.execute(httpGet);

        HttpEntity httpEntity = httpResponse.getEntity();

        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            InputStream inputStream = httpEntity.getContent();

            assert inputStream != null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8), 8);

            StringBuilder stringBuilder = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            String s = stringBuilder.toString();

            return new JSONObject(s);
        }

        return new JSONObject();
    }
}
