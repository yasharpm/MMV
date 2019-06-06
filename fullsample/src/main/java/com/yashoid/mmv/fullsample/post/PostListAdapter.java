package com.yashoid.mmv.fullsample.post;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.fullsample.Basics;

import java.util.ArrayList;
import java.util.List;

public class PostListAdapter extends BaseAdapter {

    private Context mContext;

    private List<ModelFeatures> mPosts = new ArrayList<>();

    public PostListAdapter(Context context) {
        mContext = context;
    }

    public void setPosts(List<ModelFeatures> posts) {
        mPosts.clear();
        mPosts.addAll(posts);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPosts.size();
    }

    @Override
    public Object getItem(int position) {
        return mPosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (Integer) mPosts.get(position).get(Post.ID);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PostView view;

        if (convertView == null) {
            view = new PostView(mContext);
        }
        else {
            view = (PostView) convertView;
            Managers.unregisterTarget(view, view.getModel());
        }

        ModelFeatures post = mPosts.get(position);
        post.set(Basics.TYPE, Post.TYPE);

        Managers.registerTarget(view, mPosts.get(position));

        return view;
    }

}
