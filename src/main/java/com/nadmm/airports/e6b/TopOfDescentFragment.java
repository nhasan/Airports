/*
 * FlightIntel for Pilots
 *
 * Copyright 2011-2017 Nadeem Hasan <nhasan@nadmm.com>
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

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.nadmm.airports.R;

import java.util.Locale;

public class TopOfDescentFragment extends E6bFragmentBase {

    private EditText mInitAltEdit;
    private EditText mDesiredAltEdit;
    private EditText mGsEdit;
    private EditText mDscntRateEdit;
    private EditText mDistanceEdit;

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged( CharSequence s, int start, int before, int count ) {
        }

        @Override
        public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
        }

        @Override
        public void afterTextChanged( Editable s ) {
            processInput();
        }
    };

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.e6b_top_of_descent_view, container, false );
        return createContentView( view );
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState ) {
        super.onActivityCreated( savedInstanceState );

        mInitAltEdit = findViewById( R.id.e6b_edit_initial_alt );
        mDesiredAltEdit = findViewById( R.id.e6b_edit_desired_alt );
        mGsEdit = findViewById( R.id.e6b_edit_gs );
        mDscntRateEdit = findViewById( R.id.e6b_edit_descent_rate );
        mDistanceEdit = findViewById( R.id.e6b_edit_distance );

        mInitAltEdit.addTextChangedListener( mTextWatcher );
        mDesiredAltEdit.addTextChangedListener( mTextWatcher );
        mGsEdit.addTextChangedListener( mTextWatcher );
        mDscntRateEdit.addTextChangedListener( mTextWatcher );

        setFragmentContentShown( true );
    }

    @Override
    protected String getMessage() {
        return "Find the distance at which to start the descent to arrive at the" +
                " destination at the desired altitude.";
    }

    @Override
    protected void processInput() {
        double initAlt = Double.MAX_VALUE;
        double desiredAlt = Double.MAX_VALUE;
        double gs = Double.MAX_VALUE;
        double dscntRate = Double.MAX_VALUE;

        try {
            initAlt = Double.parseDouble( mInitAltEdit.getText().toString() );
            desiredAlt = Double.parseDouble( mDesiredAltEdit.getText().toString() );
            gs = Double.parseDouble( mGsEdit.getText().toString() );
            dscntRate = Double.parseDouble( mDscntRateEdit.getText().toString() );
        } catch ( NumberFormatException ignored ) {
        }

        if ( initAlt != Double.MAX_VALUE && desiredAlt != Double.MAX_VALUE
                && gs != Double.MAX_VALUE && dscntRate != Double.MAX_VALUE ) {
            double distance = gs*( ( initAlt-desiredAlt )/( dscntRate*60 ) );
            mDistanceEdit.setText( String.format( Locale.US,  "%.1f", distance ) );
        } else {
            mDistanceEdit.setText( "" );
        }
    }

}
