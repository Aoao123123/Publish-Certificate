package com.example.pengllrn.publishcertificate.Adapter;

import android.content.Context;

import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.base.ListViewAdapter;
import com.example.pengllrn.publishcertificate.base.ViewHolder;
import com.example.pengllrn.publishcertificate.bean.Tagg;

import java.util.List;

/**
 * Created by Aoao Bot on 2019/3/17.
 */

public class TaggAdapter extends ListViewAdapter<Tagg> {
    public TaggAdapter(Context context, List<Tagg> datas, int layoutId) {
        super(context,datas,layoutId);
    }

    @Override
    public void convert(ViewHolder holder, Tagg tag) {
        holder.setText(R.id.item1, tag.getGoods_name());
        holder.setText(R.id.item2, tag.getModel());
        holder.setText(R.id.item3, tag.getMaterial());
        holder.setText(R.id.item4, tag.getSn());
        holder.setText(R.id.item5, tag.getManufacturer());
        holder.setText(R.id.item6, tag.getProduce_date());
    }
}
