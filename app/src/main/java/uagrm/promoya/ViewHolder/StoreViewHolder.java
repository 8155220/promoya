package uagrm.promoya.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uagrm.promoya.Common.Common;
import uagrm.promoya.Interface.ItemClickListener;
import uagrm.promoya.R;

/**
 * Created by Shep on 10/26/2017.
 */

public class StoreViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    public TextView product_name;
    public ImageView product_image;
    public ImageView product_share;

    private ItemClickListener itemClickListener;
    public StoreViewHolder(View itemView) {
        super(itemView);
        product_name = (TextView)itemView.findViewById(R.id.product_name);
        product_image = (ImageView)itemView.findViewById(R.id.product_image);
        product_share = (ImageView)itemView.findViewById(R.id.product_share);

        //itemView.setOnCreateContextMenuListener(this);
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
