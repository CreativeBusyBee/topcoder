<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>CBB Analysing You</title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon">
<link rel="icon" href="images/favicon.ico" type="image/x-icon">
<link rel="stylesheet" href="css/watson-bootstrap-dark.css">
<link rel="stylesheet" href="css/browser-compatibility.css">
<link rel="stylesheet" href="css/watson-code.css">
<link rel="stylesheet" href="css/style.css">
</head>

<body>
	<script>
		window.fbAsyncInit = function() {
			FB.init({
				appId : '1492417327698464',
				xfbml : true,
				version : 'v2.2'
			});
		};

		(function(d, s, id) {
			var js, fjs = d.getElementsByTagName(s)[0];
			if (d.getElementById(id)) {
				return;
			}
			js = d.createElement(s);
			js.id = id;
			js.src = "//connect.facebook.net/en_US/sdk.js";
			fjs.parentNode.insertBefore(js, fjs);
		}(document, 'script', 'facebook-jssdk'));
	</script>

	<div class="container">
		<div class="header row">
			<div class="col-lg-3">
				<img src="images/app.png">
			</div>
			<div class="col-lg-8">
				<h2>You are what you read!</h2>

			</div>
		</div>
		<div class="row">
			<div class="col-lg-12">
				<center>
					<h2>Try it</h2>
				</center>
				<div class="well" style="height: 400px; overflow: auto;">
					<form method="post" class="form-horizontal" action="demo">
						<fieldset>
							<div class="form-group row">
								<div class="col-lg-6">


									<textarea id="textArea" name="content" rows="10"
										placeholder="Please enter a list of Blogs and their feed (maximum of 10 blogs)..."
										required class="form-control">${content}</textarea>

								</div>

							</div>
							<div style="margin-bottom: 0px; padding-top: 5px;"
								class="form-group row">

								<div class="col-lg-2 col-lg-push-2">
									<button type="submit" class="btn btn-block">Check</button>
								</div>
							</div>
						</fieldset>
					</form>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-lg-12">
				<h2>Results</h2>
				<div class="row">
					<div class="col-lg-6">
						<c:if test="${not empty traits}">
							<div class="well" style="height: 400px; overflow: auto;">
								<c:forEach var="trait" items="${traits}">
									<div
										class="row <c:if test='${not empty trait.title }'>flag</c:if> ">
										<div class="col-lg-10">
											<span>${trait.id}</span>
										</div>
										<div class="col-lg-2">
											<span>${trait.value}</span>
										</div>
									</div>
								</c:forEach>
							</div>
						</c:if>

					</div>

				</div>
			</div>

		</div>
		<c:if test="${not empty viz}">
			<a name="figure"></a>
			<div class="row">
				<div class="col-lg-12">
					<h2>Visualization</h2>
					<div class="col-lg-6">${viz}</div>

				</div>
				
				
 </div>
			</div>
			<div class="row">
				<div class="col-lg-6">
<center>
					<img id="share_button"
						src="http://www.ryanhanley.com/wp-content/uploads/2012/05/facebook-share-button.png"/>
</center>
				</div>
			</div>
		</c:if>

		<script type="text/javascript" src="js/css_browser_selector.js"></script>
		<script type="text/javascript" src="js/dummy-text.js"></script>
		<script
			src="//ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"
			type="text/javascript"></script>
		<script type="text/javascript">
			$(document)
					.ready(
							function() {
								$('#share_button')
										.click(function(e){
e.preventDefault();
FB.ui(
{
																method : 'feed',
																name : 'This is the content of the "name" field.',
																link : 'http://cbb-top1.mybluemix.net/demo',
																picture : 'http://www.hyperarts.com/external-xfbml/share-image.gif',
																caption : 'This is the content of the "caption" field.',
																description : 'This is the content of the "description" field, below the caption.',
																message : ''
															});
												});
							});
		</script>
		<c:if test="${(empty content) && (empty param.reset) }">


		</c:if>
</body>
</html>