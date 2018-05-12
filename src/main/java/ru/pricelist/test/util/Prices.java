package ru.pricelist.test.util;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ru.pricelist.test.api.Price;

/**
 * Утилитный класс для работы с ценами.
 *
 * @see Price
 *
 * @author Valery Skavysh.
 */
public class Prices
{
    static private Comparator<Price> priceTreeComparator;

    static {
        //В общем-то костыль
        priceTreeComparator = (p1, p2) -> {
            Function<Price, IllegalArgumentException> illegalPriceTypeExceptionCreator = p ->
                    new IllegalArgumentException("Unexpected price type: " + p.getClass());

            if (p1.getClass().isAssignableFrom(Price.class)
                    && p2.getClass().isAssignableFrom(Price.class))
            {
                return p1.getBegin().compareTo(p2.getBegin());
            }

            if (p1.getClass().isAssignableFrom(Price.class))
            {
                if (p2 instanceof PriceBeginBound)
                {
                    return p1.getBegin().compareTo(p2.getBegin());
                }

                if (p2 instanceof PriceEndBound)
                {
                    return p1.getEnd().compareTo(p2.getEnd());
                }

                throw illegalPriceTypeExceptionCreator.apply(p2);
            }

            if (p1 instanceof PriceBeginBound)
            {
                if (p2.getClass().isAssignableFrom(Price.class) || p2 instanceof PriceBeginBound)
                {
                    return p1.getBegin().compareTo(p2.getBegin());
                }

                if (p2 instanceof PriceEndBound)
                {
                    return p1.getBegin().compareTo(p2.getEnd());
                }

                throw illegalPriceTypeExceptionCreator.apply(p2);
            }

            if (p1 instanceof PriceEndBound)
            {
                if (p2.getClass().isAssignableFrom(Price.class) || p2 instanceof PriceEndBound)
                {
                    return p1.getEnd().compareTo(p2.getEnd());
                }

                if (p2 instanceof PriceBeginBound)
                {
                    return p1.getEnd().compareTo(p2.getBegin());
                }

                throw illegalPriceTypeExceptionCreator.apply(p2);
            }

            throw illegalPriceTypeExceptionCreator.apply(p1);
        };
    }

    private Prices()
    {
    }

    /**
     * Объединяет множество старых цен с новыми.
     * <br>
     * Резрешает возможные конфликты пересечения сроков действия цен:
     *  <ul>
     *      <li>если значения цен одинаковы, период действия имеющейся цены
     *      увеличивается согласно периоду новой цены;</li>
     *
     *      <li>если значения цен отличаются, добавляется новая цена,
     *      а период действия старой цены уменьшается согласно периоду новой цены.</li>
     *  </ul>
     *
     * @param oldPrices
     *        коллекция старых цен.
     * @param newPrices
     *        коллекция новых цен.
     * @return объединенная коллекция старых и новых цен.
     */
    public static Set<Price> merge(
            Collection<Price> oldPrices,
            Collection<Price> newPrices)
    {
        HashMap<PriceKey, TreeSet<Price>> numberDepartPrices = new HashMap<>();
        for (Price price : oldPrices)
        {
            PriceKey key = new PriceKey(price.getProductCode(), price.getNumber(), price.getDepart());
            TreeSet<Price> pricesTree;
            if (numberDepartPrices.containsKey(key))
            {
                pricesTree = numberDepartPrices.get(key);
            }
            else
            {
                pricesTree = new TreeSet<>(priceTreeComparator);
                numberDepartPrices.put(key, pricesTree);
            }

            pricesTree.add(price);
        }

        LinkedList<Price> nonMergedNewPrices = new LinkedList<>();
        for (Price newPrice: newPrices)
        {
            PriceKey key = new PriceKey(newPrice.getProductCode(), newPrice.getNumber(), newPrice.getDepart());
            TreeSet<Price> pricesTree = numberDepartPrices.get(key);
            if (pricesTree == null)
            {
                nonMergedNewPrices.add(newPrice);
            }
            else
            {
                if (!mergeNewPrice(pricesTree, newPrice))
                {
                    nonMergedNewPrices.add(newPrice);
                }
            }
        }

        return Stream
                .concat(
                    numberDepartPrices.values().stream()
                        .flatMap(TreeSet::stream),
                    nonMergedNewPrices.stream())
                .collect(Collectors.toSet());
    }

    private static boolean mergeNewPrice(
            TreeSet<Price> pricesTree,
            Price newPrice)
    {
        LocalDateTime newPriceBegin = newPrice.getBegin();
        LocalDateTime newPriceEnd = newPrice.getEnd();
        Long newPriceValue = newPrice.getValue();

        boolean newPriceMerged = false;
        Iterator<Price> crossedPricesIterator = pricesTree
                .subSet(new PriceEndBound(newPriceBegin), false, new PriceBeginBound(newPriceEnd), false)
                .iterator();
        while (crossedPricesIterator.hasNext())
        {
            Price crossedPrice = crossedPricesIterator.next();
            LocalDateTime crossedPriceBegin = crossedPrice.getBegin();
            LocalDateTime crossedPriceEnd = crossedPrice.getEnd();
            Long crossedPriceValue = crossedPrice.getValue();

            if (crossedPriceBegin.isBefore(newPriceBegin))
            {
                if (!crossedPriceEnd.isAfter(newPriceEnd))
                {
                    if (crossedPriceValue.equals(newPriceValue))
                    {
                        crossedPrice.setEnd(newPriceEnd);
                        newPriceMerged = true;
                    }
                    else
                    {
                        crossedPrice.setEnd(newPriceBegin);
                    }
                }
                else
                {
                    if (crossedPriceValue.equals(newPriceValue))
                    {
                        newPriceMerged = true;
                    }
                    else
                    {
                        crossedPrice.setEnd(newPriceBegin);

                        Price remainingPartedPrice = new Price();
                        remainingPartedPrice.setDepart(crossedPrice.getDepart());
                        remainingPartedPrice.setNumber(crossedPrice.getNumber());
                        remainingPartedPrice.setProductCode(crossedPrice.getProductCode());
                        remainingPartedPrice.setValue(crossedPriceValue);
                        remainingPartedPrice.setBegin(newPriceEnd);
                        remainingPartedPrice.setEnd(crossedPriceEnd);

                        pricesTree.add(remainingPartedPrice);
                    }
                }
            }
            else
            {
                if (!crossedPriceEnd.isAfter(newPriceEnd))
                {
                    if (crossedPriceValue.equals(newPriceValue))
                    {
                        crossedPrice.setBegin(newPriceBegin);
                        crossedPrice.setEnd(newPriceEnd);

                        newPriceMerged = true;
                    }
                    else
                    {
                        crossedPricesIterator.remove();
                    }
                }
                else
                {
                    if (crossedPriceValue.equals(newPriceValue))
                    {
                        crossedPrice.setBegin(newPriceBegin);

                        newPriceMerged = true;
                    }
                    else
                    {
                        crossedPrice.setBegin(newPriceEnd);
                    }
                }
            }
        }

        return newPriceMerged;
    }

    private static class PriceKey
    {
        private String productCode;

        private int number;

        private int depart;

        private PriceKey(
                String productCode,
                int number,
                int depart)
        {
            this.productCode = productCode;
            this.number = number;
            this.depart = depart;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(productCode, number, depart);
        }

        @Override
        public boolean equals(
                Object obj)
        {
            if (obj instanceof PriceKey)
            {
                PriceKey that = (PriceKey) obj;

                return Objects.equals(productCode, that.productCode)
                        && Objects.equals(number, that.number)
                        && Objects.equals(depart, that.depart);
            }

            return false;
        }
    }

    private static final class PriceBeginBound
    extends Price
    {
        private PriceBeginBound(
                LocalDateTime begin)
        {
            setBegin(begin);
        }
    }

    private static final class PriceEndBound
    extends Price
    {
        private PriceEndBound(
                LocalDateTime end)
        {
            setEnd(end);
        }
    }
}
