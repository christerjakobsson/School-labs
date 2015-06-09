#!/usr/bin/perl

use strict;
use warnings;

my $dir = $ARGV[0];

traverse($dir);    
    
    sub traverse {
	my ($thing) = @_;

	return if not -d $thing;
	
	opendir(my $DIR, $thing) or die $!;    
	while (my $file = readdir($DIR)) {
	
	    # A file test to check that it is a directory
	    # Use -f to test for a file
	    
	    next if $file eq '.' or $file eq '..';
	    print "$thing/$file	\n";
	    traverse("$thing/$file");
	}

	closedir($DIR);
    }
exit 0;
