package uagrm.promoya.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.share.internal.LikeButton;

import uagrm.promoya.Common.Common;
import uagrm.promoya.Interface.ItemClickListener;
import uagrm.promoya.R;

/**
 * Created by Shep on 10/26/2017.
 */

public class ProductViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener
        ,View.OnCreateContextMenuListener {

    public TextView product_name;
    public ImageView product_image;
    public ImageView heart_button;
    private ItemClickListener itemClickListener;

    public ProductViewHolder(View itemView) {
        super(itemView);
        product_name = (TextView)itemView.findViewById(R.id.product_name);
        product_image = (ImageView)itemView.findViewById(R.id.product_image);
        heart_button = (ImageView)itemView.findViewById(R.id.heart_button);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view){
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        contextMenu.setHeaderTitle("Seleccionar accion");
        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);
        contextMenu.add(0,2,getAdapterPosition(), Common.OFFER);
    }
}
