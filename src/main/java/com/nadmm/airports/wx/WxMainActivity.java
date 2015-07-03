/*
 * FlightIntel for Pilots
 *
 * Copyright 2012-2015 Nadeem Hasan <nhasan@nadmm.com>
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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.nadmm.airports.FragmentBase;
import com.nadmm.airports.ListFragmentBase;
import com.nadmm.airports.PreferencesActivity;
import com.nadmm.airports.TabPagerActivityBase;

import java.util.ArrayList;

public final class WxMainActivity extends TabPagerActivityBase {

    private final String[] mTabTitles = new String[] {
            "Favorites",
            "Nearby",
    };

    private final Class<?>[] mClasses = new Class<?>[] {
            FavoriteWxFragment.class,
            NearbyWxFragment.class
    };

    private final int ID_FAVORITES = 0;
    private final int ID_NEARBY = 1;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        setActionBarTitle( "Weather", null );

        Bundle args = new Bundle();
        for ( int i=0; i<mTabTitles.length; ++i ) {
            addTab( mTabTitles[ i ], mClasses[ i ], args );
        }
    }

    @Override
    protected int getInitialTabIndex() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );
        boolean alwaysShowNearby = prefs.getBoolean(
                PreferencesActivity.KEY_ALWAYS_SHOW_NEARBY, false );
        ArrayList<String> fav = getDbManager().getWxFavorites();
        if ( alwaysShowNearby || fav.isEmpty() ) {
            return ID_NEARBY;
        } else {
            return ID_FAVORITES;
        }
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_WX;
    }

}
