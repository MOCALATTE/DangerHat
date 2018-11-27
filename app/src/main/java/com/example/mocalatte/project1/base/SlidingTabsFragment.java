package com.example.mocalatte.project1.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mocalatte.project1.R;
import com.example.mocalatte.project1.adapter.SlidingMenuAdapter;
import com.example.mocalatte.project1.item.ItemSlideMenu;
import com.example.mocalatte.project1.vm.ContactViewModel;
import com.example.mocalatte.project1.vm.HomeViewModel;
import com.example.mocalatte.project1.vm.MapViewModel;
import com.example.mocalatte.project1.vm.MoreViewModel;
import com.example.mocalatte.project1.vm.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SlidingTabsFragment extends Fragment {
    public ViewModel[] m_viewModels = new ViewModel[4];
    private SlidingTabLayout mSlidingTabLayout;
    private List<ItemSlideMenu> listSliding;
    private SlidingMenuAdapter adapter;
    private ListView listViewSliding;
    private DrawerLayout drawerLayout;
    private boolean mClickMenu = false;
    private int mPos;
    private ViewPager mViewPager;
    public SlidingTabsFragment() {
        for (int i=0; i<m_viewModels.length; ++i) {
            m_viewModels[i] = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.viewpager_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mViewPager.setAdapter(new CustomPagerAdapter());
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        listViewSliding = (ListView) view.findViewById(R.id.lv_sliding_menu);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
    }

    private void drawer() {
        listSliding = new ArrayList<>();
        listSliding.add(new ItemSlideMenu(R.drawable.home_icon, "Home"));
        listSliding.add(new ItemSlideMenu(R.drawable.contact_icon, "Contact"));
        listSliding.add(new ItemSlideMenu(R.drawable.map_icon, "Map"));
        listSliding.add(new ItemSlideMenu(R.drawable.more_icon, "More"));
        adapter = new SlidingMenuAdapter(listSliding);
        listViewSliding.setAdapter(adapter);

        listViewSliding.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //

            }
        });
    }

    class CustomPagerAdapter extends PagerAdapter {

        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 4;
        }

        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        // BEGIN_INCLUDE (pageradapter_getpagetitle)
        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
/*
        @Override
        public CharSequence getPageTitle(int position) {
                return "Step " + (position + 1);
        }
*/
        // END_INCLUDE (pageradapter_getpagetitle)

        /**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            switch (position) {
                case 0:
                    return instantiateItemByPosition(container, position, R.layout.home_layout);
                case 1:
                    return instantiateItemByPosition(container, position, R.layout.contact_layout);
                case 2:
                    return instantiateItemByPosition(container, position, R.layout.map_layout);
                case 3:
                    return instantiateItemByPosition(container, position, R.layout.more_layout);
                default:
                    return instantiateItemByPosition(container, position, R.layout.home_layout);
            } // end switch
        }

        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        private Object instantiateItemByPosition(ViewGroup container, int position, int rid) {
            try {
                View view = getActivity().getLayoutInflater().inflate(rid, container, false);

                // Add the newly created View to the ViewPager
                container.addView(view);

                switch (position) {
                    case 0: bindingViewModel(view, position, new HomeViewModel(view)); break;
                    case 1: bindingViewModel(view, position, new ContactViewModel(view)); break;
                    case 2: bindingViewModel(view, position, new MapViewModel(view)); break;
                    case 3: bindingViewModel(view, position, new MoreViewModel(view)); break;
                    default:bindingViewModel(view, position, new HomeViewModel(view)); break;
                }

                // Return the View
                return view;
            }
            catch (Exception e) {
                return  null;
            }
        } // end method

        /**
         * 초기화 작업
         * @param view
         * @param position
         * @param newVM
         * @param <T>
         */
        private <T extends ViewModel> void bindingViewModel(View view, int position, T newVM) {
            // 1. binding
            newVM.binding();

            // 2. copy old data
            ViewModel oldVM = m_viewModels[position];
            if (oldVM != null) {
                newVM.initialization(oldVM);
            }

            // 3. save viewmodel
            m_viewModels[position] = newVM;
        }

    } // end class
}