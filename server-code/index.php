<?php

ini_set('display_errors',1);
ini_set('display_startup_errors',1);
error_reporting(-1);

require_once 'medoo.min.php';

function scoreStringToArray($scoreString, $delimeter, $delimeter2){
	$scoreArray = explode($delimeter2, $scoreString);
	for ($i = 0; $i < count($scoreArray); $i++){
		$scoreData = explode($delimeter, $scoreArray[$i]);
		$scoreArray[$i] = array($scoreData[0], floatval($scoreData[1]));
	}
	return $scoreArray;
}

function sortFunction($a, $b){
    if($a[1] == $b[1]) {
        return 0;
    }
    return ($a[1] < $b[1]) ? 1 : -1;
}

$scoreString = $_REQUEST["scoreString"];
$delimeter = $_REQUEST["delimeter"];
$delimeter2 = $_REQUEST["delimeter2"];
$errorflag = $_REQUEST["errorflag"];

$receivedScores = scoreStringToArray($scoreString, $delimeter, $delimeter2);

// Establish a connection using medoo with options depending on accessing localhost or GAE
$database = new medoo([
	'database_type' => 'mysql',
	'database_name' => 'polygon',
	'server' => 'db4free.net',
	'charset' => 'utf8',
	'username' => 'lchan1994',
	'password' => 'polygonpassword'
]);


// Check for connection errors
if ($database->error()[1]){
	echo json_encode($database->error()) . "\n";
	return;
}

for ($i = 0; $i < 5; $i++){
	$database->query("insert into PolygonScores (Name, Score) values ('" . addslashes($receivedScores[$i][0]) . "', '" . $receivedScores[$i][1] . "') on duplicate key update Score = GREATEST(Score," . $receivedScores[$i][1] . ")");
}

$combinedScores = $database->select("PolygonScores","*",array(
	"ORDER" => "Score DESC",
	"LIMIT" => 5
));

$globalScoresString = "";
for ($i = 0; $i < count($combinedScores); $i++){
	$globalScoresString .= $combinedScores[$i]["Name"] . $delimeter . $combinedScores[$i]["Score"];
	if ($i < 4)
		$globalScoresString .= $delimeter2;
}

echo $globalScoresString;

?>
