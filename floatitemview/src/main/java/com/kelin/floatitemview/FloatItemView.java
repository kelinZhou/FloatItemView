package com.kelin.floatitemview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 描述 用来显示悬浮条目的布局容器。
 * 创建人 kelin
 * 创建时间 2018/4/16  下午5:12
 * 版本 v 1.0.0
 */

public class FloatItemView extends FrameLayout {

    private View mFloatLayout;
    private int mLayoutPosition = -1;
    private SparseArray<View> contents = new SparseArray<>(2);
    private int mCurFloatLayoutId;
    private RecyclerView.Adapter mAdapter;
    private int mCurPosition;
    private DataBinding mDataBinding;

    public FloatItemView(@NonNull Context context) {
        this(context, null);
    }

    public FloatItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatItemView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVisibility(GONE);
    }

    public boolean isEmpty() {
        return mFloatLayout == null;
    }

    private void setFloatContent(@LayoutRes int floatContentId) {
        View floatContent = contents.get(floatContentId);
        if (floatContent == null) {
            floatContent = LayoutInflater.from(getContext()).inflate(floatContentId, this, false);
            if (floatContent == null) {
                throw new InflateException("the LayoutRes:" + floatContentId + " not found!");
            } else {
                contents.put(floatContentId, floatContent);
            }
        }
        if (isEmpty()) {  //如果当前是空布局则加入，否则可能一个屏幕内出现了两个可以悬浮的条目，则先不加入。
            inflaterContent(floatContentId, floatContent);
        }
    }

    private void inflaterContent(int floatContentId, View floatContent) {
        removeAllViews();
        ViewGroup parent = (ViewGroup) floatContent.getParent();
        if (parent != null) parent.removeView(floatContent);
        addView(floatContent);
        floatContent.requestLayout();  //7.0上必须要调用这段代码，否则view不会被绘制。
        mFloatLayout = floatContent;
        mCurFloatLayoutId = floatContentId;
    }

    /**
     * 获取当前悬浮控件绑定的数据以及填充的View在RecyclerView中所对应的索引。
     * 这个方法等同于{@link RecyclerView.ViewHolder#getLayoutPosition()}。
     *
     * @return 返回Recycler所对应的索引。
     * @see RecyclerView.ViewHolder#getLayoutPosition()
     */
    public int getLayoutPosition() {
        return mLayoutPosition;
    }

    /**
     * 设置当前悬浮条的所在列表的布局位置。
     *
     * @param layoutPosition 当前的布局位置。
     */
    void setLayoutPosition(int layoutPosition) {
        mLayoutPosition = layoutPosition;
    }

    /**
     * 获取悬浮条目中所填充的布局。
     *
     * @return 返回 {@link #attachToRecyclerView(RecyclerView, int, int, DataBinding)} 方法中 layoutRes参数所被填充后得到的View。
     * @see #attachToRecyclerView(RecyclerView, int, DataBinding)
     * @see #attachToRecyclerView(RecyclerView, int, int, DataBinding)
     */
    public View getItemView() {
        return mFloatLayout;
    }

    /**
     * 关联到RecyclerView。
     *
     * @param recyclerView 要关联的RecyclerView。
     * @param layoutRes    要悬浮的条目所对应的布局文件资源ID。
     * @param binding      绑定数据的回调。
     */
    public void attachToRecyclerView(@NonNull RecyclerView recyclerView, @LayoutRes int layoutRes, @NonNull DataBinding binding) {
        attachToRecyclerView(recyclerView, layoutRes, layoutRes, binding);
    }

    /**
     * @param recyclerView 要关联的RecyclerView。
     * @param viewType     要悬浮的条目所对应的ViewHolder的viewType。如果你RecyclerView的Adapter的
     *                     {@link RecyclerView.Adapter#getItemViewType(int)}方法的返回值就是你布局文件的话
     *                     （使用google推荐的方式用布局资源ID作为ViewType），你可以直接调用三个参数的
     *                     {@link #attachToRecyclerView(RecyclerView, int, DataBinding)} 方法。
     * @param layoutRes    要悬浮的条目所对应的布局文件资源ID。
     * @param binding      绑定数据的回调。
     */
    public void attachToRecyclerView(@NonNull RecyclerView recyclerView, int viewType, @LayoutRes int layoutRes, @NonNull DataBinding binding) {
        setFloatContent(layoutRes);
        mAdapter = recyclerView.getAdapter();
        if (mAdapter == null) {
            throw new NullPointerException("The recyclerView must set one Adapter!");
        }

        recyclerView.addOnScrollListener(new RecyclerViewOnScrollListener(viewType));
        mDataBinding = binding;
    }

    /**
     * 判断当前悬浮条目是否正在显示中。
     *
     * @return 如果是则返回true，否则返回false。
     */
    private boolean isShowing() {
        return getVisibility() == View.VISIBLE;
    }

    private class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {

        private final int mFloatHolderViewType;

        RecyclerViewOnScrollListener(int floatHolderViewType) {
            this.mFloatHolderViewType = floatHolderViewType;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (dy != 0) {
                int position = dy > 0 ? mCurPosition + 1 : mCurPosition;
                View view;
                if (isFloatTypeFromPosition(mCurPosition + 1) && (view = lm.findViewByPosition(mCurPosition + 1)) != null) {
                    if ((dy > 0 && isFirstFloatAble(position)) ||
                            (dy < 0 && isFirstFloatAble(position) && lm.findViewByPosition(position).getTop() >= 0)) {
                        setVisibility(GONE);
                    } else {
                        int top = view.getTop();
                        if (top <= (isShowing() ? getHeight() : 0)) {
                            setVisibility(VISIBLE);
                            int marginTop = 0;
                            MarginLayoutParams lp = getMarginLayoutParams(view);
                            if (lp != null) {
                                marginTop = lp.topMargin;
                            }
                            if (top - marginTop <= 0) {
                                setY(0);
                            } else {
                                setY(-(getHeight() - top));
                            }
                        } else {
                            setY(0);
                        }
                    }
                }
            }

            int first = lm.findFirstVisibleItemPosition();
            if (first == 0 && isFloatTypeFromPosition(first)) {
                updateFloatLayout(dy, first);
            } else if (mCurPosition != first) {
                int max = Math.max(mCurPosition, first);
                int min = Math.min(mCurPosition, first);
                //如果本次的位置和上一次的位置不是相邻的，那么就循环将跳过的位置进行更新。否则直接更新。
                if (max - min > 1) {
                    if (dy < 0) {
                        for (int i = max - 1; i >= min; i--) {
                            updateFloatLayout(dy, i);
                        }
                    } else {
                        for (int i = min + 1; i <= max; i++) {
                            updateFloatLayout(dy, i);
                        }
                    }
                } else {
                    updateFloatLayout(dy, first);
                }
                mCurPosition = first;
            }
        }

        private void updateFloatLayout(int dy, int position) {
            int targetPosition = -1;
            if (dy < 0) {
                for (int i = position; i >= 0; i--) {
                    if (isFloatTypeFromPosition(i)) {
                        targetPosition = i;
                        break;
                    }
                }
            } else {
                if (isFloatTypeFromPosition(position)) {
                    targetPosition = position;
                }
            }

            if (targetPosition >= 0 && getLayoutPosition() != targetPosition) {
                if (!isShowing()) {
                    setVisibility(VISIBLE);
                }

                setLayoutPosition(targetPosition);
                mDataBinding.onBind(FloatItemView.this, targetPosition);
            }
        }

        /**
         * 根据position判断是否为悬浮条目。
         *
         * @param position 条目索引。
         * @return 如果是悬浮条目则返回true，否则返回false。
         */
        private boolean isFloatTypeFromPosition(int position) {
            return mAdapter.getItemViewType(position) == mFloatHolderViewType;
        }

        MarginLayoutParams getMarginLayoutParams(@NonNull View view) {
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp instanceof MarginLayoutParams) {
                return (MarginLayoutParams) lp;
            }
            return null;
        }

        /**
         * 根据布局位置判断是否是第一个可悬浮的条目。
         *
         * @param position 要判断的位置。
         */
        private boolean isFirstFloatAble(int position) {
            boolean result = true;
            for (int i = 0; i < position; i++) {
                if (mAdapter.getItemViewType(i) == mFloatHolderViewType) {
                    result = false;
                    break;
                }
            }
            return result;
        }
    }

    public interface DataBinding {

        void onBind(@NonNull FloatItemView floatItemView, int position);
    }
}
