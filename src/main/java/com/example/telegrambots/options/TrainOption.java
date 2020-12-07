package com.example.telegrambots.options;
import com.example.telegrambots.command.ParsedCommand;
import com.example.telegrambots.ability.Train;
import com.example.telegrambots.bot.Bot;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.api.objects.Update;

import java.io.FileReader;
import java.io.IOException;

public class TrainOption extends AbstractOption{
    private static final Logger log = Logger.getLogger(TrainOption.class);
    private final String WRONG_INPUT_MESSAGE="Wrong input. Check that your language is correct ";
    private String INCORRECTRLY_CITY="Double-check that you have entered the correct city - ";
    private  String nameOfCityArrivalToYandexCode;
    private  String nameOfCityDepartureToYandexCode;
    private String nameOfCityDeparture;
    private String nameOfCityArrival;
    private String date;
    public TrainOption(Bot bot) {
        super(bot);
    }

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        String text = parsedCommand.getText();
        if ("".equals(text)){ return "You should check the spelling of your search words. For example: \n"+
                "/train Харьков Киев 2012-12-31";

        }
        String [] params;
        params=text.trim().split(" ");
        try{
            nameOfCityDeparture=params[0];
            nameOfCityArrival=params[1];
            date=params[2];
            searchYandexCode(nameOfCityDeparture,nameOfCityArrival);
            if(nameOfCityArrivalToYandexCode==null) return INCORRECTRLY_CITY + nameOfCityArrival;
            if(nameOfCityDepartureToYandexCode==null) return INCORRECTRLY_CITY + nameOfCityDeparture;
            Thread thread = new Thread(new Train(nameOfCityDepartureToYandexCode,nameOfCityArrivalToYandexCode,date,chatId,bot));
            thread.start();

        }
        catch (ArrayIndexOutOfBoundsException e){
            log.error(e.getMessage(),e);
            return WRONG_INPUT_MESSAGE;
        } catch (ParseException e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "";
    }
    public void searchYandexCode(String nameOfCityDeparture,String nameOfCityArrival ) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("Z:/Ukraine.json"));
        JSONArray regions = (JSONArray) ((JSONObject) obj).get("regions");
        while (nameOfCityArrivalToYandexCode ==null && nameOfCityDepartureToYandexCode ==null) {
            for (int i = 0; i < regions.size(); i++) {
                JSONObject settlements = (JSONObject) (regions).get(i);
                JSONArray title1 = (JSONArray) (settlements).get("settlements");
                for (int j = 0; j < title1.size(); j++) {
                    JSONObject title = (JSONObject) (title1).get(j);
                    String title2 = (String) title.get("title");
                    if (title2.equals(nameOfCityDeparture)) {
                        JSONObject code = (JSONObject) (title).get("codes");
                        String yandex_code = (String) code.get("yandex_code");
                        nameOfCityDepartureToYandexCode =yandex_code;
                    }
                    if(title2.equals(nameOfCityArrival)){
                        JSONObject code = (JSONObject) (title).get("codes");
                        String yandex_code = (String) code.get("yandex_code");
                        nameOfCityArrivalToYandexCode =yandex_code;
                    }
                }
            }
            break;
        }
    }
}
