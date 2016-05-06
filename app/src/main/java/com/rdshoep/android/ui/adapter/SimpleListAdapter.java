package com.rdshoep.android.ui.adapter;
/*
 * @description
 *   Please write the DemosAdapter module's description
 * @author Zhang (rdshoep@126.com)
 *   http://www.rdshoep.com/
 * @version 
 *   1.0.0(5/5/2016)
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rdshoep.android.R;
import com.rdshoep.android.pojo.SimpleItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleListAdapter extends RecyclerView.Adapter<SimpleListAdapter.ViewHolder> {

    private List<SimpleItem> itemList;
    private OnSimpleItemClickListener clickListener;

    public List<SimpleItem> getItemList() {
        return itemList;
    }

    public SimpleListAdapter setItemList(List<SimpleItem> itemList) {
        this.itemList = itemList;
        return this;
    }

    public OnSimpleItemClickListener getClickListener() {
        return clickListener;
    }

    public SimpleListAdapter setClickListener(OnSimpleItemClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_simple_item, parent, false), clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimpleItem item = itemList.get(position);

        holder.tvTitle.setText(String.valueOf(item.getTitle()));
        holder.tvDescription.setText(String.valueOf(item.getDescription()));

        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(android.R.id.text1)
        TextView tvTitle;
        @BindView(android.R.id.text2)
        TextView tvDescription;

        OnSimpleItemClickListener clickListener;

        public ViewHolder(View itemView, OnSimpleItemClickListener listener) {
            super(itemView);
            this.clickListener = listener;

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SimpleItem simpleItem = (SimpleItem) v.getTag();
                    if (simpleItem != null) {
                        if (clickListener != null) {
                            clickListener.onItemClick(simpleItem);
                        }
                    }
                }
            });
        }
    }

    public interface OnSimpleItemClickListener {
        void onItemClick(SimpleItem item);
    }
}
