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

async function registerUser(event) {
    event.preventDefault(); // Prevents form from submitting normally

    const fullName = document.getElementById('sign-up-full-name').value;
    const email = document.getElementById('sign-up-email').value;
    const password = document.getElementById('sign-up-password').value;
    const confirmPassword = document.getElementById('sign-up-confirm-password').value;
    const role = "user"; // Assuming role is "user" by default

    if (password !== confirmPassword) {
        alert("Passwords do not match");
        return;
    }

    const user = { name: fullName, email: email, password: password };

    try {
        const response = await fetch('http://localhost:8080/api/users/register?role=' + role, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        });

        if (response.ok) {
            const data = await response.json();
            alert('Registration successful: ' + data.name);
            showLogin();
        } else {
            alert('Registration failed');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}