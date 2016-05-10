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
var awsObj = JSON.parse(fs.readFileSync('json/aws.json', 'utf8'));
// var emailObj = JSON.parse(fs.readFileSync('json/email.json', 'utf8'));
var dateFormat = require('dateformat');
var bcrypt = require('bcrypt');
var nodemailer = require('nodemailer');
var uuid = require('node-uuid');
var shortid = require('shortid');
var cmd = require('node-cmd');
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
	var username = req.session.username;

	var params1 = {
		Key : {
			username : {
				S : username
			}
		},
		TableName: 'users'
	};

	dynamodb.getItem(params1, function (err1, data1) {
	  if (err1) {
	  	console.log('/home: Error 1');
	  	console.log(err1);
	  } else { 
	  	var dataitems = [];
	  	var getDataItems = function(it, callback) {
	  		var params3 = {
	  			Key : {
						id : {
							S : it
						}
					},
					TableName: 'dataitems'
	  		};
	  		dynamodb.getItem(params3, function (err3, data3) {
				  if (err3) {
				  	console.log('/home: Error 3');
				  	console.log(err3);
				  } else { 
				  	var ditem = {};
				  	ditem.id = data3.Item.id.S;
				  	ditem.url = data3.Item.url.S;
				  	ditem.type = data3.Item.type.S;
				  	ditem.name = data3.Item.name.S;
	  				ditem.isPub = data3.Item.isPub.N;
				  	if (data3.Item.owner.S === username) {
					  	ditem.owner = data3.Item.owner.S;
					  } else {
					  	ditem.owner = data3.Item.owner.S;
					  }
					  ditem.info = '/dataitem/' + data3.Item.id.S;
				  	dataitems.push(ditem);
				  	callback();
				  }
				});
	  	};
	  	var itemlist = [];
	  	if (data1.Item.dataitems === undefined) {
	  		itemlist = [];
	  	} else {
	  		itemlist = data1.Item.dataitems.SS;
	  	}
	  	async.each(itemlist, getDataItems, function (err2) {
	  		if (err2) {
	  			console.log('/home: Error 2');
			  	console.log(err2);
	  		} else {
	  			var t = 'Home';
				  res.render('home', { 
				    title: t,
				    login: true,
				    signin: false,
				    signup: false,
				    dataitems: dataitems,
				    username: req.session.username,
				    email: req.session.email,
				    color: req.session.color
				  });
	  		}
	  	});
	  }
	});
});

app.get('/dataitem/:id', function (req, res) {
	if (!req.session.login) {
		res.redirect('/');
		return;
	}
	var username = req.session.username;
	var id = req.params.id;
	var params1 = {
		Key : {
			id : {
				S : id
			}
		},
		TableName: 'dataitems'
	};
	dynamodb.getItem(params1, function (err1, data1) {
	  if (err1) {
	  	console.log('/dataitem/: Error 1');
	  	console.log(err1);
	  } else { 
	  	var ditem = {};
	  	ditem.isPub = data1.Item.isPub.N;
	  	ditem.viewers = data1.Item.viewers.SS;
	  	var permissiontoview = (ditem.viewers.indexOf(username) > -1) | ditem.isPub;
	  	if (!permissiontoview) {
	  		res.redirect('/home');
				return;
	  	}
	  	ditem.id = data1.Item.id.S;
	  	ditem.url = data1.Item.url.S;
	  	ditem.type = data1.Item.type.S;
	  	ditem.name = data1.Item.name.S;
	  	ditem.isPub = data1.Item.isPub.N;
	  	ditem.owner = data1.Item.owner.S;
	  	if (data1.Item.owner.S === username) {
		  	ditem.isOwner = 1;
		  } else {
		  	ditem.isOwner = 0;
		  }
		  ditem.info = '/dataitem/' + data1.Item.id.S;
	  	var t = 'Data';
		  res.render('data', { 
		    title: t,
		    login: true,
		    signin: false,
		    signup: false,
		    dataitem: ditem,
		    username: req.session.username,
		    email: req.session.email,
		    color: req.session.color
		  });
	  }
	});
});

app.get('/upload', function (req, res) {
	if (!req.session.login) {
		res.redirect('/');
		return;
	}
	var username = req.session.username;

	var t = 'Upload';
  res.render('upload', { 
    title: t,
    login: true,
    signin: false,
    signup: false,
    username: req.session.username,
    email: req.session.email,
    color: req.session.color
  });
});

app.get('/search', function (req, res) {
	if (!req.session.login) {
		res.redirect('/');
		return;
	}
	var username = req.session.username;

	var t = 'Search';
  res.render('search', { 
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
		ReturnValues : 'UPDATED_NEW'
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

app.post('/updatePub', function (req, res) {
	var username = req.session.username;
	var id = req.body.id;
	var isPub = req.body.isPub;

	var params1 = {
		Key : {
			id : {
				S: id
			}
		},
		TableName: 'dataitems'
	};
	dynamodb.getItem(params1, function (err1, data1) {
		if (err1) {
			console.log('/updatePub: Error 1');
			console.log(err1);
			res.send({success: false});
			return;
		} else {
			if (data1.Item.owner.S !== username) {
				res.send({success: false});
				return;
			} else {
				var params2 = {
					Key : {
						id : {
							S: id
						}
					},
					TableName: 'dataitems',
					UpdateExpression : 'SET isPub = :c',
					ExpressionAttributeValues : {
						':c' : { N: isPub }
					},
					ReturnValues : 'UPDATED_NEW'
				};
				dynamodb.updateItem(params2, function (err2, data2) {
					if (err2) {
						console.log('/updatePub: Error 2');
						console.log(err1);
						res.send({success: false});
						return;
					} else {
						res.send({success: true});
						return;
					}
				});
			}
		}
	});
});

app.post('/addViewer', function (req, res) {
	var username = req.session.username;
	var id = req.body.id;
	var viewer = req.body.viewer;
	console.log(req.body);
	var params1 = {
		Key : {
			id : {
				S: id
			}
		},
		TableName: 'dataitems'
	};
	dynamodb.getItem(params1, function (err1, data1) {
		if (err1) {
			console.log('/addViewer: Error 1');
			console.log(err1);
			res.send({success: false});
			return;
		} else {
			if (username !== data1.Item.owner.S) {
				res.send({success: false, msg: 'You do not have permission to add viewers.'});
				return;
			} else if (viewer === username) {
				res.send({success: false, msg: 'You are already a viewer.'});
				return;
			} else if (data1.Item.viewers.SS.indexOf(viewer) > -1) {
				res.send({success: false, msg: 'This user is already a viewer.'});
				return;
			} else {
				var params2 = {
					Key : {
						username : {
							S: viewer
						}
					},
					TableName: 'users'
				};
				dynamodb.getItem(params2, function (err2, data2) {
					if (err2) {
						console.log('/addViewer: Error 2');
						console.log(err2);
						res.send({success: false});
						return;
					} else {
						if (isObjectEmpty(data2)) {
							res.send({success: false, msg: 'The username you entered does not exist.'});
							return;
						} else {
							var viewers = data1.Item.viewers.SS;
							viewers.push(viewer);
							var params3 = {
								Key : {
									id : {
										S: id
									}
								},
								TableName: 'dataitems',
								UpdateExpression : 'SET viewers = :c',
								ExpressionAttributeValues : {
									':c' : { SS: viewers }
								},
								ReturnValues : 'UPDATED_NEW'
							};
							dynamodb.updateItem(params3, function (err3, data3) {
								if (err3) {
									console.log('/addViewer: Error 3');
									console.log(err3);
									res.send({success: false});
									return;
								} else {
									var dataitems = [];
									if (data2.Item.dataitems === undefined) {
										dataitems = [];
									} else {
										dataitems = data2.Item.dataitems.SS;
									}
									dataitems.push(id);
									var params4 = {
										Key : {
											username : {
												S: viewer
											}
										},
										TableName: 'users',
										UpdateExpression : 'SET dataitems = :c',
										ExpressionAttributeValues : {
											':c' : { SS: dataitems }
										},
										ReturnValues : 'UPDATED_NEW'
									};
									dynamodb.updateItem(params4, function (err4, data4) {
										if (err4) {
											console.log('/addViewers: Error 4');
											console.log(err4);
											res.send({success: false});
											return;
										} else {
											res.send({success: true});
											return;
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

app.post('/search', function (req, res) {
	var username = req.session.username;
	var query = req.body.query;

	// TODO: Search
	var command = 'java -jar ./jar/search.jar ' + username + ' "' + query + '"';
	cmd.get(command, function () {
		var queryResult = JSON.parse(fs.readFileSync('./jar/querry.json', 'utf8'));
		res.send({success: true, tree: queryResult});
		return;
	});

	// var x = {
	//   "nodes": [
	//     {
	//       "id": "n0",
	//       "label": "A node",
	//       "x": 0,
	//       "y": 0,
	//       "size": 3
	//     },
	//     {
	//       "id": "n1",
	//       "label": "Another node",
	//       "x": 3,
	//       "y": 1,
	//       "size": 2
	//     },
	//     {
	//       "id": "n2",
	//       "label": "And a last one",
	//       "x": 1,
	//       "y": 3,
	//       "size": 1
	//     },
	//     {
	//       "id": "n3",
	//       "label": "blah",
	//       "x": 0,
	//       "y": 6,
	//       "size": 3
	//     }
	//   ],
	//   "edges": [
	//     {
	//       "id": "e0",
	//       "source": "n0",
	//       "target": "n1"
	//     },
	//     {
	//       "id": "e1",
	//       "source": "n1",
	//       "target": "n2"
	//     },
	//     {
	//       "id": "e2",
	//       "source": "n2",
	//       "target": "n0"
	//     }
	//   ]
	// };

	// res.send({success: true, tree: x});
	// return;
});

app.post('/uploadfile', function (req, res) {
	var username = req.session.username;
	var filename = req.body.filename;
	var filetype = req.body.filetype;
	uploadToS3(req.file, filename, filetype, function (url) {
		if (url == null) {
			console.log('/uploadfile: Error 1');
		} else {
			console.log(url);
			var k = shortid.generate();
			var params2 = {
				Item : {
					id : {
						S: k
					},
					type : {
						S: filetype
					},
					name : {
						S: filename
					},
					url : {
						S : url
					},
					owner : {
						S : username
					},
					isPub : {
						N : '0'
					},
					viewers : {
						SS : [
							username
						]
					}
				},
				TableName : 'dataitems'
			};
			dynamodb.putItem(params2, function (err2, data2) {
				if (err2) {
					console.log('/uploadfile: Error 2');
					console.log(err2);
				} else {
					var params3 = {
						Key : {
							username : {
								S : username
							}
						},
						TableName : 'users'
					};
					dynamodb.getItem(params3, function (err3, data3) {
						if (err3) {
							console.log('/uploadfile: Error 3');
							console.log(err3);
						} else {
							var l = [];
							if (data3.Item.dataitems) l = data3.Item.dataitems.SS;
							l.push(k);
							var params4 = {
								Key : {
									username : {
										S: username
									}
								},
								TableName : 'users',
								UpdateExpression : 'SET dataitems = :c',
								ExpressionAttributeValues : {
									':c' : { SS: l }
								},
								ReturnValues : "UPDATED_NEW"
							};

							dynamodb.updateItem(params4, function (err4, data4) {
								if (err4) {
									console.log('/uploadfile: Error 4');
									console.log(err4);
								} else {
									// TODO: Extract file
									// var command = 'java -jar extract.jar ' + k;
									// cmd.run(command);
									res.redirect('/home');
									return;
								}
							});
						}
					});
				}
			});
		}
	});
});

var uploadToS3 = function (file, filename, type, callback) {
  var file_suffix = uuid.v1();
  var s3 = require('s3');
  var client = s3.createClient({
    maxAsyncS3: 20,     // this is the default 
    s3RetryCount: 3,    // this is the default 
    s3RetryDelay: 1000, // this is the default 
    multipartUploadThreshold: 20971520, // this is the default (20 MB) 
    multipartUploadSize: 15728640, // this is the default (15 MB) 
    s3Options: {
      accessKeyId: awsObj.accessKeyId,
      secretAccessKey: awsObj.secretAccessKey
      // any other options are passed to new AWS.S3() 
      // See: http://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/Config.html#constructor-property 
    },
  });

  var file_ext = '';

  if (file.mimetype === 'application/pdf') {
    file_ext = '.pdf';
  } else if (file.mimetype === 'application/json') {
  	file_ext = '.json';
  } else if (file.mimetype === 'application/xml' || file.mimetype === 'text/xml') {
  	file_ext = '.xml';
  } else {
    res.send({success: false, msg: 'Please upload only .pdf, .json, or .xml files!'});
  }

  var params = {
    localFile: file.path,     
    s3Params: {
      Bucket: 'dlms',
      Key: 'uploads/' + file_suffix + '/' + filename + file_ext
      // other options supported by putObject, except Body and ContentLength. 
      // See: http://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/S3.html#putObject-property 
    },
    defaultContentType: file.mimetype
  };

  var uploader = client.uploadFile(params);
  uploader.on('error', function(err) {
    console.error('unable to upload:', err.stack);
  });
  uploader.on('progress', function() {
    console.log("progress", uploader.progressMd5Amount,
              uploader.progressAmount, uploader.progressTotal);
  });
  uploader.on('end', function() {
    console.log('done uploading');
    var image_url = 'https://s3.amazonaws.com/dlms/uploads/' + file_suffix + '/' + filename + file_ext;
    callback(image_url);
  });
};

/////////////////

var server = app.listen(8080, function () {
  var host = server.address().address;
  var port = server.address().port;

  console.log('App listening at http://localhost:%s', port);
});
