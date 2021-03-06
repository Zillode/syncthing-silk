/*
 * Copyright (c) 2015 OpenSilk Productions LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package syncthing.android.ui.common;

import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import timber.log.Timber;

/**
 * Created by drew on 3/10/15.
 */
public abstract class CardRecyclerAdapter extends RecyclerView.Adapter<CardViewHolder> {

    protected ExpandableView.OnExpandListener mExpandListener;
    protected RecyclerView mRecyclerView;
    protected LayoutInflater mLayoutInflater;

    public CardRecyclerAdapter() {
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mExpandListener = (ExpandableView.OnExpandListener) mRecyclerView;
        mLayoutInflater = LayoutInflater.from(recyclerView.getContext());
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = null;
        mExpandListener = null;
        mLayoutInflater = null;
    }

    public abstract Card getItem(int pos);
    public android.databinding.DataBindingComponent getBindingComponent(){
        return null;
    }

    @Override
    public final CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = mLayoutInflater.inflate(viewType, parent, false);
        final CardViewHolder viewHolder;
        if (getBindingComponent() != null) {
            viewHolder = new CardViewHolder(v, getBindingComponent());
        } else {
            viewHolder = new CardViewHolder(v);
        }
        //Trick from the databinding talk by google
        viewHolder.getBinding().addOnRebindCallback(new OnRebindCallback() {
            @Override
            public boolean onPreBind(ViewDataBinding binding) {
                return mRecyclerView != null && mRecyclerView.isComputingLayout();
            }

            @Override
            public void onCanceled(ViewDataBinding binding) {
                if (mRecyclerView == null || mRecyclerView.isComputingLayout()) {
                    return;
                }
                int position = mRecyclerView.getChildAdapterPosition(binding.getRoot());
                if (position != RecyclerView.NO_POSITION) {
                    notifyItemChanged(position, DATA_INVALIDATION);
                }
            }
        });
        return viewHolder;
    }

    public void applyAdditionalBinding(Card card, CardViewHolder holder) {

    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.recycle();
        Card c = getItem(position);
        holder.bind(c, mExpandListener);
        applyAdditionalBinding(c, holder);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position, List<Object> payloads) {
        if (isForDataBinding(payloads)) {
            Timber.d("executing pending binding for %d", position);
            holder.getBinding().executePendingBindings();
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public int getItemViewType(int pos) {
        Card c = getItem(pos);
        return c.getLayout();
    }

    @Override
    public void onViewRecycled(CardViewHolder holder) {
        holder.recycle();
    }

    private static final Object DATA_INVALIDATION = new Object();
    private static boolean isForDataBinding(List<Object> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            return false;
        }
        for (Object obj : payloads) {
            if (obj != DATA_INVALIDATION) {
                return false;
            }
        }
        return true;
    }
}
