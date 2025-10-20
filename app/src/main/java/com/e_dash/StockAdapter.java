package com.e_dash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class StockAdapter extends ArrayAdapter<StockItem> {

    public StockAdapter(Context context, List<StockItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StockItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_stock, parent, false);
        }

        TextView tvIngredient = convertView.findViewById(R.id.tvIngredientName);
        TextView tvInfo = convertView.findViewById(R.id.tvStockInfo);

        tvIngredient.setText(item.getIngredient());
        tvInfo.setText("In: " + item.getInQty() + " " + item.getUnit() +
                " | Out: " + item.getOutQty() + " " + item.getUnit() +
                " | Remaining: " + item.getRemaining() + " " + item.getUnit());

        return convertView;
    }
}
