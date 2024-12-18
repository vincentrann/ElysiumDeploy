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

function proceedToDetails(event) {
    event.preventDefault(); // Prevent form submission
    document.getElementById('signup-screen').classList.add('hidden');
    document.getElementById('details-screen').classList.remove('hidden');
}

function backToSignup() {
    document.getElementById('details-screen').classList.add('hidden');
    document.getElementById('signup-screen').classList.remove('hidden');
}

function registerUser(event) {
    event.preventDefault(); // Prevent form submission
    
    // Collect data from both screens
    const userDetails = {
        firstName: document.getElementById('sign-up-first-name').value,
        lastName: document.getElementById('sign-up-last-name').value,
        email: document.getElementById('sign-up-email').value,
        phone: document.getElementById('sign-up-phone').value,
        password: document.getElementById('sign-up-password').value,
        cardInfo: {
            number: document.getElementById('card-number').value,
            expiry: document.getElementById('card-expiry').value,
            cvv: document.getElementById('card-cvv').value,
        },
        shippingAddress: {
            address: document.getElementById('shipping-address').value,
            city: document.getElementById('shipping-city').value,
            postalCode: document.getElementById('shipping-postal-code').value,
            country: document.getElementById('shipping-country').value,
        },
    };

    console.log('User Details:', userDetails);
    // Send data to the backend or process it as needed
}

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