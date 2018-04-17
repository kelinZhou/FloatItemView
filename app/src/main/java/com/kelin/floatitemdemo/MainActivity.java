package com.kelin.floatitemdemo;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.rvList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        items = loadData();
        recyclerView.setAdapter(new FloatAdapter(items));
((FloatItemView)findViewById(R.id.flItem)).attachToRecyclerView(recyclerView, R.layout.item_title_layout, new FloatItemView.DataBinding() {
    @Override
    public void onBind(@NonNull FloatItemView floatItemView, int position) {
        ((TextView) floatItemView.findViewById(R.id.tvTitle)).setText((CharSequence) items.get(position));
    }
});
    }

    @SuppressWarnings("unchecked")
    private List loadData() {
        List list = new ArrayList();
        for (int i = 0; i < 20; i++) {
            list.add("我是第" + i + "组");
            for (int x = 0; x < 10; x++) {
                list.add(new ItemModel(x, "我是条目" + (i * 10 + x + 1)));
            }
        }
        return list;
    }

    private abstract class BaseViewHolder<D> extends RecyclerView.ViewHolder {
        BaseViewHolder(ViewGroup parent, @LayoutRes int layoutRes) {
            super(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
        }

        abstract void bindData(D item);
    }

    private class FloatViewHolder extends BaseViewHolder<String> {

        private TextView tvTitle;

        FloatViewHolder(ViewGroup parent, int layoutRes) {
            super(parent, layoutRes);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        @Override
        void bindData(String item) {
            itemView.setTag(item);
            tvTitle.setText(item);
        }
    }

    private class ItemViewHolder extends BaseViewHolder<ItemModel> {

        private final TextView tvNumber;
        private final TextView tvText;

        ItemViewHolder(ViewGroup parent, int layoutRes) {
            super(parent, layoutRes);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvText = itemView.findViewById(R.id.tvText);
        }

        @Override
        void bindData(ItemModel item) {
            itemView.setTag(item);
            tvNumber.setText(String.valueOf(item.id));
            tvText.setText(item.title);
        }
    }


    private class FloatAdapter extends RecyclerView.Adapter<BaseViewHolder> {

        List items;

        FloatAdapter(List items) {
            this.items = items;
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return viewType == R.layout.item_title_layout ? new FloatViewHolder(parent, viewType) : new ItemViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            holder.bindData(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position) instanceof String ? R.layout.item_title_layout : R.layout.item_item_layout;
        }
    }

    private class ItemModel {
        private int id;
        private String title;

        public ItemModel(int id, String title) {
            this.id = id;
            this.title = title;
        }
    }
}

