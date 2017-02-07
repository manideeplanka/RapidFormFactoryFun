package com.rapidBizApps.lavasa.Views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.rapidBizApps.lavasa.Constants.RFConstants;
import com.rapidBizApps.lavasa.Controller.RFController;
import com.rapidBizApps.lavasa.Models.RFPageModel;
import com.rapidBizApps.lavasa.ParseAndRender.RFPageContainer;
import com.rapidBizApps.lavasa.R;

/**
 * Created by cdara on 03-02-2016.
 */
public class RFPage extends RFBaseElement {

    private static final int COL_SPAN = 1;
    private LinearLayout mPageView;
    private RFPageModel mPageModel;
    private Context mContext;
    private LayoutInflater mInflater;
    private RFController mController;

    public RFPage(RFController controller, RFPageModel pageModel) {
        super(controller.getContext(), pageModel);
        mController = controller;
        mContext = controller.getContext();
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setPageModel(pageModel);
        init();
    }

    // All the initializations for the page will be present in the init() method
    // setUp() will be called in the init method
    private void init() {
        mPageView = (LinearLayout) mInflater.inflate(R.layout.form_element_view, null);

        setSectionsContainer(mPageView);
        //For page title
        new RFTextView(mContext, mPageModel.getPageTitleField(), RFConstants.FC_PAGE_TITLE);

        GridLayoutManager layoutManager;

        layoutManager = new GridLayoutManager(mContext, COL_SPAN);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int itemPosition) {
                return 1;
            }
        });

        RecyclerView recyclerView = ((RecyclerView) mPageView.findViewById(R.id.page_view));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.removeAllViews();


        RFPageContainer adapter;

        adapter = new RFPageContainer(mController, mPageModel, this);

        recyclerView.setAdapter(adapter);

        setPageView(mPageView);
        setUp(mPageView);
    }

    @Override
    public String getData() {
        return null;
    }

    @Override
    protected void setData(String js) {

    }

    @Override
    public void setError(String error) {

    }

    private void setPageView(LinearLayout pageView) {
        mPageView = pageView;

    }

    private void setPageModel(RFPageModel pageModel) {
        mPageModel = pageModel;
    }

}
