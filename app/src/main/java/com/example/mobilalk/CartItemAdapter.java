package com.example.mobilalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolderCart> {
    private final ArrayList<TicketItem> mTicketItemData;
    private final Context nContext;
    private int lastPosition = -1;

    CartItemAdapter(Context context, ArrayList<TicketItem> ticketData) {
        this.mTicketItemData = ticketData;
        nContext = context;
    }

    @NonNull
    @Override
    public CartItemAdapter.ViewHolderCart onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderCart(LayoutInflater.from(nContext).inflate(R.layout.cart_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.ViewHolderCart holder, int position) {
        TicketItem currentItem = mTicketItemData.get(position);

        holder.bindToCart(currentItem);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(nContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mTicketItemData.size();
    }

    class ViewHolderCart extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mDetail;
        private TextView mPrice;
        private TextView mDate;
        private ImageView mImage;
        private TextView mAmount;

        ViewHolderCart(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.ticketTitle);
            mDetail = itemView.findViewById(R.id.ticketDetail);
            mPrice = itemView.findViewById(R.id.price);
            mDate = itemView.findViewById(R.id.concertDate);
            mImage = itemView.findViewById(R.id.ticketImage);
            mAmount = itemView.findViewById(R.id.cartamountcount);
        }

        public void bindToCart(TicketItem currentItem) {
            mTitle.setText(currentItem.getBandname());
            mDetail.setText(currentItem.getDetails());
            mPrice.setText(currentItem.getPrice());
            mDate.setText(android.text.format.DateFormat.format("yyyy.MM.dd. EEEE, HH:mm 'GMT'z", currentItem.getTime().toDate()));
            Glide.with(nContext)
                    .load(currentItem.getBandimages()) // image url
                    .error(R.mipmap.ic_launcher_round)  // any image in case of error
                    .into(mImage);
            mAmount.setText(String.valueOf(currentItem.getIn_cart()));
            itemView.findViewById(R.id.delete_from_cart)
                    .setOnClickListener(view -> ((CartActivity) nContext).deleteTicket(currentItem));


        }
    }
}


