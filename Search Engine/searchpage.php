<script
  src="https://code.jquery.com/jquery-2.2.4.js"
  integrity="sha256-iT6Q9iMJYuQiMWNd9lDyBUStIq/8PuOW33aOqmvFpqI="
  crossorigin="anonymous"></script>
<script
  src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"
  integrity="sha256-T0Vest3yCU7pafRw9r+settMBX6JkKN06dqBnpQ8d30="
  crossorigin="anonymous"></script>
<style>
ul,#q.ui-autocomplete-input {
  list-style-type: none;
  background-color: Azure;
	
	width:15em;
}
.ui-helper-hidden-accessible {
	display:none;
}

</style>
<script>
$(function() {
            $("#q").autocomplete({
                source : function(request, response) {
                    var lWord = $("#q").val().toLowerCase().split(" ").pop(-1);
                    var prevWord = "";
                    var query = $("#q").val();
                    var multiWord = query.split(" ");
                    $.ajax({
                        url : "http://localhost:8983/solr/irhw3/suggest?q=" + lWord + "&wt=json",
			crossDomain: true,
			dataType : 'jsonp',
                        jsonp : 'json.wrf',
                        success : function(data) {
                            var suggestions = data.suggest.suggest[lWord].suggestions;
                            suggestions = $.map(suggestions, function (value, index) {
                                 if (multiWord.length > 1) {
                                    var lIndex = query.lastIndexOf(" ");
                                    prevWord = query.substring(0, lIndex + 1).toLowerCase();
                                }
                                if (!/^[0-9a-zA-Z]+$/.test(value.term)) {
                                    return null;
                                }
                                return prevWord + value.term;
                            });
                            response(suggestions.slice(0, 5));
                        }
                        
                    });
                },
                minLength : 1
            });
        });
var reClick = 0;
function reSearch(){
	reClick = 1;	
	document.getElementById("hid").value = 1;
	document.forms['qpage'].submit();	
}
</script>
<?php 
ini_set('max_execution_time',600);
ini_set('memory_limit','3G');
include_once '/var/www/html/SpellCorrector.php';
include_once '/var/www/html/simple_html_dom.php'; 
?>
<?php
$flag = 0;
header('Content-Type: text/html; charset=utf-8');
$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$algoType = isset($_REQUEST['algo']) ? $_REQUEST['algo'] : lucene ;
$results = false;
if ($query)
{
	$query = strtolower($query);
	$queryForSolr = $query;
	ini_set('memory_limit', '-1');
	require_once('/var/www/html/Apache/Solr/Service.php');
	$solr = new Apache_Solr_Service('localhost', 8983, '/solr/irhw3/');
	if (get_magic_quotes_gpc() == 1)
	{
		$queryForSolr = stripslashes($queryForSolr);
	}
	$i = 0;
	$arrString = array();
     $arr =  explode(" ", $query);
     $queryAfterCorrection = "";
foreach($arr as $v){
    
    $queryAfterCorrection=$queryAfterCorrection.SpellCorrector::correct($v)." ";

}

	$queryBeforeCorrection = $queryForSolr;
	if($queryBeforeCorrection != $queryAfterCorrection){
		$queryForSolr = $queryAfterCorrection;
		$flag = 1;
		
	}
	if($algoType == pagerank){
		$additionalParameters = array(
		'sort' => 'pageRankFile desc'
		);
	}
	try{		
		?><script> var qBefore = '<?php echo $queryBeforeCorrection; ?>'; 
</script>
<?php		
		$rc = $_REQUEST['hid'];
		if($rc){
			$queryForSolr = $queryBeforeCorrection;		
		}
		$results = $solr->search($queryForSolr, 0, $limit, $additionalParameters);
	}
	catch (Exception $e){
		die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
	}
	
}

function getLinkfromMap($input) {
 $val = array_map('str_getcsv', file('/home/ajitesj/Desktop/WSJ/WSJmap.csv'));
    foreach($val as $value) {
        if ($value[0] == $input) {
            return $value[1];
        }
    }
}
?>
<html>
	<head>
		<title>PHP Solr Client Example</title>
	</head>
	<body>
		<form id = "qpage" accept-charset="utf-8" method="get">
		<div class="ui-widget">    
    
<center>
			<label for="q">Search:</label>
			<input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
			<input type="radio" name = "algo"  value="lucene"> Solr Lucene
			<input type="radio" name = "algo" value="pagerank"> Pagerank
			<input id="hid" type="hidden" name="hid" value = "0">
			<input type="submit"/></center></div>
		</form>
<?php
if ($results)
{
if(trim($queryForSolr) != trim($queryBeforeCorrection)){
		echo '<span>Showing results for:</span>'; echo " "; echo "<span style='color:blue;'><b><i>$queryAfterCorrection</i></b></span>"."<br/>"; 
		echo '<span>Search instead for:</span>'; echo " "; ?><a href="#" onclick="reSearch(qBefore);" >
 		<?php  echo "<span style='color:blue;'><b><i>$queryBeforeCorrection</i></b></span>".'<br/><br/>' ?></a>  
	<?php ;}
	$total = (int) $results->response->numFound;
	$start = min(1, $total);
	$end = min($limit, $total);
?>
<div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
<ol>
<?php
foreach ($results->response->docs as $doc)
{
	$json = json_decode($doc, true);
	echo '<pre>';
	echo $json->id;

?>

<table style="border: 1px solid black; text-align: left"> 
<?php 
foreach ($doc as $field => $value)
{?>
<?php { 
?>
<li>
	<tr> 
		<th>Title</th>
		<?php if($doc->title != ""){ ?>
		<td><a target = "_blank" href = "<?php echo htmlspecialchars($doc->og_url, ENT_NOQUOTES, 'utf-8');?>">
		<?php  echo $doc->title; ?></a></td>
		<?php }
		else {  ?>
		<td><?php echo "No Title"; }  ?></td>
	</tr>
	<tr> 
		<th>URL</th>
		<td><a target="_blank" href="<?php echo htmlspecialchars($doc->og_url, ENT_NOQUOTES, 'utf-8');?>">
		<?php $fileName = $doc->resourcename;
	        $MapFileIndex = substr($fileName, strrpos($fileName, '/') + 1); echo getLinkfromMap($MapFileIndex); ?></a></td>
	</tr>

	<tr> 
		<th>ID</th>
		<td><?php if($doc->id != "") {echo $doc->id;} else {echo "NA";} ?></td></tr>
	<tr> 
		<th>Description</th><td><?php if($doc->description != "") {echo $doc->description;} else {echo "No Description";} ?></td></tr>
	<tr> <th>Snippet</th>
		<td>
		<?php 
		$fileContent = file_get_html($fileName)->plaintext;
               
                //echo $fileContent;
                
        	
        	$sentenceArray = explode(".", $fileContent);
        	$flag = "false";
        	$length = count($sentenceArray);
                

                $flagMulti = 1;
                $break_flag = 0;
		$break_flag1 = 0;

        	for ($i = 0; $i < $length; $i++) {
        	     $value = $sentenceArray[$i];
                     if ($doc->title != ''){

                      if (strpos(strtolower($value), strtolower($doc->title)) !== false) {
                       continue;

                            }
                          }
			$queryToCheck = $queryAfterCorrection;
			$rc = $_REQUEST['hid'];
			if($rc){
				$queryToCheck = $queryBeforeCorrection;		
			}
        	     if(stripos(strtolower($value),strtolower($queryToCheck))!== false) {
        	     	$stopWordArray = array('WSJ');
                        $stopWordLength = count($stopWordArray);
                        for($i=0; $i <$stopWordLength;$i++){
                        if(stripos($value,$stopWordArray[$i]) !== false ){
	                        $value = str_ireplace($stopWordArray[$i]," ",$value);
                                

                        }
                    }
                    //$value = substr($value,0,160);
                    $value1 = stripos($value, $queryAfterCorrection);
                    echo substr($value, $value1, 200);
                    echo ('...');
                    
                    $flag ="true";
                    $flagMulti =0;
                    break;
		}               
	}

         if($flagMulti ==1 && $flag == "false"){
    
    $query_list = explode(" ",$queryToCheck);
      
        for ($i = 0; $i < $length; $i++) {
            $value = $sentenceArray[$i];

        if ($doc->title != ''){
 
            if (strpos(strtolower($value), strtolower($doc->title)) !== false) {
                   continue;
 
            }
            }
        foreach($query_list as $q_val){
                       
            $break_flag = 0;
            if (strpos(strtolower($value), strtolower($q_val)) !== false) {
 
             
              
 
 
                $value3 = stripos($value, $q_val);
                $substring = substr($value,$value3,200);
                echo $substring;
                $break_flag = 1;

                 echo "...";
                
                $snippet_count += strlen($substring);
                $flag = "true";
                if($snippet_count > 200){
                $break_flag = 1;
                break;
                }
 
                
 
               
 
                }

       if($break_flag == 1){
            break;

        }
           
        }
        if($break_flag == 1){
            break;

        }
    }
if($flag == "false"){
 $query_list = explode(" ",$queryToCheck);
       for ($i = 0; $i < $length; $i++) {
 $value = $doc->description;
	       
        foreach($query_list as $q_val){
                        
            $break_flag = 0;
            if (strpos(strtolower($value), strtolower($q_val)) !== false) { 
               $value1 = stripos($value, $q_val); 
                $substring = substr($value, $value1,160); 
                echo $substring; 
                
                echo "..."; 
 		
                $snippet_count += strlen($substring);
                $flag = "true"; 
                if($snippet_count > 160){
                $break_flag1 = 1; 
                break;
                } 
 

                 
 
                }
if($break_flag1 == 1){
            break;

        }
else{
	echo " ";
	break;
}

}
 if($break_flag1 == 1){
            break;

        }
}

}


     }
         
       
        ?>  </td>
	</tr>
</li>
<?php }
break; } 
?>
</tbody>
</table>  
<?php
 }
?>
 </ol>
<?php
}
?>
</body>
</html>
