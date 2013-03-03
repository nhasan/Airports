/*
 * FlightIntel for Pilots
 *
 * Copyright 2011-2013 Nadeem Hasan <nhasan@nadmm.com>
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

package com.nadmm.airports.e6b;

import java.util.HashMap;

import android.database.Cursor;

import com.nadmm.airports.ListMenuFragment;
import com.nadmm.airports.R;

public class E6bMenuFragment extends ListMenuFragment {

    private static final HashMap<Long, Class<?>> mDispatchMap;
    static {
        mDispatchMap = new HashMap<Long, Class<?>>();
        mDispatchMap.put( (long)R.id.E6B_UNIT_CONVERSIONS, UnitConvertFrament.class );
        mDispatchMap.put( (long)R.id.E6B_WIND_CALCS, E6bMenuFragment.class );
        mDispatchMap.put( (long)R.id.E6B_CROSS_WIND, CrossWindFragment.class );
        mDispatchMap.put( (long)R.id.E6B_WIND_TRIANGLE_WIND, WindTriangleFragment.class );
        mDispatchMap.put( (long)R.id.E6B_WIND_TRIANGLE_HDG_GS, WindTriangleFragment.class );
        mDispatchMap.put( (long)R.id.E6B_WIND_TRIANGLE_CRS_GS, WindTriangleFragment.class );
    }

    private static final HashMap<Long, String> mTitleMap;
    static {
        mTitleMap = new HashMap<Long, String>();
        mTitleMap.put( (long)R.id.CATEGORY_MAIN, "E6B Flight Computer" );
        mTitleMap.put( (long)R.id.E6B_UNIT_CONVERSIONS, "Unit Conversions" );
        mTitleMap.put( (long)R.id.E6B_WIND_CALCS, "Wind Calculations" );
        mTitleMap.put( (long)R.id.E6B_CROSS_WIND, "Crosswind and Headwind" );
        mTitleMap.put( (long)R.id.E6B_WIND_TRIANGLE_WIND, "Find Wind Speed and Direction" );
        mTitleMap.put( (long)R.id.E6B_WIND_TRIANGLE_HDG_GS, "Find Heading and Ground Speed" );
        mTitleMap.put( (long)R.id.E6B_WIND_TRIANGLE_CRS_GS, "Find Course and Ground Speed" );
    }

    @Override
    protected String getItemTitle( long id ) {
        return mTitleMap.get( id );
    }

    @Override
    protected Class<?> getItemFragmentClass( long id ) {
        return mDispatchMap.get( id );
    }

    @Override
    protected Cursor getMenuCursor( long id ) {
        return new E6bMenuCursor( id );
    }

    public class E6bMenuCursor extends ListMenuCursor {

        public E6bMenuCursor( long id ) {
            super( id );
        }

        @Override
        protected void populateMenuItems( long id ) {
            if ( id == R.id.CATEGORY_MAIN ) {
                addRow( R.id.E6B_WIND_CALCS, "Calculate x-wind, h-wind, heading and course" );
                addRow( R.id.E6B_UNIT_CONVERSIONS, "Convert between units of measurement" );
            } else if ( id == R.id.E6B_WIND_CALCS ) {
                addRow( R.id.E6B_CROSS_WIND, "Cross wind and head wind for a runway" );
                addRow( R.id.E6B_WIND_TRIANGLE_WIND, "WS and WDIR using Wind Triangle" );
                addRow( R.id.E6B_WIND_TRIANGLE_HDG_GS, "HDG and GS using Wind Triangle" );
                addRow( R.id.E6B_WIND_TRIANGLE_CRS_GS, "CRS and GS using Wind Triangle" );
            }
        }

        private void addRow( long id, String summary ) {
            newRow().add( id )
                .add( 0 )
                .add( getItemTitle( id ) )
                .add( summary );
        }
    }

}