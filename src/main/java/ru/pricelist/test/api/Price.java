package ru.pricelist.test.api;

import java.time.LocalDateTime;

/**
 * Сущность цены.
 *
 * @author Valery Skavysh.
 */
public class Price
{
    private Long id;

    private String productCode;

    private Integer number;

    private Integer depart;

    private LocalDateTime begin;

    private LocalDateTime end;

    private Long value;

    public Price()
    {
    }

    public Long getId()
    {
        return id;
    }

    public void setId(
            Long id)
    {
        this.id = id;
    }

    public String getProductCode()
    {
        return productCode;
    }

    public void setProductCode(
            String productCode)
    {
        this.productCode = productCode;
    }

    public Integer getNumber()
    {
        return number;
    }

    public void setNumber(
            Integer number)
    {
        this.number = number;
    }

    public Integer getDepart()
    {
        return depart;
    }

    public void setDepart(
            Integer depart)
    {
        this.depart = depart;
    }

    public LocalDateTime getBegin()
    {
        return begin;
    }

    public void setBegin(
            LocalDateTime begin)
    {
        this.begin = begin;
    }

    public LocalDateTime getEnd()
    {
        return end;
    }

    public void setEnd(
            LocalDateTime end)
    {
        this.end = end;
    }

    public Long getValue()
    {
        return value;
    }

    public void setValue(
            Long value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return String.format("product_code_%s{number: %d, depart: %d, time_range: %s .. %s, value: %d}",
                productCode, number, depart, begin, end, value);
    }
}
