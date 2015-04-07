/*
 * FlightIntel for Pilots
 *
 * Copyright 2012 Nadeem Hasan <nhasan@nadmm.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */

package com.nadmm.airports.aeronav;

import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;

import com.nadmm.airports.ActivityBase;
import com.nadmm.airports.R;
import com.nadmm.airports.utils.TabsAdapter;

public class SubscribeActivity extends ActivityBase {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.fragment_pager_layout );

        ViewPager pager = (ViewPager) findViewById( R.id.content_pager );

        TabsAdapter adapter = new TabsAdapter( this, getSupportFragmentManager(), pager );
        adapter.addTab( "d-TPP", SubscribeDtppFragment.class, null );
        adapter.addTab( "d-AFD", SubscribeDafdFragment.class, null );

        PagerTabStrip tabs = (PagerTabStrip) findViewById( R.id.pager_tabs );
        tabs.setTabIndicatorColor( getResources().getColor( R.color.tab_indicator ) );

        if ( savedInstanceState != null ) {
            pager.setCurrentItem( savedInstanceState.getInt( "aeronavtab" ) );
        }
    }

    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        super.onSaveInstanceState( outState );
        ViewPager pager = (ViewPager) findViewById( R.id.content_pager );
        outState.putInt( "aeronavtab", pager.getCurrentItem() );
    }

}
