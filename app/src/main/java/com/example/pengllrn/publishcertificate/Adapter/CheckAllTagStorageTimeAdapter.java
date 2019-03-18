package com.example.pengllrn.publishcertificate.Adapter;

import android.content.Context;

import com.example.pengllrn.publishcertificate.R;
import com.example.pengllrn.publishcertificate.base.ListViewAdapter;
import com.example.pengllrn.publishcertificate.base.ViewHolder;
import com.example.pengllrn.publishcertificate.bean.Tagg;

import java.util.List;

/**
 * Created by pengllrn on 2019/3/18.
 */

public class CheckAllTagStorageTimeAdapter extends ListViewAdapter<Tagg> {
    public CheckAllTagStorageTimeAdapter(Context context, List<Tagg> datas, int layoutId) {
        super(context,datas,layoutId);
    }

    @Override
    public void convert(ViewHolder holder, Tagg tag) {
        holder.setText(R.id.item7, tag.getGoods_name());
        holder.setText(R.id.item8, tag.getUid());
        if (tag.getDate_in_storage().equals("")) {
            holder.setText(R.id.item9, "该物品尚未入库");
        } else {
            holder.setText(R.id.item9, tag.getDate_in_storage());
        }
        holder.setText(R.id.item10, tag.getDate_out_storage());
    }
}
