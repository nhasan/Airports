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

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

public class CountDownService extends Service {

    private final long TICK_MILLIS = 100;

    private final int STATE_PAUSED = 1;
    private final int STATE_RUNNING = 2;
    private final int STATE_FINISHED = 3;
    private final int STATE_RESET = 4;

    private IBinder mBinder = new CountDownBinder();
    private OnTickHandler mClient = null;
    private CountDownTimer mTimer = null;
    private int mState = STATE_RESET;

    public interface OnTickHandler {
        void onTick( long millis );
    }

    public class CountDownBinder extends Binder {
        public CountDownService getService() {
            return CountDownService.this;
        }
    }

    @Override
    public IBinder onBind( Intent intent ) {
        return mBinder;
    }

    public void setOnTickHandler( OnTickHandler client ) {
        mClient = client;
    }

    public void startCountDown( long millis ) {
        mTimer = new CountDownTimer( millis, TICK_MILLIS ) {

            @Override
            public void onTick( long millisRemain ) {
                notifyClient( millisRemain );
            }

            @Override
            public void onFinish() {
                mState = STATE_FINISHED;
                notifyClient( 0 );
            }
        };
        mTimer.start();
        mState = STATE_RUNNING;
    }

    public void stopCountDown() {
        if ( mTimer != null ) {
            mTimer.cancel();
            mTimer = null;
        }
        mState = STATE_PAUSED;
    }

    public void resetCountDown() {
        mState = STATE_RESET;
    }

    protected void notifyClient( long millisRemain ) {
        if ( mClient != null ) {
            mClient.onTick( millisRemain );
        }
    }

    public boolean isPaused() {
        return mState == STATE_PAUSED;
    }

    public boolean isRunning() {
        return mState == STATE_RUNNING;
    }

    public boolean isFinished() {
        return mState == STATE_FINISHED;
    }

    public boolean isReset() {
        return mState == STATE_RESET;
    }

}
