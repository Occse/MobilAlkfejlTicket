package com.example.mobilalk;

import android.content.Context;
import android.util.Log;
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

public class TicketItemAdapter extends RecyclerView.Adapter<TicketItemAdapter.ViewHolder> {
    private final ArrayList<TicketItem> mTicketItemData;
    private final Context nContext;
    private int lastPosition = -1;
    TicketItemAdapter(Context context, ArrayList<TicketItem> ticketData) {
        this.mTicketItemData = ticketData;
        nContext = context;
    }

    @NonNull
    @Override
    public TicketItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(nContext).inflate(R.layout.list_tickets, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TicketItemAdapter.ViewHolder holder, int position) {
        TicketItem currentItem = mTicketItemData.get(position);

        holder.bindTo(currentItem);

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

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mDetail;
        private TextView mPrice;
        private TextView mDate;
        private ImageView mImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.ticketTitle);
            mDetail = itemView.findViewById(R.id.ticketDetail);
            mPrice = itemView.findViewById(R.id.price);
            mDate = itemView.findViewById(R.id.concertDate);
            mImage = itemView.findViewById(R.id.ticketImage);
        }

        public void bindTo(TicketItem currentItem) {
            mTitle.setText(currentItem.getBandname());
            mDetail.setText(currentItem.getDetails());
            mPrice.setText(currentItem.getPrice());
            mDate.setText(android.text.format.DateFormat.format("yyyy.MM.dd. EEEE, HH:mm 'GMT'z", currentItem.getTime().toDate()));
            Glide.with(nContext)
                    .load(currentItem.getBandimages()) // image url
                    .error(R.mipmap.ic_launcher_round)  // any image in case of error
                    .into(mImage);

            itemView.findViewById(R.id.add_to_cart).setOnClickListener(v -> {
                Log.i("Activity", "Added to cart");
                ((ConcertListActivity) nContext).updateAlertIcon(currentItem);
            });
        }
    }
}


