package uagrm.promoya.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import uagrm.promoya.Interface.ItemClickListener;
import uagrm.promoya.Model.Product;
import uagrm.promoya.ProductDetail;
import uagrm.promoya.ProductList;
import uagrm.promoya.R;
import uagrm.promoya.ViewHolder.ProductViewHolder;

/**
 * Created by Shep on 11/12/2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

    private List<Product> list;
    Context mContext;

    public ProductAdapter(List<Product> list, Context context) {
        this.list = list;
        this.mContext = context;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        final Product model = list.get(position);

        holder.product_name.setText(model.getName());
        Picasso.with(mContext)
                .load(model.getListImage().get(0))
                .into(holder.product_image);

        //final Product local = model;
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //start new activity
                //Toast.makeText(ProductList.this,local.getName(),Toast.LENGTH_SHORT).show();

                Intent producDetail = new Intent(mContext,ProductDetail.class);
                //producDetail.putExtra("ProductId",adapter.getRef(position).getKey());
                producDetail.putExtra("PRODUCT",model);
                //SE PUEDE MEJORAR ESTO SI LE PASAMOS EL MODELO PRODUCTO IMPLEMENTANDO EL SERIALISABLE
                mContext.startActivity(producDetail);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
