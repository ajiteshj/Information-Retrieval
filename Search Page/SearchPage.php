#Search Page Frontend.

<?php
// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');
$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$algoType = isset($_REQUEST['algo']) ? $_REQUEST['algo'] : lucene ;
$results = false;
if ($query)
{
require_once('/var/www/html/Apache/Solr/Service.php');
$solr = new Apache_Solr_Service('localhost', 8983, '/solr/irhw3/');
if (get_magic_quotes_gpc() == 1)
{
$query = stripslashes($query);
}
if($algoType == pagerank){
$additionalParameters = array(
'sort' => 'pageRankFile desc'
);
try{
$results = $solr->search($query, 0, $limit, $additionalParameters);
}
catch (Exception $e)
{
die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
}
}
else{
try
{
$results = $solr->search($query, 0, $limit);
}
catch (Exception $e)
{
die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
}
}
}
?>
<html>
	<head>
		<title>PHP Solr Client Example</title>
	</head>
	<body>
		<form accept-charset="utf-8" method="get">
		<center>
			<label for="q">Search:</label>
			<input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
			<input type="radio" name = "algo"  value="lucene"> Solr Lucene
			<input type="radio" name = "algo" value="pagerank"> Pagerank<br><br>
			<input type="submit"/></center>
		</form>
<?php
if ($results)
{
 $total = (int) $results->response->numFound;
 $start = min(1, $total);
 $end = min($limit, $total);
?>
 <div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
 <ol>
<?php
 foreach ($results->response->docs as $doc)
 {

//echo json_decode($doc);
$json = json_decode($doc, true);
echo '<pre>';
echo $json->id;
?>

<table style="border: 1px solid black; text-align: left"> 
<?php
 foreach ($doc as $field => $value)
 {
?>
<?php { 
?>
 <li><tr> 
 <th>Title</th>
<?php if($doc->title != ""){ ?>
<td><a target = "_blank" href = "<?php echo htmlspecialchars($doc->og_url, ENT_NOQUOTES, 'utf-8');?>">
 <?php  echo $doc->title; ?></a></td>
<?php }
else {  ?>
<td><?php echo "NA"; } ?></td>
</tr>
<tr> 
<th>URL</th>
<?php if($doc->og_url != ""){ ?>
 <td><a target="_blank" href="<?php echo htmlspecialchars($doc->og_url, ENT_NOQUOTES, 'utf-8');?>">
 <?php echo htmlspecialchars($doc->og_url, ENT_NOQUOTES, 'utf-8'); ?></a></td>
<?php }
else { ?>
<td><?php echo "NA"; } ?></td>
</tr>
<tr> <th>ID</th>
<td><?php if($doc->id != "") {echo $doc->id;} else {echo "NA";} ?></td></tr>
<tr> <th>Description</th><td><?php if($doc->description != "") {echo $doc->description;} else {echo "NA";} ?></td></tr></li>
<?php }
 ?>
<?php 
break;
?>
<?php } ?>
</tbody></table> 
 
<?php
 }
?>
 </ol>
<?php
}
?>
 </body>
</html>
