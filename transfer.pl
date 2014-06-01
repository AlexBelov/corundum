#!/usr/bin/perl
use warnings;
use Cwd;
use constant { true => 1, false => 0 };

# ============ FUNC ==============
sub open_file {
	my ($file) = @_;
	open my $info, $file or die "Could not open $file: $!";
	return $info;
}

sub include_require {
	my ($check_line) = @_;
	my @word_arr = split /[ ]+/, $check_line;
	return ($word_arr[0] eq "require") ? true : false;
}

sub file_from_require {
	my ($whole_str) = @_;
	my @words = split /["' ]+/, $whole_str;
	return (scalar @words >= 2)? $words[1] : die "require SYNTAX ERROR";
}

sub file_path {
	my ($file_path) = @_;
	my $count_dirs = scalar(split '/', $file_path); 
	return ($count_dirs > 1)? substr($file_path, 0, rindex($file_path, '/')) : '';
} 

sub get_file_name {
	my ($f_path) = @_;
	my @d_words = split('/', $f_path);
	return (scalar @d_words > 0)? $d_words[-1] : die "require SYNTAX ERROR";
}

sub overwrite {
	my ($h_file, $current_dir) = @_;

	my $dir = ($current_dir eq '')? $script_dir.'/'.$current_dir : $script_dir.'/'.$current_dir.'/';

	while(my $line = <$h_file>) {
	    if(include_require($line) == true)
	    {
	    	my $next_file_path = file_from_require($line);	    	

	    	my $file_dir = file_path($next_file_path);
	    	my $file_name = get_file_name($next_file_path);

	    	$file_dir = ($file_dir eq '')? '' : $file_dir.'/';

	    	my $h_next_file = open_file($dir.($file_dir).$file_name);

	    	overwrite($h_next_file, $current_dir.'/'.$file_dir);
	    }
	    else
	    {
	    	print $f_result $line;
	    }
	}
}

# ============ RUN CODE ==============
my $file = $ARGV[0];
our $script_dir = cwd; #Cwd::realpath($0); #chdir

open our $f_result, '>', 'new'.$file or die "Can't write new file: $!";
my $f_open = open_file($file);

overwrite($f_open, "");

close $f_open;
close $f_result;





