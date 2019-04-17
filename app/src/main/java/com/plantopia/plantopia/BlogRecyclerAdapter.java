package com.plantopia.plantopia;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {


    public List<BlogPost> blog_list;
    public Context context;
    public BlogRecyclerAdapter(List<BlogPost> blog_list)
    {

        this.blog_list=blog_list;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view =LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blog_list_item,viewGroup,false);
        context=viewGroup.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        String desc_data=blog_list.get(i).getDesc();
        viewHolder.setDescText(desc_data);
        String image_uri=blog_list.get(i).getImage_url();
    viewHolder.setBlogImage(image_uri);


    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private  View mView;
        private TextView descView;
        private ImageView blogImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView=itemView;
        }

        public void setDescText(String descText)
        {
            descView=mView.findViewById(R.id.blog_desc);
            descView.setText(descText);

        }

        public void setBlogImage(String downloadUri)
        {
            blogImageView=mView.findViewById(R.id.blog_image);
            Glide.with(context).load(downloadUri).into(blogImageView);


        }
    }
}
