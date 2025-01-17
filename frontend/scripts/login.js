function showLogin() {
    document.getElementById('login-screen').classList.remove('hidden');
    document.getElementById('signup-screen').classList.add('hidden');
    document.getElementById('admin-login-screen').classList.add('hidden');
}

function showSignup() {
    document.getElementById('login-screen').classList.add('hidden');
    document.getElementById('signup-screen').classList.remove('hidden');
    document.getElementById('admin-login-screen').classList.add('hidden');
}

function proceedToDetails(event)
{
    event.preventDefault(); // Prevent form submission
    document.getElementById('signup-screen').classList.add('hidden');
    document.getElementById('details-screen').classList.remove('hidden');
}

function backToSignup() {
    document.getElementById('details-screen').classList.add('hidden');
    document.getElementById('signup-screen').classList.remove('hidden');
}

async function registerUser(event) {
    event.preventDefault();

    // Step 1: Register User
    const userRegistrationData = {
        username: document.getElementById('sign-up-first-name').value + document.getElementById('sign-up-last-name').value,
        firstName: document.getElementById('sign-up-first-name').value,
        lastName: document.getElementById('sign-up-last-name').value,
        email: document.getElementById('sign-up-email').value,
        phone: document.getElementById('sign-up-phone').value,
        password: document.getElementById('sign-up-password').value,
        address: document.getElementById('street').value + ", " +
            document.getElementById('city').value + ", " +
            document.getElementById('state').value + " " +
            document.getElementById('zip').value,
        billingAddress: document.getElementById('street').value + ", " +
            document.getElementById('city').value + ", " +
            document.getElementById('state').value + " " +
            document.getElementById('zip').value,
        dob: "1990-01-01" // Add a default dob or get from input field if available
    };

    try {
        // Call the User Registration API
        const response = await fetch("http://localhost:8080/api/users/register?role=member", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(userRegistrationData)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || "User registration failed");
        }

        // Get the registered user data
        const newUser = await response.json();
        console.log("User registered successfully:", newUser);

        // Step 2: Add Credit Card
        const creditCardData = {
            cardNumber: document.getElementById('card-number').value,
            expiryDate: document.getElementById('expiry-date').value,
            cvv: document.getElementById('cvv').value,
            cardHolderName: newUser.firstName + " " + newUser.lastName,
            billingAddress: newUser.billingAddress
        };

        const creditCardResponse = await fetch(`http://localhost:8080/api/credit-cards/user/${newUser.id}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(creditCardData)
        });

        if (!creditCardResponse.ok) {
            const errorData = await creditCardResponse.json();
            throw new Error(errorData.message || "Credit card registration failed");
        }

        alert("Registration and credit card addition successful!");
        console.log("Credit Card added successfully!");

    } catch (error) {
        console.error("Error:", error);
        alert("An error occurred: " + error.message);
    }
}

// Member Login Function
async function loginMemberUser(event) {
    event.preventDefault();

    const email = document.getElementById('login-email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch("http://localhost:8080/api/users/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || "Invalid email or password");
        }

        const user = await response.json();
        console.log("User logged in successfully:", user);

        // Store the user details in localStorage or sessionStorage
        localStorage.setItem('userId', user.id);
        localStorage.setItem('userRole', user.role);

        // Redirect user based on role
        if (user.role === "ADMIN") {
            window.location.href = "/admin.html";
        } else {
            alert("Successful Login");
            window.location.href = "../index.html";
        }

    } catch (error) {
        console.error("Login Error:", error);
        alert("Login failed: " + error.message);
    }
}

// Admin Login Function
async function loginAdminUser(event) {
    event.preventDefault();

    const email = document.getElementById('admin-email').value;
    const password = document.getElementById('admin-password').value;

    try {
        const response = await fetch("http://localhost:8080/api/users/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        if (!response.ok) throw new Error("Invalid email or password");

        const user = await response.json();
        console.log("Admin logged in successfully:", user);

        // Store the admin details in localStorage or sessionStorage
        localStorage.setItem('userId', user.id);
        localStorage.setItem('userRole', user.role);

        // Redirect admin to the admin dashboard
        window.location.href = "/admin.html";

    } catch (error) {
        console.error("Login Error:", error);
        alert("Admin Login failed: " + error.message);
    }
}

// Attach event listeners for login forms
document.getElementById('member-login-form')?.addEventListener('submit', loginMemberUser);
document.getElementById('admin-login-form')?.addEventListener('submit', loginAdminUser);

async function loginUser(event){
    event.preventDefault();
    const email = document.getElementById("login-email").value;
    const password = document.getElementById("password").value;
    const response = await fetch("http://localhost:8080/api/users/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, password })
    });

    if (response.ok) {
        const loggedInUser = await response.json();
        localStorage.setItem("userId",loggedInUser.id);
        localStorage.setItem("role", loggedInUser.role)
        if (localStorage.getItem("role") == "ADMIN"){
            window.location.assign("admin.html")
        }
        else{
            window.location.replace("/index.html");
        }
    } else {
        const error = await response.json();
        alert(`Error: ${error.message || "Invalid credentials"}`);
    }
}