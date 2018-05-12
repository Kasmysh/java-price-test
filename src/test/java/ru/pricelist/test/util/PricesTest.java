package ru.pricelist.test.util;

import org.junit.Assert;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import ru.pricelist.test.api.Price;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PricesTest
{
    private static int PRICE_POSITION = 1;

    private static String PRODUCT_CODE = "122856";

    private static int PRICE_NUMBER = 1;

    private static int PRICE_DEPART = 1;

    private static int PRICE_VALUE = 11000;

    private static LocalDateTime PRICE_BEGIN = LocalDateTime.parse("2013-01-01T00:00:00");

    private static LocalDateTime PRICE_END = LocalDateTime.parse("2013-01-31T23:59:59");

    @Test
    public void testMergeWithBothEmptyPricesCollections()
    {
        Set<Price> mergedPrices = Prices.merge(Collections.emptyList(),
                Collections.emptyList());

        Assert.assertNotNull(mergedPrices);
        Assert.assertTrue(mergedPrices.isEmpty());
    }

    @Test
    public void testMergeWithEmptyOldPricesCollection()
    {
        ReflectionAssert.assertReflectionEquals(createPricelist(),
                Prices.merge(Collections.emptyList(), createPricelist()), ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void testMergeWithEmptyNewPricesCollection()
    {
        ReflectionAssert.assertReflectionEquals(createPricelist(),
                Prices.merge(createPricelist(), Collections.emptyList()), ReflectionComparatorMode.LENIENT_ORDER);
    }

    @Test
    public void testMergeWithNewPriceCoveringDifferentValueOldPrice()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, 110,
                PRICE_BEGIN.minusMinutes(10), PRICE_END.plusMinutes(10)));

        List<Price> expectedPrices = createPricelist();
        expectedPrices.set(PRICE_POSITION, createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, 110,
                PRICE_BEGIN.minusMinutes(10), PRICE_END.plusMinutes(10)));

        test(expectedPrices, newPrices);
    }

    @Test
    public void testMergeWithNewPriceCoveringSameValueOldPrice()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, PRICE_VALUE,
                PRICE_BEGIN.minusMinutes(10), PRICE_END.plusMinutes(10)));

        List<Price> expectedPrices = createPricelist();
        expectedPrices.set(PRICE_POSITION, createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, PRICE_VALUE,
                PRICE_BEGIN.minusMinutes(10), PRICE_END.plusMinutes(10)));

        test(expectedPrices, newPrices);
    }

    @Test
    public void testMergeWithNewPriceSameBoundsDifferentValueOldPrice()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, 110, PRICE_BEGIN, PRICE_END));

        List<Price> expectedPrices = createPricelist();
        expectedPrices.set(PRICE_POSITION, createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, 110, PRICE_BEGIN, PRICE_END));

        test(expectedPrices, newPrices);
    }

    @Test
    public void testMergeWithNewPriceSameBoundsSameValueOldPrice()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, PRICE_VALUE, PRICE_BEGIN, PRICE_END));

        test(createPricelist(), newPrices);
    }

    @Test
    public void testMergeWithNewPriceWithinDifferentValueOldPrice()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, 110,
                PRICE_BEGIN.plusMinutes(10), PRICE_END.minusMinutes(10)));

        List<Price> expectedPrices = createPricelist();
        expectedPrices.get(PRICE_POSITION).setEnd(PRICE_BEGIN.plusMinutes(10));
        expectedPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, PRICE_VALUE,
                PRICE_END.minusMinutes(10), PRICE_END));
        expectedPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, 110,
                PRICE_BEGIN.plusMinutes(10), PRICE_END.minusMinutes(10)));

        test(expectedPrices, newPrices);
    }

    @Test
    public void testMergeWithNewPriceWithinSameValueOldPrice()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, PRICE_VALUE,
                PRICE_BEGIN.plusMinutes(10), PRICE_END.minusMinutes(10)));

        test(createPricelist(), newPrices);
    }

    @Test
    public void testMergeWithNewPriceWithinSameBeginDifferentValueOldPrice()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, 110,
                PRICE_BEGIN, PRICE_END.minusMinutes(10)));

        List<Price> expectedPrices = createPricelist();
        expectedPrices.get(PRICE_POSITION).setBegin(PRICE_END.minusMinutes(10));
        expectedPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, 110,
                PRICE_BEGIN, PRICE_END.minusMinutes(10)));

        test(expectedPrices, newPrices);
    }

    @Test
    public void testMergeWithNewPriceWithinSameBeginSameValueOldPrice()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, PRICE_VALUE,
                PRICE_BEGIN, PRICE_END.minusMinutes(10)));

        test(createPricelist(), newPrices);
    }

    @Test
    public void testMergeWithNewPriceWithinSameEndDifferentValueOldPrice()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, 110,
                PRICE_BEGIN.plusMinutes(10), PRICE_END));

        List<Price> expectedPrices = createPricelist();
        expectedPrices.get(PRICE_POSITION).setEnd(PRICE_BEGIN.plusMinutes(10));
        expectedPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, 110,
                PRICE_BEGIN.plusMinutes(10), PRICE_END));

        test(expectedPrices, newPrices);
    }

    @Test
    public void testMergeWithNewPriceWithinSameEndSameValueOldPrice()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, PRICE_VALUE,
                PRICE_BEGIN.plusMinutes(10), PRICE_END));

        test(createPricelist(), newPrices);
    }

    @Test
    public void testMergeWithNewPriceCrossingBeginDifferentValueOldPrice()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, 110,
                PRICE_BEGIN.minusMinutes(10), PRICE_END.minusMinutes(10)));

        List<Price> expectedPrices = createPricelist();
        expectedPrices.get(PRICE_POSITION).setBegin(PRICE_END.minusMinutes(10));
        expectedPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, 110,
                PRICE_BEGIN.minusMinutes(10), PRICE_END.minusMinutes(10)));

        test(expectedPrices, newPrices);
    }

    @Test
    public void testMergeWithNewPriceCrossingBeginSameValueOldPrice()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, PRICE_VALUE,
                PRICE_BEGIN.minusMinutes(10), PRICE_END.minusMinutes(10)));

        List<Price> expectedPrices = createPricelist();
        expectedPrices.get(PRICE_POSITION).setBegin(PRICE_BEGIN.minusMinutes(10));

        test(expectedPrices, newPrices);
    }

    @Test
    public void testMergeWithNewPriceCrossingEndDifferentValueOldPrice()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, 110,
                PRICE_BEGIN.plusMinutes(10), PRICE_END.plusMinutes(10)));

        List<Price> expectedPrices = createPricelist();
        expectedPrices.get(PRICE_POSITION).setEnd(PRICE_BEGIN.plusMinutes(10));
        expectedPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, 110,
                PRICE_BEGIN.plusMinutes(10), PRICE_END.plusMinutes(10)));

        test(expectedPrices, newPrices);
    }

    @Test
    public void testMergeWithNewPriceCrossingEndSameValueOldPrice()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, PRICE_VALUE,
                PRICE_BEGIN.plusMinutes(10), PRICE_END.plusMinutes(10)));

        List<Price> expectedPrices = createPricelist();
        expectedPrices.get(PRICE_POSITION).setEnd(PRICE_END.plusMinutes(10));

        test(expectedPrices, newPrices);
    }

    @Test
    public void testMergeWithNewPriceCrossingDifferentProductCodeOldPrices()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice("8090", PRICE_NUMBER, PRICE_DEPART, 24000,
                PRICE_BEGIN, PRICE_END));

        List<Price> expectedPrices = createPricelist();
        expectedPrices.add(createPrice("8090", PRICE_NUMBER, PRICE_DEPART, 24000,
                PRICE_BEGIN, PRICE_END));

        test(expectedPrices, newPrices);
    }

    @Test
    public void testMergeWithNewPriceCrossingDifferentPriceNumberOldPrices()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER + 50, PRICE_DEPART, 24000,
                PRICE_BEGIN, PRICE_END));

        List<Price> expectedPrices = createPricelist();
        expectedPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER + 50, PRICE_DEPART, 24000,
                PRICE_BEGIN, PRICE_END));

        test(expectedPrices, newPrices);
    }

    @Test
    public void testMergeWithNewPriceCrossingDifferentPriceDepartOldPrices()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART + 50, 18000,
                PRICE_BEGIN, PRICE_END));

        List<Price> expectedPrices = createPricelist();
        expectedPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART + 50, 18000,
                PRICE_BEGIN, PRICE_END));

        test(expectedPrices, newPrices);
    }

    @Test
    public void testMergeWithNewPriceCrossingSeveralOldPrices()
    {
        LocalDateTime begin = LocalDateTime.parse("2012-11-01T10:00:00");
        LocalDateTime end = LocalDateTime.parse("2013-02-21T20:59:59");
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, 14000, begin, end));

        List<Price> expectedPrices = createPricelist();
        expectedPrices.get(0).setEnd(begin);
        expectedPrices.get(2).setBegin(begin);
        expectedPrices.remove(1);

        test(expectedPrices, newPrices);
    }

    @Test
    public void testMergeWithSeveralNewPrices()
    {
        List<Price> newPrices = new ArrayList<>();
        newPrices.add(createPrice("122856", 1, 1, 11000,
                LocalDateTime.parse("2013-01-20T00:00:00"), LocalDateTime.parse("2013-02-20T23:59:59")));
        newPrices.add(createPrice("122856", 2, 1, 92000,
                LocalDateTime.parse("2013-01-15T00:00:00"), LocalDateTime.parse("2013-01-25T23:59:59")));
        newPrices.add(createPrice("6654", 1, 2, 4000,
                LocalDateTime.parse("2013-01-12T00:00:00"), LocalDateTime.parse("2013-01-13T23:59:59")));

        List<Price> expectedPrices = createPricelist();
        expectedPrices.get(PRICE_POSITION).setEnd(LocalDateTime.parse("2013-02-20T23:59:59"));
        expectedPrices.get(3).setEnd(LocalDateTime.parse("2013-01-15T00:00:00"));
        expectedPrices.get(4).setEnd(LocalDateTime.parse("2013-01-12T00:00:00"));
        expectedPrices.add(createPrice("122856", 2, 1, 92000,
                LocalDateTime.parse("2013-01-15T00:00:00"), LocalDateTime.parse("2013-01-25T23:59:59")));
        expectedPrices.add(createPrice("6654", 1, 2, 4000,
                LocalDateTime.parse("2013-01-12T00:00:00"), LocalDateTime.parse("2013-01-13T23:59:59")));
        expectedPrices.add(createPrice("6654", 1, 2, 5000,
                LocalDateTime.parse("2013-01-13T23:59:59"), LocalDateTime.parse("2013-01-31T23:59:59")));

        test(expectedPrices, newPrices);
    }

    private void test(
            List<Price> expectedPrices,
            List<Price> newPrices)
    {
        ReflectionAssert.assertReflectionEquals(expectedPrices,
                Prices.merge(createPricelist(), newPrices), ReflectionComparatorMode.LENIENT_ORDER);
    }

    private List<Price> createPricelist()
    {
        List<Price> prices = new ArrayList<>();
        prices.add(createPrice("122856", 1, 1, 12000,
                LocalDateTime.parse("2012-11-01T00:00:00"), LocalDateTime.parse("2012-11-30T23:59:59")));
        prices.add(createPrice("122856", 1, 1, 14000,
                LocalDateTime.parse("2013-02-20T23:59:59"), LocalDateTime.parse("2013-02-21T23:59:59")));
        prices.add(createPrice("122856", 2, 1, 99000,
                LocalDateTime.parse("2013-01-10T00:00:00"), LocalDateTime.parse("2013-01-20T23:59:59")));
        prices.add(createPrice("6654", 1, 2, 5000,
                LocalDateTime.parse("2013-01-01T00:00:00"), LocalDateTime.parse("2013-01-31T23:59:59")));

        prices.add(PRICE_POSITION, createPrice(PRODUCT_CODE, PRICE_NUMBER, PRICE_DEPART, PRICE_VALUE,
                PRICE_BEGIN, PRICE_END));

        return prices;
    }

    private Price createPrice(
            String productCode,
            int number,
            int depart,
            long value,
            LocalDateTime begin,
            LocalDateTime end)
    {
        Price price = new Price();
        price.setProductCode(productCode);
        price.setNumber(number);
        price.setDepart(depart);
        price.setBegin(begin);
        price.setEnd(end);
        price.setValue(value);

        return price;
    }
}
