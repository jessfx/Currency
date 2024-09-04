package currency;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.data.JSONObject;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class CurrencyCalculationsTest {

    // parameters for compareRate()
    private static User tester;
    private static final String USDCurrency = "USD";
    private static final String EURCurrency = "EUR";
    private static final String GBPCurrency = "GBP";
    private static final String AUDCurrency = "AUD";

    private static final String missingCurrency = "CAD";

    private static final String existingDate = "2024-08-25";
    private static final String missingDate = "2024-12-26";

    private static final JSONObject CurrencyData = PApplet.loadJSONObject(new File("Currency_Data.json"));

    private static String fromCurrency;
    private static String toCurrency;

    private static final List<String> popularCurrencies = new ArrayList<>();

    @BeforeAll
    public static void setUp() {
        tester = new User("123456789", "password");

        popularCurrencies.add(USDCurrency);
        popularCurrencies.add(EURCurrency);
        popularCurrencies.add(GBPCurrency);
        popularCurrencies.add(AUDCurrency);

    }

    // 1. Test getRate()
    @Test
    void getRateCurrencyIsUSD() {
        assertEquals(1, tester.getRate(USDCurrency, existingDate, CurrencyData), "The getRate method for an existing currency is incorrect");
    }

    @Test
    void getRateDateMissing() {
        assertEquals(0.0, tester.getRate(USDCurrency, missingDate, CurrencyData), "The getRate method for a missing date does not return 0");
    }

    @Test
    void getRateCurrencyMissing() {
        assertEquals(0.0, tester.getRate(missingCurrency, existingDate, CurrencyData), "The getRate method for a missing currency does not return 0");
    }

    @Test
    void getRateCurrencyNotUSD() {
        assertEquals(1.12, tester.getRate(EURCurrency, existingDate, CurrencyData), "The getRate method for an existing, nonUSD currency is incorrect");
    }

    // 2. Test calculateRate()

    @Test
    void calculateRateUSDToEUR() {
        fromCurrency = USDCurrency;
        toCurrency = EURCurrency;

        assertEquals((1/1.12), tester.calculateRate(fromCurrency, toCurrency, existingDate, CurrencyData), 0.0001, "The calculateRate method from USD to EUR is calculated incorrectly");
    }

    @Test
    void calculateRateEURToUSD() {
        fromCurrency = EURCurrency;
        toCurrency = USDCurrency;

        assertEquals(1.12, tester.calculateRate(fromCurrency, toCurrency, existingDate, CurrencyData), 0.0001, "The calculateRate method from EUR to USD is calculated incorrectly");
    }

    @Test
    void calculateRateUSDToUSD() {
        // edged case of converting to itself - USD
        fromCurrency = USDCurrency;
        toCurrency = USDCurrency;

        assertEquals(1, tester.calculateRate(fromCurrency, toCurrency, existingDate, CurrencyData), 0.0001, "The calculateRate method from USD to USD is calculated incorrectly");
    }

    @Test
    void calculateRateEURToEUR() {
        // edged case of converting to itself - not USD
        fromCurrency = EURCurrency;
        toCurrency = EURCurrency;

        assertEquals(1, tester.calculateRate(fromCurrency, toCurrency, existingDate, CurrencyData), 0.0001, "The calculateRate method from EUR to EUR is calculated incorrectly");
    }

    @Test
    void calculateRateEURToGBP() {
        fromCurrency = EURCurrency;
        toCurrency = GBPCurrency;

        assertEquals((1.12/1.32), tester.calculateRate(fromCurrency, toCurrency, existingDate, CurrencyData), 0.0001, "The calculateRate method from EUR to GBP is calculated incorrectly");
    }

    @Test
    void calculateRateUSDToMissingCurr() {
        fromCurrency = USDCurrency;
        toCurrency = missingCurrency;

        assertEquals(0.0, tester.calculateRate(fromCurrency, toCurrency, existingDate, CurrencyData), "The calculateRate method from USD to non-existent currency should return 0");
    }

    @Test
    void calculateRateUSDToEURMissingDate() {
        fromCurrency = USDCurrency;
        toCurrency = EURCurrency;

        assertEquals(0.0, tester.calculateRate(fromCurrency, toCurrency, missingDate, CurrencyData), "The calculateRate method from USD using a non-existent date should return 0");
    }

    @Test
    void calculateRateMissingCurrToUSD() {
        fromCurrency = missingCurrency;
        toCurrency = USDCurrency;

        assertEquals(0.0, tester.calculateRate(fromCurrency, toCurrency, existingDate, CurrencyData), "The calculateRate method from non-existent currency to USD should return 0");
    }

    @Test
    void calculateRateEURToUSDMissingDate() {
        fromCurrency = EURCurrency;
        toCurrency = USDCurrency;

        assertEquals(0.0, tester.calculateRate(fromCurrency, toCurrency, missingDate, CurrencyData), "The calculateRate method to USD using a non-existing date should return 0");
    }

    @Test
    void calculateRateEURToGBPMissingDate() {
        fromCurrency = EURCurrency;
        toCurrency = GBPCurrency;

        assertEquals(0.0, tester.calculateRate(fromCurrency, toCurrency, missingDate, CurrencyData), "The calculateRate method from non-USD to non-USD using a non-existing date should be 0");
    }

    @Test
    void calculateRateEURToMissingCurr() {
        fromCurrency = EURCurrency;
        toCurrency = missingCurrency;

        assertEquals(0.0, tester.calculateRate(fromCurrency, toCurrency, existingDate, CurrencyData), "The calculateRate method from non-USD to non-existent currency should return 0");
    }

    @Test
    void calculateRateMissingCurrToEUR() {
        fromCurrency = missingCurrency;
        toCurrency = EURCurrency;

        assertEquals(0.0, tester.calculateRate(fromCurrency, toCurrency, existingDate, CurrencyData), "The calculateRate method from non-existent currency to non-USD should return 0");
    }

    // 3. Test popularRateMap()

    @Test
    void popularRateMap() {
        Map <String, Double> USDMap = new HashMap<>();
        Map <String, Double> EURMap = new HashMap<>();
        Map <String, Double> GBPMap = new HashMap<>();
        Map <String, Double> AUDMap = new HashMap<>();

        Map<String, Map<String, Double>> expectedMap = new HashMap<>();

        USDMap.put(EURCurrency, (1/1.12));
        USDMap.put(GBPCurrency, (1/1.32));
        USDMap.put(AUDCurrency, (1/0.68));

        EURMap.put(USDCurrency, (1.12));
        EURMap.put(GBPCurrency, (1.12/1.32));
        EURMap.put(AUDCurrency, (1.12/0.68));

        GBPMap.put(USDCurrency, (1.32));
        GBPMap.put(EURCurrency, (1.32/1.12));
        GBPMap.put(AUDCurrency, (1.32/0.68));

        AUDMap.put(USDCurrency, (0.68));
        AUDMap.put(EURCurrency, (0.68/1.12));
        AUDMap.put(GBPCurrency, (0.68/1.32));

        expectedMap.put(USDCurrency, USDMap);
        expectedMap.put(EURCurrency, EURMap);
        expectedMap.put(GBPCurrency, GBPMap);
        expectedMap.put(AUDCurrency, AUDMap);

        assertEquals(expectedMap, tester.popularRateMap(popularCurrencies, existingDate, CurrencyData), "The popularRateMap method is returning an incorrect Map");
    }

}