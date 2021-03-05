package com.sealstudios.bullsheetgenerator2.utils;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;


public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;
    private final int moveType;  // 1 is final list activity // 2 is archive without selection // 3 is archive with selection

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter, int moveType) {
        this.moveType = moveType;
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        switch (moveType){
            case 1:
                return false;
            case 2:
                return false;
            case 3:
                return true;
        }
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        switch (moveType){
            case 1:
                return true;
            case 2:
                return false;
            case 3:
                return false;
        }
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        if (moveType == 3)
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

}