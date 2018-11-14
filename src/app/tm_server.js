//express_demo.js 文件

// node xxx.js  ////set up serviece


var express = require('express');
var app = express();

const https = require('https');
var cors = require('cors');

var app = express();

app.use(cors());

app.use(function (req, res, next) {
    console.log(`${req.method} request for '${req.url}'`);
    next();
});

app.use(express.static("./public"));

const googleApiKey = 'AIzaSyB-UV6eMbgrMrfaZ4x4m0w1-stENc6fO-8';
const googleCustomSearchKey = "AIzaSyAbsgTHmRL_pKbuiv4F8CUH_xVDMuQZn3E";
const ticketmasterkey = "uy2mh8pBbjq91kGF2qse3pSUWbw4oZAS";
// const ticketmasterkey = "1JSKzidqrh8KAaXLMbqr3ysmUd3r2Uze";
const songkickkey = "BiAIX2ZaBEfxncTe";
const cxKey = "009381045914159416803:dbyqgxoghzk";


const catergory2Id = {
    "all": "",
    "music": "KZFzniwnSyZfZ7v7nJ", 
    "sports":"KZFzniwnSyZfZ7v7nE", 
    "arttheatre":"KZFzniwnSyZfZ7v7na", 
    "film":"KZFzniwnSyZfZ7v7nn", 
    "miscellanenous":"KZFzniwnSyZfZ7v7n1"
  };

app.get('/', function (req, res) {
   res.send('Node.js Backend | Hello World');
})


app.get("/location", function (req, res) {
    let lat, lng;
    let location = req.query.keyword;
    let locationUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + location + "&key=" + googleApiKey;
    let request = https.get(locationUrl, function(response) {
        let responseBody = "";
        response.on("data", function(data) {
            responseBody +=data;
        });
        response.on("end", function (err) {
            if (err) {
                console.error(err);
                throw err;
            }
            res.send(responseBody);
        });
    });
    request.on("error", function (err) {
        console.error(err);
        throw err;
    });
    request.end();
});

var geohash = require('ngeohash');

app.get("/events", function (req, res) {
    let reqInfo = req.query;
    // console.log(reqInfo);
    let hscode = geohash.encode(reqInfo.lat, reqInfo.lng)
    // console.log(hscode);
    let nearbysearch_url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=" + ticketmasterkey
        + "&radius=" + reqInfo.distance
        + "&keyword=" + reqInfo.keyword
        + "&unit=miles"
        + "&geoPoint=" + hscode;

    // console.log(reqInfo.category);
    if (reqInfo.category != 'all') {
        catergoryId = catergory2Id[reqInfo.category];
        nearbysearch_url = nearbysearch_url + "&segmentId=" + catergoryId;
    }

    // console.log(nearbysearch_url);

    let request = https.get(nearbysearch_url, function(response) {
        let responseBody = "";
        response.on("data", function(data) {
            
            responseBody +=data;
        });
        response.on("end", function (err) {
            if (err) {
                console.error(err);
                throw err;
            }
            console.log(JSON.parse(responseBody))
            res.send(responseBody);
        });
    });
    request.on("error", function (err) {
        console.error(err);
        throw err;
    });
    request.end();
});


app.get("/eventdetails", function(req, res) {
    let detailsUrl = "https://app.ticketmaster.com/discovery/v2/events/" + req.query.event_id
        + ".json?apikey=" + ticketmasterkey;
    let request = https.get(detailsUrl, function(response) {
        var responseBody = "";
        response.on("data", function(data) {
            responseBody +=data;
        });
        response.on("end", function (err) {
            if (err) {
                console.error(err);
                throw err;
            }
            res.send(responseBody);
        });
    });
    request.on("error", function (err) {
        console.error(err);
        throw err;
    });
    request.end();
});



const spotifyClientId = "fbadba213da44caa8797cc5305b9be9e";
const spotifyClientSecret = "5b1c94cb533947bf809d24629e98f61d";
var SpotifyWebApi = require('spotify-web-api-node');

var spotifyApi = new SpotifyWebApi({
    clientId: spotifyClientId,
    clientSecret: spotifyClientSecret,
  });

app.get("/artistdetails", function(req, res) {
    let keyword = req.query.keyword;
    spotifyApi.searchArtists(keyword).then(function(data) {
        //console.log('Search artists by $KEYWORD$', data.body);
        res.send(data.body);

    }, function(err) {
        //console.error(err);
        //console.log("Init Connection Error, Try to refresh access token and try it again");

        spotifyApi.clientCredentialsGrant().then(
            function(data2) {
              // console.log('The access token expires in ' + data2.body['expires_in']);
              // console.log('The access token is ' + data2.body['access_token']);
              // Save the access token so that it's used in future calls
              spotifyApi.setAccessToken(data2.body['access_token']);
              
              spotifyApi.searchArtists(keyword).then(function(data3) {
                    console.log('Search artists by $KEYWORD$', data3.body);
                    res.send(data3.body);
        
                    }, function(err) {
                    console.log("Connection Failed, please debug");
                    console.error(err);
                });
            },
            function(err) {
              console.log('Wrong when retrieving an access token', err);
            }
        );

    });

});


app.get("/photos", function(req, res) {
    let reqInfo = req.query;
    let keyword = reqInfo.keyword;
    keyword = keyword.split("+").join("+");
    console.log(keyword);

    let photoURL = "https://www.googleapis.com/customsearch/v1?q=" + keyword 
                + "&cx=" + cxKey
                // + "&imgType=photo"
                // + "&imgSize=huge" 
                + "&num=8"
                + "&searchType=image"
                + "&key=" + googleCustomSearchKey;

    console.log(photoURL);
    let request = https.get(photoURL, function(response) {
        let responseBody = "";
        response.on("data", function(data) {
            responseBody +=data;
        });
        response.on("end", function (err) {
            if (err) {
                console.error(err);
                throw err;
            }
            res.send(responseBody);
        });
    });
    request.on("error", function (err) {
        console.error(err);
        throw err;
    });
    request.end();
});


app.get("/venues", function(req, res) {
    let reqInfo = req.query;
    let venuename = reqInfo.venuename;

    let venueURL = "https://api.songkick.com/api/3.0/search/venues.json?query=" + venuename 
                + "&apikey=" + songkickkey;

    console.log(venueURL);
    let request = https.get(venueURL, function(response) {
        let responseBody = "";
        response.on("data", function(data) {
            responseBody +=data;
        });
        response.on("end", function (err) {
            if (err) {
                console.error(err);
                throw err;
            }
            res.send(responseBody);
        });
    });
    request.on("error", function (err) {
        console.error(err);
        throw err;
    });
    request.end();
});


app.get("/venuesdetail", function(req, res) {
    let reqInfo = req.query;
    let query_id = reqInfo.id;

    let venuedetailURL = "https://api.songkick.com/api/3.0/venues/" + query_id + "+/calendar.json?apikey=" + songkickkey;

    console.log(venuedetailURL);
    let request = https.get(venuedetailURL, function(response) {
        let responseBody = "";
        response.on("data", function(data) {
            responseBody +=data;
        });
        response.on("end", function (err) {
            if (err) {
                console.error(err);
                throw err;
            }
            res.send(responseBody);
        });
    });
    request.on("error", function (err) {
        console.error(err);
        throw err;
    });
    request.end();
});


app.set('PORT', process.env.PORT || 3000);
app.listen(app.get('PORT'));
console.log('Server is running');
