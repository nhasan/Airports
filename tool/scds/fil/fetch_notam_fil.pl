#! /usr/bin/perl

#/*
# * FlightIntel
# *
# * Copyright 2021 Nadeem Hasan <nhasan@nadmm.com>
# *
# * This program is free software: you can redistribute it and/or modify
# * it under the terms of the GNU General Public License as published by
# * the Free Software Foundation, either version 3 of the License, or
# * (at your option) any later version.
# *
# * This program is distributed in the hope that it will be useful,
# * but WITHOUT ANY WARRANTY; without even the implied warranty of
# * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# * GNU General Public License for more details.
# *
# * You should have received a copy of the GNU General Public License
# * along with this program.  If not, see <http://www.gnu.org/licenses/>.
# */

# Ubuntu package dependencies
#   libconfig-simple-perl
#   libnet-sftp-foreign-perl
#   libpath-tiny-perl
#

use strict;
use warnings;
use v5.012;

use Config::Simple;
use Net::SFTP::Foreign;
use Path::Tiny;

my $cfgfile = shift or die "Missing config file parameter.";
-f $cfgfile or die "Config file not found.";
my $cfg = new Config::Simple($cfgfile) or die Config::Simple->error();

my $SWIM = $cfg->param(-block => "SWIM");
my $host = $SWIM->{host} or die "Missing config 'SWIM.host'.";
my $user = $SWIM->{user} or die "Missing config 'SWIM.user'.";

my $FIL = $cfg->param(-block => "FIL");
my $timestampfile = $FIL->{timestampfile} or die "Missing config 'FIL.timestampfile'.";
my $localtimestamp = $FIL->{timestamp} // "";
my $outdir = $FIL->{outdir} or die "Missing config 'FIL.outdir'.";
my $tmpdir = $FIL->{tmpdir} or die "Missing config 'FIL.tmpdir'.";
my $datafile = $FIL->{datafile} or die "Missing config 'FIL.datafile'.";

my $retry = 5;      
my $error;

# Create the output directories if missing
-e $outdir || path($outdir)->mkpath;
-e $tmpdir || path($tmpdir)->mkpath;

my $lock_file = "$outdir/lock";

-f $lock_file && die "Lock file found.";

# Wait here to make sure we have overlap between JMS and FIL
my $wait = 300;
say "Waiting for $wait secs...";
sleep($wait);

# Create the lock file
open my $fh, ">", $lock_file or die "Couldn't create lock: $!";
close $fh;

while ($retry) {
    $error = 0;
    say "Connecting to $user\@$host...";
    my $sftp = Net::SFTP::Foreign->new($host, user => $user, queue_size => 1) or $error = 1;
    if ($error) { 
        say "SFTP connection failed: ".sftp->error;
        sleep(15);
        $retry--;
        next;
    }
    say "Created SFTP session.";
    my $remotetimestamp = $sftp->get_content($timestampfile) or $error = 1;
    if ($error) {
        say "$timestampfile failed: ".$sftp->error;
        undef $sftp;
        sleep(15);
        $retry--;
        next;
    }

    chomp($remotetimestamp);

    say "Last fetch timestamp was $localtimestamp";
    if ($localtimestamp lt $remotetimestamp) {
        say "Fetching data file $datafile from FAA server.";
        $sftp->get($datafile, "$tmpdir/$datafile") or $error = 1;
        if ($error) {
            say "$datafile failed: ".$sftp->error;
            undef $sftp;
            sleep(15);
            $retry--;
            next;
        }

        unlink $lock_file or die "Couldn't unlink lock: $!";
        path("$tmpdir/$datafile")->move("$outdir/$datafile")
                or die "Couldn't move datafile: $!";

        $FIL->{timestamp} = $remotetimestamp;
        $cfg->param(-block => "FIL", -values => $FIL);
        $cfg->save();
    } else {
        say "Noting new to fetch from FAA server.";
    }

    say "Closing SFTP session.";
    undef $sftp;
    last;
}
