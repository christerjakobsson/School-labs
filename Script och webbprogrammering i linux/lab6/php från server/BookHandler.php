
<?php

$data = json_decode(file_get_contents('php://input'), true);

$dbhost = "localhost";
$dbusername = "root";
$dbuserpass = "XXXXXX";
$dbname = "myDb";

if(is_null($data)) {
	http_response_code(404);
	echo "Error: No data found";
} 
else if ($data['command'] == "add_book") {
	$author = $data['author'];
	$title = $data['title'];
	$genre = $data['genre'];
	$genre2 = $data['genre2'];
	$date = $data['dateRead'];
	$grade = $data['grade'];
	$comments = $data['comments'];

	try {
	    $conn = new PDO("mysql:host=$dbhost;dbname=$dbname", $dbusername, $dbuserpass);
	    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

	    $create = "CREATE TABLE IF NOT EXISTS book_table (
	    author VARCHAR(30) NOT NULL,  
	    title VARCHAR(30) NOT NULL UNIQUE, 
	    genre VARCHAR(30) NOT NULL, 
	    genre2 VARCHAR(30) NOT NULL, 
	    dateRead DATE NOT NULL, 
	    grade ENUM('1','2','3','4','5') NOT NULL, 
	    comments VARCHAR(50))";
	    $conn->exec($create);

		$sql = "INSERT INTO book_table (author, title, genre, genre2, dateRead, grade, comments) VALUES 
    	(:author, :title, :genre, :genre2, :dateRead, :grade, :comments)";
    	
	    $smtp = $conn->prepare($sql);
	    $smtp->bindParam(':author', $author);
	    $smtp->bindParam(':title', $title);
	    $smtp->bindParam(':genre', $genre);
	    $smtp->bindParam(':genre2', $genre2);
	    $smtp->bindParam(':dateRead', $date);
	    $smtp->bindParam(':grade', $grade);
	    $smtp->bindParam(':comments', $comments);
	    $smtp->execute();
	    http_response_code(201);
	}
	catch(PDOException $e) {
		echo $e->getMessage();
		http_response_code(404);
	}
$conn = null;
}
else if ($data['command'] == "update_book") {
	$author = $data['author'];
	$title = $data['title'];
	$genre = $data['genre'];
	$genre2 = $data['genre2'];
	$dateRead = $data['dateRead'];
	$grade = $data['grade'];
	$comments = $data['comments'];

	try {
	    $conn = new PDO("mysql:host=$dbhost;dbname=$dbname", $dbusername, $dbuserpass);
	    
	    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	    $sql = "UPDATE book_table SET 
	    author=:author,
	    title=:title,
	    genre=:genre,
	    genre2=:genre2, 
	    dateRead=:dateRead, 
	    grade=:grade,
	    comments=:comments WHERE title=:title"; 

	    $smtp = $conn->prepare($sql);
	    $smtp->bindParam(':author', $author);
	    $smtp->bindParam(':title', $title);
	    $smtp->bindParam(':genre', $genre);
	    $smtp->bindParam(':genre2', $genre2);
	    $smtp->bindParam(':dateRead', $date);
	    $smtp->bindParam(':grade', $grade);
	    $smtp->bindParam(':comments', $comments);
	    $smtp->execute();

	    
	    if($smtp->rowCount() == 0) {
	    	echo "No book updated.";
			http_response_code(404);
	    } else {
	    	http_response_code(202);
		}
	} 
	catch(PDOException $e) {
	    	echo $e->getMessage();
	    	http_response_code(404);
	}
$conn = null;
}
else if($data['command'] == "get_list") {
	try {
	
		$conn = new PDO("mysql:host=$dbhost;dbname=$dbname", $dbusername, $dbuserpass);
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		
		$array = $conn->query("SELECT title FROM book_table")->fetchAll(PDO::FETCH_ASSOC);
		echo json_encode($array);
		http_response_code(202);
	}
	catch(PDOException $e) {
		http_response_code(404);
		echo $e->getMessage();
	}

	$conn = null;
}
else if($data['command'] == "get_title") {
	try {
		$title = $data['title'];

		$conn = new PDO("mysql:host=$dbhost;dbname=$dbname", $dbusername, $dbuserpass);
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

		$array = $conn->query("SELECT * FROM book_table WHERE title='$title'")->fetchAll(PDO::FETCH_ASSOC);

		if(count($array) == 0) {
			echo "Title ".$title." not found.";
			http_response_code(404);
		} else {
			echo json_encode($array);
		}
	}
	catch(PDOException $e) {
		echo $e->getMessage();
		http_response_code(404);
	}
	$conn = null;
}
else if($data['command'] == "get_whole_list") {
	try {
		$title = $data['title'];

		$conn = new PDO("mysql:host=$dbhost;dbname=$dbname", $dbusername, $dbuserpass);
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
			
		$return_arr = array();

		$array = $conn->query("SELECT * FROM book_table")->fetchAll(PDO::FETCH_ASSOC);
		http_response_code(202);
		echo json_encode($array);
	}
	catch(PDOException $e) {
		echo $e->getMessage();
		http_response_code(404);
	}

	$conn = null;
}
else if($data['command'] == "remove_book") {
	try {
		$titleToRemove = $data['titleToRemove'];

		$conn = new PDO("mysql:host=$dbhost;dbname=$dbname", $dbusername, $dbuserpass);
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		
		$sql = 'DELETE from book_table where title=:title';
		$preparedStatement = $conn->prepare($sql);
		$preparedStatement->execute(array(':title' => $titleToRemove));
		
		if($preparedStatement->rowCount() == 0) {
			http_response_code(400);
			echo "Title \"".$titleToRemove."\" not found, nothing was deleted";
		} else {
			http_response_code(202);
		}
	}
	catch(PDOException $e) {
		http_response_code(404);
		echo $e->getMessage();
	}
	$conn = null;
}
else {
	echo 'POST ';
	http_response_code(404);
}

?>