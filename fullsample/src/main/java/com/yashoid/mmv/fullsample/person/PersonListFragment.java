package com.yashoid.mmv.fullsample.person;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.PersistentTarget;
import com.yashoid.mmv.fullsample.Basics;
import com.yashoid.mmv.fullsample.MainFlow;
import com.yashoid.mmv.fullsample.R;
import com.yashoid.mmv.Target;

import java.util.List;

public class PersonListFragment extends Fragment implements PersonList, Target {

    private Model mModel;
    private Model mMainFlowModel;

    private ViewGroup mHolderPersonList;
    private View mLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHolderPersonList = view.findViewById(R.id.holder_personlist);
        mLoading = view.findViewById(R.id.loading);

        ModelFeatures features = new ModelFeatures.Builder()
                .add(Basics.TYPE, TYPE)
                .build();

        final ModelFeatures mainFlowFeatures = new ModelFeatures.Builder()
                .add(Basics.TYPE, MainFlow.TYPE)
                .build();

        Managers.registerTarget(this, features);

        Managers.registerTarget(new PersistentTarget() {

            @Override
            public void setModel(Model model) {
                mMainFlowModel = model;

                Managers.unregisterTarget(this);
            }

            @Override
            public void onFeaturesChanged(String... featureNames) { }

        }, mainFlowFeatures);
    }

    @Override
    public void setModel(Model model) {
        mModel = model;

        onModelChanged();

        Integer state = mModel.get(STATUS);

        if (state == null || state == STATUS_IDLE) {
            mModel.perform(GET_LIST);
        }
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        onModelChanged();
    }

    private void onModelChanged() {
        Integer state = mModel.get(STATUS);

        if (state == null) {
            return;
        }

        switch (state) {
            case STATUS_LOADING:
                mHolderPersonList.setVisibility(View.GONE);
                mLoading.setVisibility(View.VISIBLE);
                break;
            default:
                mHolderPersonList.setVisibility(View.VISIBLE);
                mLoading.setVisibility(View.INVISIBLE);

                List<ModelFeatures> personList = mModel.get(PERSON_LIST);

                mHolderPersonList.removeAllViews();

                final Context context = getContext();

                if (personList != null) {
                    for (final ModelFeatures person: personList) {
                        PersonView personView = new PersonView(context);

                        person.set(Basics.TYPE, Person.TYPE);

                        Managers.registerTarget(personView, person);

                        mHolderPersonList.addView(personView);

                        personView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMainFlowModel.set(MainFlow.PERSON_ID, person.get(Person.ID));
                            }
                        });
                    }
                }

                break;
        }
    }

}
