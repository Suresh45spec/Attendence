function toggleSidebar() {
    var sidebar = document.querySelector(".sidebar");
    if (sidebar) {
        sidebar.classList.toggle("open");
    }
}

function validateLoginForm() {
    var username = document.getElementById("username");
    var password = document.getElementById("password");
    if (!username.value.trim() || !password.value.trim()) {
        showFormError("Please enter both username and password.");
        return false;
    }
    return true;
}

function validateStudentForm() {
    var roll = document.getElementById("rollNo");
    var name = document.getElementById("name");
    var division = document.getElementById("division");
    var total = document.getElementById("totalClasses");
    var attended = document.getElementById("classesAttended");

    if (!roll.value.trim() || !name.value.trim() || !division.value.trim()
            || total.value === "" || attended.value === "") {
        showFormError("Please fill all student details.");
        return false;
    }

    var totalNum = Number(total.value);
    var attendedNum = Number(attended.value);

    if (!Number.isInteger(totalNum) || !Number.isInteger(attendedNum)) {
        showFormError("Total Classes and Classes Attended must be whole numbers.");
        return false;
    }
    if (totalNum < 0 || attendedNum < 0) {
        showFormError("Class counts cannot be negative.");
        return false;
    }
    if (attendedNum > totalNum) {
        showFormError("Classes Attended cannot be more than Total Classes.");
        return false;
    }
    return true;
}

function showFormError(message) {
    var box = document.getElementById("jsError");
    if (!box) {
        alert(message);
        return;
    }
    box.textContent = message;
    box.classList.remove("hidden");
    box.style.display = "block";
}

function filterTable() {
    var input = document.getElementById("liveSearch");
    if (!input) {
        return;
    }
    var term = input.value.toLowerCase();
    var rows = document.querySelectorAll("#studentTable tbody tr");
    rows.forEach(function (row) {
        row.style.display = row.textContent.toLowerCase().indexOf(term) === -1 ? "none" : "";
    });
}

function confirmLogout(event) {
    if (!window.confirm("Log out of the attendance system?")) {
        event.preventDefault();
    }
}

function validateStudentLoginForm() {
    var roll = document.getElementById("rollNo");
    var password = document.getElementById("password");
    if (!roll.value.trim() || !password.value.trim()) {
        showFormError("Please enter both roll number and password.");
        return false;
    }
    return true;
}

function validateRegisterForm() {
    var fullName = document.getElementById("fullName");
    var email = document.getElementById("email");
    var username = document.getElementById("username");
    var password = document.getElementById("password");
    var confirm = document.getElementById("confirmPassword");

    if (!fullName.value.trim() || !email.value.trim() || !username.value.trim()
            || !password.value || !confirm.value) {
        showFormError("Please fill in all fields.");
        return false;
    }
    if (!/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(email.value.trim())) {
        showFormError("Please enter a valid email address.");
        return false;
    }
    if (password.value.length < 6) {
        showFormError("Password must be at least 6 characters long.");
        return false;
    }
    if (password.value !== confirm.value) {
        showFormError("Passwords do not match.");
        return false;
    }
    return true;
}

function validateForgotForm() {
    var username = document.getElementById("username");
    var email = document.getElementById("email");
    var password = document.getElementById("password");
    var confirm = document.getElementById("confirmPassword");

    if (!username.value.trim() || !email.value.trim() || !password.value || !confirm.value) {
        showFormError("Please fill in all fields.");
        return false;
    }
    if (!/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(email.value.trim())) {
        showFormError("Please enter a valid email address.");
        return false;
    }
    if (password.value.length < 6) {
        showFormError("New password must be at least 6 characters long.");
        return false;
    }
    if (password.value !== confirm.value) {
        showFormError("Passwords do not match.");
        return false;
    }
    return true;
}

/* Switches the "Username / Roll Number" label when the role radio changes. */
function toggleRoleFields(radio) {
    var label = document.getElementById("usernameLabel");
    if (!label) {
        return;
    }
    label.textContent = radio.value === "student" ? "Roll Number" : "Username";
}

/* Shows only the fields relevant to the chosen report type. */
function toggleReportFields() {
    var select = document.getElementById("type");
    if (!select) {
        return;
    }
    var type = select.value;
    setVisibility(".report-daily", type === "daily");
    setVisibility(".report-range", type === "weekly" || type === "monthly" || type === "subject");
    setVisibility(".report-subject", type === "subject");
    setVisibility(".report-student", type === "student");
}

function setVisibility(selector, visible) {
    document.querySelectorAll(selector).forEach(function (element) {
        element.style.display = visible ? "flex" : "none";
    });
}

function validateAttendanceForm() {
    var checked = document.querySelectorAll("input[type='radio']:checked");
    if (checked.length === 0) {
        showFormError("Mark at least one student as Present or Absent.");
        return false;
    }
    return true;
}

function validateProfileForm() {
    var fullName = document.getElementById("fullName");
    var email = document.getElementById("email");
    if (!fullName.value.trim()) {
        showFormError("Please enter your name.");
        return false;
    }
    if (!/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(email.value.trim())) {
        showFormError("Please enter a valid email address.");
        return false;
    }
    return true;
}

function validatePasswordForm() {
    var current = document.getElementById("currentPassword");
    var password = document.getElementById("password");
    var confirm = document.getElementById("confirmPassword");
    if (!current.value || !password.value || !confirm.value) {
        showFormError("Please fill in all password fields.");
        return false;
    }
    if (password.value.length < 6) {
        showFormError("New password must be at least 6 characters long.");
        return false;
    }
    if (password.value !== confirm.value) {
        showFormError("New passwords do not match.");
        return false;
    }
    return true;
}

/* Run report field toggling as soon as the page is ready. */
toggleReportFields();
