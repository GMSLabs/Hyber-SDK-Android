package com.hyber.example.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hyber.example.AndroidUtilities;
import com.hyber.example.LocaleController;
import com.hyber.example.R;
import com.hyber.example.ui.Adapters.CountryAdapter;
import com.hyber.example.ui.Adapters.CountryAdapter.Country;
import com.hyber.example.ui.Adapters.CountrySearchAdapter;
import com.hyber.example.ui.Components.LayoutHelper;
import com.hyber.example.ui.Components.LetterSectionsListView;

public class CountrySelectActivity extends Fragment {

    public interface CountrySelectActivityDelegate {
        void didSelectCountry(String name);
    }

    protected View fragmentView;
    private LetterSectionsListView listView;
    private TextView emptyTextView;
    private CountryAdapter listViewAdapter;
    private CountrySearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private CountrySelectActivityDelegate delegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        searching = false;
        searchWas = false;

        listViewAdapter = new CountryAdapter(getActivity());
        searchListViewAdapter = new CountrySearchAdapter(getActivity(), listViewAdapter.getCountries());

        fragmentView = new FrameLayout(getActivity());

        LinearLayout emptyTextLayout = new LinearLayout(getActivity());
        emptyTextLayout.setVisibility(View.INVISIBLE);
        emptyTextLayout.setOrientation(LinearLayout.VERTICAL);
        ((FrameLayout) fragmentView).addView(emptyTextLayout);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) emptyTextLayout.getLayoutParams();
        layoutParams.width = LayoutHelper.MATCH_PARENT;
        layoutParams.height = LayoutHelper.MATCH_PARENT;
        layoutParams.gravity = Gravity.TOP;
        emptyTextLayout.setLayoutParams(layoutParams);
        emptyTextLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        emptyTextView = new TextView(getActivity());
        emptyTextView.setTextColor(0xff808080);
        emptyTextView.setTextSize(20);
        emptyTextView.setGravity(Gravity.CENTER);
        emptyTextView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        emptyTextLayout.addView(emptyTextView);
        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) emptyTextView.getLayoutParams();
        layoutParams1.width = LayoutHelper.MATCH_PARENT;
        layoutParams1.height = LayoutHelper.MATCH_PARENT;
        layoutParams1.weight = 0.5f;
        emptyTextView.setLayoutParams(layoutParams1);

        FrameLayout frameLayout = new FrameLayout(getActivity());
        emptyTextLayout.addView(frameLayout);
        layoutParams1 = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams1.width = LayoutHelper.MATCH_PARENT;
        layoutParams1.height = LayoutHelper.MATCH_PARENT;
        layoutParams1.weight = 0.5f;
        frameLayout.setLayoutParams(layoutParams1);

        listView = new LetterSectionsListView(getActivity());
        listView.setEmptyView(emptyTextLayout);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setFastScrollEnabled(true);
        listView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        listView.setAdapter(listViewAdapter);
        listView.setFastScrollAlwaysVisible(true);
        listView.setVerticalScrollbarPosition(ListView.SCROLLBAR_POSITION_RIGHT);
        ((FrameLayout) fragmentView).addView(listView);
        layoutParams = (FrameLayout.LayoutParams) listView.getLayoutParams();
        layoutParams.width = LayoutHelper.MATCH_PARENT;
        layoutParams.height = LayoutHelper.MATCH_PARENT;
        listView.setLayoutParams(layoutParams);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Country country;
                if (searching && searchWas) {
                    country = searchListViewAdapter.getItem(i);
                } else {
                    int section = listViewAdapter.getSectionForPosition(i);
                    int row = listViewAdapter.getPositionInSectionForPosition(i);
                    if (row < 0 || section < 0) {
                        return;
                    }
                    country = listViewAdapter.getItem(section, row);
                }
                if (i < 0) {
                    return;
                }
                if (country != null && delegate != null) {
                    delegate.didSelectCountry(country.name);
                }
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == SCROLL_STATE_TOUCH_SCROLL && searching && searchWas) {
                    AndroidUtilities.hideKeyboard(getActivity().getCurrentFocus());
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (absListView.isFastScrollEnabled()) {
                    AndroidUtilities.clearDrawableAnimation(absListView);
                }
            }
        });

        return fragmentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listViewAdapter != null) {
            listViewAdapter.notifyDataSetChanged();
        }
    }

    public void setCountrySelectActivityDelegate(CountrySelectActivityDelegate delegate) {
        this.delegate = delegate;
    }

}
