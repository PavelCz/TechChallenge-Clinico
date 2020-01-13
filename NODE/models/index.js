const mongoose = require("mongoose");
mongoose.connect( process.env.MONGODB_URI ||
    "mongodb://localhost:27017/booksData",{ useUnifiedTopology: true, useNewUrlParser: true });

module.exports.books = require("./books.js");