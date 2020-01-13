// GLOBAL VARIABLES
const form = document.getElementById('bookForm');
const results = document.getElementById('results');
const baseURL = '/api/books/';

const render = (books) => {
  results.innerHTML = '';
  form.children[0] = '';
  form.children[1] = '';
  form.children[2] = '';
  form.children[3] = '';
  form.children[4] = '';
  form.children[5] = '';

  const book = books.forEach(book => {
    results.insertAdjacentHTML('afterbegin', `
               
                
				<div style="padding: 40px">
					<p class="col-sm-4">Title : <strong>${book.title}</strong></p>
					<p class="col-sm-4">Author: <strong>${book.author}</strong></p>
					<p class="col-sm-4">Release Date: <strong>${book.releaseDate}</strong></p>
					<p class="col-sm-4">Genre: <strong>${book.genre}</strong></p>
					<p class="col-sm-4">Rating: <strong>${book.rating}</strong></p>
					<p class="col-sm-4">Language: <strong>${book.language}</strong></p>
					<button class="btn btn-danger" id="${book._id}">Delete</button>
					<button class="btn btn-success" id="${book._id}">Edit</button>
				</div>
      `)
  })
};

// GET ALL Books
const getBooks = () => {
  fetch(baseURL)
    .then(res => res.json())
    .then(books => render(books))
    .catch(err => console.log(err));
};

getBooks();

// ADD NEW Book
const handleSubmit = (event) => {
  event.preventDefault();
  console.log('Form submitted!');
  const title = document.getElementById('title').value;
  const author = document.getElementById('author').value;
  const releaseDate = document.getElementById('releaseDate').value;
  const genre = document.getElementById('genre').value;
  const rating = document.getElementById('rating').value;
  const language = document.getElementById('language').value;
  const data = {
    title: title, author: author, releaseDate: releaseDate,
    genre: genre, rating: rating, language: language
  };
  console.log(data);

  fetch(baseURL, {
    method: 'post',
    headers: {
      'Content-Type': 'application/json; charset=utf-8',
    },
    body: JSON.stringify(data),
  }).then(res => res.json(res))
    .then(data => getBooks())
    .catch(err => console.log(err));
};


// DELETE Book
const handleEditDelete = (event) => {
  if (event.target.innerText === 'Delete') {
    console.log(event.target.id);
    console.log(baseURL + event.target.id);
    fetch(baseURL + event.target.id, {
      method: 'delete',
    })
      .then(() => getBooks())
      .catch(err => console.log(err));
  } else if (event.target.innerText === 'Edit') {
    console.log('Edit Clicked!');
    const parent = event.target.parentNode;
    const title = parent.children[0].children[0].innerText;
    const author = parent.children[1].children[0].innerText;
    const releaseDate = parent.children[2].children[0].innerText;
    const genre = parent.children[3].children[0].innerText;
    const rating = parent.children[4].children[0].innerText;
    const language = parent.children[5].children[0].innerText;
    const bookId = parent.children[6].id;

    console.log(parent.children[6].id);

    parent.insertAdjacentHTML('beforeend', `
				<span id="editBook">
					<input id="editTitle" name="title" type="text" value="${title}" />
					<input id="editAuthor" name="author" type="text" value="${author}" />
					<input id="editReleaseDate" name="releaseDate" type="text" value="${releaseDate}" />
					<input id="editGenre" name="genre" type="text" value="${genre}" />
					<input id="editRating" name="rating" type="text" value="${rating}" />
					<input id="editLanguage" name="language" type="text" value="${language}" />
					<button id="editCancel">Cancel</button>
					<button id="editSubmit" data-id="${bookId}">Submit</button>
				</span>
    `);
  } else if (event.target.id === 'editCancel') {
    const form = document.getElementById('editBook');
    form.remove();
  } else if (event.target.id === 'editSubmit') {
    let bookId = event.target.getAttribute('data-id');
    console.log(bookId);
    const newTitle = document.getElementById('editTitle').value;
    const newAuthor = document.getElementById('editAuthor').value;
    const newReleaseDate = document.getElementById('editReleaseDate').value;
    const newGenre = document.getElementById('editGenre').value;
    const newRating = document.getElementById('editRating').value;
    const newLanguage = document.getElementById('editLanguage').value;
    const data = {
      title: newTitle, author: newAuthor, releaseDate: newReleaseDate,
      genre: newGenre, rating: newRating, language: newLanguage
    };
    if (newTitle.length !== 0 && newAuthor.length !== 0
      && newReleaseDate.length !== 0 && newGenre.length !== 0
      && newRating.length !== 0 && newLanguage.length !== 0) {
      fetch(baseURL + bookId, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json; charset=utf-8',
        },
        body: JSON.stringify(data),
      })
        .then(() => getBooks());
    }
    else {
      window.alert('Some fields are empty!!');
    }
  }
};

// EVENT LISTENERS
form.addEventListener('submit', handleSubmit);
results.addEventListener('click', handleEditDelete);
