function logout(){
    localStorage.removeItem("userId");
    window.location.replace("/index.html");
}