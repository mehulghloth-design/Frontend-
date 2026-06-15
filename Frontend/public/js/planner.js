document.addEventListener("DOMContentLoaded", () => {
    const API_BASE_URL = "https://springboot-backend-zsqe.onrender.com";

    const courseSelect = document.getElementById("courseSelect");
    const selectedCoursesDiv = document.getElementById("selectedCourses");
    const plansContainer = document.getElementById("plansContainer");
    const statusMessage = document.getElementById("statusMessage");
    const totalCreditsDisplay = document.getElementById("totalCreditsDisplay");
    const currentUserInfo = document.getElementById("currentUserInfo");

    let allCourses = [];
    let selectedCourses = [];

    function getCurrentUser() {
        const localUser = localStorage.getItem("user");
        const sessionUser = sessionStorage.getItem("loggedInUser");

        try {
            const raw = JSON.parse(localUser || sessionUser || "{}");

            // Normalize common id shapes so callers can rely on userId
            if (raw) {
                if (!raw.userId && raw.id) raw.userId = raw.id;
                if (!raw.userId && raw.user && (raw.user.userId || raw.user.id)) {
                    raw.userId = raw.user.userId || raw.user.id;
                }
            }

            return raw;
        } catch {
            return {};
        }
    }

    const currentUser = getCurrentUser();

    if (currentUserInfo) {
        if (currentUser && currentUser.email) {
            currentUserInfo.textContent = `Logged in as: ${currentUser.email}`;
        } else {
            currentUserInfo.textContent = "No user found. Please log in again.";
        }
    }

    // Debug helper: show resolved userId and token (local demo only)
    const userDebugDiv = document.getElementById('userDebug');
    if (userDebugDiv) {
        const debugUserId = currentUser && currentUser.userId ? currentUser.userId : '(none)';
        const runtimeToken = localStorage.getItem('token');
        const shortToken = runtimeToken ? runtimeToken.slice(0, 20) + '...' : '(no token)';
        userDebugDiv.textContent = `userId: ${debugUserId} | token: ${shortToken}`;
    }

    // Read token at call-time so the page works if the token is set/changed after load
    function authHeaders(extraHeaders = {}) {
        const headers = { ...extraHeaders };
        const runtimeToken = localStorage.getItem('token');
        if (runtimeToken) {
            headers.Authorization = `Bearer ${runtimeToken}`;
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

    function setStatus(message, isError = false) {
        if (!statusMessage) return;
        statusMessage.textContent = message;
        statusMessage.style.color = isError ? "crimson" : "green";
    }

    function escapeHtml(value) {
        return String(value ?? "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }

    function updateTotalCredits() {
        const total = selectedCourses.reduce((sum, course) => sum + Number(course.credits || 0), 0);
        if (totalCreditsDisplay) {
            totalCreditsDisplay.textContent = String(total);
        }
    }

    function renderSelectedCourses() {
        if (!selectedCoursesDiv) return;

        if (selectedCourses.length === 0) {
            selectedCoursesDiv.innerHTML = "<p>No courses selected.</p>";
            updateTotalCredits();
            return;
        }

        selectedCoursesDiv.innerHTML = `
            <h3>Selected Courses</h3>
            ${selectedCourses.map((course, index) => `
                <div class="course-item">
                    <span>
                        ${escapeHtml(course.name)}
                        (${escapeHtml(course.code)})
                        - ${escapeHtml(course.credits)} credits
                    </span>

                    <button
                        class="remove-course"
                        type="button"
                        onclick="removeSelectedCourse(${index})">
                        Remove
                    </button>
                </div>
            `).join("")}
        `;

        updateTotalCredits();
    }

    async function fetchCourses() {
        try {
            const response = await fetch(`${API_BASE_URL}/api/courses`, {
                headers: authHeaders({
                    "Accept": "application/json"
                })
            });

            const data = await readResponse(response);

            if (!response.ok) {
                throw new Error(data.message || `Failed to load courses (${response.status})`);
            }

            allCourses = Array.isArray(data.data) ? data.data : Array.isArray(data) ? data : [];

            if (courseSelect) {
                courseSelect.innerHTML = `<option value="">Choose a course</option>`;
                allCourses.forEach(course => {
                    const option = document.createElement("option");
                    option.value = course.id;
                    option.textContent = `${course.name} (${course.code})`;
                    courseSelect.appendChild(option);
                });
            }
        } catch (error) {
            console.error("Error fetching courses:", error);
            setStatus("Failed to load courses.", true);
        }
    }

    function addCourseToPlan() {
        if (!courseSelect) return;

        const courseId = courseSelect.value;
        if (!courseId) {
            setStatus("Please choose a course.", true);
            return;
        }

        const course = allCourses.find(c => String(c.id) === String(courseId));
        if (!course) {
            setStatus("Selected course not found.", true);
            return;
        }

        const alreadyAdded = selectedCourses.some(c => String(c.id) === String(courseId));
        if (alreadyAdded) {
            setStatus("Course already added.", true);
            return;
        }

        selectedCourses.push(course);
        setStatus("");
        renderSelectedCourses();
    }

    async function createPlan() {
        const semesterName = document.getElementById("semesterName")?.value.trim() || "";
        const semesterOrderValue = document.getElementById("semesterOrder")?.value.trim() || "";
        const academicYearValue = document.getElementById("academicYear")?.value.trim() || "";

        if (!currentUser || !currentUser.userId) {
            setStatus("Please log in again. User information is missing.", true);
            return;
        }

        if (!semesterName || !semesterOrderValue || !academicYearValue) {
            setStatus("Please fill all fields.", true);
            return;
        }

        const semesterOrder = parseInt(semesterOrderValue, 10);
        const academicYear = parseInt(academicYearValue, 10);

        if (Number.isNaN(semesterOrder) || semesterOrder <= 0) {
            setStatus("Semester order must be a valid number greater than 0.", true);
            return;
        }

        if (Number.isNaN(academicYear) || academicYear <= 0) {
            setStatus("Academic year must be a valid number greater than 0.", true);
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/api/plans`, {
                method: "POST",
                headers: authHeaders({
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                }),
                body: JSON.stringify({
                    userId: currentUser.userId,
                    semesterName,
                    semesterOrder,
                    academicYear
                })
            });

            const result = await readResponse(response);

            if (!response.ok) {
                setStatus(result.message || "Could not save plan.", true);
                return;
            }

            const savedPlan = result.data || result;
            const planId = savedPlan.id || savedPlan.planId;

            for (const course of selectedCourses) {
                const pcResp = await fetch(`${API_BASE_URL}/api/planned-courses`, {
                    method: "POST",
                    headers: authHeaders({
                        "Content-Type": "application/json",
                        "Accept": "application/json"
                    }),
                    body: JSON.stringify({
                        planId,
                        courseId: course.id
                    })
                });

                if (!pcResp.ok) {
                    const pcResult = await readResponse(pcResp);
                    console.error("Failed to add planned course:", pcResult, pcResp.status);
                    setStatus(pcResult.message || `Failed to add course ${course.name} (status ${pcResp.status})`, true);
                    // continue attempting to add other courses but surface the error
                }
            }

            setStatus("Plan and courses saved successfully!");

            document.getElementById("semesterName").value = "";
            document.getElementById("semesterOrder").value = "";
            document.getElementById("academicYear").value = "";
            selectedCourses = [];
            renderSelectedCourses();
            fetchPlans();
        } catch (error) {
            console.error("Error creating plan:", error);
            setStatus("Something went wrong while saving.", true);
        }
    }

    async function fetchPlans() {
        if (!plansContainer) return;

        if (!currentUser || !currentUser.userId) {
            plansContainer.innerHTML = "<p>Please log in first.</p>";
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/api/plans/user/${currentUser.userId}`, {
                headers: authHeaders({
                    "Accept": "application/json"
                })
            });

            const result = await readResponse(response);

            if (response.status === 403) {
                plansContainer.innerHTML = "<p>Please log in to view your plans.</p>";
                return;
            }

            if (!response.ok) {
                const errMsg = result?.message || `Failed to load plans (${response.status})`;
                plansContainer.innerHTML = `<p>${escapeHtml(errMsg)}</p>`;
                return;
            }

            const plans = Array.isArray(result.data) ? result.data : Array.isArray(result) ? result : [];

            if (plans.length === 0) {
                plansContainer.innerHTML = "<p>No plans found.</p>";
                return;
            }

            plansContainer.innerHTML = plans.map(plan => {
                const planId = plan.id || plan.planId;
                const courses = Array.isArray(plan.plannedCourses) ? plan.plannedCourses : [];
                const totalCredits = courses.reduce((sum, item) => sum + Number(item.course?.credits || 0), 0);

                const courseItems = courses.length > 0
                    ? courses.map(pc => `
                        <li>
                            ${escapeHtml(pc.course?.name || "Unknown course")}
                            (${escapeHtml(pc.course?.code || "")})
                            - ${escapeHtml(pc.course?.credits || 0)} credits
                        </li>
                    `).join("")
                    : "<li>No courses added yet.</li>";

                return `
                    <div class="plan-card">
                        <h3>${escapeHtml(plan.semesterName)}</h3>
                        <div class="plan-meta">
                            <p><strong>Semester Order:</strong> ${escapeHtml(plan.semesterOrder)}</p>
                            <p><strong>Academic Year:</strong> ${escapeHtml(plan.academicYear)}</p>
                            <p><strong>Total Credits:</strong> ${escapeHtml(totalCredits)}</p>
                        </div>

                        <div class="courses-badge">
                            <strong>Courses:</strong>
                            <ul>${courseItems}</ul>
                        </div>

                        <div class="plan-actions">
                            <button class="edit-plan-btn" type="button" onclick="editPlan(${planId}, '${escapeHtml(plan.semesterName)}', ${plan.semesterOrder}, ${plan.academicYear})">Edit</button>
                            <button class="delete-plan-btn" type="button" onclick="deletePlan(${planId})">Delete</button>
                        </div>
                    </div>
                `;
            }).join("");
        } catch (error) {
            console.error("Error fetching plans:", error);
            plansContainer.innerHTML = "<p>Failed to load plans.</p>";
        }
    }

    async function editPlan(planId, currentName, currentOrder, currentYear) {
        const newName = prompt("Enter new semester name:", currentName);
        if (newName === null) return;

        const newOrder = prompt("Enter new semester order:", currentOrder);
        if (newOrder === null) return;

        const newYear = prompt("Enter new academic year:", currentYear);
        if (newYear === null) return;

        const semesterOrder = parseInt(newOrder, 10);
        const academicYear = parseInt(newYear, 10);

        if (!newName.trim() || Number.isNaN(semesterOrder) || Number.isNaN(academicYear)) {
            setStatus("Please enter valid values.", true);
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/api/plans/${planId}`, {
                method: "PUT",
                headers: authHeaders({
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                }),
                body: JSON.stringify({
                    userId: currentUser.userId,
                    semesterName: newName.trim(),
                    semesterOrder,
                    academicYear
                })
            });

            const result = await readResponse(response);

            if (response.ok) {
                setStatus("Plan updated successfully!");
                fetchPlans();
            } else {
                setStatus(result.message || "Could not update plan.", true);
            }
        } catch (error) {
            console.error("Error updating plan:", error);
            setStatus("Something went wrong while updating.", true);
        }
    }

    async function deletePlan(planId) {
        const confirmed = confirm("Delete this semester plan?");
        if (!confirmed) return;

        try {
            const response = await fetch(`${API_BASE_URL}/api/plans/${planId}`, {
                method: "DELETE",
                headers: authHeaders({
                    "Accept": "application/json"
                })
            });

            const result = await readResponse(response);

            if (response.ok) {
                setStatus("Plan deleted successfully!");
                fetchPlans();
            } else {
                setStatus(result.message || "Could not delete plan.", true);
            }
        } catch (error) {
            console.error("Error deleting plan:", error);
            setStatus("Something went wrong while deleting.", true);
        }
    }

    window.addCourseToPlan = addCourseToPlan;
    window.createPlan = createPlan;
    window.editPlan = editPlan;
    window.deletePlan = deletePlan;
    window.removeSelectedCourse = function (index) {
        selectedCourses.splice(index, 1);
        renderSelectedCourses();
    };

    renderSelectedCourses();
    fetchCourses();
    fetchPlans();
    fetchPlans();
});