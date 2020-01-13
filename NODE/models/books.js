const mongoose = require('mongoose'),
 Schema = mongoose.Schema;
 mongoose.set('useFindAndModify', false);

 const BooksSchema = new Schema({
      title: String, // title of the book
      author: String, // name of the first author
      releaseDate:String, // release date of the book
      genre: String, //like fiction or non fiction
      rating: String, // rating if you have read it out of 5
      language: String // language in which the book is released
 });

 const BooksModel = mongoose.model('books', BooksSchema);

 module.exports = BooksModel;
