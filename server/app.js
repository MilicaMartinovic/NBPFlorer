const express = require('express');
const path = require('path');
const logger = require('morgan');
const bodyParser = require('body-parser');
const mongoose = require('mongoose');
const multer = require('multer');
const storage = multer.diskStorage({
	destination: function(req, file, cb) {
		 cb(null, './uploads');
	},
	filename: function(req, file, cb) {
		cb(null, file.originalname);
	}
	
})
const upload = multer({storage: storage});



mongoose.connect('mongodb://localhost/playyground')
.then(()=> {
	console.log('connected to mongodb')
})

let db = mongoose.connection;

//------------------------------------------------------------------------------------------USER

const userSchema = new mongoose.Schema({
	username: String,
	password: String,
	fullname: String,
	email: String,
	lokacija_slike: String,
	motherland: String,
	bio: String,
	slike_biljaka: [ { type: mongoose.Schema.Types.ObjectId, ref: 'Picture' } ],
	comments: { type: mongoose.Schema.Types.ObjectId, ref: 'Comment'}
});

const User = mongoose.model('User', userSchema);

/*async function createUser() {
	const user = new User({
		username: 'rista',
		password: 'glista',
		fullname: 'Nikola Ristic',
		email: 'rista@gmail.com',
		lokacija_slike: 'lokacija',
		motherland: 'Srbija',
		bio: 'student, zna da vozi kola',
		biljke: [ '5c4097d539fe6d0a28204171' ]
	});

	const result = await user.save();
	console.log(result);
}

createUser();*/

async function getUser(uname, passw) {
	const user = await User.findOne({ username: uname, password: passw });

	return user;
}

async function getUser(uname) {
	const user = await User.findOne({ username: uname })
	.populate('slike_biljaka');

	return user;
}

//getUser('comi', 'comi');

//-----------------------------------------------------------------------------------------

//-----------------------------------------------------------------------------------------PLANT

const plantSchema = new mongoose.Schema({
	latinski_naziv: String,
	username: String,
	lokacija_slike: String,
	tags: [ String ],
	slike: [ { type: mongoose.Schema.Types.ObjectId, ref: 'Picture' } ],
	datum_dodavanja: String,
	name_suggestions: [String],
	comments: { type: mongoose.Schema.Types.ObjectId, ref: 'Comment' }
});

const Plant = mongoose.model('Plant', plantSchema);

/*async function createPlant() {
	const plant = new Plant({
		latinski_naziv: 'maslacak lat',
		username: 'comi',
		lokacija_slike: 'asd/mul.jpg',
		tags: [ 'maslacak', 'masla', 'cak' ],
		slike: [ '5c4095f18a7b5e0ed8702924', '5c4096256bffea0f28586ae1' ]
		datum_dodavanja : new Date().getTime().toString()
	});

	const result = await plant.save();
	console.log(result);
}

createPlant();*/

async function getPlants() {
	const plants = await Plant.find().sort({datum_dodavanja: -1});//datum dodavanja desc

	return plants;
}

/*async function getPlant(lat_naziv) {
	const plant = await Plant.findOne({ latinski_naziv: lat_naziv })
		.populate('slike');

	console.log(plant);
}

getPlant('maslacak lat');*/

/*async function getUser(uname) {
	const user = await User.findOne({ username: uname });

	return user;
}

//getUser('comi', 'comi');*/

//-----------------------------------------------------------------------------------------

//-----------------------------------------------------------------------------------------PICTURES

const pictureSchema = new mongoose.Schema({
	lokacija_slike: String,
	longitude: String,
	latitude: String,
	biljka: String,
	datum_dodavanja: String
});

const Picture = mongoose.model('Picture', pictureSchema);

const commentSchema = new mongoose.Schema({
	komentar: String,
	datum_dodavanja: String,
	parent: { type: mongoose.Schema.Types.ObjectId, ref: 'Comment' },
	username: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
	plant: { type: mongoose.Schema.Types.ObjectId, ref: 'Plant' }
});

const Comment = mongoose.model('Comment', commentSchema);

async function createComment() {
	const comm = new Comment( {
		komentar: "Dobra biljka",
		datum_dodavanja: "5.6.1996",
		parent: null,
		username: null,
		plant: null
	})
	const result = await comm.save();
}

createComment();
/*async function createPicture() {
	const picture = new Picture({
		lokacija_slike: 'asd/mul.jpg',
		longitude: '58',
		latitude: '43'
	});

	const result = await picture.save();
	console.log(result);
}

createPicture();

async function getPicture() {
	const pictures = await User.find();

	return pictures;
}

//getPictures();*/

//-----------------------------------------------------------------------------------------

const app = express();

app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, 'public')));

app.post('/login', (req, res) => {
	let username = req.body.username;
	let password = req.body.password

	getUser(username, password).then(user => {

		if(user != null) {
			res.send("success")
		}
		else {
			res.send("")
		}
	});
})

app.post('/account', (req, res) => {
	let username = req.body.username;
	//console.log(username);

	getUser(username).then(user => {
	//	console.log(user);

		if(user != null) {
			res.send(user);
		}
		else {
			res.send(null);
		}
	});
})

app.get('/plants', (req, res) => {
	getPlants().then(plants => {
		res.send(plants);
	});
})

app.post('/upload', upload.single("uploaded_file"), async function(req, res, next){

	let latinName = req.query.latinski_naziv;
	let lon = req.query.lon;
	let lat = req.query.lat;
	let username = req.query.username;
	let country = req.query.tag;
	let tags = [];
	tags.push(latinName);
	tags.push(country);

	findWithLatinName(latinName)
		.then(async plant_id => {
			if(plant_id) {
				//postoji
				insertPicture(req.file.originalname, lon, lat, latinName)
					.then(id_slike => {
						updatePlant(latinName, username, req.file.originalname, tags, id_slike)
						updateUser(username, id_slike)
						.then((result) => {
							res.send({message: "ok"})
						})
						.catch(err => {
							res.send({message: err})
						})
					})
			}
			else {
				insertPicture(req.file.originalname, lon, lat, latinName)
					.then(id_slike => {
						insertPlant(latinName, username, req.file.originalname, tags, id_slike)
						updateUser(username, id_slike)
						.then(result => {
							res.send({message:"ok"})
						})
						.catch(err => {
							res.send({message: err})
						})
					})
			}
		})
});

app.post("/plantLocations", (req, res) => {
	let id = req.body.id_biljke;
	Plant.findById(id).then(plant => {
		let latin = plant.latinski_naziv;
		Picture.find({biljka: latin}).then(elements => {
			let locations = [];
			elements.forEach(element => {
				locations.push({
					longitude: element.longitude,
					latitude: element.latitude
				})
			})
			res.send(locations);
		})
	})
})

app.get('/plantLocations', (req, res) => {
	let name = req.query.plant_name;
	Plant.find({latinski_naziv: name})
	.populate('slike')
	.then(plants => {
		res.send(plants);
	})
});

app.post('/plantImages', (req, res) => {
	let id = req.body.id_biljke;
	Plant.findById(id).then(plant => {
		let latin = plant.latinski_naziv;
		Picture.find({biljka: latin}).then(elements => {
			let images = [];
			elements.forEach(element => {
				images.push({
					lokacija_slike: element.lokacija_slike
				})
			})
			res.send(images);
		})
	})
}) 

app.post('/plantTags', (req, res) => {
	let id = req.body.id_biljke;
	Plant.findById(id).then(plant => {
		let tags = [];
		plant.tags.forEach(el => {
			tags.push({
				tag: el
			})
		})
		res.send(tags);
	})
})

app.post('/comments', (req, res) => {

})

app.post('/deleteTag', (req, res) => {
	let id = req.body.id_biljke;
	let tag = req.body.tag;

	Plant.findById(id)
		.then(plant => {
			plant.update({$pullAll: {tags: [tag]}}).then(plant => {
				res.send(plant)
			})
			
		})
})

app.post('/addTag', (req, res) => {
	let id = req.body.id_biljke;
	let tag = req.body.tag;

	Plant.findById(id)
		.then(plant => {
			if(plant.tags.includes(tag)) 
				res.json({message: "Cannot add, already exists"})
			else {
				plant.tags.push(tag);
				plant.save().then(result => res.send(result))
			}
		})
})

app.post('/addSuggestion', (req, res) => {
	let id = req.body.id_biljke;
	let suggestion = req.body.suggestion;
	Plant.findById(id)
	.then(async plant => {
		plant.name_suggestions.push(suggestion);
		let mostFrequentName = mode(plant.name_suggestions);

		if(plant.name_suggestions.filter((v) => (v === mostFrequentName)).length > 1) {
			let res = await updatePicturePlant(plant.latinski_naziv, mostFrequentName);
			plant.latinski_naziv = mostFrequentName;
		}
		plant.save().then(result => {
			res.send(result);
		})
	})
	.catch(err => { res.send({message: err})})
})

async function findWithLatinName(latinName) {
	return new Promise((resolve, reject) => {
		Plant.findOne({ latinski_naziv: latinName}, function(err, plant) {
			if(plant) 
				resolve(plant._id)
			else 
				resolve(null);
		})
	})
}

async function updateUser(username, id_slike) {
	return new Promise((resolve, reject) => {
		User.findOne({username: username}, async function(err, user) {
			user.slike_biljaka.push(id_slike);
			let result = await user.save();
			resolve(result);
		})
	})
}

async function insertPicture(lokacija, lon, lat, biljka) {
	return new Promise((resolve, reject) => {
		const picture = new Picture({
			lokacija_slike: lokacija,
			longitude: lon,
			latitude: lat,
			biljka: biljka,
			datum_dodavanja: new Date().getTime().toString()
		})
		picture.save().then(res => {
			resolve(res._id);
		})
	})
}

async function updatePlant(name, username, lokacija, tags, id_slike) {
	Plant.findOne({latinski_naziv: name}, async function(err, plant) {
		tags.forEach(element => {
			if(!plant.tags.includes(element))
				plant.tags.push(element)
		})
		plant.slike.push(id_slike)
		plant.username = username
		plant.lokacija_slike = lokacija;
		plant.datum_dodavanja = new Date().getTime().toString()
		let res = await plant.save();
		return res._id;
	})
}

async function insertPlant(name, username, lokacija, tags, slika) {
	return new Promise((resolve, reject) => {
		let arr = [];
		arr.push(slika);
		const plant = new Plant({
			latinski_naziv: name,
			username: username,
			lokacija_slike: lokacija,
			tags: tags,
			slike: arr,
			datum_dodavanja : new Date().getTime().toString(),
			name_suggestions: new Array()
		});
		plant.save().then(result => {
			resolve(result._id)
		})
	})
}

async function updatePicturePlant(oldName, newName) {
	Picture.find({biljka: oldName})
		.then(result => {
			result.forEach(async pic => {
				pic.biljka = newName;
				let res = await pic.save();
				return res;
			})
		})
}

function mode(array) {
    if(array.length == 0)
        return null;
    var modeMap = {};
    var maxEl = array[0], maxCount = 1;
    for(var i = 0; i < array.length; i++)
    {
        var el = array[i];
        if(modeMap[el] == null)
            modeMap[el] = 1;
        else
            modeMap[el]++;  
        if(modeMap[el] > maxCount)
        {
            maxEl = el;
            maxCount = modeMap[el];
        }
    }
    return maxEl;
}


app.listen(3000);

console.log("Server Started on Port 3000");

module.exports = app;
