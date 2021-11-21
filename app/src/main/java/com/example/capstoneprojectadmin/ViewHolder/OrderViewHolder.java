package com.example.capstoneprojectadmin.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import Interface.ItemClickListener;
import com.example.capstoneprojectadmin.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId,txtOrderStatus,txtOrderDate,txtOrderPrice,txtOrderType,txtScheduledTime;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderDate = itemView.findViewById(R.id.order_time);
        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderType = itemView.findViewById(R.id.order_type);
        txtOrderPrice = itemView.findViewById(R.id.order_price);
        txtScheduledTime = itemView.findViewById(R.id.scheduled_time);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
    }


}
