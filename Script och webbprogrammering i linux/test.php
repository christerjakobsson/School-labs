<?php
// set some variables 
$host = '0.0.0.0'; 
$port = 24069; 

// don't timeout! 
set_time_limit(0); 

// create socket 
$socket = socket_create(AF_INET, SOCK_STREAM, 0) or die("Could not create socket\n"); 

// bind socket to port 
$result = socket_bind($socket, "0.0.0.0", $port) or die("Could not bind to socket\n"); 

echo "Listen for incoming connections on port ".$port.", exit by pressing CTRL-C\n";

while(true) {
$running = true;
	// start listening for connections 
	$result = socket_listen($socket, 3) or die("Could not set up socket listener\n"); 
	
	echo "res ".$result;
	// accept incoming connections 
	// spawn another socket to handle communication 
	$spawn = socket_accept($socket) or die("Could not accept incoming connection\n"); 
	print "Incoming connection from ".socket_getsockname($spawn, $host,$port)."\n";
    	echo "res ".$spawn;
	 // we are the child
         // Use $spawn for communication as you see fit
         // exit();
	while($running) {
		// read client input 	
		$input = socket_read($spawn, 1024) or die("Could not read input, disconnecting\n"); 
		switch($input) {
			case "Quit":
				socket_close($spawn);
				$running = false;
				break;
			default:
				// clean up input string 
				$input = trim($input); 

				// echo input back 
				$output = $input . "\n"; 
				echo $output;
				socket_write($spawn, $output, strlen ($output)) or die("Could not write output\n"); 
		}
		
		
	}
	socket_close($spawn);
} 
socket_close($socket);
printf("Program ended cleanly\n", PHP_EOL);


/*
while(true) {
	
	// read client input 	
	$input = socket_read($spawn, 1024) or die("Could not read input\n"); 
	
	// clean up input string 
	$input = trim($input); 

	// echo input back 
	$output = $input . "\n"; 
	echo $output;
	socket_write($spawn, $output, strlen ($output)) or die("Could not write output\n"); 

	}
	
// close sockets 
socket_close($spawn);
*/


/*
$server = stream_socket_server("tcp://0.0.0.0:24069", $errno, $errorMessage);

if ($server === false) {
    throw new UnexpectedValueException("Could not bind to socket: $errorMessage");
}

for (;;) {
    $client = @stream_socket_accept($server);

    if ($client) {
    echo "HEJ";
        stream_copy_to_stream($client, $client);
        fclose($client);
    }
}
*/
?>
