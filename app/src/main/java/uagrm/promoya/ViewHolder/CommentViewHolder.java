package uagrm.promoya.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uagrm.promoya.R;

/**
 * Created by Shep on 11/19/2017.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public TextView authorView;
    public TextView bodyView;
    public TextView comment_time;
    public ImageView photoUrl;

    public CommentViewHolder(View itemView) {
        super(itemView);
        authorView = (TextView) itemView.findViewById(R.id.comment_author);
        bodyView = (TextView) itemView.findViewById(R.id.comment_body);
        comment_time = (TextView) itemView.findViewById(R.id.comment_time);
        photoUrl = (ImageView) itemView.findViewById(R.id.comment_photoUrl);
    }
}