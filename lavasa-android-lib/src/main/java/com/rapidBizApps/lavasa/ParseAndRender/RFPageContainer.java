package com.rapidBizApps.lavasa.ParseAndRender;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rapidBizApps.lavasa.Controller.RFController;
import com.rapidBizApps.lavasa.Models.RFPageModel;
import com.rapidBizApps.lavasa.Models.RFSectionModel;
import com.rapidBizApps.lavasa.R;
import com.rapidBizApps.lavasa.Views.RFPage;
import com.rapidBizApps.lavasa.Views.RFSection;

import java.util.ArrayList;


/**
 * Created by cdara on 07-12-2015.
 */

/**
 * Use: This is used for rendering the elements/sections of page.
 */
public final class RFPageContainer extends RecyclerView.Adapter<RFPageContainer.ViewHolder> {

    private ArrayList<RFSectionModel> mSections;
    private RFController mController;
    private RFPage mPageView;

    public RFPageContainer(RFController controller, RFPageModel page, RFPage pageView) {
        mController = controller;
        mSections = page.getSections();
        mPageView = pageView;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RFSectionModel currentSection = mSections.get(position);

        if (holder.container != null) {

            mPageView.setSectionsContainer(holder.container);

            // To avoid duplicate views.
            holder.container.removeAllViews();

            holder.container.setTag(mSections.get(position).getId());
            new RFSection(mController, currentSection);
        }
    }


    @Override
    public int getItemCount() {
        return mSections.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_element_view, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }


    // here Every section is added to a container.
    protected class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout container;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            container = (LinearLayout) itemLayoutView.findViewById(R.id.formlayout_LL);
        }
    }
}
