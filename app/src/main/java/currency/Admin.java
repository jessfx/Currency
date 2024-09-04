package currency;

import processing.data.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Admin extends User{
    /**
     * constructor for Admin
     * might not need UID and Password
     * @param UID, User ID
     * @param Password, Password
     */
    public String UID;
    public String Password;

    public Admin(String UID, String Password){
        super(UID, Password);
    }

    @Override
    public void Menu() {
        System.out.println("Please choose what you wish to do by typing the number:");
        System.out.println("1. Convert Currency");
        System.out.println("2. Display Popular");
        System.out.println("3. Display Summary");
        System.out.println("4. Add Currency");
        System.out.println("5. Update Rate");
        System.out.println("6. Update Popular");
        System.out.println("7. Logout");
        System.out.println("8. Exit");
    }

    public void addCurrency(String currency, double rateToUSD, JSONObject CurrencyData){
        //Set json objects to string list
        Set<String> keys = CurrencyData.keys();
        List<String> keysList = new ArrayList<String>(keys);

        // Sort the keys (dates) in natural order
        Collections.sort(keysList);

        // Get the most recent date
        String curDate = keysList.get(keysList.size() - 1);
        JSONObject recentData = CurrencyData.getJSONObject(curDate);
        if(recentData.hasKey(currency)){
            System.out.println("Currency has existed");
        }else{
            JSONObject newCurrency=new JSONObject();
            newCurrency.setInt("Popularity",1);
            newCurrency.setDouble("USD",rateToUSD);
            recentData.setJSONObject(currency,newCurrency);
        }
    }

    public void updateRate(String fromCurrency, String toCurrency, String updateDate, double fromRateToUSD, double toRateToUSD, JSONObject CurrencyData){

        JSONObject currencies;
        if(!CurrencyData.hasKey(updateDate)){
            currencies=new JSONObject();
            JSONObject USDRate=new JSONObject();
            USDRate.setInt("Popularity",1);
            USDRate.setInt("USD",1);
            currencies.setJSONObject("USD",USDRate);
            CurrencyData.setJSONObject(updateDate,currencies);
        }

        currencies=CurrencyData.getJSONObject(updateDate);

        if(currencies.hasKey(fromCurrency)){
            currencies.getJSONObject(fromCurrency).setDouble("USD",1/fromRateToUSD);
        }else{
            JSONObject fromRate=new JSONObject();
            fromRate.setInt("Popularity",1);
            fromRate.setDouble("USD",1/fromRateToUSD);
            currencies.setJSONObject(fromCurrency,fromRate);
        }

        if(currencies.hasKey(toCurrency)){
            currencies.getJSONObject(toCurrency).setDouble("USD",1/toRateToUSD);
        }else{
            JSONObject toRate=new JSONObject();
            toRate.setInt("Popularity",1);
            toRate.setDouble("USD",1/toRateToUSD);
            currencies.setJSONObject(toCurrency,toRate);
        }

    }

    public void updatePopular(String currency, JSONObject CurrencyData){
        Set<String> keys = CurrencyData.keys();
        for(String key:keys){
            if(CurrencyData.getJSONObject(key).hasKey(currency)){
                CurrencyData.getJSONObject(key).getJSONObject(currency).setInt("Popularity",1);
                break;
            }
        }
    }

}
