document.addEventListener("DOMContentLoaded", () => {
    const API_BASE_URL = "https://springboot-backend-zsqe.onrender.com";

    function getToken() {
        return localStorage.getItem("token");
    }

    function getAuthHeaders(extraHeaders = {}) {
        const headers = { ...extraHeaders };
        const token = getToken();
        if (token) {
            headers.Authorization = `Bearer ${token}`;
        }
        return headers;
    }

    async function readResponse(response) {
        const text = await response.text();
        if (!text) return {};
        try {
            return JSON.parse(text);
        } catch {
            return { message: text };
        }
    }

    // LOGIN
    const loginForm = document.getElementById("loginForm");
    if (loginForm) {
        loginForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const email = document.getElementById("email")?.value.trim() || "";
            const password = document.getElementById("password")?.value.trim() || "";
            const errorDiv = document.getElementById("errorMsg");

            if (errorDiv) errorDiv.innerText = "";

            if (!email || !password) {
                if (errorDiv) errorDiv.innerText = "Please enter email and password";
                return;
            }

            const button = e.target.querySelector("button");
            if (button) {
                button.disabled = true;
                button.textContent = "Logging in...";
            }

            try {
                const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    },
                    body: JSON.stringify({
                        login: email,
                        email: email,
                        username: email,
                        password: password
                    })
                });

                const data = await readResponse(response);

                if (response.ok && data.status === "success") {
                    localStorage.setItem("token", data.token || "");
                    localStorage.setItem("user", JSON.stringify(data.data || {}));
                    sessionStorage.setItem("loggedInUser", JSON.stringify(data.data || {}));
                    sessionStorage.setItem("isLoggedIn", "true");
                    window.location.href = "dashboard.html";
                } else {
                    if (errorDiv) errorDiv.innerText = data.message || `Login failed (${response.status})`;
                }
            } catch (error) {
                console.error("Login error:", error);
                if (errorDiv) {
                    errorDiv.innerText =
                        "Cannot connect to backend/gateway. Make sure it is running on port 8000. Error: " +
                        error.message;
                }
            } finally {
                if (button) {
                    button.disabled = false;
                    button.textContent = "Login";
                }
            }
        });
    }

    // REGISTER
    const registerForm = document.getElementById("registerForm");
    if (registerForm) {
        registerForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            const email = document.getElementById("regEmail")?.value.trim() || "";
            const password = document.getElementById("regPassword")?.value.trim() || "";
            const errorDiv = document.getElementById("errorMsg");

            if (errorDiv) errorDiv.innerText = "";

            if (!email || !password) {
                if (errorDiv) errorDiv.innerText = "Please enter email and password";
                return;
            }

            const button = e.target.querySelector("button");
            if (button) {
                button.disabled = true;
                button.textContent = "Registering...";
            }

            try {
                const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    },
                    body: JSON.stringify({
                        email: email,
                        password: password
                    })
                });

                const data = await readResponse(response);

                if (response.ok && data.status === "success") {
                    alert("Registration successful. Please login.");
                    window.location.href = "login.html";
                } else {
                    if (errorDiv) errorDiv.innerText = data.message || `Registration failed (${response.status})`;
                }
            } catch (error) {
                console.error("Register error:", error);
                if (errorDiv) {
                    errorDiv.innerText =
                        "Cannot connect to backend/gateway. Make sure it is running on port 8000. Error: " +
                        error.message;
                }
            } finally {
                if (button) {
                    button.disabled = false;
                    button.textContent = "Register";
                }
            }
        });
    }

    // SEARCH PAGE SUPPORT
    const courseList = document.getElementById("courseList");
    const searchInput = document.getElementById("searchInput");
    const departmentFilter = document.getElementById("departmentFilter");
    const levelFilter = document.getElementById("levelFilter");

    let allCourses = [];

    function escapeHtml(value) {
        return String(value ?? "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }

    function displayCourses(courses) {
        if (!courseList) return;

        if (!courses || courses.length === 0) {
            courseList.innerHTML = "<p>No matching courses found.</p>";
            return;
        }

        courseList.innerHTML = courses.map(course => `
            <div class="course-card" data-course-id="${escapeHtml(course.id)}">
                <h3>${escapeHtml(course.name)}</h3>
                <p><strong>Code:</strong> ${escapeHtml(course.code)}</p>
                <p><strong>Credits:</strong> ${escapeHtml(course.credits)}</p>
                <p><strong>Department:</strong> ${escapeHtml(course.department)}</p>
                <p><strong>Level:</strong> ${escapeHtml(course.level)}</p>
                <p>${escapeHtml(course.description)}</p>
                <span class="badge">Course ID: ${escapeHtml(course.id)}</span>
                <div class="card-actions">
                    <button class="delete-course" data-id="${escapeHtml(course.id)}">Delete</button>
                </div>
            </div>
        `).join("");

        // Wire delete buttons
        document.querySelectorAll('.delete-course').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                // prevent card click from firing
                e.stopPropagation();
                const id = btn.getAttribute('data-id');
                if (!id) return;
                if (!confirm('Delete course id ' + id + '?')) return;
                try {
                    const response = await fetch(`${API_BASE_URL}/api/courses/${id}`, {
                        method: 'DELETE',
                        headers: getAuthHeaders({ 'Accept': 'application/json' })
                    });
                    const result = await readResponse(response);
                    const statusDiv = document.getElementById('courseStatus');
                    if (response.ok) {
                        if (statusDiv) statusDiv.textContent = 'Course deleted.';
                        fetchCourses();
                    } else {
                        if (statusDiv) statusDiv.textContent = result.message || 'Failed to delete course.';
                    }
                } catch (err) {
                    console.error('Delete course error', err);
                    const statusDiv = document.getElementById('courseStatus');
                    if (statusDiv) statusDiv.textContent = 'Error deleting course.';
                }
            });
        });

        // Wire card clicks to show details modal
        document.querySelectorAll('.course-card').forEach(card => {
            card.addEventListener('click', (e) => {
                const cid = card.getAttribute('data-course-id');
                if (!cid) return;
                const c = allCourses.find(x => String(x.id) === String(cid));
                if (!c) return;

                document.getElementById('modalTitle').textContent = c.name || '';
                document.getElementById('modalCode').textContent = c.code || '';
                document.getElementById('modalCredits').textContent = c.credits || '';
                document.getElementById('modalDept').textContent = c.department || '';
                document.getElementById('modalLevel').textContent = c.level || '';
                document.getElementById('modalDesc').textContent = c.description || '';
                document.getElementById('modalId').textContent = c.id || '';

                document.getElementById('courseModalOverlay').style.display = 'block';
                document.getElementById('courseModal').style.display = 'block';
            });
        });

        // Modal close
        const closeModal = () => {
            document.getElementById('courseModalOverlay').style.display = 'none';
            document.getElementById('courseModal').style.display = 'none';
        }
        const closeBtn = document.getElementById('closeCourseModal');
        if (closeBtn) closeBtn.addEventListener('click', closeModal);
        const overlay = document.getElementById('courseModalOverlay');
        if (overlay) overlay.addEventListener('click', closeModal);
    }

    // Add course form handling (admin/demo)
    const addCourseForm = document.getElementById('addCourseForm');
    if (addCourseForm) {
        addCourseForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const code = document.getElementById('newCourseCode')?.value.trim();
            const name = document.getElementById('newCourseName')?.value.trim();
            const credits = parseInt(document.getElementById('newCourseCredits')?.value, 10) || 0;
            const department = document.getElementById('newCourseDept')?.value.trim();
            const level = document.getElementById('newCourseLevel')?.value;
            const description = document.getElementById('newCourseDesc')?.value.trim();
            const statusDiv = document.getElementById('courseStatus');

            if (!code || !name) {
                if (statusDiv) statusDiv.textContent = 'Please provide code and name.';
                return;
            }

            try {
                const response = await fetch(`${API_BASE_URL}/api/courses`, {
                    method: 'POST',
                    headers: {
                        ...getAuthHeaders({ 'Content-Type': 'application/json', 'Accept': 'application/json' })
                    },
                    body: JSON.stringify({ code, name, credits, department, level, description })
                });

                const result = await readResponse(response);
                if (response.ok) {
                    if (statusDiv) statusDiv.textContent = 'Course added.';
                    addCourseForm.reset();
                    fetchCourses();
                } else {
                    if (statusDiv) statusDiv.textContent = result.message || 'Failed to add course.';
                }
            } catch (err) {
                console.error('Add course error', err);
                if (statusDiv) statusDiv.textContent = 'Error adding course.';
            }
        });
    }

    function filterCourses() {
        if (!courseList) return;

        const searchText = (searchInput?.value || "").toLowerCase().trim();
        const department = departmentFilter?.value || "";
        const level = levelFilter?.value || "";

        const filtered = allCourses.filter(course => {
            const name = (course.name || "").toLowerCase();
            const code = (course.code || "").toLowerCase();
            const description = (course.description || "").toLowerCase();
            const dept = course.department || "";
            const lvl = course.level || "";

            const matchesSearch =
                name.includes(searchText) ||
                code.includes(searchText) ||
                description.includes(searchText);

            const matchesDepartment = department === "" || dept === department;
            const matchesLevel = level === "" || lvl === level;

            return matchesSearch && matchesDepartment && matchesLevel;
        });

        displayCourses(filtered);
    }

    async function fetchCourses() {
        if (!courseList) return;

        try {
            const response = await fetch(`${API_BASE_URL}/api/courses`, {
                headers: getAuthHeaders({
                    "Accept": "application/json"
                })
            });

            const data = await readResponse(response);

            if (response.status === 403) {
                courseList.innerHTML = "<p>Please log in to view courses.</p>";
                return;
            }

            if (!response.ok) {
                throw new Error(data.message || `Failed to load courses (${response.status})`);
            }

            allCourses = Array.isArray(data.data) ? data.data : Array.isArray(data) ? data : [];
            displayCourses(allCourses);
        } catch (error) {
            console.error("Error fetching courses:", error);
            courseList.innerHTML = "<p>Failed to load courses from backend.</p>";
        }
    }

    if (searchInput) searchInput.addEventListener("input", filterCourses);
    if (departmentFilter) departmentFilter.addEventListener("change", filterCourses);
    if (levelFilter) levelFilter.addEventListener("change", filterCourses);
    if (courseList) fetchCourses();

    // --- NODE.JS FEEDBACK API INTEGRATION ---
    const NODE_API_URL = "http://localhost:3000";
    const feedbackForm = document.getElementById("feedbackForm");
    const feedbackList = document.getElementById("feedbackList");
    const fbErrorMsg = document.getElementById("fbErrorMsg");
    const fbSuccessMsg = document.getElementById("fbSuccessMsg");

    async function loadFeedbacks() {
        if (!feedbackList) return;
        try {
            const res = await fetch(`${NODE_API_URL}/api/feedback`);
            const data = await res.json();
            if (res.ok && data.status === "success") {
                displayFeedbacks(data.data);
            } else {
                feedbackList.innerHTML = "<p style='color: red;'>Failed to load feedbacks.</p>";
            }
        } catch (error) {
            console.error("Feedback error:", error);
            feedbackList.innerHTML = "<p style='color: red;'>Node.js server not running on port 3000</p>";
        }
    }

    function displayFeedbacks(items) {
        if (!feedbackList) return;
        if (items.length === 0) {
            feedbackList.innerHTML = "<p>No feedback submitted yet.</p>";
            return;
        }
        feedbackList.innerHTML = items.map(fb => `
            <div style="border-bottom: 1px solid #eee; padding-bottom: 10px; margin-bottom: 5px;">
                <div style="display: flex; justify-content: space-between; font-weight: bold;">
                    <span>${escapeHTML(fb.name)} (${escapeHTML(fb.email)})</span>
                    <span style="color: #ff9800;">${"★".repeat(fb.rating)}${"☆".repeat(5-fb.rating)}</span>
                </div>
                <p style="margin: 5px 0;">${escapeHTML(fb.comment)}</p>
                <div style="display: flex; gap: 10px; font-size: 0.8em;">
                    <a href="#" onclick="editFeedback(${fb.id}, '${escapeHTML(fb.comment)}', ${fb.rating}); return false;" style="color: blue;">Edit</a>
                    <a href="#" onclick="deleteFeedback(${fb.id}); return false;" style="color: red;">Delete</a>
                </div>
            </div>
        `).join("");
    }

    function escapeHTML(str) {
        return str.replace(/[&<>'"]/g, 
            tag => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;' }[tag] || tag)
        );
    }

    window.deleteFeedback = async function(id) {
        if (!confirm("Are you sure you want to delete this feedback?")) return;
        try {
            const res = await fetch(`${NODE_API_URL}/api/feedback/${id}`, { method: "DELETE" });
            const data = await res.json();
            if (res.ok && data.status === "success") {
                fbSuccessMsg.innerText = "Feedback deleted successfully!";
                setTimeout(() => fbSuccessMsg.innerText = "", 3000);
                loadFeedbacks();
            } else {
                fbErrorMsg.innerText = data.message || "Failed to delete feedback";
            }
        } catch (error) {
            fbErrorMsg.innerText = "Failed to communicate with Node.js server";
        }
    };

    window.editFeedback = async function(id, currentComment, currentRating) {
        const newComment = prompt("Edit your comment:", currentComment);
        if (newComment === null) return;
        const newRating = prompt("Edit your rating (1-5):", currentRating);
        if (newRating === null) return;

        try {
            const res = await fetch(`${NODE_API_URL}/api/feedback/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ comment: newComment, rating: newRating })
            });
            const data = await res.json();
            if (res.ok && data.status === "success") {
                fbSuccessMsg.innerText = "Feedback updated successfully!";
                setTimeout(() => fbSuccessMsg.innerText = "", 3000);
                loadFeedbacks();
            } else {
                fbErrorMsg.innerText = data.message || "Failed to update feedback";
            }
        } catch (error) {
            fbErrorMsg.innerText = "Failed to communicate with Node.js server";
        }
    };

    if (feedbackForm) {
        feedbackForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            fbErrorMsg.innerText = "";
            fbSuccessMsg.innerText = "";

            const name = document.getElementById("fbName").value;
            const email = document.getElementById("fbEmail").value;
            const comment = document.getElementById("fbComment").value;
            const rating = document.getElementById("fbRating").value;

            try {
                const res = await fetch(`${NODE_API_URL}/api/feedback`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ name, email, comment, rating })
                });
                const data = await res.json();
                if (res.ok && data.status === "success") {
                    fbSuccessMsg.innerText = "Feedback submitted successfully!";
                    feedbackForm.reset();
                    loadFeedbacks();
                } else {
                    fbErrorMsg.innerText = data.message || "Failed to submit feedback";
                }
            } catch (error) {
                fbErrorMsg.innerText = "Failed to communicate with Node.js server";
            }
        });
    }

    if (feedbackList) loadFeedbacks();
});