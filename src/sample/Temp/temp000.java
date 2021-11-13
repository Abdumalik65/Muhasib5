package sample.Temp;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class temp000 {
    public static void main(String[] args) {
        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

        String apiToken = "2005166438:AAF3ewUi_iWvqqmYm51LpuLFSEEcupRlZQQ";
        String chatId = "@techno_chain_uz";
        String text = "Bismillah!";

        urlString = String.format(urlString, apiToken, chatId, text);

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLConnection conn = null;
        try {
            conn = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        InputStream is = null;
        try {
            is = new BufferedInputStream(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String inputLine = "";
        while (true) {
            try {
                if (!((inputLine = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            sb.append(inputLine);
        }
        String response = sb.toString();
// Do what you want with response
    }
}
