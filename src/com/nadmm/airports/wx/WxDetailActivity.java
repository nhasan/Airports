/*
 * FlightIntel for Pilots
 *
 * Copyright 2011-2012 Nadeem Hasan <nhasan@nadmm.com>
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

package com.nadmm.airports.wx;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;

import com.nadmm.airports.ActivityBase;
import com.nadmm.airports.R;
import com.nadmm.airports.utils.TabsAdapter;

public class WxDetailActivity extends ActivityBase {

    private TabsAdapter mTabsAdapter;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        setContentView( R.layout.fragment_pager_layout );

        ViewPager pager = (ViewPager) findViewById( R.id.content_pager );
        mTabsAdapter = new TabsAdapter( this, pager );
        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        mTabsAdapter.addTab( "METAR", MetarFragment.class, args );
        mTabsAdapter.addTab( "TAF", TafFragment.class, args );
        mTabsAdapter.addTab( "PIREPs", PirepFragment.class, args );
        mTabsAdapter.addTab( "AIRMET/SIGMET", AirSigmetFragment.class, args );
        mTabsAdapter.addTab( "RADAR", RadarFragment.class, args );
        mTabsAdapter.addTab( "PROGNOSIS CHARTS", ProgChartFragment.class, args );
        mTabsAdapter.addTab( "WINDS ALOFT", WindFragment.class, args );
        mTabsAdapter.addTab( "SIG WX", SigWxFragment.class, args );
        mTabsAdapter.addTab( "CEILING & VISIBILIY", CvaFragment.class, args );

        PagerTabStrip tabs = (PagerTabStrip) findViewById( R.id.pager_tabs );
        tabs.setTabIndicatorColor( 0x33b5e5 );

        if ( savedInstanceState != null ) {
            pager.setCurrentItem( savedInstanceState.getInt( "wxtab" ) );
        }

        super.onCreate( savedInstanceState );
    }

    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        super.onSaveInstanceState( outState );
        ViewPager pager = (ViewPager) findViewById( R.id.content_pager );
        outState.putInt( "wxtab", pager.getCurrentItem() );
    }

}