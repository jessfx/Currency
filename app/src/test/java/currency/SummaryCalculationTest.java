package currency;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SummaryCalculationTest {

    private static final double INVALID_RATE = -1;
    private static final List<Double> EMPTY_RATE_LIST = new ArrayList<>();
    private static User tester;

    private static List<Double> testerRateList1;
    private static List<Double> testerRateList2;

    @BeforeAll
    static void setUpBeforeClass() {
        tester = new User("123456789", "password");

        testerRateList1 = Arrays.asList(0.5, 2.0, 1.4, 0.8, 2.6, 9.0);
        testerRateList2 = Arrays.asList(0.5, 2.0, 1.4, 0.8, 2.6);
    }

    @Test
    void AverageValidArray() {
        assertEquals(2.71666, tester.getAverage(testerRateList1), 0.0001, "The average of the array is miscalculated");
    }

    @Test
    void AverageInvalidArray() {
        assertEquals(INVALID_RATE, tester.getAverage(EMPTY_RATE_LIST), 0.0001, "Average should return -1 if the array is empty");
    }

    @Test
    void MedianValidArrayEvenLength() {
        assertEquals(1.7, tester.getMedian(testerRateList1), 0.0001, "The median of the array with an even number of elements is miscalculated");
    }

    @Test
    void MedianValidArrayOddLength() {
        assertEquals(1.4, tester.getMedian(testerRateList2), 0.0001, "The median of the array with an odd number of elements is miscalculated");
    }

    @Test
    void MedianInvalidArray() {
        assertEquals(INVALID_RATE, tester.getMedian(EMPTY_RATE_LIST), 0.0001, "Median should return -1 if the array is empty");
    }

    @Test
    void StandardDeviationValidArray() {
        assertEquals(2.89621, tester.getStandardDeviation(testerRateList1), 0.0001, "The standard deviation of the array is miscalculated");
    }

    @Test
    void StandardDeviationInvalidArray() {
        assertEquals(INVALID_RATE, tester.getStandardDeviation(EMPTY_RATE_LIST), 0.0001, "Standard deviation should return -1 if the array is empty");
    }

    @Test
    void MinValidArray() {
        assertEquals(0.5, tester.getMin(testerRateList1), 0.0001, "The minimum of the array is miscalculated");
    }

    @Test
    void MinInvalidArray() {
        assertEquals(INVALID_RATE, tester.getMin(EMPTY_RATE_LIST), 0.0001, "Minimum should return -1 if the array is empty");
    }

    @Test
    void MaxValidArray() {
        assertEquals(9.0, tester.getMax(testerRateList1), 0.0001, "The maximum of the array is miscalculated");
    }

    @Test
    void MaxInvalidArray() {
        assertEquals(INVALID_RATE, tester.getMax(EMPTY_RATE_LIST), 0.0001, "Maximum should return -1 if the array is empty");
    }

}
