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

// In-memory Database for CRUD Demo
let feedbacks = [
    { id: 1, name: "Alice", email: "alice@example.com", comment: "Great planner app! Very helpful.", rating: 5 },
    { id: 2, name: "Bob", email: "bob@example.com", comment: "Would love to see more features.", rating: 4 }
];

// --- NODE.JS BACKEND CRUD API ---

// 1. GET: Read all feedbacks
app.get('/api/feedback', (req, res) => {
    try {
        res.status(200).json({ status: "success", data: feedbacks });
    } catch (error) {
        res.status(500).json({ status: "error", message: "Failed to fetch feedback", error: error.message });
    }
});

// 2. POST: Create feedback (with validation)
app.post('/api/feedback', (req, res) => {
    try {
        const { name, email, comment, rating } = req.body;

        // Validations
        if (!name || name.trim() === "") {
            return res.status(400).json({ status: "error", message: "Name is required" });
        }
        if (!email || !email.includes("@")) {
            return res.status(400).json({ status: "error", message: "A valid email is required" });
        }
        if (!comment || comment.trim() === "") {
            return res.status(400).json({ status: "error", message: "Comment is required" });
        }
        if (rating === undefined || rating < 1 || rating > 5) {
            return res.status(400).json({ status: "error", message: "Rating must be between 1 and 5" });
        }

        const newFeedback = {
            id: feedbacks.length ? feedbacks[feedbacks.length - 1].id + 1 : 1,
            name: name.trim(),
            email: email.trim(),
            comment: comment.trim(),
            rating: Number(rating)
        };

        feedbacks.push(newFeedback);
        res.status(201).json({ status: "success", message: "Feedback submitted successfully", data: newFeedback });
    } catch (error) {
        res.status(500).json({ status: "error", message: "Failed to submit feedback", error: error.message });
    }
});

// 3. PUT: Update feedback
app.put('/api/feedback/:id', (req, res) => {
    try {
        const id = parseInt(req.params.id);
        const { name, email, comment, rating } = req.body;

        const index = feedbacks.findIndex(f => f.id === id);
        if (index === -1) {
            return res.status(404).json({ status: "error", message: "Feedback not found" });
        }

        // Validations
        if (name && name.trim() === "") return res.status(400).json({ status: "error", message: "Name cannot be empty" });
        if (email && !email.includes("@")) return res.status(400).json({ status: "error", message: "Email is invalid" });
        if (comment && comment.trim() === "") return res.status(400).json({ status: "error", message: "Comment cannot be empty" });
        if (rating !== undefined && (rating < 1 || rating > 5)) return res.status(400).json({ status: "error", message: "Rating must be between 1 and 5" });

        feedbacks[index] = {
            ...feedbacks[index],
            ...(name && { name: name.trim() }),
            ...(email && { email: email.trim() }),
            ...(comment && { comment: comment.trim() }),
            ...(rating !== undefined && { rating: Number(rating) })
        };

        res.status(200).json({ status: "success", message: "Feedback updated successfully", data: feedbacks[index] });
    } catch (error) {
        res.status(500).json({ status: "error", message: "Failed to update feedback", error: error.message });
    }
});

// 4. DELETE: Delete feedback
app.delete('/api/feedback/:id', (req, res) => {
    try {
        const id = parseInt(req.params.id);
        const index = feedbacks.findIndex(f => f.id === id);

        if (index === -1) {
            return res.status(404).json({ status: "error", message: "Feedback not found" });
        }

        const deleted = feedbacks.splice(index, 1);
        res.status(200).json({ status: "success", message: "Feedback deleted successfully", data: deleted[0] });
    } catch (error) {
        res.status(500).json({ status: "error", message: "Failed to delete feedback", error: error.message });
    }
});

app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public/login.html'));
});

const PORT = 3000;

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});