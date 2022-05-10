package com.ezyedu.student.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ezyedu.student.OrderInfo_Page;
import com.ezyedu.student.R;
import com.ezyedu.student.model.CompletedOrders;
import com.ezyedu.student.model.Globals;
import com.ezyedu.student.model.ImageGlobals;

import java.util.ArrayList;
import java.util.List;

public class CompletedOrderAdapter extends RecyclerView.Adapter<CompletedOrderAdapter.CompletedHolder> {
    private Context context;
    private List<CompletedOrders> completedOrdersList = new ArrayList<>();
    public  static String img_url_base;
    public static  String base_app_url;


    public CompletedOrderAdapter(Context context, List<CompletedOrders> completedOrdersList) {
        this.context = context;
        this.completedOrdersList = completedOrdersList;
    }

    @NonNull
    @Override
    public CompletedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_order_adapter,parent,false);
        return new CompletedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedHolder holder, int position)
    {
        CompletedOrders pendingOrders = completedOrdersList.get(position);
        int status = pendingOrders.getStatus();
        if (status == 4)
        {
            int order_id = pendingOrders.getOrder_id();
            holder.ven_name.setText(pendingOrders.getVendor_name());
            holder.order_ref_id.setText(pendingOrders.getOrder_ref_id());
            holder.date.setText(pendingOrders.getDate());
            holder.price.setText("Rp "+pendingOrders.getFinal_amount().toString());
             String img_url = "https://dpzt0fozg75zu.cloudfront.net/";
            Glide.with(context).load(img_url_base+pendingOrders.getVendor_images()).into(holder.imageView);
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OrderInfo_Page.class);
                    intent.putExtra("id",order_id);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return completedOrdersList == null ?0: completedOrdersList.size();
    }

    public class CompletedHolder extends RecyclerView.ViewHolder{
        TextView ven_name,order_ref_id,date,price;
        ImageView imageView;
        RelativeLayout relativeLayout;


        //retrive base url
        Globals sharedData = Globals.getInstance();


        //get img global url
        ImageGlobals shareData1 = ImageGlobals.getInstance();



        public CompletedHolder(@NonNull View itemView) {
            super(itemView);
            ven_name = itemView.findViewById(R.id.ven_tittle);
            order_ref_id = itemView.findViewById(R.id.order_ref_id);
            date = itemView.findViewById(R.id.order_date);
            price = itemView.findViewById(R.id.order_price);
            imageView = itemView.findViewById(R.id.img_pending);
            relativeLayout = itemView.findViewById(R.id.pending_relative);

            //get domain url
            base_app_url = sharedData.getValue();
            Log.i("domain_url",base_app_url);

            //get image loading url
            img_url_base = shareData1.getIValue();
            Log.i("img_url_global",img_url_base);

        }
    }
}
