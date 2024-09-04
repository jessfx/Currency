package currency;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.data.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModifyCurrencyTest {

    // parameters for compareRate()
    private static User tester;
    private static Admin admin;
    private static final String USDCurrency = "USD";
    private static final String EURCurrency = "EUR";
    private static final String GBPCurrency = "GBP";
    private static final String AUDCurrency = "AUD";

    private static final String missingCurrency = "CAD";
    private static final String existedCurrency = "USD";

    private static final String existingDate = "2024-08-25";
    private static final String missingDate = "2024-12-26";

    private static final JSONObject CurrencyData = PApplet.loadJSONObject(new File("Currency_Data.json"));

    private static String fromCurrency;
    private static String toCurrency;

    private static final List<String> popularCurrencies = new ArrayList<>();

    @BeforeAll
    public static void setUp() {
        tester = new User("123456789", "password");
        admin = new Admin("1234", "password");

        popularCurrencies.add(USDCurrency);
        popularCurrencies.add(EURCurrency);
        popularCurrencies.add(GBPCurrency);
        popularCurrencies.add(AUDCurrency);

    }

    // 1. Test addCurrency()
    @Test
    void addCurrencyMissingCurrencyTest() {
        admin.addCurrency(missingCurrency,1.02,CurrencyData);
        assertEquals(1.02, tester.getRate(missingCurrency, existingDate, CurrencyData), "The addCurrency method for adding not existed currency is incorrect");
    }

    @Test
    void addCurrencyExistedCurrencyTest() {
        admin.addCurrency(existedCurrency,1.02,CurrencyData);
        assertEquals(1.0, tester.getRate(existedCurrency, existingDate, CurrencyData),0.0001, "The addCurrency method for adding existed currency is incorrect");
    }

    // 2. Test updatePopular()
    @Test
    void updatePopularCurrencyMissingCurrencyTest() {
        admin.addCurrency(missingCurrency,1.02,CurrencyData);
        admin.updatePopular(missingCurrency, CurrencyData);
        assertEquals(1,CurrencyData.getJSONObject(existingDate).getJSONObject(missingCurrency).getInt("Popularity"),"The updatePopular method for new currency is incorrect");
    }

    @Test
    void updatePopularCurrencyExistedCurrencyTest() {
        admin.addCurrency(existedCurrency,1.02,CurrencyData);
        admin.updatePopular(existedCurrency, CurrencyData);
        assertEquals(1,CurrencyData.getJSONObject(existedCurrency).getJSONObject(missingCurrency).getInt("Popularity"),"The updatePopular method for existed currency is incorrect");
    }

    @Test
    void updateRateExistedCurrencyOnExistedDateTest(){
        admin.updateRate("USD","EUR",existingDate,1.0,0.5,CurrencyData);
        assertEquals(0.5,admin.calculateRate("USD","EUR",existingDate,CurrencyData),0.0001,"The updateRate method for existed currency on existed date is incorrect");
    }

    @Test
    void updateRateExistedCurrencyOnMissingDateTest(){
        admin.updateRate("USD","EUR",missingDate,1.0,0.5,CurrencyData);
        assertEquals(0.5,admin.calculateRate("USD","EUR",missingDate,CurrencyData),0.0001,"The updateRate method for existed currency on missing date is incorrect");
    }

    @Test
    void updateRateMissingCurrencyOnExistedDateTest(){
        admin.updateRate("USD",missingCurrency,existingDate,1.0,0.5,CurrencyData);
        assertEquals(0.5,admin.calculateRate("USD",missingCurrency,existingDate,CurrencyData),0.0001,"The updateRate method for existed currency on existed date is incorrect");
    }

    @Test
    void updateRateMissingCurrencyOnMissingDateTest(){
        admin.updateRate("USD",missingCurrency,missingDate,1.0,0.5,CurrencyData);
        assertEquals(0.5,admin.calculateRate("USD",missingCurrency,missingDate,CurrencyData),0.0001,"The updateRate method for existed currency on missing date is incorrect");
    }

}
