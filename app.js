var appurl = 'http://localhost:8080';

var express = require('express');
var app = express();

var http = require('http');
var path = require('path');
var engine = require('ejs-locals');
var session = require('express-session');
var multer  = require('multer');
var s3 = require('multer-s3');
var bodyParser = require('body-parser');
var fs = require('fs');
var async = require('async');
var assert = require('assert');
var secretObj = JSON.parse(fs.readFileSync('json/secret.json', 'utf8'));
// var mysqlObj = JSON.parse(fs.readFileSync('json/mysqldb.json', 'utf8'));
// var awsObj = JSON.parse(fs.readFileSync('json/aws.json', 'utf8'));
// var emailObj = JSON.parse(fs.readFileSync('json/email.json', 'utf8'));
var dateFormat = require('dateformat');
var bcrypt = require('bcrypt');
var nodemailer = require('nodemailer');
var uuid = require('node-uuid');
// var PythonShell = require('python-shell');
app.engine('ejs', engine);
app.set('views', path.join( __dirname, 'views'));
app.set('view engine', 'ejs');

app.use(multer({dest: './uploads/', includeEmptyFields: true}).single('photo'));
app.use( express.static( path.join( __dirname, 'public' )));
app.use(express.static(__dirname + '/views/stylesheets'));
app.use(express.static(__dirname + '/views/images'));
app.use(express.static(__dirname + '/views/js'));
app.use(bodyParser.urlencoded({extended: true }));
app.use(bodyParser.json());

app.use(session({
  secret: secretObj.secret,
  resave: true,
  saveUninitialized: true,
  login: false
}));

var AWS = require('aws-sdk'); 
AWS.config.update({region:'us-east-1'});
var dynamodb = new AWS.DynamoDB();

// var transporter = nodemailer.createTransport({
//   service: emailObj.serv,
//   auth: {
//     user: emailObj.user,
//     pass: emailObj.pass
//   }
// });

// var mysql      = require('mysql');
// var connection = mysql.createConnection({
//   host     : mysqlObj.host,
//   user     : mysqlObj.user,
//   password : mysqlObj.password,
//   port     : mysqlObj.port,
//   database : mysqlObj.database
// });

// var ddb = require('dynamodb').ddb({ 
//   accessKeyId: awsObj.accessKeyId, 
//   secretAccessKey: awsObj.secretAccessKey,
//   region: "us-east-1"
// });

// var MongoClient = require('mongodb').MongoClient;
// var url = 'mongodb://localhost:27017/test';
// MongoClient.connect(url, function(err, db) {
//   assert.equal(null, err);
//   console.log("Connected correctly to server.");
//   db.close();
// });

/////////////////

function isObjectEmpty(obj) {
  var key;
  for (key in obj) {
      if (obj.hasOwnProperty(key)) {
          return false;
      }
  }
  return true;
};

app.get('/login', function (req, res) {
	res.redirect('/');
  return;
});

app.get('/', function (req, res) {
	if (req.session.login) {
    res.redirect('/home');
    return;
  }

  var t = 'DLMS';
  res.render('index', { 
    title: t,
    login: false,
    signin: true,
    signup: false
  });
});

app.get('/signup', function (req, res) {
  if (req.session.login) {
    res.redirect('/home');
    return;
  }

  var t = 'Create Account';
  res.render('signup', { 
    title: t,
    login: false,
    signin: false,
    signup: true
  });
});

app.get('/home', function (req, res) {
	if (!req.session.login) {
		res.redirect('/');
		return;
	}

	var t = 'Home';
  res.render('home', { 
    title: t,
    login: true,
    signin: false,
    signup: false,
    username: req.session.username,
    email: req.session.email,
    color: req.session.color
  });
});

app.post('/login', function (req, res) {
	var username = req.body.username;
	var password = req.body.password;

	var params1 = {
		Key : {
			username : {
				S: username
			}
		},
		TableName : 'users'
	};

	dynamodb.getItem(params1, function (err1, data1) {
	  if (err1) {
	  	console.log('/login: Error 1');
	  	console.log(err1);
	  	res.send({success: false, msg: '/login: Error 1'});
	  	return;
	  } else {
	  	if (isObjectEmpty(data1)) {
	  		res.send({success: false, msg: 'Username does not exist in system!'});
				return;
	  	} else {
	  		bcrypt.compare(password, data1.Item.password.S, function (err2, data2) {
	  			if (err2) {
	  				console.log('/login: Error 2');
				  	console.log(err2);
				  	res.send({success: false, msg: '/login: Error 2'});
				  	return;
	  			} else {
	  				if (!data2) {
	  					res.send({success: false, msg: 'Incorrect password!'});
							return;
	  				} else {
	  					req.session.login = true;
							req.session.username = username;
							req.session.email = data1.Item.email.S;
							req.session.color = data1.Item.color.N;
			  			res.send({success: true, msg: ''});
							return;
	  				}
	  			}
	  		});
	  	}
	  }
	});
});

app.post('/logout', function (req, res) {
	req.session.login = false;
  req.session.username = undefined;
  req.session.email = undefined;
  req.session.color = undefined;
  res.send({success: true, msg: 'Bye!'});
  return;
});

app.post('/create', function (req, res) {
	var username = req.body.username;
	var email = req.body.email;
	var password = req.body.password;

	var params1 = {
		Key : {
			email : {
				S: email
			}
		},
		TableName : 'emails'
	};

	dynamodb.getItem(params1, function (err1, data1) {
	  if (err1) {
	  	console.log('/create: Error 1');
	  	console.log(err1);
	  	res.send({success: false, msg: '/create: Error 1'});
			return;
	  } else {
	  	if (!isObjectEmpty(data1)) {
	  		res.send({success: false, msg: 'A username (' + data1.Item.username.S + ') is already associated to this email!'});
	  	} else {
	  		var params2 = {
					Key : {
						username : {
							S: username
						}
					},
					TableName : 'users'
				};

				dynamodb.getItem(params2, function (err2, data2) {
					if (err2) {
						console.log('/create: Error 2');
	  				console.log(err2);
	  				res.send({success: false, msg: '/create: Error 2'});
						return;
					} else {
						if (!isObjectEmpty(data2)) {
				  		res.send({success: false, msg: 'Sorry, this username already exists! Please pick another.'});
				  	} else {
				  		bcrypt.hash(password, 8, function (err3, hash) {
				  			if (err3) {
				  				console.log('/create: Error 3');
	  							console.log(err3);
	  							res.send({success: false, msg: '/create: Error 3'});
									return;
				  			} else {
				  				var randomcolor = Math.floor(Math.random() * 6) + 1;
				  				console.log(randomcolor);
				  				var params4 = {
										Item : {
											username : {
												S: username
											},
											email : {
												S: email
											},
											password : {
												S: hash
											},
											color : {
												N: randomcolor.toString()
											}
										},
										TableName : 'users'
									};

									dynamodb.putItem(params4, function (err4, data4) {
										if (err4) {
											console.log('/create: Error 4');
											console.log(err4);
											res.send({success: false, msg: '/create: Error 4'});
											return;
										} else {
											var params5 = {
												Item : {
													email : {
														S: email
													},
													username : {
														S: username
													}
												},
												TableName : 'emails'
											};

											dynamodb.putItem(params5, function (err5, data5) {
												if (err5) {
													console.log('/create: Error 5');
													console.log(err5);
													res.send({success: false, msg: '/create: Error 5'});
													return;
												} else {
													req.session.login = true;
													req.session.username = username;
													req.session.email = email;
													req.session.color = randomcolor;
									  			res.send({success: true, msg: ''});
													return;
												}
											});
										}
									});
				  			}
				  		});
				  	}
					}
				});
	  	}
	  }
	});
});

app.post('/changeColor', function (req, res) {
	var username = req.session.username;
	var color = req.body.color;
	var params1 = {
		Key : {
			username : {
				S: username
			}
		},
		TableName : 'users',
		UpdateExpression : 'SET color = :c',
		ExpressionAttributeValues : {
			':c' : { N: color }
		},
		ReturnValues : "UPDATED_NEW"
	};

	dynamodb.updateItem(params1, function (err1, data1) {
		if (err1) {
			console.log('/changeColor: Error 1');
			console.log(err1);
			res.send({success: false, msg: '/changeColor: Error 1'});
			return;
		} else {
			req.session.color = parseInt(color);
			res.send({success: true, msg: ''});
			return;
		}
	});
});

/////////////////

var server = app.listen(8080, function () {
  var host = server.address().address;
  var port = server.address().port;

  console.log('App listening at http://localhost:%s', port);
});
