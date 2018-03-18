package com.codeworm.barkapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;
import java.util.List;

/**
 * Created by Harvie Marcelino on 02/11/2018.
 */

public class ProductListAdapter extends BaseAdapter{
    private Context mContext;
    private List<Product> mProductList;

    //Constructor

    public ProductListAdapter(Context mContext, List<Product> mProductList) {
        this.mContext = mContext;
        this.mProductList = mProductList;
    }

    public void addListItemToAdapter(List<Product> list) {
        //Add list to current array list of data
        mProductList.addAll(list);
        //Notify UI
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_product_list, null);
//        TextView tvName = (TextView)v.findViewById(R.id.tv_name);
//        TextView tvPrice = (TextView)v.findViewById(R.id.tv_price);
//        TextView tvDescription = (TextView)v.findViewById(R.id.tv_description);
//        //Set text for TextView
//        tvName.setText(mProductList.get(position).getName());
//        tvPrice.setText(String.valueOf(mProductList.get(position).getPrice()) + " $");
//        tvDescription.setText(mProductList.get(position).getDescription());

        //Save product id to tag
        v.setTag(mProductList.get(position).getId());

        return v;
    }
}
