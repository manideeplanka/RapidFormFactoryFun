package com.rapidBizApps.lavasa.Views;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rapidBizApps.lavasa.Controller.RFController;
import com.rapidBizApps.lavasa.Exceptions.RFInvalidKeyName;
import com.rapidBizApps.lavasa.Listeners.RFFormListener;
import com.rapidBizApps.lavasa.Models.RFFormModel;
import com.rapidBizApps.lavasa.Models.RFPageModel;
import com.rapidBizApps.lavasa.Models.RFPagesStates;
import com.rapidBizApps.lavasa.Models.RFSectionModel;
import com.rapidBizApps.lavasa.ParseAndRender.RFContainer;
import com.rapidBizApps.lavasa.R;
import com.rapidBizApps.lavasa.Validations.RFValidator;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

/**
 * its bind up the RFViewPager class.
 * <p/>
 * Created by cdara on 14-03-2016.
 */
public class RFForm extends RFBaseElement {

    // This key is used for navigation to a particular field in a form.
    public static String sPendingNavigationKey;

    // Below variable is static because the value in that can be destroyed when there an orientation change and config changes are not mentioned
    private static int mCurrentOrientation;

    public View mFormContainer;
    private RFViewPager mFormViewPager;
    private RFValidator mValidator;
    private int mCurrentPage, mPreviousPage;
    private RFFormListener mFormListener;
    private RFFormModel mForm;
    private RFContainer mContainer;
    private Context mContext;
    private OrientationEventListener mOrientationListener;

    private CircleIndicator mCircleIndicator;

    public RFForm(RFController controller, RFFormModel formModel, View formContainer, RFContainer container) {
        super(controller.getContext(), formModel);
        mForm = formModel;
        mFormContainer = formContainer;
        mContainer = container;
        mFormListener = controller.getFormListener();
        mContext = controller.getContext();
        mValidator = new RFValidator(mContext);
        mCurrentOrientation = mContext.getResources().getConfiguration().orientation;
        //todo : need to fix this issue anirudh , its fix for sunedata app // app is getting crashing by enabling below code in nexus 9
        /*mOrientationListener = new OrientationEventListener(mContext) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (mCurrentOrientation == mContext.getResources().getConfiguration().orientation) {
                    return;
                }

                if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mCurrentOrientation = Configuration.ORIENTATION_LANDSCAPE;
                    init();
                    return;
                }
                if (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    mCurrentOrientation = Configuration.ORIENTATION_PORTRAIT;
                    init();
                    return;
                }
            }
        };

        mOrientationListener.enable();*/
        init();
    }

    // TODO:need to change the creation of RFViewPager from xml to dynamic.
    public void init() {
        int currentPagePosition = -1;
        LinearLayout mLayout = null;
        if (mFormViewPager == null) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, 0, 8);
            mFormViewPager = new RFViewPager(mContext, mForm);
            mFormViewPager.setLayoutParams(params);

            LinearLayout.LayoutParams circularparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);
            circularparams.gravity = Gravity.CENTER;
            mCircleIndicator = new CircleIndicator(mContext);
            mCircleIndicator.setLayoutParams(circularparams);
            mCircleIndicator.configureIndicator(20, 20, 7, R.animator.scale, 0, R.drawable.circle, R.drawable.circle);
            //mCircleIndicator.setBackgroundColor(R.color.grey);
            mCircleIndicator.setGravity(Gravity.CENTER);
            mCircleIndicator.setPadding(0, 5, 0, 5);

            mLayout = new LinearLayout(mContext);
            mLayout.setWeightSum(10);
            mLayout.setOrientation(LinearLayout.VERTICAL);
            mLayout.addView(mFormViewPager);
            mLayout.addView(mCircleIndicator);

        } else {
            currentPagePosition = mFormViewPager.getCurrentItem();
            mFormViewPager.removeAllViews();
        }

        mForm.setCurrentPageState(getCurrentPageState());
        mFormViewPager.setAdapter(mContainer);

        if (mContainer.getCount() > 1) {
            mCircleIndicator.setViewPager(mFormViewPager);
        }else{
            mCircleIndicator.setVisibility(View.GONE);
        }

        // This is callback to the user after loading the first page of the form for the first time.
        if (mFormListener != null) {
            mFormListener.onLoad();
        }

        mFormViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mForm.setCurrentPageState(getCurrentPageState());
                if (mFormListener != null) {
                    mFormListener.onPageChange();
                }
                ;
                mPreviousPage = mCurrentPage;
                mCurrentPage = position;

                RFPage previousPageView = ((RFContainer) mFormViewPager.getAdapter()).getPageView(mPreviousPage);

                mValidator.applyValidations(previousPageView, null);

                if (!mForm.getPages().get(mCurrentPage).getStyle().isVisibility()) {
                    managePagesBasedOnVisibility();
                }

                // for removing screenOffsetPageLimit
                mFormViewPager.getAdapter().notifyDataSetChanged();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setFormContainer(mFormContainer);
        setUp(mLayout);
        if (currentPagePosition >= 0) {
            mFormViewPager.setCurrentItem(currentPagePosition);
        }

        if ((currentPagePosition < 0 && !mForm.getPages().get(0).getStyle().isVisibility())
                || !mForm.getPages().get(0).getStyle().isVisibility()) {
            managePagesBasedOnVisibility();
        }
    }

    /**
     * This method will refresh the current page of the form.
     */
    public void refreshCurrentPage() {
        int currentPageNumber = mFormViewPager.getCurrentItem();
        String currentPageTag = mForm.getPages().get(currentPageNumber).getId();

        if (mFormViewPager.findViewWithTag(currentPageTag) == null) {
            return;
        }

        LinearLayout linearLayout = (LinearLayout) mFormViewPager.findViewWithTag(currentPageTag);
        final RecyclerView recyclerView = (RecyclerView) linearLayout.findViewById(R.id.page_view);

        /*
         *  java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling
         This probably occurs because you are calling notifyItemInserted(position);, notifyItemChanged(position), or notifyItemRemoved(position); from a background thread (or from a callback, which runs on a background thread).
         To solve this, use a handler:
         */
        android.os.Handler handler = ((Activity) mContext).getWindow().getDecorView().getHandler();
        handler.post(new Runnable() {
            public void run() {
                //change adapter contents
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    @Override
    public String getData() {
        return null;
    }

    @Override
    protected void setData(String data) {

    }

    @Override
    public void setError(String error) {

    }

    /**
     * Returns the current page state.
     *
     * @return the page constant values
     */
    public RFPagesStates getCurrentPageState() {
        int currentPage = mFormViewPager.getCurrentItem();

        if (currentPage + 1 == mForm.getPages().size() && currentPage == 0) {
            return RFPagesStates.SINGLE;
        } else if (currentPage + 1 == mForm.getPages().size()) {
            return RFPagesStates.END;
        } else if (currentPage == 0) {
            return RFPagesStates.BEGIN;
        } else {
            return RFPagesStates.MIDDLE;
        }
    }


    /**
     * Used for navigation to particular field/section/page
     *
     * @param keyName keyName can be pageKeyName/sectionKeyName/fieldKeyName
     * @return
     */
    public void navigateTo(final String keyName) {
        if (mForm.getId().equals(keyName)) {
            return;
        }

        // If the key name is page key name. The below if condition will navigate the form to that page.
        int pagePosition = -1;
        if (mForm.getPagePositionDetails().containsKey(keyName)) {
            pagePosition = mForm.getPagePositionDetails().get(keyName);
        }
        if (pagePosition >= 0) {
            mFormViewPager.setCurrentItem(pagePosition);
            return;
        }

        ArrayList<RFPageModel> pages = mForm.getPages();

        for (int pageNumber = 0; pageNumber < pages.size(); pageNumber++) {

            RFPageModel currentPage = pages.get(pageNumber);
            int sectionPosition = -1;
            if (currentPage.getSectionPositionDetails().containsKey(keyName)) {
                sectionPosition = currentPage.getSectionPositionDetails().get(keyName);
            }

            // If the key name is related to section. The below if will navigate to that particular section
            if (sectionPosition >= 0) {

                mFormViewPager.setCurrentItem(pageNumber);

                LinearLayout linearLayout = (LinearLayout) mFormViewPager.findViewWithTag(currentPage.getId());
                final RecyclerView recyclerView = (RecyclerView) linearLayout.findViewById(R.id.page_view);

                final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                gridLayoutManager.scrollToPosition(sectionPosition);


                return;
            }

            ArrayList<RFSectionModel> sections = pages.get(pageNumber).getSections();

            for (int sectionNumber = 0; sectionNumber < sections.size(); sectionNumber++) {

                final RFSectionModel currentSection = sections.get(sectionNumber);
                int fieldPosition = -1;

                if (currentSection.getFieldPositionDetails().containsKey(keyName)) {
                    fieldPosition = currentSection.getFieldPositionDetails().get(keyName);
                }

                // If the key name is related to field. The below if will navigate to that particular section
                if (fieldPosition >= 0) {

                    mFormViewPager.setCurrentItem(pageNumber);

                    final LinearLayout linearLayout = (LinearLayout) mFormViewPager.findViewWithTag(currentPage.getId());
                    final RecyclerView recyclerView = (RecyclerView) linearLayout.findViewById(R.id.page_view);

                    final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    gridLayoutManager.scrollToPosition(sectionNumber);

                    sPendingNavigationKey = keyName;
                    /*Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                    try {
                        Thread.sleep(7000);
                    } catch (Exception e) {

                    }
                    Log.e("tags", currentSection.getId() + ";;;;" + recyclerView.getChildCount() + linearLayout.getTag());
                    for (int test = 0; test < recyclerView.getChildCount(); test++) {
                        Log.e("child tags", recyclerView.getChildAt(test).getTag() + "???");
                    }

                    LinearLayout containerLayout = (LinearLayout) recyclerView.findViewWithTag(currentSection.getId());
                    LinearLayout sectionLayout = (LinearLayout) containerLayout.findViewWithTag(currentSection.getId());

                    View fieldView = sectionLayout.findViewWithTag(keyName);
                    if (fieldView != null) {
                        fieldView.requestFocus();
                    }
                    thread.run();
                    return;*/
                    return;
                }

            }

        }


        throw new RFInvalidKeyName();
    }

    /**
     * This will navigates the form to next page from current page.
     *
     * @return page state  - returns the page state of the form.
     */
    public RFPagesStates moveToNextPage() {
        int currentPage = mFormViewPager.getCurrentItem();

        // this will check whether the current page is last page.
        if (currentPage + 1 >= mForm.getPages().size()) {
            return RFPagesStates.END;
        }

        mFormViewPager.setCurrentItem(currentPage + 1);
        return RFPagesStates.MIDDLE;
    }

    /**
     * This will navigates the form to prev page from current page
     *
     * @return page state  - returns the page state of the form.
     */
    public RFPagesStates moveToPreviousPage() {
        int currentPage = mFormViewPager.getCurrentItem();

        // this will check whether the current page is first page.
        if (currentPage - 1 < 0) {
            return RFPagesStates.BEGIN;
        }

        mFormViewPager.setCurrentItem(currentPage - 1);
        return RFPagesStates.MIDDLE;
    }

    /**
     * If the current page is invisible then it will search for the next visible page based on the order of swiping.
     */
    private void managePagesBasedOnVisibility() {
        int noOfPages = mForm.getPages().size();
        int pagePosition;

        if (mPreviousPage < mCurrentPage || mPreviousPage == mCurrentPage) {
            pagePosition = mCurrentPage + 1;
            while (pagePosition < noOfPages) {
                RFPageModel page = mForm.getPages().get(pagePosition);
                if (page.getStyle().getVisibility()) {
                    mFormViewPager.setCurrentItem(pagePosition);
                    break;
                }
                pagePosition++;
            }

            if (pagePosition == noOfPages && mForm.getPages().get(mPreviousPage).getStyle().getVisibility()) {
                mFormViewPager.setCurrentItem(mPreviousPage);
            }

        } else if (mPreviousPage > mCurrentPage) {
            pagePosition = mCurrentPage - 1;
            while (pagePosition >= 0) {
                RFPageModel page = mForm.getPages().get(pagePosition);
                if (page.getStyle().getVisibility()) {
                    mFormViewPager.setCurrentItem(pagePosition);
                    break;
                }
                pagePosition--;
            }

            if (pagePosition < 0 && mForm.getPages().get(mPreviousPage).getStyle().getVisibility()) {
                mFormViewPager.setCurrentItem(mPreviousPage);
            }
        }
    }


    public void applyValidations() {

        for(int i= 0;i<mFormViewPager.getAdapter().getCount();i++){
            RFPage previousPageView = ((RFContainer) mFormViewPager.getAdapter()).getPageView(i);
            Log.e("kar", "applyValidations previousPageView: "+previousPageView );
            mValidator.applyValidations(previousPageView, null);
        }

        /*RFPage currentPageView = ((RFContainer) mFormViewPager.getAdapter()).getPageView(mPreviousPage + 1);
        mValidator.applyValidations(currentPageView, null);

        if (getCurrentPageState() == RFPagesStates.END) {

        }*/


    }


}
