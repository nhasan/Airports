/*
 * FlightIntel for Pilots
 *
 * Copyright 2011-2018 Nadeem Hasan <nhasan@nadmm.com>
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

package com.nadmm.airports.afd;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.nadmm.airports.LocationListFragmentBase;
import com.nadmm.airports.data.DatabaseManager;
import com.nadmm.airports.utils.CursorAsyncTask;

import java.util.Locale;

import androidx.cursoradapter.widget.CursorAdapter;

import static com.nadmm.airports.data.DatabaseManager.Airports;

public class NearbyAirportsFragment extends LocationListFragmentBase {

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState ) {
        super.onActivityCreated( savedInstanceState );

        setEmptyText( "No airports found nearby." );

        if ( !isLocationUpdateEnabled() ) {
            setActionBarTitle( "Nearby Airports" );
            setActionBarSubtitle( String.format( Locale.US, "Within %d NM radius", getNearbyRadius() ) );
        }
    }

    @Override
    protected void startLocationTask() {
        setBackgroundTask( new NearbyAirportsTask( this ) ).execute();
    }

    @Override
    protected CursorAdapter newListAdapter( Context context, Cursor c ) {
        return new AirportsCursorAdapter( context, c );
    }

    @Override
    protected void onListItemClick( ListView l, View v, int position ) {
        Cursor c = (Cursor) l.getItemAtPosition( position );
        String siteNumber = c.getString( c.getColumnIndex( DatabaseManager.Airports.SITE_NUMBER ) );
        Intent intent = new Intent( getActivity(), AirportActivity.class );
        intent.putExtra( DatabaseManager.Airports.SITE_NUMBER, siteNumber );
        startActivity( intent );
    }

    private Cursor[] doQuery() {
        SQLiteDatabase db = getDatabase( DatabaseManager.DB_FADDS );

        String extraSelection= null;
        Bundle args = getArguments();
        if ( args != null ) {
            String faaCode = args.getString( Airports.FAA_CODE );
            if ( faaCode != null && !faaCode.isEmpty() ) {
                extraSelection = "AND "+Airports.FAA_CODE+" <> '"+faaCode+"'";
            }
        }

        Cursor c = new NearbyAirportsCursor( db, getLastLocation(), getNearbyRadius(), extraSelection );
        return new Cursor[] { c };
    }

    private static class NearbyAirportsTask extends CursorAsyncTask<NearbyAirportsFragment> {

        private NearbyAirportsTask( NearbyAirportsFragment fragment ) {
            super( fragment );
        }

        @Override
        protected Cursor[] onExecute( NearbyAirportsFragment fragment, String... params ) {
            return fragment.doQuery();
        }

        @Override
        protected boolean onResult( NearbyAirportsFragment fragment, Cursor[] result ) {
            fragment.setCursor( result[ 0 ] );
            return false;
        }

    }

}
