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

function showAdminLogin() {
    document.getElementById('login-screen').classList.add('hidden');
    document.getElementById('signup-screen').classList.add('hidden');
    document.getElementById('admin-login-screen').classList.remove('hidden');
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