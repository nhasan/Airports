/*
 * FlightIntel for Pilots
 *
 * Copyright 2011-2015 Nadeem Hasan <nhasan@nadmm.com>
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

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nadmm.airports.ActivityBase;
import com.nadmm.airports.FragmentBase;
import com.nadmm.airports.R;
import com.nadmm.airports.clocks.StopWatchService.OnTickHandler;
import com.nadmm.airports.clocks.StopWatchService.StopWatchBinder;

import java.util.ArrayList;

public class StopWatchFragment extends FragmentBase implements OnTickHandler {

    private final int BLINK_DELAY = 500;

    private Button mBtnAction;
    private Button mBtnReset;
    private Button mBtnLeg;
    private TextView mTimeMinutes;
    private TextView mTimeColon;
    private TextView mTimeSeconds;
    private TextView mTimeTenths;
    private LinearLayout mLegsLayout;

    private StopWatchService mService = null;
    private StopWatchConnection mConnection = new StopWatchConnection();
    private Handler mHandler = new Handler();
    private Runnable mBlink = new Runnable() {

        @Override
        public void run() {
            blink();
        }
    };

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        ActivityBase activity = getActivityBase();
        Intent service = new Intent( activity, StopWatchService.class );
        activity.startService( service );
        setRetainInstance( true );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.clocks_stopwatch_view, container, false );
        return createContentView( view );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if ( mService != null ) {
            mService.stopSelf();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ActivityBase activity = getActivityBase();
        activity.setDrawerIndicatorEnabled( false );

        Intent service = new Intent( activity, StopWatchService.class );
        activity.bindService( service, mConnection, 0 );
    }

    @Override
    public void onPause() {
        super.onPause();

        getView().setKeepScreenOn( false );
        getActivity().unbindService( mConnection );
        mHandler.removeCallbacks( mBlink );
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState ) {
        super.onActivityCreated( savedInstanceState );

        mTimeMinutes = (TextView) findViewById( R.id.stopwatch_mins );
        mTimeColon = (TextView) findViewById( R.id.stopwatch_colon );
        mTimeSeconds = (TextView) findViewById( R.id.stopwatch_secs );
        mTimeTenths = (TextView) findViewById( R.id.stopwatch_tenths );
        mBtnAction = (Button) findViewById( R.id.stopwatch_action );
        mBtnAction.setOnClickListener( new OnClickListener() {

            @Override
            public void onClick( View v ) {
                actionPressed();
            }
        } );
        mBtnReset = (Button) findViewById( R.id.stopwatch_reset );
        mBtnReset.setOnClickListener( new OnClickListener() {

            @Override
            public void onClick( View v ) {
                resetPressed();
            }
        } );
        mBtnLeg = (Button) findViewById( R.id.stopwatch_leg );
        mBtnLeg.setOnClickListener( new OnClickListener() {

            @Override
            public void onClick( View v ) {
                legPressed();
            }
        } );
        mLegsLayout = (LinearLayout) findViewById( R.id.legs_view );

        setFragmentContentShown( true );
    }

    protected void actionPressed() {
        if ( !mService.isRunning() ) {
            mService.startTimimg();
            getView().setKeepScreenOn( true );
        } else {
            mService.stopTimimg();
        }
        updateUiState();
    }

    protected void resetPressed() {
        getView().setKeepScreenOn( false );
        mService.reset();
        updateUiState();
    }

    protected void legPressed() {
        mService.addLeg();
        showLegs();
    }

    @Override
    public void onTick( long millis ) {
        showElapsedTime();
    }

    protected void blink() {
        mHandler.postDelayed( mBlink, BLINK_DELAY );
        boolean visible = ( mTimeSeconds.getVisibility()==View.VISIBLE );
        mTimeMinutes.setVisibility( visible? View.INVISIBLE : View.VISIBLE );
        mTimeColon.setVisibility( visible? View.INVISIBLE : View.VISIBLE );
        mTimeSeconds.setVisibility( visible? View.INVISIBLE : View.VISIBLE );
        mTimeTenths.setVisibility( visible? View.INVISIBLE : View.VISIBLE );
    }

    protected void startBlink() {
        mHandler.postDelayed( mBlink, BLINK_DELAY );
    }

    protected void stopBlink() {
        mTimeMinutes.setVisibility( View.VISIBLE );
        mTimeColon.setVisibility( View.VISIBLE );
        mTimeSeconds.setVisibility( View.VISIBLE );
        mTimeTenths.setVisibility( View.VISIBLE );
        mHandler.removeCallbacks( mBlink );
    }

    protected void updateUiState() {
        if ( mService != null && mService.isRunning() ) {
            mBtnAction.setText( R.string.pause );
            mBtnReset.setVisibility( View.GONE );
            mBtnLeg.setVisibility( View.VISIBLE );
            stopBlink();
        } else {
            mBtnAction.setText( R.string.start );
            mBtnReset.setVisibility( mService.getElapsedTime() > 0? View.VISIBLE : View.GONE );
            mBtnLeg.setVisibility( View.GONE );
            if ( mService.getElapsedTime() > 0 ) {
                mBtnReset.setVisibility( View.VISIBLE );
                startBlink();
            } else {
                mBtnReset.setVisibility( View.GONE );
                stopBlink();
            }
        }
        showElapsedTime();
        showLegs();
    }

    protected void showElapsedTime() {
        String time = formatElapsedTime( mService.getElapsedTime() );
        mTimeMinutes.setText( time.substring( 0, 2 ) );
        mTimeSeconds.setText( time.substring( 3, 5 ) );
        mTimeTenths.setText( time.substring( 5 ) );
    }

    @SuppressLint("DefaultLocale")
    protected String formatElapsedTime( long millis ) {
        long hrs = millis / DateUtils.HOUR_IN_MILLIS;
        long mins = ( millis % DateUtils.HOUR_IN_MILLIS ) / DateUtils.MINUTE_IN_MILLIS;
        long secs = ( millis % DateUtils.MINUTE_IN_MILLIS ) / DateUtils.SECOND_IN_MILLIS;
        long tenths = ( millis % DateUtils.SECOND_IN_MILLIS )/( DateUtils.SECOND_IN_MILLIS/10 );
        if ( hrs > 0 ) {
            return String.format( "%02d:%02d:%02d.%01d", hrs, mins, secs, tenths );
        } else {
            return String.format( "%02d:%02d.%01d", mins, secs, tenths );
        }
    }

    protected void showLegs() {
        ArrayList<Long> legsList = mService.getLegs();
        int size = legsList.size();
        View parent = findViewById( R.id.legs_view_parent );

        if ( size > 0 ) {
            int count = mLegsLayout.getChildCount();
            while ( count < size ) {
                long leg = legsList.get( count );
                long prev = count==0? 0 : legsList.get( count-1 );
                addLeg( ++count, leg, prev );
            }
            parent.setVisibility( View.VISIBLE );
        } else {
            mLegsLayout.removeAllViews();
            parent.setVisibility( View.GONE );
        }
    }

    protected void addLeg( int count, long leg, long prev ) {
        long delta = leg-prev;
        View view = inflate( R.layout.leg_item_view );
        TextView tv = (TextView) view.findViewById( R.id.leg_label );
        tv.setText( String.format( "Leg %d", count ) );
        tv = (TextView) view.findViewById( R.id.leg_delta );
        tv.setText( formatElapsedTime( delta ) );
        tv = (TextView) view.findViewById( R.id.leg_total );
        tv.setText( formatElapsedTime( leg ) );
        mLegsLayout.addView( view, 0 );
    }

    private class StopWatchConnection implements ServiceConnection {

        @Override
        public void onServiceConnected( ComponentName name, IBinder service ) {
            StopWatchBinder binder = (StopWatchBinder) service;
            mService = binder.getService();
            mService.setOnTickHandler( StopWatchFragment.this );
            updateUiState();
        }

        @Override
        public void onServiceDisconnected( ComponentName name ) {
            mService.setOnTickHandler( null );
            mService = null;
        }

    }

}
