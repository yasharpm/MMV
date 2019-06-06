package com.yashoid.mmv.fullsample.post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.fullsample.Basics;
import com.yashoid.mmv.fullsample.MainFlow;
import com.yashoid.mmv.fullsample.R;
import com.yashoid.mmv.Target;

import java.util.List;

public class PostListFragment extends Fragment implements Target, PostList {

    private ListView mListPosts;
    private View mLoading;

    private PostListAdapter mPostListAdapter;

    private Model mPostListModel;
    private Model mMainFlowModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_postlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListPosts = view.findViewById(R.id.list_posts);
        mLoading = view.findViewById(R.id.loading);

        mListPosts.setVisibility(View.INVISIBLE);
        mLoading.setVisibility(View.INVISIBLE);

        mPostListAdapter = new PostListAdapter(getContext());
        mListPosts.setAdapter(mPostListAdapter);

        ModelFeatures mainFlowFeatures = new ModelFeatures.Builder()
                .add(Basics.TYPE, MainFlow.TYPE)
                .build();

        Managers.registerTarget(mMainFlowTarget, mainFlowFeatures);
    }

    @Override
    public void setModel(Model model) {
        mPostListModel = model;

        onModelChanged();
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        onModelChanged();
    }

    private void onModelChanged() {
        Integer status = mPostListModel.get(STATUS);

        if (status == null || status == STATUS_IDLE) {
            mPostListModel.perform(GET_POSTS);
            return;
        }

        switch (status) {
            case STATUS_LOADING:
                mListPosts.setVisibility(View.INVISIBLE);
                mLoading.setVisibility(View.VISIBLE);
                break;
            default:
                mListPosts.setVisibility(View.VISIBLE);
                mLoading.setVisibility(View.INVISIBLE);

                List<ModelFeatures> posts = mPostListModel.get(POSTS);

                if (posts != null) {
                    mPostListAdapter.setPosts(posts);
                }
                break;
        }
    }

    private void onMainFlowModelChanged() {
        Integer personId = mMainFlowModel.get(MainFlow.PERSON_ID);

        if (personId == null) {
            return;
        }

        ModelFeatures postListFeatures = new ModelFeatures.Builder()
                .add(Basics.TYPE, TYPE)
                .add(PERSON_ID, personId)
                .build();

        if (mPostListModel != null) {
            Managers.unregisterTarget(this, mPostListModel);
        }

        Managers.registerTarget(this, postListFeatures);
    }

    private Target mMainFlowTarget = new Target() {

        @Override
        public void setModel(Model model) {
            mMainFlowModel = model;

            onMainFlowModelChanged();
        }

        @Override
        public void onFeaturesChanged(String... featureNames) {
            onMainFlowModelChanged();
        }

    };

}
