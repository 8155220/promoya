package uagrm.promoya.ViewHolder.ClientViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.like.LikeButton;

import uagrm.promoya.Interface.ItemClickListener;
import uagrm.promoya.R;

/**
 * Created by Shep on 11/13/2017.
 */

public class ClientProductViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        public TextView product_name;
        public TextView product_price;
        public TextView product_store;
        public ImageView product_image;
        public LikeButton heart_button;


        private ItemClickListener itemClickListener;


        public ClientProductViewHolder(View itemView) {
            super(itemView);
            product_name = (TextView)itemView.findViewById(R.id.product_name);
            product_price = (TextView)itemView.findViewById(R.id.product_price);
            product_store = (TextView)itemView.findViewById(R.id.product_store);
            product_image = (ImageView)itemView.findViewById(R.id.product_image);
            heart_button = (LikeButton)itemView.findViewById(R.id.heart_button);
            itemView.setOnClickListener(this);

        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view){
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }

}

