package currency;

import processing.data.JSONObject;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.*;
import java.util.Set;

public class User {
    public String UID;
    public String Password;

    /**
     * constructor for currency.
     * Don't have to include UID and Password
     * @param UID, currency. ID
     * @param Password, Password
     */
    public User(String UID, String Password) {
        this.UID = UID;
        this.Password = Password;
    }

    public String getUID(){
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    /**
     * Menu options
     */
    public void Menu() {
        System.out.println("Please choose what you wish to do by typing the number:");
        System.out.println("1. Convert Currency");
        System.out.println("2. Display Popular");
        System.out.println("3. Display Summary");
        System.out.println("4. Logout");
        System.out.println("5. Exit");
    }

    /**
     * Convert money from one currency to another
     * @param moneyAmount, amount of money in fromCurrency to be converted
     * @param fromCurrency, the currency to be converted
     * @param toCurrency, the currency to be converted to
     * @param CurrencyData, JSON Database
     */
    public void convertCurrency(Double moneyAmount, String fromCurrency, String toCurrency, JSONObject CurrencyData) {
        //Get most current date
        Set<String> keys = CurrencyData.keys();
        List<String> keysList = new ArrayList<>(keys);

        // Sort the keys (dates) in natural order
        Collections.sort(keysList);
        String curDate = keysList.get(keysList.size() - 1);

        Double rate = calculateRate(fromCurrency, toCurrency, curDate, CurrencyData);
        Double result = moneyAmount * rate;
        System.out.printf("%.2f %s can be converted to %.2f %s.\n", moneyAmount, fromCurrency, result, toCurrency);
    }

    /**
     * Display the 4 popular currencies
     */
    public void displayPopular(JSONObject CurrencyData){
        List<String> popularCurrencies= new ArrayList<String>();

        //Set json objects to string list
        Set<String> keys = CurrencyData.keys();
        List<String> keysList = new ArrayList<String>(keys);

        // Sort the keys (dates) in natural order
        Collections.sort(keysList);

        // Get the most recent date
        String curDate = keysList.get(keysList.size() - 1);
        JSONObject recentData = CurrencyData.getJSONObject(curDate);

        // Filter currencies with popularity of 1
        Set<String> allCurrencies = recentData.keys();
        List<String> currenciesList = new ArrayList<>(allCurrencies);
        for (String currency : currenciesList) {
            JSONObject recentCurrency = recentData.getJSONObject(currency);
            if (recentCurrency.getInt("Popularity") == 1) {
                System.out.println(currency);
                popularCurrencies.add(currency);
            }
        }

//        Define the data structure
//        Map<String, Map<String, Double>> exchangeRate;
        Map<String, Map<String, Double>> exchangeRates = popularRateMap(popularCurrencies, curDate, CurrencyData);

        //print table
        printPopularTable(popularCurrencies,exchangeRates, CurrencyData);

    }

    /**
     * Printing table using the extracted information
     * @param popularCurrencies list storing names of popular currencies
     * @param exchangeRates HashMap of rates between popular currencies
     * @param CurrencyData JSON Database
     */
    public void printPopularTable(List<String> popularCurrencies, Map<String, Map<String, Double>>  exchangeRates, JSONObject CurrencyData){
        // Display the table header
        System.out.println("----------------------------------------------------------");
        System.out.print("| From/To    |");
        for (String currency : popularCurrencies) {
            System.out.printf(" %5s    |", currency);
        }
        System.out.println();
        System.out.println("----------------------------------------------------------");

        // Populate the table with exchange rates
        for(String fromCurrency : popularCurrencies){
            System.out.printf("| %7s    |", fromCurrency);
            for(String toCurrency : popularCurrencies){

                //if to and from are same
                if(fromCurrency.equals(toCurrency)){
                    System.out.print("   -      |");
                }else{
                    //if they are not same
                    Double rate = exchangeRates.get(fromCurrency).get(toCurrency);
                    if(rate != null){
                        char trend = compareRate(fromCurrency, toCurrency, CurrencyData);
                        if(trend != 'E'){
                            System.out.printf(" %5.2f (%c)|", rate, trend);
                        }else{
                            System.out.printf(" %5.2f    |", rate);
                        }
                    } else {
                        System.out.print("          |");
                    }
                }
            }
            System.out.println();
            System.out.println("----------------------------------------------------------");
        }

    }


    /**
     * Check if the rate between 2 currencies has increased or decreased comparing to last update
     * Used in DisplayPopular()
     * @param fromCurrency, one of the currencies
     * @param toCurrency, one of the currencies
     * @param CurrencyData, JSON Database
     * @return char, "I" for increase, "D" as decrease, "E" as equal
     */
    public char compareRate(String fromCurrency, String toCurrency, JSONObject CurrencyData) {
        char result = 'N';
        Double currentRate;
        Double previousRate = 0.0;

        Set<String> keys = CurrencyData.keys();
        List<String> keysList = new ArrayList<>(keys);
        Collections.sort(keysList);

        // Get currentRate
        String curDate = keysList.get(keysList.size()-1);
        currentRate = calculateRate(fromCurrency, toCurrency, curDate, CurrencyData);

        // Get previousRate if has any
        if(keysList.size() > 1){
            String prevDate = keysList.get(keysList.size()-2);
            previousRate = calculateRate(fromCurrency, toCurrency, prevDate, CurrencyData);
        }

        if(previousRate != 0 && currentRate != 0){
            if(previousRate < currentRate){
                result = 'I';
            }else if(previousRate > currentRate){
                result = 'D';
            }else{
                result = 'E';
            }
        }else if(currentRate != 0){
            result = 'E';
        }

        return result;
    }

    /**
     * Display summary of history rate changes between 2 currencies during a specific time frame
     * @param fromCurrency, one of the currencies
     * @param toCurrency, one of the currencies
     * @param startDate, start date of the time frame
     * @param endDate, start date of the time frame
     * @param CurrencyData, JSON Database
     */
    public void displaySummary(String fromCurrency, String toCurrency, LocalDate startDate, LocalDate endDate, JSONObject CurrencyData) {
        List<Double>rateList = getAllRates(fromCurrency,toCurrency,startDate,endDate,CurrencyData);
        // Print all collected rates
        if(!rateList.isEmpty()){
            System.out.printf(fromCurrency + " to "+ toCurrency + " rates from " + startDate + " to " + endDate + ": \n");
            for(Double rate : rateList){
                System.out.printf("%.2f\n", rate);
            }

            double average = getAverage(rateList);
            double min = getMin(rateList);
            double max = getMax(rateList);
            double stddev = getStandardDeviation(rateList);
            double median = getMedian(rateList);

            System.out.printf("Average: %.2f\n", average);
            System.out.printf("Min: %.2f\n", min);
            System.out.printf("Max: %.2f\n", max);
            System.out.printf("Standard Deviation: %.2f\n", stddev);
            System.out.printf("Median: %.2f\n", median);

        }else{
            System.out.println("Cannot find any history between " + fromCurrency + " and " + toCurrency + "\n");
        }
    }

    /**
     * Get and calculate all history conversion rates from database between 2 specified currencies during a specified time frame
     * @param fromCurrency, one of the currencies
     * @param toCurrency, one of the currencies
     * @param startDate, start date of the time frame
     * @param endDate, start date of the time frame
     * @param CurrencyData, JSON Database
     * @return List<Double>, a list of all conversion rates
     */
    public List<Double> getAllRates(String fromCurrency, String toCurrency, LocalDate startDate, LocalDate endDate, JSONObject CurrencyData) {
        List<Double> rateList = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Set<String> keys = CurrencyData.keys();
        List<String> keysList = new ArrayList<>(keys);
        Collections.sort(keysList);

        for(String dateString : keysList) {
            LocalDate currentDate = LocalDate.parse(dateString, formatter);
            if((currentDate.isEqual(startDate) || currentDate.isAfter(startDate)) && (currentDate.isEqual(endDate) || currentDate.isBefore(endDate))){

                Double Rate = calculateRate(fromCurrency, toCurrency, dateString, CurrencyData);

                if(Rate > 0){
                    rateList.add(Rate);
                }
            }
        }
        return rateList;

    }

    /**
     * Construct a HashMap recording exchange rates between 4 popular currencies
     * Used in DisplayPopular()
     * @param popularCurrencies String list of 4 currencies name
     * @param date date
     * @param CurrencyData JSON Database
     * @return HashMap recording exchange rates between 4 popular currencies
     */
    public Map<String, Map<String, Double>> popularRateMap (List<String> popularCurrencies, String date, JSONObject CurrencyData) {
        Map<String, Map<String, Double>> exchangeRate= new HashMap<>();

        for(String fromCurrency : popularCurrencies){
            Map<String, Double> rates = new HashMap<>();
            for(String toCurrency : popularCurrencies){
                if(!toCurrency.equals(fromCurrency)){
                    Double rate = calculateRate(fromCurrency,toCurrency,date,CurrencyData);
                    rates.put(toCurrency,rate);
                }
            }
            exchangeRate.put(fromCurrency,rates);
        }
        return exchangeRate;
    }

    /**
     * Helper meethod to calculate rate between 2 currencies
     * @param fromCurrency from which currency
     * @param toCurrency convert to which currency
     * @param dateString date
     * @param CurrencyData JSON Database
     * @return rate from currency 1 to currency 2
     */
    public Double calculateRate(String fromCurrency, String toCurrency, String dateString, JSONObject CurrencyData) {
        Double fromRate;
        Double toRate;
        Double Rate = 0.0;

        if(fromCurrency.equals("USD") || toCurrency.equals("USD")){
            if(fromCurrency.equals("USD")){
                toRate = getRate(toCurrency, dateString, CurrencyData);
                if(toRate != 0){
                    Rate = 1/toRate;
                }
            }else{
                fromRate = getRate(fromCurrency, dateString, CurrencyData);
                if(fromRate != 0){
                    Rate = fromRate;
                }
            }
        }else{
            // Check if currency 1 exist
            fromRate = getRate(fromCurrency,dateString,CurrencyData);
            toRate = getRate(toCurrency,dateString,CurrencyData);
            if(fromRate != 0 && toRate != 0){
                Rate = fromRate/toRate;
            }
        }
        return Rate;
    }

    /**
     * Helper method to return rate to USD of a specified rate at specified date
     * @param currency, currency
     * @param date date
     * @param CurrencyData JSON Database
     * @return rate to USD
     */
    public Double getRate(String currency, String date, JSONObject CurrencyData) {
        Double result = 0.0;
        if (CurrencyData.hasKey(date)) {
            JSONObject currencies = CurrencyData.getJSONObject(date);

            if (currencies.hasKey(currency)) {
                if(currency.equals("USD")){
                    result = currencies.getJSONObject(currency).getDouble("USD");
                }else{
                    if(currencies.hasKey("USD")){
                        result = currencies.getJSONObject(currency).getDouble("USD") * currencies.getJSONObject("USD").getDouble("USD");
                    }
                }
            }
        }
        return result;
    }

    /**
     * returns the average of the rates extracted via getAllRates method
     * if the rate array is empty, -1 is returned
     * @param rateList list of rates
     * @return average of the rates
     * see displaySummary() for implementation
     */
    public double getAverage(List<Double> rateList) {
        if (rateList.isEmpty()) {
            return -1;
        }

        double sum = 0;
        for (double rate : rateList) {
            sum += rate;
        }

        return sum / rateList.size();
//        If rounding to 2 decimal places or something:
//        return (float) Math.round((sum / rateList.length) * 100) / 100;
        }


    /**
     * returns the median of the rates extracted via getAllRates method
     * if the rate array is empty, -1 is returned
     * @param rateList list of rates
     * @return median of the rates
     * see displaySummary() for implementation
     */

    public double getMedian(List<Double> rateList) {
        if (rateList.isEmpty()) {
            return -1;
        }

        Collections.sort(rateList);
        int size = rateList.size();
        if (rateList.size() % 2 == 0) {
            List<Double> takeAverage = new ArrayList<Double>();
            takeAverage.add(rateList.get(rateList.size()/2));
            takeAverage.add(rateList.get(rateList.size() / 2 - 1));
            return getAverage(takeAverage);
        }

        return rateList.get(size / 2);

//        If rounding to 2 decimal places or something:
//        return (float) Math.round((rateList[(rateList.length / 2)]) * 100) / 100;

    }

    /**
     * returns the standard deviation of the rates extracted via getAllRates() method
     * if the rate array is empty, -1 is returned
     * @param rateList list of rates
     * @return standard deviation of the rates
     * see displaySummary() for implementation
     */
    public double getStandardDeviation(List<Double> rateList) {
        if (rateList.isEmpty()) {
            return -1;
        }

        double average = getAverage(rateList);
        double variance = 0;
        for (double rate : rateList) {
            variance += Math.pow(rate - average, 2);
        }

        return Math.sqrt(variance / rateList.size());
//        If rounding to 2 decimal places or something:
//        return (float) Math.round((Math.sqrt(variance / rateList.length)) * 100) / 100;
    }

    /**
     * returns the minimum value of the rates extracted via getAllRates() method
     * if the rate array is empty, -1 is returned
     * @param rateList list of rates
     * @return minimum value of the rates
     * see displaySummary() for implementation
     */
    public double getMin(List<Double> rateList) {
        if (rateList.isEmpty()) {
            return -1;
        }

        Collections.sort(rateList);
        return rateList.get(0);

//        If rounding to 2 decimal places or something:
//        return (float) Math.round((rateList[0]) * 100) / 100;
    }

    /**
     * returns the maximum value of the rates extracted via getAllRates() method
     * if the rate array is empty, -1 is returned
     * @param rateList list of rates
     * @return maximum value of the rates
     * see displaySummary() for implementation
     */
    public double getMax(List<Double> rateList) {
        if (rateList.isEmpty()) {
            return -1;
        }

        Collections.sort(rateList);
        return rateList.get(rateList.size() - 1);
//        If rounding to 2 decimal places or something:
//        return (float) Math.round((rateList[rateList.length - 1]) * 100) / 100;
    }


}
