const express = require('express');
const sqlite3 = require('sqlite3').verbose();
const bodyParser = require('body-parser');
const fs = require('fs');
const path = require('path');
const jwt = require('jsonwebtoken');
const swaggerUi = require('swagger-ui-express');
const swaggerJsDoc = require('swagger-jsdoc');

const app = express();
const port = 3000;
const dbFilePath = path.join(__dirname, 'database.sqlite');
const secretKey = 'your_secret_key'; 

// Middleware
app.use(bodyParser.json());

// Swagger setup
const swaggerOptions = {
    swaggerDefinition: {
        openapi: '3.0.0',
        info: {
            title: 'CRUD API Documentation',
            version: '1.0.0',
            description: 'API documentation for the CRUD operations',
        },
    },
    apis: [__filename],
};
const swaggerDocs = swaggerJsDoc(swaggerOptions);
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocs));

// Initialize SQLite database
const dbExists = fs.existsSync(dbFilePath);
if (dbExists) {
    console.log('Database file found.');
}

const db = new sqlite3.Database(dbFilePath, (err) => {
    if (err) {
        console.error('Error opening database:', err.message);
    } else {
        console.log('Connected to SQLite database.');

        if (!dbExists) {
            // Create tables
            db.serialize(() => {
                db.run(`CREATE TABLE Users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    total_score INTEGER DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )`);

                db.run(`CREATE TABLE Words (
                    word_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    word_tr TEXT NOT NULL,
                    word_en TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )`);

                db.run(`CREATE TABLE UserWords (
                    user_word_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    word_id INTEGER NOT NULL,
                    score INTEGER DEFAULT 0,
                    definition TEXT NOT NULL,
                    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE(user_id, word_id),
                    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
                    FOREIGN KEY (word_id) REFERENCES Words(word_id) ON DELETE CASCADE
                )`);
            });
        }
    }
});

// JWT Middleware
function authenticateToken(req, res, next) {
    const token = req.headers['authorization'];
    if (!token) return res.sendStatus(401);

    jwt.verify(token.split(' ')[1], secretKey, (err, user) => {
        if (err) return res.sendStatus(403);
        req.user = user;
        next();
    });
}

/**
 * @swagger
 * /register:
 *   post:
 *     summary: Register a new user
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               username:
 *                 type: string
 *               email:
 *                 type: string
 *               password:
 *                 type: string
 *     responses:
 *       201:
 *         description: User registered
 */
app.post('/register', (req, res) => {
    const { username, email, password } = req.body;
    db.run(`INSERT INTO Users (username, email, password) VALUES (?, ?, ?)`, [username, email, password], function (err) {
        if (err) {
            res.status(400).json({ error: err.message });
        } else {
            res.status(201).json({ user_id: this.lastID });
        }
    });
});

/**
 * @swagger
 * /login:
 *   post:
 *     summary: Log in a user
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               email:
 *                 type: string
 *               password:
 *                 type: string
 *     responses:
 *       200:
 *         description: JWT token
 */
app.post('/login', (req, res) => {
    const { email, password } = req.body;
    db.get(`SELECT user_id FROM Users WHERE email = ? AND password = ?`, [email, password], (err, row) => {
        if (err || !row) {
            return res.status(401).json({ error: 'Invalid credentials' });
        }
        const token = jwt.sign({ user_id: row.user_id }, secretKey, { expiresIn: '1h' });
        res.json({ token });
    });
});

/**
 * @swagger
 * /words:
 *   post:
 *     summary: Add a new word
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               word_tr:
 *                 type: string
 *               word_en:
 *                 type: string
 *     responses:
 *       201:
 *         description: Word added
 */
app.post('/words', authenticateToken, (req, res) => {
    const { word_tr, word_en } = req.body;
    db.run(`INSERT INTO Words (word_tr, word_en) VALUES (?, ?)`, [word_tr, word_en], function (err) {
        if (err) {
            res.status(400).json({ error: err.message });
        } else {
            res.status(201).json({ word_id: this.lastID });
        }
    });
});

/**
 * @swagger
 * /userwords:
 *   post:
 *     summary: Associate a word with the user
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               word_id:
 *                 type: integer
 *               score:
 *                 type: integer
 *               definition:
 *                 type: string
 *     responses:
 *       201:
 *         description: User word added
 */
app.post('/userwords', authenticateToken, (req, res) => {
    const { word_id, score, definition } = req.body;
    const userId = req.user.user_id;
    db.run(`INSERT INTO UserWords (user_id, word_id, score, definition) VALUES (?, ?, ?, ?)`, [userId, word_id, score, definition], function (err) {
        if (err) {
            res.status(400).json({ error: err.message });
        } else {
            res.status(201).json({ user_word_id: this.lastID });
        }
    });
});

/**
 * @swagger
 * /words:
 *   get:
 *     summary: Get all words
 *     responses:
 *       200:
 *         description: List of words
 */
app.get('/words', (req, res) => {
    db.all(`SELECT * FROM Words`, [], (err, rows) => {
        if (err) {
            res.status(400).json({ error: err.message });
        } else {
            res.json(rows);
        }
    });
});

/**
 * @swagger
 * /words/{id}:
 *   put:
 *     summary: Update a word
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         schema:
 *           type: integer
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               word_tr:
 *                 type: string
 *               word_en:
 *                 type: string
 *     responses:
 *       200:
 *         description: Word updated
 */
app.put('/words/:id', (req, res) => {
    const { id } = req.params;
    const { word_tr, word_en } = req.body;
    db.run(`UPDATE Words SET word_tr = ?, word_en = ? WHERE word_id = ?`, [word_tr, word_en, id], function (err) {
        if (err) {
            res.status(400).json({ error: err.message });
        } else {
            res.json({ updated: this.changes });
        }
    });
});

/**
 * @swagger
 * /words/{id}:
 *   delete:
 *     summary: Delete a word
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         schema:
 *           type: integer
 *     responses:
 *       200:
 *         description: Word deleted
 */
app.delete('/words/:id', (req, res) => {
    const { id } = req.params;
    db.run(`DELETE FROM Words WHERE word_id = ?`, [id], function (err) {
        if (err) {
            res.status(400).json({ error: err.message });
        } else {
            res.json({ deleted: this.changes });
        }
    });
});

/**
 * @swagger
 * /userwords:
 *   get:
 *     summary: Get user-specific words
 *     responses:
 *       200:
 *         description: List of user-specific words
 */
app.get('/userwords', authenticateToken, (req, res) => {
    const userId = req.user.user_id;
    db.all(`SELECT * FROM UserWords WHERE user_id = ?`, [userId], (err, rows) => {
        if (err) {
            res.status(400).json({ error: err.message });
        } else {
            res.json(rows);
        }
    });
});

/**
 * @swagger
 * /userwords/{id}:
 *   put:
 *     summary: Update a user word
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         schema:
 *           type: integer
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               score:
 *                 type: integer
 *               definition:
 *                 type: string
 *     responses:
 *       200:
 *         description: User word updated
 */
app.put('/userwords/:id', authenticateToken, (req, res) => {
    const { id } = req.params;
    const { score, definition } = req.body;
    const userId = req.user.user_id;
    db.run(`UPDATE UserWords SET score = ?, definition = ? WHERE user_word_id = ? AND user_id = ?`, [score, definition, id, userId], function (err) {
        if (err) {
            res.status(400).json({ error: err.message });
        } else {
            res.json({ updated: this.changes });
        }
    });
});

/**
 * @swagger
 * /userwords/{id}:
 *   delete:
 *     summary: Delete a user word
 *     parameters:
 *       - name: id
 *         in: path
 *         required: true
 *         schema:
 *           type: integer
 *     responses:
 *       200:
 *         description: User word deleted
 */
app.delete('/userwords/:id', authenticateToken, (req, res) => {
    const { id } = req.params;
    const userId = req.user.user_id;
    db.run(`DELETE FROM UserWords WHERE user_word_id = ? AND user_id = ?`, [id, userId], function (err) {
        if (err) {
            res.status(400).json({ error: err.message });
        } else {
            res.json({ deleted: this.changes });
        }
    });
});

/**
 * @swagger
 * /translate:
 *   post:
 *     summary: Translate text using LibreTranslate
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               q:
 *                 type: string
 *               source:
 *                 type: string
 *               target:
 *                 type: string
 *     responses:
 *       200:
 *         description: Translated text
 */
app.post('/translate', async (req, res) => {
    const { q, source, target } = req.body;

    try {
        const response = await fetch("http://libretranslate:5000/translate", {
            method: "POST",
            headers: { "Content-Type": "application/json", "accept": "application/json" },
            body: JSON.stringify({
                q,
                source: source || 'auto',
                target
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result = await response.json();
        res.json(result);
    } catch (error) {
        console.error('Error translating text:', error);
        res.status(500).json({ error: 'Translation service error', details: error.message });
    }
});


// -------------------------------------------------------


// Start server
app.listen(port, () => {
    console.log(`Server running on http://localhost:${port}`);
    console.log(`Swagger Docs available at http://localhost:${port}/api-docs`);
});
