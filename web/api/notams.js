/*
 * FlightIntel
 *
 * Copyright 2021 Nadeem Hasan <nhasan@nadmm.com>
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

const sqlite3 = require('sqlite3').verbose();
const config = require('config');

let db = new sqlite3.Database(config.get("Notams.dbname"), 
    sqlite3.OPEN_READONLY,
    (err) => { 
        if (err) {
            return console.error(err.message);
        }
    }
);

let getNotams = function (location, finish) {
    db.all("SELECT * FROM notams "
        + "WHERE (location = ?1 "
        + "AND ((classification IN ('DOM', 'FDC') AND xovernotamID <> '') OR xovernotamID = '') "
        + "AND (effectiveEnd = '' OR effectiveEnd > strftime('%Y-%m-%dT%H:%M:%fZ', 'now'))) "
        + "OR "
        + "(location <> ?1 AND xoveraccountID <> '' "
        + "AND xoveraccountID = (SELECT max(icaoLocation) FROM notams WHERE location = ?1)) "
        + "order by issued DESC, notamID DESC",
    [location],
    (err, rows) => {
        if (err) {
            finish({ error: err.message });
        } else {
            finish(rows);
        }
    });
}

const express = require('express');
const router = express.Router();

router.get('/:location', function (req, res) {
    console.log("Request for location="+req.params.location+" from "+req.headers['x-forwarded-for']);
    getNotams(req.params.location,
        function finish(result) {
            if (result.hasOwnProperty("error")) {
                res.status(500);
            }
            res.type('json').send(JSON.stringify(result, null, 2));
        });
});

module.exports = router;
