package com.example.telegrambots.ability;
import com.example.telegrambots.bot.Bot;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;


public class Train implements Runnable {
    private static final Logger log = Logger.getLogger(Train.class);
    private String nameOfCityArrivalToYandexCode;
    private String nameOfCityDepartureToYandexCode;
    private String date;
    String chatID;
    Bot bot;
    private  final String AMPERSAND="&";

    public Train(String nameOfCityDepartureToYandexCode, String nameOfCityArrivalToYandexCode, String date, String chatID, Bot bot) {
        this.nameOfCityDepartureToYandexCode=nameOfCityDepartureToYandexCode;
        this.nameOfCityArrivalToYandexCode=nameOfCityArrivalToYandexCode;
        this.date = date;
        this.chatID = chatID;
        this.bot = bot;
        log.debug("CREATE. " + toString());

    }

    @Override
    public void run() {
        bot.sendQueue.add(getFirstMessage());
        try {
            log.info("Send Request. " + toString());
            getSecondMessage();
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        } catch (ParseException e) {
            log.error(e.getMessage(),e);
        }
        log.info("FIHISH. " + toString());


    }

    private SendMessage getFirstMessage() {
        return new SendMessage(chatID, "Waiting...");
    }

    private void getSecondMessage() throws IOException, ParseException {
            JSONObject response =sendGetRequest();
            JSONArray segments = (JSONArray) (response).get("segments");
            for (int i = 0; i < segments.size(); i++) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatID);
                sendMessage.enableMarkdown(true);
                Object obj2 = segments.get(i);
                StringBuilder message = new StringBuilder();
                JSONObject from = (JSONObject) ((JSONObject) obj2).get("from");
                JSONObject to = (JSONObject) ((JSONObject) obj2).get("to");
                JSONObject thread = (JSONObject) ((JSONObject) obj2).get("thread");
                message.append("Train: ")
                        .append(thread.get("title") + " ")
                        .append((String) thread.get("number"))
                        .append("\n")
                        .append("Station(Departure at station): ")
                        .append((String) from.get("title"))
                        .append("(" + ((JSONObject) obj2).get("departure") + ")")
                        .append("\n")
                        .append("Station(Arrival at station):")
                        .append((String) to.get("title"))
                        .append("(" + ((JSONObject) obj2).get("arrival") + ")")
                        .append("\n")
                        .append("duration: ")
                        .append(((JSONObject) obj2).get("duration"));
                bot.sendQueue.add(sendMessage.setText(message.toString()));


            }
        }


    public JSONObject sendGetRequest() throws IOException, ParseException {
        final String ACCESS_TOKEN = "2d99d1f5-9ba2-4feb-8bc8-75d23e590f8a";
        StringBuilder url=new StringBuilder();
        url.append("https://api.rasp.yandex.net/v3.0/search/?apikey=").append(ACCESS_TOKEN).append(AMPERSAND)
                .append("format=json").append(AMPERSAND)
                .append("from=").append(nameOfCityDepartureToYandexCode).append(AMPERSAND)
                .append("to=").append(nameOfCityArrivalToYandexCode).append(AMPERSAND)
                .append("transport_types=train").append(AMPERSAND)
                .append("lang=ru_RU&page=1").append(AMPERSAND)
                .append("date=").append(date);
        StringBuffer response = new StringBuffer();
        URL obj = new URL(url.toString());
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setHostnameVerifier(new Verifiered());
        con.setRequestMethod("GET");
        con.addRequestProperty("Accept", "application/json");
        con.addRequestProperty("Authorization", ACCESS_TOKEN);
        int responseCode = con.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } else {
            con.disconnect();
        }
        JSONParser parser=new JSONParser();
        JSONObject jsonResponse= (JSONObject)parser.parse(response.toString());
        return jsonResponse;


    }

}

