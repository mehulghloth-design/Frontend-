const express = require('express');
const path = require('path');

const app = express();

// Middleware to parse JSON bodies
app.use(express.json());

// Enable CORS manually (no extra package needed)
app.use((req, res, next) => {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
    res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    if (req.method === 'OPTIONS') {
        return res.sendStatus(200);
    }
    next();
});

app.use(express.static(path.join(__dirname, 'public')));

// Use the separate feedback routes
const feedbackRouter = require('./routes/feedback');
app.use('/api/feedback', feedbackRouter);

app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public/login.html'));
});

const PORT = 3000;

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});