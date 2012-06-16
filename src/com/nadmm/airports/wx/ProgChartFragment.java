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

package com.nadmm.airports.wx;

import android.content.Intent;


public class ProgChartFragment extends WxMapFragmentBase {

    private static final String[] sProgChartCodes = new String[] {
        "00hr",
        "12hr",
        "24hr",
        "36hr",
        "48hr"
    };

    private static final String[] sProgChartNames = new String[] {
        "Current Analysis",
        "12 hr Forecast",
        "24 hr Forecast",
        "36 hr Forecast",
        "48 hr Forecast" 
    };

    public ProgChartFragment() {
        super( NoaaService.ACTION_GET_PROGCHART, sProgChartCodes, sProgChartNames,
                "Select Prognosis Chart" );
    }

    @Override
    protected Intent getServiceIntent() {
        return new Intent( getActivity(), ProgChartService.class );
    }

}