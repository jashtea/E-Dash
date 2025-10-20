package com.e_dash;

public class StockItem {
    private String ingredient, unit;
    private double inQty, outQty, remaining;

    public StockItem(String ingredient, double inQty, double outQty, double remaining, String unit) {
        this.ingredient = ingredient;
        this.inQty = inQty;
        this.outQty = outQty;
        this.remaining = remaining;
        this.unit = unit;
    }

    public String getIngredient() { return ingredient; }
    public double getInQty() { return inQty; }
    public double getOutQty() { return outQty; }
    public double getRemaining() { return remaining; }
    public String getUnit() { return unit; }
}
