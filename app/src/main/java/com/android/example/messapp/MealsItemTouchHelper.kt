package com.android.example.messapp

import android.R
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class MealsItemTouchHelper( adapter: MealsListAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private var mAdapter:MealsListAdapter = adapter
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        mAdapter.deleteItem(position)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val icon: Drawable?
        lateinit var background: ColorDrawable
        val itemView: View = viewHolder.itemView
        val backgroundCornerOffset = 20
        if (dX > 0) {
            icon = ContextCompat.getDrawable(mAdapter.getContext1()!!, R.drawable.ic_delete)
            if(mAdapter.context!=null){
                background = ColorDrawable(ContextCompat.getColor(mAdapter.getContext1()!!, R.color.holo_red_light))
            }

        } else {
            icon = ContextCompat.getDrawable(mAdapter.getContext1()!!, R.drawable.ic_delete)
            background = ColorDrawable(Color.RED)
        }
        assert(icon != null)
        val iconMargin: Int = (itemView.getHeight() - icon!!.intrinsicHeight) / 2
        val iconTop: Int = itemView.getTop() + (itemView.getHeight() - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight
        if (dX > 0) { // Swiping to the right
            val iconLeft: Int = itemView.getLeft() + iconMargin
            val iconRight: Int = itemView.getLeft() + iconMargin + icon.intrinsicWidth
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(
                itemView.getLeft(), itemView.getTop(),
                itemView.getLeft() + dX.toInt() + backgroundCornerOffset, itemView.getBottom()
            )
        } else if (dX < 0) { // Swiping to the left
            val iconLeft: Int = itemView.getRight() - iconMargin - icon.intrinsicWidth
            val iconRight: Int = itemView.getRight() - iconMargin
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(
                itemView.getRight() + dX.toInt() - backgroundCornerOffset,
                itemView.getTop(), itemView.getRight(), itemView.getBottom()
            )
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0)
        }
        background.draw(c)
        icon.draw(c)
    }
}