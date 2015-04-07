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

package com.nadmm.airports.clocks;

import java.util.HashMap;

import android.database.Cursor;

import com.nadmm.airports.DrawerActivityBase;
import com.nadmm.airports.ListMenuFragment;
import com.nadmm.airports.R;

public class ClocksMenuFragment extends ListMenuFragment {

    private static final HashMap<Long, Class<?>> mDispatchMap;
    static {
        mDispatchMap = new HashMap<Long, Class<?>>();
        mDispatchMap.put( (long)R.id.TIME_CLOCKS, ClockFragment.class );
        mDispatchMap.put( (long)R.id.TIME_STOPWATCH, StopWatchFragment.class );
        mDispatchMap.put( (long)R.id.TIME_COUNTDOWN, CountDownFragment.class );
    }

    @Override
    public void onResume() {
        super.onResume();

        DrawerActivityBase activity = (DrawerActivityBase) getActivity();
        activity.setDrawerIndicatorEnabled( true );
    }

    @Override
    protected Class<?> getItemFragmentClass( long itemId ) {
        return mDispatchMap.get( itemId );
    }

    @Override
    protected Cursor getMenuCursor( long id ) {
        return new ClocksMenuCursor();
    }

    public class ClocksMenuCursor extends ListMenuCursor {

        public ClocksMenuCursor() {
            super( 0 );
        }

        @Override
        protected void populateMenuItems( long id ) {
            newRow().add( R.id.TIME_CLOCKS )
                .add( R.drawable.clock )
                .add( "Current Time" )
                .add( "Display UTC clock, local clock" );
            newRow().add( R.id.TIME_STOPWATCH )
                .add( R.drawable.stopwatch )
                .add( "Stop Watch" )
                .add( "Stop watch for timing legs and approaches" );
            newRow().add( R.id.TIME_COUNTDOWN )
                .add( R.drawable.countdown )
                .add( "Countdown Timer" )
                .add( "Countdown timer for timing approaches and holds" );
        }
    }

}
