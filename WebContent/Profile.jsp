<!DOCTYPE HTML>
<!-- <jsp:useBean id="uBean" class="model.UserBean" scope="session"/> -->
<!-- <jsp:setProperty name="uBean" property="*"/> -->
<html>
	<head>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" href="css/Profile.css"></link>
		<script type="text/javascript" src="js/profileErrors.js"></script>	<!-- there is no profileErrors.js -->
		<title>Personal Profile Page - Diet Patcher</title>
	</head>
	<body>
		<div id="header">
			<img src="resources/DietPatcherIco.png" />
			<b>Diet Patcher</b>
			<a href=""> Logout </a> 
			<a> / </a>
			<a href="">Meal Planner </a>
		</div>
		<div id="container">
			<div id="title">
				<h1>${uBean.username}'s Profile</h1>
			</div>
			<div id="main">
				<h2> Information about your account </h2>
				<form class="form-class" id="Profile" method="POST">
				
					<div class="form-group" id="accountInfo">
						<div class="input-group">
					      <div class="input-group-addon">User Name</div>
					      <input type="text" class="form-control" id="profileUserName" value=${uBean.username}>
					    </div>
 
 						<div class="input-group">
					      <div class="input-group-addon">Password</div>
					      <input type="password" class="form-control" id="profilePassword" value=${uBean.password}>
					    </div>

					    <div class="input-group">
					      <div class="input-group-addon">Email</div>
					      <input type="email" class="form-control" id="profileEmail" value=${uBean.email}>
					    </div>
					</div>
					
					<h2> Information about you </h2>
					<div class="form-group" id="userInfo">
						 
						 <div class="input-group">
					      <div class="input-group-addon">Gender</div>
					      <input type="text" class="form-control" id="profileGender" placeholder="M (Male), F (Female), O (Other)"><br>
					    </div>
						<div class="input-group">
					      <div class="input-group-addon">Age</div>
					      <input type="number" min="0" class="form-control" id="profileAge" placeholder="40">
					      <div class="input-group-addon">y/o</div>
					    </div>
						<div class="input-group">
					      <div class="input-group-addon">Height</div>
					      <input type="number" min="0" class="form-control" id="profileHeight" placeholder="170">
					      <div class="input-group-addon">cm</div>
					    </div>
					    <div class="input-group">
					      <div class="input-group-addon">Weight</div>
					      <input type="number" min="0" class="form-control" id="profileWeight" placeholder="70">
					      <div class="input-group-addon">kg</div>
					    </div>
					    <div class="input-group">
					      <div class="input-group-addon">Waist</div>
					      <input type="number" min="0" class="form-control" id="profileWaist" placeholder="60">
					      <div class="input-group-addon">cm</div>
					    </div>

					</div>
					<h2> Some useful information </h2>
							Your BMI (body mass index): <span id="BMI"></span><br>
							Your BAS (body adiposity index): <span id="BAS"></span><br>
					<button type="submit" class="btn btn-primary">Save Changes</button>
				</form>
			</div>
			<div id="footer">
			</div>
		</div>
	</body>
</html>